package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ApArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.search.dtos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/8 16:51
 * @Version 1.0
 */
@Service
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {


    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void buildArticleToMinIO(Long articleId) {
        //1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, articleId));
        if(apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())){
            //2.通过freemarker生成html文件
            InputStream in = null;
            try {
                Template template  = configuration.getTemplate("article.ftl");
                //合成
                Map<String,Object> map = new HashMap<>();
                map.put("content", JSONArray.parseArray(apArticleContent.getContent()));
                StringWriter out = new StringWriter();

                template.process(map,out);

                in = new ByteArrayInputStream(out.toString().getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                log.info("生成文章详情页面失败");
            }

            //3.上传到minio
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

            //4.修改ap_article 中的static_url
            ApArticle apArticle = apArticleMapper.selectById(apArticleContent.getArticleId());

            //查看之前是否存在url,如存在，则删除
            if(StringUtils.isNotBlank(apArticle.getStaticUrl())){
                fileStorageService.delete(apArticle.getStaticUrl());
            }

            apArticle.setStaticUrl(path);
            apArticleMapper.updateById(apArticle);
            log.info("生成静态URL并更新数据库完成");

            sendEsMessage(apArticle,apArticleContent);
        }
    }

    public void sendEsMessage(ApArticle article,ApArticleContent apArticleContent){
        try {
            //发送消息
            SearchArticleVo articleVo = new SearchArticleVo();
            BeanUtils.copyProperties(article, articleVo);
            articleVo.setContent(apArticleContent.getContent());
            articleVo.setAuthorId(article.getId());
            kafkaTemplate.send(ApArticleConstants.TOPIC_SYNC_ES, JSON.toJSONString(articleVo));
        }catch (Exception e){
            log.error("同步到ES异常",e);
        }
    }
}
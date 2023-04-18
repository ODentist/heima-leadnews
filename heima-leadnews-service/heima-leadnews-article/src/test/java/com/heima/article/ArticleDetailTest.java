package com.heima.article;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 15:44
 * @Version 1.0
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleDetailTest {

    @Autowired
    private ApArticleContentMapper contentMapper;
    @Autowired
    private Configuration configuration;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ApArticleMapper articleMapper;

    @Test
    public void createDetail() throws Exception{

        ApArticleContent content = contentMapper.selectOne(
                Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, 1302862387124125698L));
        if(content == null){
            return;
        }
        Template template = configuration.getTemplate("article.ftl");
        //构造数据模型
        Map<String,Object> data = new HashMap<>();
        data.put("content", JSONArray.parseArray(content.getContent()));

        template.process(data,new FileWriter("D://article.html"));

        //上传
        String path = fileStorageService.uploadHtmlFile("", "article.html", new FileInputStream("D://article.html"));

        ApArticle article = new ApArticle();
        article.setId(1302862387124125698L);
        article.setStaticUrl(path);
        articleMapper.updateById(article);
    }
}
package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 9:50
 * @Version 1.0
 */
@Service
public class ApArticleServiceImpl implements ApArticleService {

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private ApArticleConfigMapper configMapper;
    @Autowired
    private ApArticleContentMapper contentMapper;


    public final static int DEFAULT_SIZE = 10;

    @Override
    public ResponseResult loadArticle(ArticleHomeDto dto,Integer type) {
        Integer size = dto.getSize();
        if(size == null || size <= 0 || size > 10){
            size = DEFAULT_SIZE;
        }
        dto.setSize(size);
        if(dto.getMaxBehotTime() == null){
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() != null){
            dto.setMinBehotTime(new Date());
        }

        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag("__all__");
        }

        List<ApArticle> articles = articleMapper.loadArticleList(dto,type);
        return ResponseResult.okResult(articles);
    }

    @Override
    @Transactional
    public ResponseResult save(ArticleDto articleDto) {
        //基本校验
        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(articleDto,article);
        article.setPublishTime(new Date());
        if(article.getId() == null){
            articleMapper.insert(article);
            //保存配置表
            ApArticleConfig config = new ApArticleConfig(article.getId());
//            config.setArticleId(article.getId());
//            config.setIsForward((short)0);
//            config.setIsDown((short)0);
//            config.setIsDelete((short)0);
//            config.setIsComment((short)0);
            configMapper.insert(config);


            ApArticleContent content = new ApArticleContent();
            content.setArticleId(article.getId());
            content.setContent(articleDto.getContent());
            contentMapper.insert(content);
        }else {
            articleMapper.updateById(article);

            ApArticleContent articleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, articleDto.getId()));
            articleContent.setContent(articleDto.getContent());
            contentMapper.updateById(articleContent);
        }
        return ResponseResult.okResult(article.getId());
    }
}
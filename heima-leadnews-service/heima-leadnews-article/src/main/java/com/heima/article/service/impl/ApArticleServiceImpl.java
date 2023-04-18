package com.heima.article.service.impl;

import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
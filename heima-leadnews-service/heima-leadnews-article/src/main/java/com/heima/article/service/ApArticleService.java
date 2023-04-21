package com.heima.article.service;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleService {

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    ResponseResult loadArticle(ArticleHomeDto dto,Integer type);

    /**
     * 保存或者修改文章
     * @param articleDto
     * @return
     */
    ResponseResult save(ArticleDto articleDto);
}

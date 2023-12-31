package com.heima.article.service;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
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

    /**
     * 加载文章详情 数据回显
     * @param dto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto dto);

    /**
     * 加载文章列表
     * @param dto
     * @param type  1 加载更多   2 加载最新
     * @param firstPage  true 是首页  false 不是首页
     * @return
     */
    public ResponseResult loadArticleListV2(ArticleHomeDto dto, Integer type,boolean firstPage);
}

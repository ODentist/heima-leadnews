package com.heima.article.service;

public interface ArticleFreemarkerService {

    /**
     * 生成文章详情静态文件，上传到minio，并更新文章表
     *
     * @param articleId
     */
    void buildArticleToMinIO(Long articleId);
}

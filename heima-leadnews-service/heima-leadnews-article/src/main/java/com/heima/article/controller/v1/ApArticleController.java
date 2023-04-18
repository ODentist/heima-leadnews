package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ApArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 9:34
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/article")
public class ApArticleController {

    @Autowired
    private ApArticleService articleService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto homeDto){
        return articleService.loadArticle(homeDto, ApArticleConstants.TYPE_MORE);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadmore(@RequestBody ArticleHomeDto homeDto){
        return articleService.loadArticle(homeDto,ApArticleConstants.TYPE_MORE);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadnew(@RequestBody ArticleHomeDto homeDto){
        return articleService.loadArticle(homeDto,ApArticleConstants.TYPE_NEW);
    }
}
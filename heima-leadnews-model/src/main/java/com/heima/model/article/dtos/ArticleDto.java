package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/21 9:54
 * @Version 1.0
 */
@Data
public class ArticleDto extends ApArticle {

    private String content;

}
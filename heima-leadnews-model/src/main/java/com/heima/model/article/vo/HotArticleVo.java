package com.heima.model.article.vo;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: ODENTIST
 * @Date: 2023/06/20/18:05
 * @Description:
 */
@Data
public class HotArticleVo extends ApArticle {
    private Integer score;

}

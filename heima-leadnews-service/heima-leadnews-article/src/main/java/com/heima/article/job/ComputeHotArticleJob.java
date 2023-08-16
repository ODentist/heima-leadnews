package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: ODENTIST
 * @Date: 2023/06/20/21:17
 * @Description:
 */
@Component
@Slf4j
public class ComputeHotArticleJob {
    @Autowired
    private HotArticleService hotArticleService;
    @XxlJob("ComputHotArticleJob")
    public void handler(){
        log.info("hot article cauculate now");
        hotArticleService.computeHotArticle();
        log.info("hot article complete");
    }
}

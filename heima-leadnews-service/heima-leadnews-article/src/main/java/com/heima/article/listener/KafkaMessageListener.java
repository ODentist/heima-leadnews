package com.heima.article.listener;

import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ApArticleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/9 15:19
 * @Version 1.0
 */
@Component
@Slf4j
public class KafkaMessageListener {

    @Autowired
    private ArticleFreemarkerService freemarkerService;

    @KafkaListener(topics = ApArticleConstants.TOPIC_CREATE_DETAIL_HTML)
    public void createStaticUrl(String message){
        log.info("收到消息：{}",message);
        freemarkerService.buildArticleToMinIO(Long.parseLong(message));
    }
}
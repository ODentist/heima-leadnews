package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ApArticleConstants;
import com.heima.model.search.dtos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/6/14 10:31
 * @Version 1.0
 */
@Component
@Slf4j
public class ArticleSyncService {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @KafkaListener(topics = ApArticleConstants.TOPIC_SYNC_ES)
    public void message(String message) throws IOException {
        log.info("收到同步文章消息：{}",message);
        SearchArticleVo articleVo = JSON.parseObject(message, SearchArticleVo.class);
        IndexRequest indexRequest = new IndexRequest("app_info_article")
                .id(articleVo.getId() + "")
                        .source(JSON.toJSONString(articleVo), XContentType.JSON);
        highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }
}
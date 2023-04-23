package com.heima.kafka.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/23 14:44
 * @Version 1.0
 */
@Component
public class MessageListener {

//    @KafkaListener(topics = "topic-1")
//    public void message(String message){
//        System.out.println("收到消息：" +message);
//    }

    @KafkaListener(topics = "topic-1")
    public void message(ConsumerRecord<String,String> record, Consumer<String,String> consumer){
        System.out.println("收到消息：" + record.value());
        System.out.println("当前偏移量：" + record.offset());
        consumer.commitSync();
    }

}
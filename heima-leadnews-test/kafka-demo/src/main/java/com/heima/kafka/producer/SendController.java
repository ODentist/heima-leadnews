package com.heima.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/23 14:42
 * @Version 1.0
 */
@RestController
public class SendController {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/send")
    public String send(){
        Student student = new Student();
        student.setAge("22");
        student.setName("zhangsan");
        kafkaTemplate.send("topic-1", JSON.toJSONString(student));
        return  "OK";
    }

}
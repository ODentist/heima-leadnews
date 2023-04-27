package com.heima.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/7 15:32
 * @Version 1.0
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.heima.apis")
public class CommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class,args);
    }
}
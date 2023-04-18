package com.heima.minio;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 15:01
 * @Version 1.0
 */
@SpringBootTest(classes = MinioApplication.class)
@RunWith(SpringRunner.class)
public class MinioTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        String path = fileStorageService.uploadHtmlFile("", "test.html", new FileInputStream("D://list.html"));
        System.out.println(path);
    }


    public static void main(String[] args) throws Exception{
        //上传文件到minio中
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.200.130:9000")
                .credentials("minio","minio123")
                .build();

        FileInputStream inputStream = new FileInputStream("D://list.html");

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .stream(inputStream,inputStream.available(),-1)
                .contentType("text/html")
                .object("test.html")
                .bucket("cd63")
                .build();
        minioClient.putObject(putObjectArgs);

        //访问规则
        //http://192.168.200.130:9000/cd63/test.html
    }

}
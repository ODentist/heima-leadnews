package com.heima.aliyun;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/21 9:14
 * @Version 1.0
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class ContentCheckTest {

    @Autowired
    private GreenTextScan textScan;

    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private FileStorageService storageService;

    @Test
    public void textScan() throws Exception {
        Map map = textScan.greeTextScan("我把自己的青春奉献给了祖国");
        System.out.println(map);
    }

    @Test
    public void imageScan() throws Exception {
        byte[] bytes = storageService.downLoadFile("http://192.168.200.130:9000/leadnews/2023/02/01/5fbe8d45c35a4534bd3171e2690be403.jpg");
        Map map = greenImageScan.imageScan(Arrays.asList(bytes));
        System.out.println(map);
    }

}
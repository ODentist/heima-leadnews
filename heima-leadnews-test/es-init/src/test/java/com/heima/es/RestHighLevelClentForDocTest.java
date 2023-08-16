package com.heima.es;

import com.alibaba.fastjson.JSON;
import com.heima.es.pojo.SearchArticleVo;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@SpringBootTest(classes = EsInitApplication.class)
@RunWith(SpringRunner.class)
public class RestHighLevelClentForDocTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 添加文档,使用对象作为数据
     * @throws IOException
     */
    @Test
    public void addDoc() throws IOException {
        SearchArticleVo vo = new SearchArticleVo();
        vo.setId(1234567856769L);
        vo.setAuthorId(11L);
        vo.setAuthorName("张三");
        vo.setContent("[{\"type\":\"text\",\"value\":\"我是一个标题1我是一个标题1我是一个标题1我是一个标题1我是一个标题1\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/04/26/213b82900e544354b382cd7fa50b8421.jpg\"}]");
        vo.setTitle("是一个标题");
        vo.setLayout((short)1);
        vo.setImages("http://192.168.200.130:9000/leadnews/2021/04/26/213b82900e544354b382cd7fa50b8421.jpg");
        vo.setPublishTime(new Date());
        vo.setStaticUrl("http://192.168.200.130:9000/leadnews/2021/10/10/1400354435804303361.html");
        IndexRequest request=new IndexRequest("app_info_article").id(vo.getId().toString()).source(JSON.toJSONString(vo), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * 修改文档：添加文档时，如果id存在则修改，id不存在则添加
     */
    @Test
    public void updateDoc() throws IOException {
        SearchArticleVo vo = new SearchArticleVo();
        vo.setId(1234567856769L);
        vo.setAuthorId(11L);
        vo.setAuthorName("张三");
        vo.setContent("[{\"type\":\"text\",\"value\":\"我是一个标题1我是一个标题1我是一个标题1我是一个标题1我是一个标题1\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/04/26/213b82900e544354b382cd7fa50b8421.jpg\"}]");
        vo.setTitle("是一个标题-v2");
        vo.setLayout((short)1);
        vo.setImages("http://192.168.200.130:9000/leadnews/2021/04/26/213b82900e544354b382cd7fa50b8421.jpg");
        vo.setPublishTime(new Date());
        vo.setStaticUrl("http://192.168.200.130:9000/leadnews/2021/10/10/1400354435804303361.html");

        String data = JSON.toJSONString(vo);

        IndexRequest request=new IndexRequest("app_info_article").id(vo.getId().toString()).source(data,XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * 根据id查询文档
     */
    @Test
    public void getDoc() throws IOException {
        //设置查询的索引、文档
        GetRequest indexRequest=new GetRequest("app_info_article","1234567856769");

        GetResponse response = restHighLevelClient.get(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    /**
     * 根据id删除文档
     */
    @Test
    public void delDoc() throws IOException {

        //设置要删除的索引、文档
        DeleteRequest deleteRequest=new DeleteRequest("app_info_article","1234567856769");

        DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }


}

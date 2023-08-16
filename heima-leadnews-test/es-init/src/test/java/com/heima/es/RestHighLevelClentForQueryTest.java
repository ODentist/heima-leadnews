package com.heima.es;

import com.alibaba.fastjson.JSON;
import com.heima.es.pojo.SearchArticleVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@SpringBootTest(classes = EsInitApplication.class)
@RunWith(SpringRunner.class)
public class RestHighLevelClentForQueryTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 查询所有  matchAllQuery
     */
    @Test
    public void matchAll() throws IOException {

        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构造查询条件
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from(0);
        sourceBuilder.size(10);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 精确查询
     */
    @Test
    public void termQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("authorId","11"));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 范围查询
     */
    @Test
    public void rangeQuery() throws IOException {

        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("authorId").lt(12));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }

    }

    /**
     * 会对查询条件进行分词之后再查询
     */
    @Test
    public void queryStringQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery("武汉高校很多").field("title").field("content").defaultOperator(Operator.OR));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }

    }

    /**
     * boolQuery：对多个查询条件连接。连接方式：
     *
     *   must（and）：条件必须成立
     *   must_not（not）：条件必须不成立
     *   should（or）：条件可以成立
     *   filter：条件必须成立，性能比must高。不会计算得分
     *           得分:即条件匹配度,匹配度越高，得分越高
     */
    @Test
    public void boolQueryBuilder() throws IOException {

        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("authorId","4"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lt(new Date().getTime()));


        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }

    }

    /**
     * 高亮查询：
     * 1. 设置高亮
     * 高亮字段
     * 前缀
     * 后缀
     * 2. 将高亮了的字段数据，替换原有数据
     */
    @Test
    public void testHighLightQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("app_info_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("authorId","4"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lt(new Date().getTime()));


        searchSourceBuilder.query(boolQueryBuilder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='red'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("总条数：" + hits.getTotalHits().value);
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            if(highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                System.out.println(StringUtils.join(fragments));
            }
        }


    }

}

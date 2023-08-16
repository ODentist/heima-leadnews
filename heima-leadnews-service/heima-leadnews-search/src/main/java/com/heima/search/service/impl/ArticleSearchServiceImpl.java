package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.search.service.ArticleSearchService;
import com.heima.search.service.UserSearchRecordService;
import com.heima.utils.common.UserIdThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/6/14 9:54
 * @Version 1.0
 */
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient highLevelClient;
    @Autowired
    private UserSearchRecordService recordService;

    @Override
    public ResponseResult searchArticle(UserSearchDto userSearchDto) {

        List<Map> list = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("app_info_article");

        //构造查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构造查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isEmpty(userSearchDto.getSearchWords())) {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }else{
            //保存用户搜索记录
            if(UserIdThreadLocalUtil.getUserId() != null && UserIdThreadLocalUtil.getUserId() != 0) {
                recordService.saveRecord(userSearchDto.getSearchWords(), UserIdThreadLocalUtil.getUserId());
            }
            boolQueryBuilder.must(QueryBuilders.queryStringQuery(userSearchDto.getSearchWords()).field("title"));
        }
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lt(userSearchDto.getMinBehotTime().getTime()));

        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页数据
        searchSourceBuilder.from(userSearchDto.getFromIndex());
        searchSourceBuilder.size(userSearchDto.getPageSize());

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color:red;font-size:20px'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = null;
        try {
            response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseResult.okResult(list);
        }
        SearchHit[] hits = response.getHits().getHits();

        Map<String,String> map = new HashMap<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            map = JSON.parseObject(sourceAsString, Map.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            if(highlightField != null){
                map.put("h_title",StringUtils.join(highlightField.getFragments()));
            }else{
                map.put("h_title",map.get("title"));
            }
            list.add(map);
        }
        return ResponseResult.okResult(list);
    }
}
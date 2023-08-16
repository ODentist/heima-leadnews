package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.pojo.ApAssociateWords;
import com.heima.search.pojo.ApUserSearch;
import com.heima.search.service.UserSearchRecordService;
import com.heima.utils.common.UserIdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/6/14 10:46
 * @Version 1.0
 */
@Service
@Slf4j
public class UserSearchRecordServiceImpl implements UserSearchRecordService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Async
    @Override
    public void saveRecord(String key, Integer userId) {
        /**
         * 查询是否存在此搜索记录
         */
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(key));
        ApUserSearch existSearch = mongoTemplate.findOne(query, ApUserSearch.class);
        if(existSearch != null){
            log.info("搜索记录里面存在此关键词:{}，更新时间即可",key);
            existSearch.setCreatedTime(new Date());
            mongoTemplate.save(existSearch);
            return;
        }
        //构造对象
        existSearch = new ApUserSearch();
        existSearch.setUserId(userId);
        existSearch.setKeyword(key);
        existSearch.setCreatedTime(new Date());

        Query queryAll = Query.query(Criteria.where("userId").is(userId))
                .with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<ApUserSearch> apUserSearches = mongoTemplate.find(queryAll, ApUserSearch.class);
        if(apUserSearches != null && apUserSearches.size() >= 10){
            ApUserSearch lastSearch = apUserSearches.get(apUserSearches.size() - 1);
            //mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastSearch.getId())),ApUserSearch.class);
            mongoTemplate.remove(Query.query(Criteria.where("id").is(lastSearch.getId())));
        }
        mongoTemplate.save(existSearch);
    }

    @Override
    public ResponseResult listRecord() {
        Query query = Query.query(Criteria.where("userId").is(UserIdThreadLocalUtil.getUserId()));
        query.with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<ApUserSearch> apUserSearches = mongoTemplate.find(query, ApUserSearch.class);
        return ResponseResult.okResult(apUserSearches);
    }

    @Override
    public ResponseResult associateWords(String word) {
        Query query = Query.query(Criteria.where("associateWords").regex("^.*" + word + ".*$")).limit(10);
        List<ApAssociateWords> apAssociateWords = mongoTemplate.find(query, ApAssociateWords.class);
        return ResponseResult.okResult(apAssociateWords);
    }

}
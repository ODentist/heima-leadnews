package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

public interface UserSearchRecordService {

    /**
     * 保存用户搜索记录
     * @param key
     * @param userId
     */
    void saveRecord(String key,Integer userId);


    ResponseResult listRecord();

    ResponseResult associateWords(String word);
}

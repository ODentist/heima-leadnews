package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApCollectionService;
import com.heima.common.cache.CacheService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.UserIdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/7 14:34
 * @Version 1.0
 */
@Service
@Slf4j
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        if(dto.getOperation() == null || dto.getEntryId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //判断是否登录
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if(userId == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        String redisKey = BehaviorConstants.COLLECTION_BEHAVIOR + dto.getEntryId();

        //收藏
        if(dto.getOperation() == 0){
            //查询
            String collectionJson = (String) cacheService.hGet(redisKey, dto.getEntryId().toString());
            if(org.apache.commons.lang3.StringUtils.isNotBlank(collectionJson) && dto.getOperation() == 0){
                log.info("用户{}已经收藏文章{}",userId,dto.getEntryId());
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
            cacheService.hPut(redisKey, userId.toString(), JSON.toJSONString(dto));
        }else {
            //取消收藏
            log.info("用户{}文章{}取消收藏",userId,dto.getEntryId());
            cacheService.hDelete(redisKey, userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
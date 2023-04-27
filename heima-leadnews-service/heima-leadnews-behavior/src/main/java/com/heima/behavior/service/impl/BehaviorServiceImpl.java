package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.BehaviorService;
import com.heima.common.cache.CacheService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.UserIdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.heima.common.constants.BehaviorConstants.LIKE_BEHAVIOR;
import static com.heima.common.constants.BehaviorConstants.UN_LIKE_BEHAVIOR;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/7 11:09
 * @Version 1.0
 */
@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private CacheService cacheService;

    /** 点赞 */
    public final static int OPERATION_LIKE = 0;

    @Override
    public ResponseResult like(LikesBehaviorDto dto) {
        if(dto.getArticleId() == null || dto.getOperation() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //获取当前用户并校验
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if(userId == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        String redisKey = LIKE_BEHAVIOR + dto.getArticleId();
        //点赞
        if(dto.getOperation() == OPERATION_LIKE){
            Object exist = cacheService.hGet(redisKey, userId.toString());
            if(exist != null){
                log.info("该用户:{}已经点赞过，直接返回成功",userId);
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
            cacheService.hPut(redisKey,userId.toString(), JSON.toJSONString(dto));
        }else{
            //取消点赞
            log.info("用户{}将文章{}取消点赞",userId,dto.getArticleId());
            cacheService.hDelete(redisKey,userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult unlike(UnLikesBehaviorDto dto) {
        if(dto.getType() == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //获取当前用户并校验
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if(userId == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        String redisKey = UN_LIKE_BEHAVIOR + dto.getArticleId();
        //不喜欢
        if(dto.getType() == 0){
            Object exist = cacheService.hGet(redisKey, userId.toString());
            if(exist != null){
                log.info("该用户:{}已经点过不喜欢，直接返回成功",userId);
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
            cacheService.hPut(redisKey,userId.toString(), JSON.toJSONString(dto));
        }else{
            //取消点赞
            log.info("用户{}将文章{}取消不喜欢",userId,dto.getArticleId());
            cacheService.hDelete(redisKey,userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult read(ReadBehaviorDto dto) {
        if(dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //获取当前用户并校验
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if(userId == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        String key = BehaviorConstants.READ_BEHAVIOR + dto.getArticleId();
        cacheService.zIncrementScore(key,userId.toString(),dto.getCount());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
package com.heima.behavior.service;

import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface BehaviorService {

    /**
     * 点赞操作（包含点赞和取消点赞）
     *
     * @param dto
     */
    ResponseResult like(LikesBehaviorDto dto);

    /**
     * 不喜欢和取消不喜欢
     *
     * @param dto
     * @return
     */
    ResponseResult unlike(UnLikesBehaviorDto dto);

    /**
     *
     * 阅读次数
     * @param dto
     * @return
     */
    ResponseResult read(ReadBehaviorDto dto);


}

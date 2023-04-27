package com.heima.behavior.controller;

import com.heima.behavior.service.BehaviorService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/2/7 11:01
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult like(@RequestBody LikesBehaviorDto behaviorDto){

        return behaviorService.like(behaviorDto);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult unlike(@RequestBody UnLikesBehaviorDto behaviorDto){
        return behaviorService.unlike(behaviorDto);
    }

    @PostMapping("/read_behavior")
    public ResponseResult read(@RequestBody ReadBehaviorDto behaviorDto){
        return behaviorService.read(behaviorDto);
    }
}
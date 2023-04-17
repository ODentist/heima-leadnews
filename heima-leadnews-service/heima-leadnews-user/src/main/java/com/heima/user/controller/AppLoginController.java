package com.heima.user.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/17 11:10
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/login")
@Api(tags = "登录相关接口")
public class AppLoginController {

    @Autowired
    private ApUserService userService;


    @PostMapping("/login_auth")
    @ApiOperation(value = "登录接口")
    public ResponseResult login(@RequestBody LoginDto dto){
        return userService.login(dto);
    }

}
package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/17 11:15
 * @Version 1.0
 */
public interface ApUserService{

    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    ResponseResult login(LoginDto loginDto);
}
package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/17 11:17
 * @Version 1.0
 */
@Service
@Slf4j
public class ApUserServiceImpl implements ApUserService {

    @Autowired
    private ApUserMapper userMapper;

    @Override
    public ResponseResult login(LoginDto loginDto) {
        log.info("登录请求：{}",loginDto);
        Map<String,Object> result = new HashMap<>();
        //登录
        if(!StringUtils.isEmpty(loginDto.getPhone()) && !StringUtils.isEmpty(loginDto.getPassword())){
            LambdaQueryWrapper<ApUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApUser::getPhone,loginDto.getPhone());
            //获取用户
            ApUser apUser = userMapper.selectOne(queryWrapper);
            if(apUser == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_OR_PASSWORD_ERROR);
            }
            //输入密码加密
            String salt = apUser.getSalt();
            String inputPassword = loginDto.getPassword();
            String inputPasswordMd5 = DigestUtils.md5DigestAsHex((inputPassword + salt).getBytes());
            log.info("登录密码加盐后MD5数据：{}",inputPasswordMd5);
            //对比密码
            if(!inputPasswordMd5.equals(apUser.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_OR_PASSWORD_ERROR);
            }
            //封装返回数据

            apUser.setPassword("");
            apUser.setSalt("");
            result.put("user",apUser);
            result.put("token",AppJwtUtil.getToken(apUser.getId().longValue()));

        }else {
            //游客登录
            //封装返回数据
            result.put("token",AppJwtUtil.getToken(0L));
        }

        return ResponseResult.okResult(result);
    }
}
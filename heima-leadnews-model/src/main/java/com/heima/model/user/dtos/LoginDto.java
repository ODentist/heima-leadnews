package com.heima.model.user.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/17 11:13
 * @Version 1.0
 */
@Data
public class LoginDto {

    /** 手机号 */
    @ApiModelProperty(value = "电话号码",required = true)
    private String phone;
    /**  密码 */
    @ApiModelProperty(value = "密码",required = true)
    private String password;

}
package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WmUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 9:23
 * @Version 1.0
 */
@Configuration
public class WmMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(new WmUserInterceptor()).addPathPatterns("/**");
    }
}
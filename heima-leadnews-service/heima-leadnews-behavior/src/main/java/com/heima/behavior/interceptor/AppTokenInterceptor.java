package com.heima.behavior.interceptor;

import com.heima.utils.common.UserIdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AppTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        log.info("获取到用户ID：{}",userId);
        if(userId != null){
            //存入到当前线程中
            UserIdThreadLocalUtil.setUserId(Integer.parseInt(userId));

        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserIdThreadLocalUtil.remove();
    }
}

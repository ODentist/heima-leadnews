package com.heima.wemedia.interceptor;

import com.heima.utils.common.UserIdThreadLocalUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 9:17
 * @Version 1.0
 */
public class WmUserInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header里面的userId
        String userId = request.getHeader("userId");
        if(!StringUtils.isEmpty(userId)){
            UserIdThreadLocalUtil.setUserId(Integer.parseInt(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserIdThreadLocalUtil.remove();
    }
}
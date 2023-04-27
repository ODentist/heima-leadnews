package com.heima.gateway.app.filter;

import com.heima.gateway.app.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/17 15:02
 * @Version 1.0
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //判断URL是不是登录,拿到请求路径 /user/api/v1/login/login_auth
        String path = request.getURI().getPath();
        if(path.contains("login")){
            log.info("登录请求，放行");
            return chain.filter(exchange);
        }
        //获取token
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //校验token
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int flag = AppJwtUtil.verifyToken(claimsBody);
        if(flag == AppJwtUtil.TIME_OUT_1 || flag == AppJwtUtil.TIME_OUT_2){
            log.info("token过期");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Object userId = claimsBody.get("id");

        ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
            httpHeaders.add("userId",userId + "");
        }).build();

        //重置header
        exchange.mutate().request(serverHttpRequest).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
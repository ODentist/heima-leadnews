package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/21 14:45
 * @Version 1.0
 */
@Component
@Slf4j
public class IArticleClientFallback implements IArticleClient {

    @Override
    public ResponseResult save(ArticleDto articleDto) {
        log.info("服务降级");
        return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_CITED);
    }
}
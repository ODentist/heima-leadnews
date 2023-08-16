package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: ODENTIST
 * @Date: 2023/06/20/19:47
 * @Description:
 */
@FeignClient("leadnews-wemedia")

public interface IWemediaClient {
    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels();
}

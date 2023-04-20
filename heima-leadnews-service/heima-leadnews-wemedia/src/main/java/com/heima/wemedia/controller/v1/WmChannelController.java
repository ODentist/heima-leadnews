package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 10:35
 * @Version 1.0
 */
@RestController
public class WmChannelController {

    @Autowired
    private WmChannelService channelService;

    @GetMapping("/api/v1/channel/channels")
    public ResponseResult listChannel(){

        return channelService.channelList();
    }
}
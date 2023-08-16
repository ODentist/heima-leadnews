package com.heima.wemedia.feign;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: ODENTIST
 * @Date: 2023/06/20/19:55
 * @Description:
 */
@RestController
@RequestMapping
public class Wemediaclient {
    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels(){
        return wmChannelService.channelList();
    }
}

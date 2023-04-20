package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMetialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 9:37
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/material")
public class WmMatialController {

    @Autowired
    private WmMetialService wmMetialService;

    @PostMapping("/upload_picture")
    public ResponseResult upload(MultipartFile multipartFile){

        return wmMetialService.upload(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmMaterialDto wmMaterialDto){

        return wmMetialService.metialList(wmMaterialDto);
    }
}
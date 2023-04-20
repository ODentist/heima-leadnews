package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.common.UserIdThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMetialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 9:39
 * @Version 1.0
 */
@Service
@Slf4j
public class WmMetialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMetialService {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ResponseResult upload(MultipartFile multipartFile) {
        //上传文件  a.jpg
        String originalFilename = multipartFile.getOriginalFilename();
        //重置文件名，防止被覆盖
        String fileName = UUID.randomUUID().toString();   //ddasf-2322da-ar2-232
        //处理后缀   获取到：.jpg
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String path = "";
        try {
            path = fileStorageService.uploadImgFile("", fileName +postfix, multipartFile.getInputStream());
        }catch (Exception e){
            log.error("文件上传异常",e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //保存到数据库
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUserId(UserIdThreadLocalUtil.getUserId());
        wmMaterial.setUrl(path);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short)0);
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult metialList(WmMaterialDto wmMaterialDto) {
        //检查参数
        wmMaterialDto.checkParam();
        //分页查询
        IPage<WmMaterial> page = new Page<>(wmMaterialDto.getPage(),wmMaterialDto.getSize());
        //定义查询条件
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
        if(wmMaterialDto.getIsCollection() != null) {
            queryWrapper.eq(WmMaterial::getIsCollection, wmMaterialDto.getIsCollection());
        }
        //用户
        queryWrapper.eq(WmMaterial::getUserId,UserIdThreadLocalUtil.getUserId());
        //按时间排序
        queryWrapper.orderByDesc(WmMaterial::getCreatedTime);

        this.page(page,queryWrapper);

        ResponseResult result = new PageResponseResult(wmMaterialDto.getPage(),wmMaterialDto.getSize(),(int)page.getTotal());
        result.setData(page.getRecords());

        return result;
    }
}
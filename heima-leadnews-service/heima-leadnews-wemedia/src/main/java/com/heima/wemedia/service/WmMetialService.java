package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMetialService extends IService<WmMaterial> {

    /**
     * 上传到minio,然后URL写入数据库
     *
     * @param multipartFile
     * @return
     */
    ResponseResult upload(MultipartFile multipartFile);

    /**
     * 素材列表分页查询
     * @param wmMaterialDto
     * @return
     */
    ResponseResult metialList(WmMaterialDto wmMaterialDto);
}

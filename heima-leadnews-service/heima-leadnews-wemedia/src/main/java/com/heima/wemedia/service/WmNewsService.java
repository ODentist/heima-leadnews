package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 分页查询文章列表
     *
     * @param reqDto
     * @return
     */
    ResponseResult queryNewsList(WmNewsPageReqDto reqDto);

    /**
     * 文章发布
     * @param newsDto
     * @return
     */
    ResponseResult submit(WmNewsDto newsDto);
}

package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

public interface ArticleSearchService {

    /**
     *
     * 搜索
     * @param userSearchDto
     * @return
     */
    ResponseResult searchArticle(UserSearchDto userSearchDto);
}

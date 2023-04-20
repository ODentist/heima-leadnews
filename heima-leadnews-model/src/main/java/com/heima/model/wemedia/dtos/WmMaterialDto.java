package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 10:02
 * @Version 1.0
 */
@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 1 查询收藏的
     */
    private Short isCollection;
}
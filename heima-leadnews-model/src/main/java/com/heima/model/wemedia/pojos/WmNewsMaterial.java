package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 自媒体图文引用素材信息表
 * </p>
 *
 * @author itheima
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wm_news_material")
@ApiModel(value="WmNewsMaterial", description="自媒体图文引用素材信息表")
public class WmNewsMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "素材ID")
    @TableField("material_id")
    private Integer materialId;

    @ApiModelProperty(value = "图文ID")
    @TableField("news_id")
    private Integer newsId;

    @ApiModelProperty(value = "引用类型	            0 内容引用	            1 主图引用")
    @TableField("type")
    private Boolean type;

    @ApiModelProperty(value = "引用排序")
    @TableField("ord")
    private Boolean ord;


}

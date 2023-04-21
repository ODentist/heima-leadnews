package com.heima.model.article.pojos;

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
 * APP已发布文章配置表
 * </p>
 *
 * @author itheima
 */
@Data
@TableName("ap_article_config")
@ApiModel(value="ApArticleConfig", description="APP已发布文章配置表")
public class ApArticleConfig implements Serializable {

    public ApArticleConfig(){

    }

    public ApArticleConfig(Long articleId){
        this.articleId = articleId;
        this.isDown = (short)0;
        this.isDelete = (short)0;
        this.isComment = (short)0;
        this.isForward = (short)0;
    }

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @ApiModelProperty(value = "是否可评论")
    @TableField("is_comment")
    private Short isComment;

    @ApiModelProperty(value = "是否转发")
    @TableField("is_forward")
    private Short isForward;

    @ApiModelProperty(value = "是否下架")
    @TableField("is_down")
    private Short isDown;

    @ApiModelProperty(value = "是否已删除")
    @TableField("is_delete")
    private Short isDelete;


}

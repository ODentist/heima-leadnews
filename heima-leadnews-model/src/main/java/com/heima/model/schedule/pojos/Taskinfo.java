package com.heima.model.schedule.pojos;

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
import java.sql.Blob;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author itheima
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("taskinfo")
@ApiModel(value="Taskinfo", description="")
public class Taskinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    @TableId(value = "task_id", type = IdType.ID_WORKER)
    private Long taskId;

    @ApiModelProperty(value = "执行时间")
    @TableField("execute_time")
    private Date executeTime;

    @ApiModelProperty(value = "参数")
    @TableField("parameters")
    private byte[] parameters;

    @ApiModelProperty(value = "优先级")
    @TableField("priority")
    private Integer priority;

    @ApiModelProperty(value = "任务类型")
    @TableField("task_type")
    private Integer taskType;


}

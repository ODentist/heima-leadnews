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
@TableName("taskinfo_logs")
@ApiModel(value="TaskinfoLogs", description="")
public class TaskinfoLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    @TableId(value = "task_id")
    private Long taskId;

    @ApiModelProperty(value = "执行时间")
    @TableField("execute_time")
    private Date executeTime;

    @ApiModelProperty(value = "参数")
    @TableField("parameters")
    private Blob parameters;

    @ApiModelProperty(value = "优先级")
    @TableField("priority")
    private Integer priority;

    @ApiModelProperty(value = "任务类型")
    @TableField("task_type")
    private Integer taskType;

    @ApiModelProperty(value = "版本号,用乐观锁")
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "状态 0=初始化状态 1=EXECUTED 2=CANCELLED")
    @TableField("status")
    private Integer status;


}

package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

public interface ITaskServcie {

    /**
     * 添加任务
     * 逻辑：xxx
     *
     * @param task
     */
    Task addTask(Task task);

    /**
     * 取消任务
     * @param taskId
     */
    Task cancelTask(Long taskId);

    /**
     * 拉取任务
     */
    Task poll(Integer taskType,Integer priority);
}

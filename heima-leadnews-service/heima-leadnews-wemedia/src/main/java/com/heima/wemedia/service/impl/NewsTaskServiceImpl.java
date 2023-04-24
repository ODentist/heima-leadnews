package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.NewsTaskService;
import com.heima.wemedia.service.WmAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/24 15:18
 * @Version 1.0
 */
@Service
@Slf4j
public class NewsTaskServiceImpl implements NewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private WmAutoScanService wmAutoScanService;
    @Autowired
    private WmNewsMapper newsMapper;

    @Override
    public void addTask(WmNews wmNews) {
        Task task = new Task();
        task.setTaskType(20);
        task.setPriority(1);
        WmNews param = new WmNews();
        param.setId(wmNews.getId());
        task.setParameters(ProtostuffUtil.serialize(param));

        ResponseResult result = scheduleClient.addTask(task);
        log.info("添加任务返回：" + JSON.toJSONString(result));

    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void pollTask(){
        ResponseResult result = scheduleClient.poll(20, 1);
        log.info("拉取任务返回：" + result);
        if(result.getCode() != AppHttpCodeEnum.SUCCESS.getCode() || result.getData() == null){
            return;
        }
        String jsonString = JSON.toJSONString(result.getData());
        Task task = JSON.parseObject(jsonString, Task.class);

        WmNews taskNews = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
        //一定要重新查询一次，因为我们添加到任务里面只有id
        WmNews wmNews = newsMapper.selectById(taskNews.getId());
        wmAutoScanService.autoScan(wmNews);
    }
}
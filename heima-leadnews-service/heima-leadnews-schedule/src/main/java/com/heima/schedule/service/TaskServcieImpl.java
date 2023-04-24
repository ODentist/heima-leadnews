package com.heima.schedule.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.cache.CacheService;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.constants.WeMediaConstants;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/24 10:55
 * @Version 1.0
 */
@Slf4j
@Service
public class TaskServcieImpl implements ITaskServcie{

    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional
    public Task addTask(Task task) {
        //添加到数据库
        addToDb(task);
        //添加到缓存
        addToCache(task);
        return task;
    }

    @Override
    public Task cancelTask(Long taskId) {
        //更新数据库
        Task task = updateDb(taskId,ScheduleConstants.STATUS_CANCEL);
        //删除缓存
        String key = task.getTaskType() + "_" + task.getPriority();
        if(task.getExecuteTime() <= System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.KEY_CURRENT + key,0,JSON.toJSONString(task));
        }else{
            cacheService.zRemove(ScheduleConstants.KEY_FUTURE+key,JSON.toJSONString(task));
        }
        return task;
    }

    @Override
    public Task poll(Integer taskType, Integer priority) {
        //从list获取任务
        String key = taskType + "_" + priority;
        String taskJson = cacheService.lRightPop(ScheduleConstants.KEY_CURRENT + key);
        if(StringUtils.isEmpty(taskJson)){
            return null;
        }
        Task task = JSON.parseObject(taskJson,Task.class);
        updateDb(task.getTaskId(), ScheduleConstants.STATUS_EXEC);
        return task;
    }

    private Task updateDb(Long taskId,Integer status){

        Task task = new Task();
        //删除taskInfo表的数据
        taskinfoMapper.deleteById(taskId);
        //更新Log的状态
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);

        BeanUtils.copyProperties(taskinfoLogs,task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());

        return task;
    }

    /**
     *
     * 添加任务到缓存
     * @param task
     */
    private void addToCache(Task task){
        String postfix = task.getTaskType() + "_" + task.getPriority();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);

        //小于等于当前时间，放list
        if(task.getExecuteTime() <= System.currentTimeMillis()){
            cacheService.lLeftPush(ScheduleConstants.KEY_CURRENT + postfix, JSON.toJSONString(task));
        }else if(task.getExecuteTime() <= calendar.getTime().getTime()){
            cacheService.zAdd(ScheduleConstants.KEY_FUTURE + postfix,JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    public void addToDb(Task task){
        Taskinfo taskinfo = new Taskinfo();
        BeanUtils.copyProperties(task,taskinfo);
        taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
        taskinfoMapper.insert(taskinfo);
        //设置taskId
        task.setTaskId(taskinfo.getTaskId());

        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(taskinfo,taskinfoLogs);
        taskinfoLogs.setVersion(1);
        taskinfoLogs.setStatus(ScheduleConstants.STATUS_INIT);
        taskinfoLogsMapper.insert(taskinfoLogs);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshToList(){
        log.info("开始执行定时刷新任务，从zset到list");
        String lockKey = "TASK_REFRESH";
        String token = cacheService.tryLock(lockKey, 30);
        if(StringUtils.isEmpty(token)){
            log.info("未获取到锁，不再执行");
            return;
        }
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.KEY_FUTURE + "*");
        if(CollectionUtils.isEmpty(futureKeys)){
            log.info("没有要即将执行的任务");
            return;
        }
        for (String futureKey : futureKeys) {
            //futureKey   FUTURE_20_1
            String currentKey = ScheduleConstants.KEY_CURRENT + futureKey.split(ScheduleConstants.KEY_FUTURE)[1];
            Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有当前时间需要执行的任务");
                continue;
            }
            cacheService.refreshWithPipeline(futureKey,currentKey,tasks);
            log.info("从future:{} 到  list:{},数据条数：{}",futureKey,currentKey,tasks.size());
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void pushDbToZset(){
        log.info("将数据库中数据刷入到zset");
        String lockKey = "TASK_DB_TO_ZSET";
        String token = cacheService.tryLock(lockKey, 30);
        if(StringUtils.isEmpty(token)){
            log.info("未获取到锁，不再执行");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);

        List<Taskinfo> taskinfos = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if(CollectionUtils.isEmpty(taskinfos)){
            log.info("没有要执行的任务");
            return;
        }
        //清除缓存
        Set<String> futures = cacheService.scan(ScheduleConstants.KEY_FUTURE + "*");
        Set<String> currents = cacheService.scan(ScheduleConstants.KEY_CURRENT + "*");
        cacheService.delete(futures);
        cacheService.delete(currents);

        for (Taskinfo taskinfo : taskinfos) {
            String key = ScheduleConstants.KEY_FUTURE + taskinfo.getTaskType() + "_" + taskinfo.getPriority();
            Task task = new Task();
            BeanUtils.copyProperties(taskinfo,task);
            task.setExecuteTime(taskinfo.getExecuteTime().getTime());
            cacheService.zAdd(key,JSON.toJSONString(task),taskinfo.getExecuteTime().getTime());
        }
    }
}
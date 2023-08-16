package com.heima.freemarker.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/6/20 14:54
 * @Version 1.0
 */
@Component
public class TestTask {


//    @Scheduled(cron = " 0 0/5 * * * ?")
    @XxlJob("testTask")
    public void execute(){
        System.out.println("开始执行任务");
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        System.out.println("当前分片：" + shardIndex + ",总片数：" + shardTotal);

        //从数据库查的时候就只查自己跑的那部分
        fromDb(shardIndex,shardTotal);
        select * from user where MOD(userid,total) == index

        select * from user where MOD(userid,total) == index

        List<Integer> userIds = new ArrayList<>();
        for(int i=1;i<11;i++){
            userIds.add(i);
        }
        for (Integer userId : userIds) {
            if(userId % shardTotal == shardIndex) {
                System.out.println("当前用户：" + userId);
            }
        }
    }
}
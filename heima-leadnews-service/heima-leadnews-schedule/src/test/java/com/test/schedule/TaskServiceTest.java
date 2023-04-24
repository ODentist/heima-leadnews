package com.test.schedule;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.ITaskServcie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/24 11:09
 * @Version 1.0
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @Autowired
    ITaskServcie taskServcie;

    @Test
    public void testAdd(){
        Task task = new Task();
        task.setTaskType(10);
        task.setPriority(1);
        task.setExecuteTime(new Date().getTime() + 40000);
        taskServcie.addTask(task);
    }

    @Test
    public void testCancel(){
        taskServcie.cancelTask(1650344290971742210L);
    }

    @Test
    public void poll(){
        taskServcie.poll(10,1);
    }

}
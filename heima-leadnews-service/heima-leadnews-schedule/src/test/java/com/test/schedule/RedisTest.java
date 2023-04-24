package com.test.schedule;

import com.heima.common.cache.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/24 10:45
 * @Version 1.0
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void testList(){
//        cacheService.lLeftPush("list-key","001");
//        cacheService.lLeftPush("list-key","002");
        String s = cacheService.lRightPop("list-key");
        System.out.println(s);
    }

    @Test
    public void testZset(){
        cacheService.zAdd("zsetKey","user001",10);
        cacheService.zAdd("zsetKey","user002",50);
        cacheService.zAdd("zsetKey","user003",60);
        cacheService.zAdd("zsetKey","user004",100);

        Set<String> zsetKey = cacheService.zRangeByScore("zsetKey", 10, 50);
        System.out.println(zsetKey);
    }
}
package com.heima.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.text.FieldPosition;
import java.util.*;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 10:54
 * @Version 1.0
 */
@SpringBootTest(classes = FreemarkerApp.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    private Configuration configuration;


    @Test
    public void testList()throws Exception{
        //获取模板对象
        Template template = configuration.getTemplate("list.ftl");
        //准备数据
        Map<String,Object> map = new HashMap<>();

        Student student = new Student();
        student.setName("lisi");
        student.setAge(22);
        student.setMoney(22f);

        Student student2 = new Student();
        student2.setName("zaadfda");
        student2.setAge(23);
        student2.setMoney(22f);

        List<Student> list = new ArrayList<>();
        list.add(student);
        list.add(student2);

        map.put("stuList",list);


        //定义map
        Map<String,Object> map1 = new HashMap<>();
        map1.put("stu1",student);
        map1.put("stu2",student2);

        map.put("stuMap",map1);

        //生成结果
        template.process(map,new FileWriter("D://list.html"));
    }

    @Test
    public void testF()throws Exception{
        //获取模板对象
        Template template = configuration.getTemplate("freemarker.ftl");
        //准备数据
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");

        map.put("billDate",new Date());

        map.put("orderNo",202323424242424242L);

        Student student = new Student();
        student.setName("lisi");
        student.setAge(22);
        student.setMoney(22f);

        map.put("stu",student);

        //生成结果
        template.process(map,new FileWriter("D://index.html"));
    }
}
package com.heima.utils.common;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 9:19
 * @Version 1.0
 */
public class UserIdThreadLocalUtil {

    private final static ThreadLocal<Integer> USER_LOCAL = new ThreadLocal<>();

    public static void setUserId(Integer userId){
        USER_LOCAL.set(userId);
    }

    public static Integer getUserId(){
        return USER_LOCAL.get();
    }

    public static void remove(){
        USER_LOCAL.remove();
    }

}
package com.example.demo.cache.cacheException.entity;

/**
 * 自定义缓存异常对象
 * 针对配置文件中的CacheExceptions属性
 * @author gubaofeng
 * @Date 2017/8/14
 * @Time 16:26
 */
public class CustomException {

    /**
     * 异常名
     */
    private String name;

    /**
     * 类型
     * 0-系统级异常 1-业务异常
     */
    private int type;

    /**
     * 优先级
     * 1-最高
     */
    private int priority;

    /**
     * 处理方式
     */
    private int dealType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }
}

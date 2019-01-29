package com.example.demo.service;

public interface SequenceService {

    /**
     * redis生成唯一序列（时间戳yyyyMMddHHmm+自增数）
     * @param key 传入参数：数据库中的表名
     * @return 唯一序列
     */
    String getSequence(String key);
}

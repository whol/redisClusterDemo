package com.example.demo.service;

import com.example.demo.entity.FantasyUser;

public interface FantasyUserService {

    /**
     * 检查用户是否存在，可检测用户名、昵称、手机号、邮箱地址
     * 返回值初始化为-1，存在返回1，不存在返回0，异常返回-1
     * @param fantasyUser
     * @return
     */
    Integer checkFantasyUser(FantasyUser fantasyUser);

}

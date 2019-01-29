package com.example.demo.service.impl;

import com.example.demo.dao.FantasyPermissionDao;
import com.example.demo.dao.FantasyRoleDao;
import com.example.demo.dao.FantasyUserDao;
import com.example.demo.entity.FantasyUser;
import com.example.demo.service.FantasyUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class FantasyUserServiceImpl implements FantasyUserService {
	@Resource
	private FantasyUserDao fantasyUserDao;

    @Override
	public Integer checkFantasyUser(FantasyUser fantasyUser) {
	    //初始化返回状态
		Integer flag = -1;
		try{
            flag = fantasyUserDao.checkFantasyUser(fantasyUser);
            log.debug("flag == {}", flag);
        } catch (Exception e) {
		    log.error(e.getMessage());
        }
		return flag;
	}


}

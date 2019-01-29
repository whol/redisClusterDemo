package com.example.demo.dao;


import com.example.demo.entity.FantasyUser;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;
import java.util.List;

@Mapper
public interface FantasyUserDao {
	/**
	 * 检查用户是否存在，可检测用户名、昵称、手机号、邮箱地址
	 * @param fantasyUser
	 * @return
	 */
	Integer checkFantasyUser(FantasyUser fantasyUser);
	/**
	 * 根据主键获取用户信息
	 * @param id
	 * @return
	 */
	FantasyUser getFantasyUserById(Integer id);


	/**
	 * 根据用户名，获取用户信息
	 * @param userName
	 * @return
	 */
	FantasyUser getFantasyUserByUserName(String userName);
	/**
	 * 根据查询条件，获取用户列表
	 * @param fantasyUser
	 * @return
	 */
	List<FantasyUser> getFantasyUserList(FantasyUser fantasyUser);

	/**
	 * 用户注册
	 * @param fantasyUser
	 * @return
	 */
	int insert(FantasyUser fantasyUser);

	/**
	 * 根据输入内容，更新用户信息
	 * @param fantasyUser
	 * @return
	 */
	int updateFantasyUserById(FantasyUser fantasyUser);

	/*//检查用户名是否存在
	int checkUser(String username);
	//检查昵称是否存在
	int checkNickname(String nickname);
	//检查邮箱是否可用
	int checkEmail(String email);*/

	//登陆验证
	//FantasyUser login(FantasyUser user);



}

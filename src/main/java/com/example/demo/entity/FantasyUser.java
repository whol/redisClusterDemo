package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户基本信息
 */

@Data
@Repository
public class FantasyUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3623214431091434178L;


	/**
	 * 账户状态
	 */
	//正常
	public static final String STATUS_VALID = "1";
	//未验证
	public static final String STATUS_UNVERIFIED = "-1";
	//锁定
	public static final String STATUS_LOCK = "0";

	private Integer id;
	//用户名
	private String username;
	//用户手机号
	private String mobile;
	//登陆密码
	@JsonIgnore
	private String password;
	//用户昵称
	private String nickname;
	//用户邮箱
	private String email;
	//注册时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date regDate;
	//用户角色
	private Integer userRole;
	//用户状态
	private Integer status;
	//private int regFlag;
	//家所在节点，与fantasy_city_map关联
	private Integer homePoint;

    //扩展字段
	private String repassword;
	private String verifycode;
	//邮箱验证加密串
	private String authStr;
	//邮箱验证原因
	private String authFor;

	private List<FantasyRole> fantasyRole;
}

package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CityMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -928885058592626676L;
	private Integer id;
	private String placename;
	private String placedesc;
	private Integer north;
	private Integer northeast;
	private Integer east;
	private Integer southeast;
	private Integer south;
	private Integer southwest;
	private Integer west;
	private Integer northwest;
	private Integer cityid;//所属城市
	//下面是各个方向的手动输入新建地点的名称
	private String northInput;
	private String northwestInput;
	private String northeastInput;
	private String westInput;
	private String eastInput;
	private String southwestInput;
	private String southInput;
	private String southeastInput;
	
	private String cityname;

	
}

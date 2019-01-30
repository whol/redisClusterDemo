package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 * <p>File: IpUtil.java 创建时间:201605031012</p>
 * <p>Title:服务器操作系统判定</p>
 * <p>Description: 服务器操作系统判定</p>
 * <p>Copyright: Copyright (c) 2016 亚信科技（南京）</p>
 * <p>模块: 工具类</p>
 * @see  
 * @history 修订历史（历次修订内容、修订人、修订时间等）
 * @author 汤其奇
 * @version 1.0
 * @created 201605031012
 * @modify 
 */
@Slf4j
public final class InetAddressUtils {

	/** 私有构造函数 */
	private InetAddressUtils() {}

	/**
	 * 获取本机IP地址，并自动区分Windows还是Linux操作系统
	 * 
	 * @return String
	 */
	public static String getLocalInetAddress() {
		List<String> ips = getLocalInetAddresses();
		if(ips != null && !ips.isEmpty()){
			return ips.get(ips.size()-1);
		}
		else{
			return "";
		}
	}

	/**
	 * 获取当前ip列表
	 * @return
     */
	private static List<String> getLocalInetAddresses() {
		InetAddress addr;
		List<String> addrList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				// ----------特定情况，可以考虑用ni.getName判断
				// 遍历所有ip
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					addr = ips.nextElement();
					if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress() // 127.开头的都是lookback地址
							&& addr.getHostAddress().indexOf(":") == -1) {
						addrList.add(addr.getHostAddress());
					}
				}
			}
		}
		catch (Exception e) {
			log.error("get local ip error:", e);
		}
		return addrList;
	}

}

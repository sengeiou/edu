package com.ubtechinc.base;

import java.io.Serializable;

/**
 * alpha信息类
 * @author chenlin
 *
 */
public class AlphaInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4183844578178650622L;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 蓝牙名字
	 */
	private String blueToothName;
	/**
	 * mac 地址
	 */
	private String macAddr;
	/**
	 * 是否连接
	 */
	private boolean isConnect;
	
	
	
	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBlueToothName() {
		return blueToothName;
	}
	
	public void setBlueToothName(String blueToothName) {
		this.blueToothName = blueToothName;
	}
	
	public String getMacAddr() {
		return macAddr;
	}
	
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	
}

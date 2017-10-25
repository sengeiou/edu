package com.ubtechinc.base;

/**
 * 公共接口类
 * @author chenlin
 *
 */
public class PublicInterface {
	/**
	 * 与蓝牙通信交互的接口
	 * @author juntian
	 *
	 */
	public interface BlueToothInteracter{
		/**
		 * 接收到数据
		 * @param mac
		 * @param cmd
		 * @param param
		 * @param len TODO
		 */
		abstract void onReceiveData(String mac, byte cmd, byte[] param, int len);
		
		/**
		 * 发送数据
		 * @param datas
		 * @param nLen
		 */
		abstract void onSendData(String mac, byte[] datas, int nLen);
		
		/**
		 * 连接失败
		 * @param mac TODO
		 */
		abstract void onConnectState(boolean bsucceed, String mac);
		
		/**
		 * 断线
		 * @param mac
		 */
		abstract void onDeviceDisConnected(String mac);
	}
}

package com.ubtechinc.base;

import android.os.SystemClock;

import com.ubtechinc.log.MyLog;

import java.util.ArrayList;
import java.util.List;

class BUFFER_DATA {
	public byte[] datas;
	public int nLen;
	public long lastSendTime;
	public int resenTime;
}

/**
 * 设备处理的纯种
 * 
 * @author juntian
 * 
 */
public class DeviceProcessThread extends Thread {

	public interface DeviceProcessThreadCallBack {
		void onSendData(byte[] data, int len);
	}

	/**
	 * 与设备交互的接口
	 */
	// private BlueToothInteracter mBlueToothInteracter;
	private ProtocolPacket mPacketRead;
	private DeviceProcessThreadCallBack mCallBack;

	/**
	 * 重发队列
	 */
	private List<BUFFER_DATA> mListReSend;
	private boolean mRun = true;

	public DeviceProcessThread(DeviceProcessThreadCallBack callback) {
		mCallBack = callback;
		mPacketRead = new ProtocolPacket();
		mListReSend = new ArrayList<BUFFER_DATA>();
	}

	/**
	 * 发送数据
	 * 
	 * @param datas
	 * @param nLen
	 */
	public void sendData(byte[] datas, int nLen) {
		synchronized (this) {
			BUFFER_DATA data = new BUFFER_DATA();

			MyLog.writeLog("发送", ByteHexHelper.bytesToHexString(datas));

			data.datas = datas;
			data.nLen = nLen;
			data.lastSendTime = SystemClock.uptimeMillis();
			data.resenTime = 0;

			mListReSend.add(data);

			if (mCallBack != null) {
				mCallBack.onSendData(datas, nLen);

			}
		}
	}

	/**
	 * 发送数据，当前数据不放入到重发队列
	 * 
	 * @param datas
	 * @param nLen
	 */
	public void sendDataNotPutToList(byte[] datas, int nLen) {
		synchronized (this) {
			if (mCallBack != null) {
				mCallBack.onSendData(datas, nLen);
			}
		}
	}

	/**
	 * 清空重发数据
	 */
	public void clearDataBuffer() {
		synchronized (this) {
			mListReSend.clear();
			mPacketRead = null;
			mPacketRead = new ProtocolPacket();
		}
	}

	/**
	 * 释放资源
	 */
	public void releaseConnection() {
		synchronized (this) {
			mRun = false;

			mListReSend.clear();
		}
	}

	public void removeFromListByCmdID(byte cmdID) {
		synchronized (this) {
			ProtocolPacket packet = new ProtocolPacket();
			BUFFER_DATA remove = null;
			for (BUFFER_DATA buffer : mListReSend) {
				packet.setRawData(buffer.datas);

				if (packet.getmCmd() == cmdID) {
					remove = buffer;
					break;
				}
			}

			if (remove != null) {
				mListReSend.remove(remove);

			}
		}
	}

	// /**
	// * 读数据
	// * @param data
	// * @param nLen
	// */
	// public void readData(byte[] data, int nLen){
	// for(int i=0; i<nLen; i++){
	// if (mPacketRead.setData_(data[i])){
	// processPacket(mPacketRead);
	// }
	// }
	// }
	//
	// /**
	// * 发送握手协议
	// */
	// public void sendHandShake(){
	// synchronized(this){
	// ProtocolPacket packet = new ProtocolPacket();
	//
	// packet.setmCmd(ConstValue.DV_HANDSHAKE);
	// byte[] params = new byte[1];
	// params[0] = 0;
	// packet.setmParam(params);
	// packet.setmParamLen(1);
	//
	// byte[] rawDatas = packet.packetData();
	//
	// sendData(rawDatas, rawDatas.length);
	// }
	// }

	@Override
	public void run() {
		while (mRun) {
			synchronized (this) {
				BUFFER_DATA removeData = null;
				for (int i = 0; i < mListReSend.size(); i++) {
					BUFFER_DATA data = mListReSend.get(i);
					// 如果1500MS没有接收到回应
					if (SystemClock.uptimeMillis() - data.lastSendTime >= 1500) {
						// 如果超过重发次数，则不发。移除列表
						if (data.resenTime >= 2) {
							removeData = data;
							break;
						} else {
							// 重发数据
							sendDataNotPutToList(data.datas, data.nLen);
							// 更新重发的时间和次数
							data.lastSendTime = SystemClock.uptimeMillis();
							data.resenTime++;
						}
					}

				}

				if (removeData != null) {
					mListReSend.remove(removeData);
					removeData = null;
					continue;
				}

			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				mRun = false;
				break;
			}

		}
	}
}

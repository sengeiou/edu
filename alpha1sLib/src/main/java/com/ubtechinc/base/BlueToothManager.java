package com.ubtechinc.base;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import com.ubtechinc.base.BlueToothClientHandler.ClentCallBack;
import com.ubtechinc.base.BluetoothUtil.BluetoothUtilCallBack;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.ubtechinc.log.MyLog;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BlueToothManager extends Thread implements ClentCallBack,
		BluetoothUtilCallBack {

	private boolean mServices = false;// 是否做为服务器
	private List<BlueToothClientHandler> mListClient;
	private boolean mRun = true;
	private List<BlueToothInteracter> mBlueToothInteractions;
	private BluetoothUtil mBluetoothUtil;
	private long mLastSendTime;
	private boolean isMonopoly = false;// 是否处于独占模式

	public BluetoothUtil getmBluetoothUtil() {
		return mBluetoothUtil;
	}

	public BlueToothManager(Context context) {
		mBlueToothInteractions = new ArrayList<BlueToothInteracter>();
		mListClient = new ArrayList<BlueToothClientHandler>();
		mBluetoothUtil = new BluetoothUtil(context, this);
		mLastSendTime = SystemClock.uptimeMillis();

		initAppVervion(context);
	}

	/**
	 * 初始化版本，在写日志的时候用到
	 * @param mContext
     */
	private void initAppVervion(Context mContext){
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_CONFIGURATIONS);
			MyLog.setAppVersion(pi.versionName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	synchronized public void addBlueToothInteraction(BlueToothInteracter s) {
		if (!mBlueToothInteractions.contains(s))
			mBlueToothInteractions.add(s);
	}

	synchronized public void removeBlueToothInteraction(BlueToothInteracter s) {
		if (mBlueToothInteractions.contains(s))
			mBlueToothInteractions.remove(s);
	}

	// 开始处理
	public void startProcess() {
		mRun = true;
		mBluetoothUtil.start(mServices);
		this.start();
	}

	public void connectDevice(BluetoothDevice device) {
		// 先停止当前的连接
		releaseAllConnected();

		mBluetoothUtil.connect(device);
	}

	// 查找客户端
	synchronized public BlueToothClientHandler findClient(String mac) {
		BlueToothClientHandler find = null;
		for (BlueToothClientHandler s : mListClient) {
			if (s.getmMacAddress().equals(mac)) {
				find = s;
				break;
			}
		}

		return find;
	}

	// 添加客户端
	synchronized public void addClientToList(String mac, BluetoothSocket socket) {
		if (findClient(mac) != null)
			return;

		BlueToothClientHandler client = new BlueToothClientHandler(mac, socket,
				this);
		mListClient.add(client);
		client.start();
	}

	// 释放所有连接
	synchronized public void releaseAllConnected() {
		Log.d("buletoothManager","mListClient size   :"+mListClient.size() );
		for (BlueToothClientHandler s : mListClient) {
			s.releaseConnection();
		}

		mListClient.clear();
	}

	// 释放资源
	synchronized public void releaseConnection() {
		mRun = false;
		for (BlueToothClientHandler s : mListClient) {
			s.releaseConnection();
		}

		mListClient.clear();
	}

	public void intoMonopoly() {
		isMonopoly = true;
	}

	public void outMonopoly() {
		isMonopoly = false;
	}

	synchronized public void sendCommand(String mac, byte cmd, byte[] param,
			int len, boolean isMonopolyRight) {
		if (isMonopoly && (!isMonopolyRight)){
			return;
		}

		ProtocolPacket packet = new ProtocolPacket();
		packet.setmCmd(cmd);
		packet.setmParam(param);
		packet.setmParamLen(len);

		byte[] rawDatas = packet.packetData();

		if (cmd == 0x11 || cmd == ConstValue.DV_READ_BLUETOOTH_VERSION || cmd == ConstValue.DV_BLUETOOTH_UPGRADE) {

			String x = ByteHexHelper.bytesToHexString(rawDatas);
			Log.i("BlueToothManager", "bytesToHexString-->"+x);
		}
		BlueToothClientHandler client = findClient(mac);

		if (client != null) {
			client.sendData(rawDatas, rawDatas.length);
		}
	}

	synchronized public void sendFile(String mac,String filePath,boolean isMonopolyRight) {

		if (isMonopoly && (!isMonopolyRight)){
			return;
		}

		BlueToothClientHandler client = findClient(mac);

		if (client != null) {
			client.sendFile(filePath);
		}
	}

	@Override
	public void run() {
		while (mRun) {
			synchronized (this) {
				BlueToothClientHandler remove = null;
				for (BlueToothClientHandler s : mListClient) {
					if (s.tryToReleaseConnection()) {
						remove = s;
						break;
					} else {
						ProtocolPacket packet = new ProtocolPacket();
						packet.setmCmd(ConstValue.DV_XT);
						packet.setmParam(null);
						packet.setmParamLen(0);

						byte[] rawDatas = packet.packetData();
						s.sendXT(rawDatas, rawDatas.length);
					}
				}

				if (remove != null) {
					String mac = remove.getmMacAddress();
					remove.releaseConnection();
					mListClient.remove(remove);

					if (mBlueToothInteractions != null) {

						for (int i = 0; i < mBlueToothInteractions.size(); i++) {
							mBlueToothInteractions.get(i).onDeviceDisConnected(mac);
						}

					}
					continue;
				}
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				mRun = false;
				break;
			}
		}
	}

	synchronized public void onReceiveData(String mac, byte cmd, byte[] param,
			int len) {
		if (mBlueToothInteractions != null) {

			for (int i = 0; i < mBlueToothInteractions.size(); i++) {
				mBlueToothInteractions.get(i).onReceiveData(mac, cmd, param,len);
				//Log.d("BlueToothManager", mBlueToothInteractions.get(i).toString());
			}

		}
	}

	public void onConnectStateChange(String mac, int state) {
		// TODO Auto-generated method stub

	}

	synchronized public void onDeviceConnected(String mac,
			BluetoothSocket socket) {
		addClientToList(mac, socket);
		if (mBlueToothInteractions != null) {

			for (int i = 0; i < mBlueToothInteractions.size(); i++) {
				mBlueToothInteractions.get(i).onConnectState(true, mac);
			}

		}
	}

	synchronized public void onConnectFailed() {
		if (mBlueToothInteractions != null) {

			for (int i = 0; i < mBlueToothInteractions.size(); i++) {
				mBlueToothInteractions.get(i).onConnectState(false, null);
			}

		}
	}
}

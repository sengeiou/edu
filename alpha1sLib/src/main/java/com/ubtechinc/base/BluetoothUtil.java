/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ubtechinc.base;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class BluetoothUtil {

	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME = "BluetoothChat";

	// Unique UUID for this application
	private static final UUID MY_UUID = UUID
			.fromString(/* "fa87c0d0-afac-11de-8a39-0800200c9a66" */"00001101-0000-1000-8000-00805F9B34FB");

	// Member fields
	private final BluetoothAdapter mAdapter;
	// private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private int mState;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	private Context mContext;
	private String mConnectMacAddr;
	private boolean mServiceMode = false;

	private final BluetoothUtilCallBack mCallBack;

	private BluetoothDevice mDevice;
	private BluetoothA2dp mBluetoothA2dp = null;

	public interface BluetoothUtilCallBack {
		/**
		 * 连接状态改变
		 * 
		 * @param state
		 */
		void onConnectStateChange(String mac, int state);

		/**
		 * 蓝牙已经连上
		 * 
		 * @param mac
		 * @param socket
		 */
		void onDeviceConnected(String mac, BluetoothSocket socket);

		/**
		 * 连接失败
		 */
		void onConnectFailed();
	}

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param callback
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothUtil(Context context, BluetoothUtilCallBack callback) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mContext = context.getApplicationContext();
		mCallBack = callback;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		if (state != STATE_CONNECTED)
			mConnectMacAddr = null;

		if (mCallBack != null) {
			mCallBack.onConnectStateChange(mConnectMacAddr, state);
		}
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start(boolean bServiceMode) {
		if (D)
			Log.d(TAG, "start");

		mServiceMode = bServiceMode;

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		if (mServiceMode) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}

		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device) {
		this.mDevice = device;
		new BluetoothThread().start();
		// try {
		// if (D)
		// Log.d(TAG, "connect to: " + device);
		//
		// // Cancel any thread attempting to make a connection
		// // if (mState == STATE_CONNECTING) {
		// // if (mConnectThread != null) {
		// // mConnectThread.cancel();
		// // mConnectThread = null;
		// // }
		// // }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// if (mConnectThread != null) {
		// Log.d("zdy", "mConnectThread.cancel() " + device);
		// mConnectThread.cancel();
		// mConnectThread = null;
		// Log.d("zzz", "mConnectThread.cancel() end");
		//
		// }
		//
		// // Start the thread to connect with the given device
		// Log.d("zzz", "mConnectThread. start");
		// mConnectThread = new ConnectThread(device);
		// mConnectThread.start();
		// setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		mConnectMacAddr = device.getAddress();
		setState(STATE_CONNECTED);

		if (mCallBack != null) {
			mCallBack.onDeviceConnected(device.getAddress(), socket);
		}
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);
		if (mCallBack != null) {
			mCallBack.onConnectFailed();
		}
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private boolean mRun = true;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mRun) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					connected(socket, socket.getRemoteDevice());

				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "cancel " + this);
			try {
				mRun = false;
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private boolean stop = false;
		ImprovedBluetoothDevice improvedBluetoothDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			// improvedBluetoothDevice = new ImprovedBluetoothDevice(device);

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (DeviceDependency.shouldUseFixChannel()) {
					Method m;
					try {
						m = device.getClass().getMethod(
								"createInsecureRfcommSocket",
								new Class[] { int.class });
						tmp = (BluetoothSocket) m.invoke(device, 6);
					} catch (SecurityException e1) {
						e1.printStackTrace();
					} catch (NoSuchMethodException e1) {
						e1.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					if (DeviceDependency.shouldUseSecure()) {
						tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
					} else {
						tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
						Log.v(TAG, "createInsecureRfcommSocketToServiceRecord");
					}
				}
				// tmp =
				// device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			int nTryTime = 0;
			while (!stop) {
				// Make a connection to the BluetoothSocket
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					Log.e(TAG, "socket.run()");
					mmSocket.connect();
					Log.i(TAG, "已经连接上服务端！可以发送信息。");

					//add A2DP connect by jason on 2016/08/29
					String bluetoothName = mmDevice.getName();
					Log.d(TAG,"connect bluetoothName:"+bluetoothName);
					if (bluetoothName.contains("Alpha1P+") || bluetoothName.contains("Alpha1S_")){//1P+ OR 1SP
						connectA2DP();
						Log.i(TAG, "已经连接上服务端A2DP！可以传送音频。");
					}else{
						// 1s/1p not support a2dp
					}
				} catch (Exception e) {

					// if (stop == true) {
					// Log.d("zdy", "mmSocket.stop == true " + stop);
					// return;
					// }
					// if (nTryTime == 0) {
					// Log.d("zdy", "mmSocket.connect() IOException"
					// + mmDevice);
					//
					// nTryTime++;
					// try {
					// mAdapter.cancelDiscovery();
					// mmSocket = mmDevice
					// .createRfcommSocketToServiceRecord(MY_UUID);
					// Log.e("zdy", "mmSocket " + mmSocket);
					// Log.v(TAG, "switch to secure mode to connect ");
					// continue;
					// } catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// }
					Log.e("zdy", "IOException", e);

					connectionFailed();
					// Close the socket
					try {
						if(mmSocket != null){
							mmSocket.close();
						}else {
							Log.e(TAG, "mmSocket = null");
						}
					} catch (Exception e2) {
						Log.e(TAG, "unable to close() socket during connection failure", e2);
					}
					// Start the service over to restart listening mode
					// BluetoothUtil.this.start(mServiceMode);
					return;
				}

				break;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothUtil.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				stop = true;
				Log.e("zdy", "socket.close()");
				if(mmSocket != null){
					mmSocket.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}

		}

		private void connectA2DP() {

			Log.d(TAG, "A2DP connect state:"+mAdapter.getProfileConnectionState(BluetoothProfile.A2DP));
			if(mAdapter.getProfileConnectionState(BluetoothProfile.A2DP) != BluetoothProfile.STATE_CONNECTED){
				//在listener中完成A2DP服务的调用
				mAdapter.getProfileProxy(mContext, new connServListener(), BluetoothProfile.A2DP);
			}
		}

		public class connServListener implements BluetoothProfile.ServiceListener {

			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				//use reflect method to get the Hide method "connect" in BluetoothA2DP
				mBluetoothA2dp = (BluetoothA2dp) proxy;
				//a2dp.isA2dpPlaying(mBTDevInThread);
				Class<? extends BluetoothA2dp> clazz = mBluetoothA2dp.getClass();
				Method method_Connect;
				//通过BluetoothA2DP隐藏的connect(BluetoothDevice btDev)函数，打开btDev的A2DP服务
				try {

						  /*
						   * 1.Reflect this method
						     public boolean connect(BluetoothDevice device);
						   *
						   * 2.function definition
						     getMethod(String methodName, Class <?>... paramType)
						   */
					//1.这步相当于定义函数
					method_Connect = clazz.getMethod("connect",BluetoothDevice.class);
					method_Connect.setAccessible(true);
					//invoke(object receiver,object... args)
					//2.这步相当于调用函数,invoke需要传入args：BluetoothDevice的实例
					method_Connect.invoke(mBluetoothA2dp, mmDevice);
					Log.d(TAG,"connect A2DP mac:"+mmDevice.getAddress());
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onServiceDisconnected(int profile) {
				// TODO Auto-generated method stub
				Log.d(TAG,"a2dp disconnect ");
				if(profile == BluetoothProfile.A2DP){
					mBluetoothA2dp = null;
				}

			}

		}
	}

	/**
	 * [保证蓝牙连续点击-- 连接--断开---再连接 ]
	 * 
	 * @author zengdengyi
	 * @version 1.0
	 * @date 2015-2-12 下午2:32:43
	 * 
	 **/

	class BluetoothThread extends Thread {

		@Override
		public void run() {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mConnectThread = new ConnectThread(mDevice);
			mConnectThread.start();
			setState(STATE_CONNECTING);

		}

	}
}

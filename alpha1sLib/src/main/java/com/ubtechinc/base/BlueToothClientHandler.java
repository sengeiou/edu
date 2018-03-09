package com.ubtechinc.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.ubtechinc.base.DeviceProcessThread.DeviceProcessThreadCallBack;
import com.ubtechinc.log.MyLog;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * 蓝牙客户端处理类
 * 
 * @author juntian018
 * 
 */
public class BlueToothClientHandler extends Thread implements
		DeviceProcessThreadCallBack {

	static private final String TAG = "BlueToothClientHandler";
	/**
	 * 客户端的MAC地址
	 */
	private final String mMacAddress;
	/**
	 * 连接的SOCKET
	 */
	private final BluetoothSocket mBlueToothSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private final ClentCallBack mCallBack;

	private boolean mRun = true;
	private ProtocolPacket mPacketRead;

	private long mLastRcvTime;
	private long mLastSendTime;

	private boolean mUpgrage = false;

	private DeviceProcessThread mProcessThread;

	public interface ClentCallBack {
		/**
		 * 客户端收到数据
		 * 
		 * @param mac
		 * @param cmd
		 * @param param
		 * @param len
		 */
		void onReceiveData(String mac, byte cmd, byte[] param, int len);

	}

	public String getmMacAddress() {
		return mMacAddress;
	}

	public void sendData(byte[] datas, int nLen) {
		mProcessThread.sendData(datas, nLen);
	}

	public BlueToothClientHandler(String mac, BluetoothSocket socket,
			ClentCallBack callback) {
		mUpgrage = false;
		mMacAddress = mac;
		mBlueToothSocket = socket;
		mCallBack = callback;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		mLastRcvTime = SystemClock.uptimeMillis();
		mLastSendTime = mLastRcvTime;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			Log.e(TAG, "temp sockets not created", e);
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;

		mPacketRead = new ProtocolPacket();
		mProcessThread = new DeviceProcessThread(this);
		mProcessThread.start();
	}

	public void sendXT(byte[] data, int nlen) {
		if (SystemClock.uptimeMillis() - mLastSendTime < 1000) {
			return;
		}
		MyLog.writeLog("发送",ByteHexHelper.bytesToHexString(data));
		onSendData(data, nlen);
	}

	/**
	 * 判断线程是否已经退出
	 * 
	 * @return
	 */
	public boolean tryToReleaseConnection() {
		boolean bTimeOut = SystemClock.uptimeMillis() - mLastRcvTime > 10000;
		if (mRun == false || this.isAlive() == false || bTimeOut){
			Log.e(TAG,"tryToReleaseConnection  mRun:"+mRun + "		isAlive():" + this.isAlive() + "	bTimeOut:" + bTimeOut);
			MyLog.writeLog(TAG,"tryToReleaseConnection  mRun:"+mRun + "		isAlive():" + this.isAlive() + "	bTimeOut:" + bTimeOut);
			return true;
		}
		return false;
	}

	/**
	 * 释放资源
	 */
	public void releaseConnection() {
		synchronized (this) {
			try {
				mRun = false;

				mProcessThread.releaseConnection();

				if (mmInStream != null)
					mmInStream.close();

				if (mmOutStream != null)
					mmOutStream.close();

				if (mBlueToothSocket != null)
					mBlueToothSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		while (mRun) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);////
				byte[] pTemp = new byte[bytes];
				System.arraycopy(buffer, 0, pTemp, 0, bytes);
				if(!ByteHexHelper.bytesToHexString(pTemp).startsWith("fb bf 09 18")
						&& !ByteHexHelper.bytesToHexString(pTemp).startsWith("fb bf 12 08")){
					Log.d(TAG,"receive : " + ByteHexHelper.bytesToHexString(pTemp) + "	bytes = " + bytes);
				}
				MyLog.writeLog("接收",ByteHexHelper.bytesToHexString(pTemp));

				for (int i = 0; i < bytes; i++) {
					if (mPacketRead.setData_(buffer[i])) {
						// 一帧数据接收完成
						if (mCallBack != null) {

							/*MyLog.writeLog("接收",ByteHexHelper.byteToHexString(mPacketRead.getmCmd()) + " " +
									ByteHexHelper.bytesToHexString(mPacketRead.getmParam()));*/

							mPacketRead.setmParamLen(mPacketRead.getmParam().length);
							mCallBack.onReceiveData(mMacAddress,
									mPacketRead.getmCmd(),
									mPacketRead.getmParam(),
									mPacketRead.getmParamLen());

							/*Log.e(TAG,
									"cmd=" + mPacketRead.getmCmd() + " param="
											+ mPacketRead.getmParam() + " len="
											+ mPacketRead.getmParamLen());
*/


						}

						mLastRcvTime = SystemClock.uptimeMillis();

						mProcessThread.removeFromListByCmdID(mPacketRead.getmCmd());
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "read data IOException, disconnected", e);
				MyLog.writeLog(TAG,"read data IOException, disconnected" + e.toString());
				// connectionLost();
				mRun = false;
				break;
			}  catch (ArrayIndexOutOfBoundsException e) {
				Log.e(TAG, "ArrayIndexOutOfBoundsException " + e.toString() );
				MyLog.writeLog(TAG,"ArrayIndexOutOfBoundsException" + e.toString());
				continue;
			}catch (Exception e) {
				Log.e(TAG, "read data Exception, disconnected", e);
				MyLog.writeLog(TAG,"read data Exception, disconnected" + e.toString());
				mRun = false;
				break;
			}
		}
	}

	synchronized public void onSendData(byte[] data, int len) {
		try {
			//is upgade bluetooth

			if(!ByteHexHelper.bytesToHexString(data).startsWith("fb bf 06 18")
					&& !ByteHexHelper.bytesToHexString(data).startsWith("fb bf 12 08")){
				Log.d(TAG,"send : "+ByteHexHelper.bytesToHexString(data));
			}

			if(mUpgrage){
				return;
			}

			if (len >= 100 && 1 == 0) {
				byte[] spli_datas = new byte[100];
				int times = 0;
				for (int i = 0; i < len; i++) {
					spli_datas[i - times * 100] = data[i];

					if ((i + 1) % 100 == 0 && i != 0) {
						times++;
						mmOutStream.write(spli_datas, 0, 100);
						mLastSendTime = SystemClock.uptimeMillis();
						Thread.sleep(30);
						int len_next = 100;
						if ((len - times * 100) < 100) {
							len_next = len - times * 100;
						}
						spli_datas = new byte[len_next];
					}
					if (i == len - 1 && (i + 1) % 100 != 0) {
						mmOutStream.write(spli_datas, 0, spli_datas.length);
						mLastSendTime = SystemClock.uptimeMillis();
					}
				}
				return;
			}
			mmOutStream.write(data, 0, len);
//			Thread.sleep(10);
//            Log.d(TAG,"ccy test :4 ");
			mLastSendTime = SystemClock.uptimeMillis();
		} catch (IOException e) {
			Log.e(TAG, "write data IOException, disconnected", e);
			MyLog.writeLog(TAG,"write data IOException, disconnected" + e.toString());
			releaseConnection();
		} catch (Exception e) {
			Log.e(TAG, "write data Exception, disconnected", e);
			MyLog.writeLog(TAG,"write data Exception, disconnected" + e.toString());
			releaseConnection();
		}
	}

	public void sendFile(String filePath){
		try {
			mUpgrage = true;
			byte[] spli_datas = new byte[1024];

			File file = new File(filePath);
			int size = (int)file.length();
			int sendSize = 0;

			FileInputStream fis = new FileInputStream(file);
			//BufferedInputStream bis = new BufferedInputStream(fis);
			//FileOutputStream mmOutStream = new FileOutputStream(file1);
			int byteread  = 0;
			while ((byteread = fis.read(spli_datas)) != -1) {
				mmOutStream.write(spli_datas,0,byteread);
				sendSize += byteread;
				Thread.sleep(30);

				if(sendSize == size){
					//String testStr = "123456789123456789123456789123456789123456789123456789123456789";
					/*Log.d(TAG ,"testStr::"+testStr.getBytes().length);
					mmOutStream.write(testStr.getBytes(),0,testStr.length());*/
					//mmOutStream.flush();
					Thread.sleep(3000);
				}
				mCallBack.onReceiveData(mMacAddress,
						ConstValue.DV_BLUETOOTH_UPGRADE_PERCENT,
						(sendSize+"").getBytes(),
						(sendSize+"").getBytes().length);
			}

			Thread.sleep(1000);
			fis.close();

			Log.d(TAG ,"send bluetooth end , file size:"+size+"		sendSize:"+sendSize);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			mUpgrage = false;
			Log.e(TAG ,"file not found:"+e);
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			mUpgrage = false;
			releaseConnection();
			Log.e(TAG,"IOException:"+e);
			e.printStackTrace();
		} catch (Exception ex){
			mUpgrage = false;
			releaseConnection();
			Log.e(TAG,"Exception:"+ex);
			ex.printStackTrace();
		}

	}

}

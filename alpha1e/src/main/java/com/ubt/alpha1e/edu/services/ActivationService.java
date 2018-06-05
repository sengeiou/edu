package com.ubt.alpha1e.edu.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.utils.log.MyLog;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 先读uid，如果读不到有效的uid则认为机器是没升级bin的旧机器直接跳过。 如果可以读到uid，读不到有效的sn，则分配sn并写入。
 * 如果可以读到uid，也可以读到sn，直接激活
 */

public class ActivationService extends Service implements BlueToothInteracter,
		IJsonListener {

	double mLongitude = -1;
	double mLatitude = -1;
	private String mCurrentSNCode = "";
	private String mCurrentUIDCode = "";
	private Timer mReadSNCodeTimer;
	private Timer mReadUIDCodeTimer;
	private long do_check_sn_request = 1001;
	private JSONArray mDevicesActivated;
	private LocationManagerProxy aMapManager;
	private Handler mHandler = new Handler();

	@Override
	public IBinder onBind(Intent arg0) {
		MyLog.writeLog("ActivationService", this.hashCode() + "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		MyLog.writeLog("ActivationService", this.hashCode() + "onCreate");
		aMapManager = LocationManagerProxy.getInstance(this);
		aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork,
				2000, 10, mAMapLocationListener);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		MyLog.writeLog("ActivationService", this.hashCode() + "onDestroy");

		if (((AlphaApplication) this.getApplicationContext())
				.getBlueToothManager() != null) {
			MyLog.writeLog("ActivationService", "解除蓝牙监听");
			((AlphaApplication) this.getApplicationContext())
					.getBlueToothManager().removeBlueToothInteraction(this);
		}
		if (aMapManager != null) {
			aMapManager.removeUpdates(mAMapLocationListener);
			aMapManager.destory();
		}
		aMapManager = null;
		super.onDestroy();
	}

	private void doTryActivation() {
		if (((AlphaApplication) ActivationService.this.getApplicationContext()).getCurrentBluetooth() == null) {
			MyLog.writeLog("ActivationService", "蓝牙掉线返回");
			this.stopSelf();
			// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
			return;
		}

		try {
			mDevicesActivated = new JSONArray(BasicSharedPreferencesOperator
					.getInstance(this, DataType.DEVICES_RECORD).doReadSync(
							BasicSharedPreferencesOperator.IS_ACTIVATE_DEVICES));
			for (int i = 0; i < mDevicesActivated.length(); i++) {
				if (mDevicesActivated.getString(i).equals(
						((AlphaApplication) ActivationService.this
								.getApplicationContext()).getCurrentBluetooth()
								.getAddress())) {
					// 本设备已激活
					MyLog.writeLog("ActivationService", "设备已激活");
					this.stopSelf();
					// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
					return;
				}
			}
		} catch (JSONException e) {
			mDevicesActivated = new JSONArray();
			e.printStackTrace();
		}

		MyLog.writeLog("ActivationService", "注册蓝牙监听");
		((AlphaApplication) ActivationService.this.getApplicationContext())
				.getBlueToothManager().addBlueToothInteraction(ActivationService.this);
		if (mReadUIDCodeTimer != null){
			mReadUIDCodeTimer.cancel();
		}

		mReadUIDCodeTimer = new Timer();
		mReadUIDCodeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				MyLog.writeLog("ActivationService", "READ_UID_CODE命令超时");
				mReadUIDCodeTimer.cancel();
				onReadUIDCode(null);
			}
		}, 5000);
		this.doSendComm(ConstValue.READ_UID_CODE, null);
	}

	@Override
	public int onStartCommand(android.content.Intent intent, int flags,
							  int startId) {
		MyLog.writeLog("ActivationService", this.hashCode() + "onStartCommand");

		//关闭定位也可以激活
		/*if (mLatitude == -1 || mLongitude == -1) {
			MyLog.writeLog("ActivationService", this.hashCode() + "没有收到地址");
			return START_REDELIVER_INTENT;
		}*/

		doTryActivation();

		// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
		return START_REDELIVER_INTENT;
	}

	private void doSendComm(byte cmd, byte[] param) {
		doSendComm(cmd, param, false);
	}

	private void doSendComm(byte cmd, byte[] param, boolean isMonopolyRight) {
		if (((AlphaApplication) this.getApplicationContext())
				.getCurrentBluetooth() == null) {
			// 如果蓝牙掉线
			return;
		}

		((AlphaApplication) this.getApplicationContext()).getBlueToothManager()
				.sendCommand(
						((AlphaApplication) this.getApplicationContext())
								.getCurrentBluetooth().getAddress(), cmd,
						param, param == null ? 0 : param.length,
						isMonopolyRight);
	}

	private void onReadSNCode(String sn_code) {
		MyLog.writeLog("ActivationService", "读取到SN：" + sn_code);
		mCurrentSNCode = sn_code;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doActivate(mCurrentSNCode == null ? "" : mCurrentSNCode,
						mCurrentUIDCode);
			}
		});
	}

	private void onReadUIDCode(String uid) {
		MyLog.writeLog("ActivationService", "读取到UID：" + uid);
		if (uid == null || uid.equals("")) {
			mCurrentUIDCode = "";
			MyLog.writeLog("ActivationService", "服务终止");
			stopSelf();
			return;
		} else {
			mCurrentUIDCode = uid;
		}
		// 读取SN码
		if (mReadSNCodeTimer != null){
			mReadSNCodeTimer.cancel();
		}

		mReadSNCodeTimer = new Timer();
		mReadSNCodeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				MyLog.writeLog("ActivationService", "READ_SN_CODE命令超时");
				onReadSNCode(null);
			}
		}, 5000);
		ActivationService.this.doSendComm(ConstValue.READ_SN_CODE,new byte[] { 0 });

	}

	private void doActivate(String sn_code, String uid) {

		MyLog.writeLog("ActivationService", "发起激活请求：sn_code-->" + sn_code + ",uid-->" + uid);

		try {
			String user_id = "";
			if (((AlphaApplication) this.getApplicationContext()).getCurrentUserInfo() != null) {
				user_id = ((AlphaApplication) this.getApplicationContext()).getCurrentUserInfo().userId + "";
			}

			String url = HttpAddress.getRequestUrl(Request_type.do_activate);
			String params = HttpAddress.getParamsForPost(new String[] {
							sn_code, "1", user_id, mLatitude + "," + mLongitude, uid },
					Request_type.do_activate, this);

			GetDataFromWeb.getJsonByPost(do_check_sn_request, url, params, this);
		} catch (Exception e) {
			MyLog.writeLog("ActivationService", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
		// TODO Auto-generated method stub

		if (cmd == ConstValue.READ_SN_CODE) {
			if (param[0] == 0) {
				this.mReadSNCodeTimer.cancel();
				onReadSNCode(new String(param, 1, param.length - 1));
			} else {
				if (param[1] == 0) {
					// 旧机器写入成功
					MyLog.writeLog("ActivationService", "旧机器写入sn成功！");

					mHandler.post(new Runnable() {

						@Override
						public void run() {

							try {
								mDevicesActivated.put(
										mDevicesActivated.length(),
										((AlphaApplication) ActivationService.this
												.getApplicationContext())
												.getCurrentBluetooth()
												.getAddress());
								BasicSharedPreferencesOperator
										.getInstance(ActivationService.this,
												DataType.DEVICES_RECORD)
										.doWrite(
												BasicSharedPreferencesOperator.IS_ACTIVATE_DEVICES,
												mDevicesActivated.toString(),
												new ISharedPreferensListenet() {

													@Override
													public void onSharedPreferenOpreaterFinish(
															boolean isSuccess,
															long request_code,
															String value) {
														MyLog.writeLog(
																"ActivationService",
																"写入历史记录成功");
													}
												}, -1);
							} catch (JSONException e) {
								MyLog.writeLog("ActivationService", "写入历史记录失败:"
										+ e.getMessage());
								e.printStackTrace();
							}
						}
					});
				} else {
					MyLog.writeLog("ActivationService", "旧机器写入sn失败！");
				}

			}
		} else if (cmd == ConstValue.READ_UID_CODE) {
			this.mReadUIDCodeTimer.cancel();
			onReadUIDCode(DataTools.bytesToHexString(param));
		}
	}

	@Override
	public void onSendData(String mac, byte[] datas, int nLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectState(boolean bsucceed, String mac) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceDisConnected(String mac) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		if (request_code == do_check_sn_request) {
			if (JsonTools.getJsonStatus(json)) {
				if (mCurrentSNCode == null || mCurrentSNCode.equals("")) {
					// 老设备没有SN码，写入SN码
					String SN_WEB = "";
					try {
						SN_WEB = new JSONObject(json).getString("models");
					} catch (JSONException e) {
						// 激活失败
						MyLog.writeLog("ActivationService", "激活失败：SN码获取失败");
						this.stopSelf();
						return;
					}
					byte[] params;
					byte[] b_sn = SN_WEB.getBytes();
					params = new byte[b_sn.length + 1];
					params[0] = 1;
					for (int i = 0; i < b_sn.length; i++) {
						params[i + 1] = b_sn[i];
					}
					ActivationService.this.doSendComm(ConstValue.READ_SN_CODE,
							params);

				} else {
					// 设备激活成功
					try {
						mDevicesActivated.put(mDevicesActivated.length(),
								((AlphaApplication) ActivationService.this
										.getApplicationContext())
										.getCurrentBluetooth().getAddress());
						BasicSharedPreferencesOperator
								.getInstance(this, DataType.DEVICES_RECORD)
								.doWrite(
										BasicSharedPreferencesOperator.IS_ACTIVATE_DEVICES,
										mDevicesActivated.toString(),
										new ISharedPreferensListenet() {

											@Override
											public void onSharedPreferenOpreaterFinish(
													boolean isSuccess,
													long request_code,
													String value) {
												MyLog.writeLog(
														"ActivationService",
														"写入历史记录成功");
												ActivationService.this
														.stopSelf();
											}
										}, -1);

					} catch (JSONException e) {
						MyLog.writeLog("ActivationService",
								"写入历史记录失败:" + e.getMessage());
						e.printStackTrace();
						this.stopSelf();
					}
				}
			} else {
				// 激活失败
				MyLog.writeLog("ActivationService", "激活失败");
				this.stopSelf();
			}
		}
	}

	private void updateWithNewLocation(Location location) {

		if (location != null) {
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
		} else {
			mLatitude = 0;
			mLongitude = 0;
		}

		MyLog.writeLog("ActivationService", "获取到位置：" + mLatitude + "," + mLongitude);
	}

	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			if (location != null) {
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				MyLog.writeLog("ActivationService", "获取到位置：" + mLatitude + "," + mLongitude);
				doTryActivation();
			}
		}
	};
}

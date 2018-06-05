package com.ubt.alpha1e.edu.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 先读uid，如果读不到有效的uid则认为机器是没升级bin的旧机器直接跳过。 如果可以读到uid，读不到有效的sn，则分配sn并写入。
 * 如果可以读到uid，也可以读到sn，直接激活
 */

public class RecordSoftService extends Service implements BlueToothInteracter,
		IJsonListener {

	private static final String TAG = "RecordSoftService";
	private String mCurrentSNCode = "";
	private String mCurrentUIDCode = "";
	private Timer mReadSNCodeTimer;
	private Timer mReadUIDCodeTimer;
	private long do_record_hard_version_request = 1002;
	private Handler mHandler = new Handler();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		UbtLog.d(TAG, this.hashCode() + "onDestroy");

		if (((AlphaApplication) this.getApplicationContext())
				.getBlueToothManager() != null) {
			UbtLog.d(TAG, "解除蓝牙监听");
			((AlphaApplication) this.getApplicationContext())
					.getBlueToothManager().removeBlueToothInteraction(this);
		}
		super.onDestroy();
	}

	private void doTryActivation() {
		if (((AlphaApplication) RecordSoftService.this.getApplicationContext())
				.getCurrentBluetooth() == null) {
			UbtLog.d(TAG, "蓝牙掉线返回");
			this.stopSelf();
			// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
			return;
		}

		UbtLog.d(TAG, "注册蓝牙监听");
		((AlphaApplication) RecordSoftService.this.getApplicationContext())
				.getBlueToothManager().addBlueToothInteraction(RecordSoftService.this);

		if (mReadUIDCodeTimer != null){
			mReadUIDCodeTimer.cancel();
		}
		mReadUIDCodeTimer = new Timer();
		mReadUIDCodeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				UbtLog.d(TAG, "READ_UID_CODE命令超时");
				mReadUIDCodeTimer.cancel();
				onReadUIDCode(null);
			}
		}, 5000);
		this.doSendComm(ConstValue.READ_UID_CODE, null);
	}

	@Override
	public int onStartCommand(Intent intent, int flags,
			int startId) {

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
		UbtLog.d(TAG, "读取到SN：" + sn_code);
		mCurrentSNCode = sn_code;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				UbtLog.d(TAG,"lihai------------hasActivation---->");

				doRecordSoftVersion(mCurrentSNCode == null ? "" : mCurrentSNCode,
						mCurrentUIDCode);
			}
		});
	}

	private void onReadUIDCode(String uid) {
		UbtLog.d(TAG, "读取到UID：" + uid);
		if (uid == null || uid.equals("")) {
			mCurrentUIDCode = "";
			UbtLog.d(TAG, "服务终止");
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
				UbtLog.d(TAG, "READ_SN_CODE命令超时");
				onReadSNCode(null);
			}
		}, 5000);
		RecordSoftService.this.doSendComm(ConstValue.READ_SN_CODE,
				new byte[] { 0 });

	}

	private void doRecordSoftVersion(String sn_code, String uid) {


		String hardVersion = ((AlphaApplication) this.getApplicationContext()).getRobotSoftVersion();
		UbtLog.d(TAG, "doRecordSolfVersion：sn_code-->" + sn_code
				+ ",uid-->" + uid + ",hardVersion-->"+hardVersion);
		try {

			String url = HttpAddress.getRequestUrl(Request_type.doRecordSoftVersion);
			String params = HttpAddress.getParamsForPost(new String[] {
							hardVersion, uid, sn_code},
					Request_type.doRecordSoftVersion, this);

			GetDataFromWeb.getJsonByPost(do_record_hard_version_request, url, params, this);
		} catch (Exception e) {
			UbtLog.d(TAG, e.getMessage());
			e.printStackTrace();
			this.stopSelf();
		}

	}

	@Override
	public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
		// TODO Auto-generated method stub

		if (cmd == ConstValue.READ_SN_CODE) {
			if (param[0] == 0) {
				this.mReadSNCodeTimer.cancel();
				onReadSNCode(new String(param, 1, param.length - 1));
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
		if(request_code == do_record_hard_version_request){
			if (JsonTools.getJsonStatus(json)) {
				UbtLog.d(TAG,"record solf version success");
			}else{
				UbtLog.d(TAG,"record solf version fail");
			}
			this.stopSelf();
		}
	}
}

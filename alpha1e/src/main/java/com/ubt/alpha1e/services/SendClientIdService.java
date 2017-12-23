package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.helper.SendClientIdHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 发送clientId服务
 */

public class SendClientIdService extends Service {

	private static final String TAG = "SendClientIdService";
	private static final int SCAN_TO_CONNECT = 3;

	private static SendClientIdService instance = null;

	static SendClientIdHelper sendClientIdHelper = null ;

	static boolean isSendClientId = false ;

	static long lastSendTime = System.currentTimeMillis();

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case SCAN_TO_CONNECT:

					break;

				default:
					break;
			}
		}
	};

    static Context mContext = null ;

	/**
	 * 启动服务
	 * @param context
     */
	public static void startService(Context context){

		if(instance == null){
            mContext = context;
			Intent mIntent = new Intent(context,SendClientIdService.class);
			context.startService(mIntent);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initHelper();
		registerBroadcastReceive();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		UbtLog.d(TAG, "-onStartCommand-");
		instance = this;
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		return START_NOT_STICKY;
	}

	private static void initHelper() {
		sendClientIdHelper = new SendClientIdHelper(mContext);
		sendClientIdHelper.RegisterHelper();
	}

	@Subscribe
	public void onEventRobot(RobotEvent event) {

		if(event.getEvent() == RobotEvent.Event.BLUETOOTH_SEND_CLIENTID_SUCCESS){
			UbtLog.d(TAG, "-发送clientId成功-");
			isSendClientId = true ;
		}

	}


	/**
	 *  注册WIFI网络状态改变广播
	 */
	public void registerBroadcastReceive() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mBroadCastReceiver, mIntentFilter);
	}

	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//connected and disconnected
				UbtLog.d(TAG, "接收到网络改变");
				wifiConnectState(intent);
			}
		}
	};

	/**
	 *  WIFI网络状态改变处理
	 */
	protected void wifiConnectState(Intent intent) {
		Parcelable parcelableExtra = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (null != parcelableExtra) {
			NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
			NetworkInfo.State state = networkInfo.getState();
			boolean isConnected = state == NetworkInfo.State.CONNECTED;
			if (isConnected) {
				UbtLog.d(TAG, "-网络已经连接--");
				if (((AlphaApplication) mContext.getApplicationContext())
						.getCurrentBluetooth() == null) {
					UbtLog.d(TAG, "-蓝牙未连上--");
					return ;
				}

				if(!isSendClientId && System.currentTimeMillis() - lastSendTime >60000){
					try {
						Thread.sleep(5000);
						send();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				UbtLog.d(TAG, "-网络已经断开--");
			}
		}
	}


	@Override
	public void onDestroy() {
		UbtLog.d(TAG, "-onDestroy--");
		super.onDestroy();
		if(mContext != null){
			mContext.unregisterReceiver(mBroadCastReceiver);
		}
	}


	public static void send(){
		if(sendClientIdHelper!= null){
			UbtLog.d(TAG, "-发送clientId--");
			isSendClientId = false ;
			lastSendTime = System.currentTimeMillis();
			sendClientIdHelper.send();
		}
	}


	/**
	 * 停止服务
	 */
	public static void doStopSelf(){
		if(instance != null){
			instance.stopSelf();
		}
	}

}

package com.ubt.alpha1e.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.ui.helper.AutoConnectBluetoothHelper;
import com.ubt.alpha1e.ui.helper.IScanUI;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.AlphaInfo;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 后台自动搜索蓝牙，自动连接服务
 */

public class AutoScanConnectService extends Service implements BlueToothInteracter,IScanUI {

	private static final String TAG = "AutoScanConnectService";

	private static final int DISCONNECTED = 1;
	private static final int CONNECT_FAIL = 2;
	private static final int SCAN_TO_CONNECT = 3;
	private static final int SCAN_FAIL = 4;
	private static final int EXIT_MANUAL_CONNECT_MODE = 5;
	private static final int OPEN_AUTO_CONNECT = 6;
	private static final int BLUETOOTH_TURN_ON = 7;
	private static final int CHECK_IS_BACKGROUND = 8;
	private static final int APP_OUT_BACKGROUND = 9;

	private static AutoScanConnectService instance = null;
	public static boolean isScaning = false;

	private AutoConnectBluetoothHelper mHelper;
	private AlphaInfo mLastConnectRobot = null;
	private boolean isScanSuccess = false;
	private boolean isAutoConnect = true;
	private boolean isManualConnectMode = false;
	private boolean isManualDisConnect = false;
	private boolean isUgradeing = false;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case DISCONNECTED:
				case SCAN_TO_CONNECT:
					doConnect();
					break;
				case CONNECT_FAIL:
				case SCAN_FAIL:
				case EXIT_MANUAL_CONNECT_MODE:
				case OPEN_AUTO_CONNECT:
				case BLUETOOTH_TURN_ON:
				case APP_OUT_BACKGROUND:
					UbtLog.d(TAG,"doScan = " + msg.what);
					doScan();
					break;
				case CHECK_IS_BACKGROUND:
					if(AlphaApplication.isBackground()){
						//UbtLog.d(TAG,"-CHECK_IS_BACKGROUND-");
						mHandler.sendEmptyMessageDelayed(CHECK_IS_BACKGROUND,1000);
					}else {
						mLastConnectRobot = getLastConnectRobot();
						if(mLastConnectRobot != null
								&& ((AlphaApplication) getApplicationContext()).getCurrentBluetooth() == null){
							mHandler.sendEmptyMessage(APP_OUT_BACKGROUND);
						}
					}
					break;
				default:
					break;
			}
		}
	};

    static Context mContext = null ;

	/**
	 * 启动自动扫描连接服务
	 * @param context
     */
	public static void startService(Context context){

		if(instance == null){
			//add by dicy.cheng
            mContext = context;
			Intent mIntent = new Intent(context,AutoScanConnectService.class);
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
	}

	/**
	 * START_STICKY : 如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，
	 *                由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
	 * START_REDELIVER_INTENT : 重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入
	 * START_NOT_STICKY : “非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统将会把它置为started状态，系统不会自动重启该服务，直到startService(Intent intent)方法再次被调用
	 * @param intent
	 * @param flags
	 * @param startId
     * @return
     */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//add by dicy.cheng
		UbtLog.d(TAG, "-onStartCommand-");
		instance = this;
		isAutoConnect = SettingHelper.isAutoConnect(this);

		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}

		initHelper();
		registerBluetoothListener();
		doConnect();

		return START_NOT_STICKY;
	}

	private void initHelper() {
		mHelper = new AutoConnectBluetoothHelper(this, mContext);
		mHelper.RegisterHelper();
	}

	/**
	 * 注册蓝牙监听
	 */
	private void registerBluetoothListener(){
		if(((AlphaApplication) getApplicationContext()).getBlueToothManager() != null){
			UbtLog.d(TAG, "注册蓝牙监听");
			((AlphaApplication) getApplicationContext())
					.getBlueToothManager().addBlueToothInteraction(AutoScanConnectService.this);
		}
	}

	/**
	 * 解除蓝牙监听
	 */
	private void UnRegisterBluetoothListener(){
		if(((AlphaApplication) getApplicationContext()).getBlueToothManager() != null){
			UbtLog.d(TAG, "解除蓝牙监听");
			((AlphaApplication) getApplicationContext())
					.getBlueToothManager().removeBlueToothInteraction(this);
		}
	}

	/**
	 * 获取最近连接的机器人
	 * @return
     */
	private AlphaInfo getLastConnectRobot(){
		List<AlphaInfo> list = mHelper.getHistoryDevices();
		AlphaInfo alphaInfo = null;
		if(!list.isEmpty()){
			alphaInfo = list.get(0);
		}
		return alphaInfo;
	}

	/**
	 * 连接机器人
	 */
	private void doConnect(){
		UbtLog.d(TAG, "-doConnect--isAutoConnect = " + isAutoConnect);
		if (isAutoConnect && ((AlphaApplication) getApplicationContext()).getCurrentBluetooth() == null
				&& !AlphaApplication.isBackground()) {
			mLastConnectRobot = getLastConnectRobot();
			if(mLastConnectRobot != null){
				Map<String, Object> robotInfo = new HashMap<>();
				robotInfo.put(AutoConnectBluetoothHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(mLastConnectRobot.getName()));
				robotInfo.put(AutoConnectBluetoothHelper.map_val_robot_mac, mLastConnectRobot.getMacAddr());
				robotInfo.put(AutoConnectBluetoothHelper.map_val_robot_connect_state, false);
				UbtLog.d(TAG, "-mLastConnectRobot-" + mLastConnectRobot.getName());
				mHelper.doReCoonect(robotInfo);
			}
		}
	}

	/**
	 * 扫描机器人蓝牙
	 */
	private void doScan(){
		UbtLog.d(TAG,"doScan isAutoConnect = " + isAutoConnect + "	isManualDisConnect == " + isManualDisConnect);
		if(isAutoConnect && !isManualDisConnect && !AlphaApplication.isBackground()){
			isScanSuccess = false;
			mLastConnectRobot = getLastConnectRobot();
			mHelper.doScan();
		}
	}

	@Subscribe
	public void onEventRobot(RobotEvent event) {
		if(!isManualConnectMode){
			//UbtLog.d(TAG,"isManualConnectMode = " + isManualConnectMode + "		Event : " + event.getEvent() );
			if(event.getEvent() == RobotEvent.Event.SCAN_ROBOT){
				dealScanResult(event.getBluetoothDevice());
			}else if(event.getEvent() == RobotEvent.Event.SCAN_ROBOT_FINISH){
				UbtLog.d(TAG," ccy SCAN_ROBOT_FINISH  1" );
				UbtLog.d(TAG,"isScanSuccess = " + isScanSuccess);
				if(!isScanSuccess){
					mHandler.sendEmptyMessage(SCAN_FAIL);
				}
			}else if(event.getEvent() == RobotEvent.Event.BLUETOOTH_TURN_ON){
				mLastConnectRobot = getLastConnectRobot();
				if(isAutoConnect && mLastConnectRobot != null && !isScanSuccess){
					mHandler.sendEmptyMessage(BLUETOOTH_TURN_ON);
				}
			}
		}
	}

	/**
	 * 处理扫描结果
	 * @param bluetoothDevice
     */
	private void dealScanResult(BluetoothDevice bluetoothDevice){
		String bluetoothAddr = bluetoothDevice.getAddress();
		UbtLog.d(TAG,"bluetoothAddr = " + bluetoothAddr);
		if(!TextUtils.isEmpty(bluetoothAddr) && mLastConnectRobot != null
				&& bluetoothAddr.equals(mLastConnectRobot.getMacAddr())){
			isScanSuccess = true;
			mHandler.sendEmptyMessage(SCAN_TO_CONNECT);
		}
	}

	@Override
	public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
		// TODO Auto-generated method stub
		//UbtLog.d(TAG,"mac : " + mac + "	cmd = " + cmd);
	}

	/**
	 * 停止服务
	 */
	public static void doStopSelf(){
		if(instance != null){
			instance.stopSelf();
		}
	}

	/**
	 * 进入退出手动连接
	 * @param isManualConnectMode 进入为true 退出为false
     */
	public static void doEntryManalConnect(boolean isManualConnectMode){
		if(instance != null){
			UbtLog.d(TAG,"isManualConnectMode = " + isManualConnectMode);
			instance.isManualConnectMode = isManualConnectMode;
			if(isManualConnectMode && ((AlphaApplication) instance.getApplicationContext()).getCurrentBluetooth() == null){
				instance.mHelper.doCancelCoon();
			}else {
				if (((AlphaApplication) instance.getApplicationContext()).getCurrentBluetooth() == null) {
					instance.mHandler.sendEmptyMessage(EXIT_MANUAL_CONNECT_MODE);
				}
			}
		}
	}

	/**
	 * 开启关闭自动连接按钮时刷新
	 */
	public static void doRefreshAutoConnect(boolean isAutoConnect){
		if(instance != null){
			instance.isAutoConnect = isAutoConnect;
			if(isAutoConnect){
				if (((AlphaApplication) instance.getApplicationContext()).getCurrentBluetooth() == null) {
					instance.mHandler.sendEmptyMessage(OPEN_AUTO_CONNECT);
				}
			}
		}
	}

	/**
	 * 设置手动断开
	 * @param isManualDisConnect 手动断开为true 重新连上后为false
     */
	public static void doManalDisConnect(boolean isManualDisConnect){
		if(instance != null){
			instance.isManualDisConnect = isManualDisConnect;
		}
	}

	/**
	 * 判断当前是否进入手动连接
	 * @return
     */
	public static boolean isManalConnectMode(){
		if(instance != null){
			return instance.isManualConnectMode;
		}
		return false;
	}

	/**
	 * app进入后台
	 */
	public static void doEntryBackground(){
		if(instance != null){

			if(instance.isAutoConnect
					&& !isManalConnectMode()
					&& instance.getLastConnectRobot() != null){
				UbtLog.d(TAG,"doEntryBackground = " + instance.getLastConnectRobot());

				if(((AlphaApplication)instance.getApplicationContext()).getCurrentBluetooth() == null){
					//没有连接，可能在扫描，释放连接
					instance.mHelper.doCancelCoon();
				}

				if(instance.mHandler.hasMessages(CHECK_IS_BACKGROUND)){
					instance.mHandler.removeMessages(CHECK_IS_BACKGROUND);
				}
				instance.mHandler.sendEmptyMessage(CHECK_IS_BACKGROUND);
			}
		}
	}

	public static void doEntryUgrade(boolean isUgradeing){
		if(instance != null){
			instance.isUgradeing = isUgradeing;
		}
	}

	@Override
	public void onDestroy() {
		UbtLog.d(TAG, "-onDestroy--");
		if(instance != null && instance.mHandler.hasMessages(CHECK_IS_BACKGROUND)){
			instance.mHandler.removeMessages(CHECK_IS_BACKGROUND);
		}

		instance = null;
		mHelper.DistoryHelper();
		UnRegisterBluetoothListener();
		EventBus.getDefault().unregister(this);
		super.onDestroy();
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
		UbtLog.d(TAG,"--onDeviceDisConnected---");
		if(!isUgradeing){//如果不是升级断开，重连
			mHandler.sendEmptyMessage(DISCONNECTED);

			RobotEvent disconnectEvent = new RobotEvent(RobotEvent.Event.DISCONNECT);
			EventBus.getDefault().post(disconnectEvent);
		}
	}

	@Override
	public void onReadHeadImgFinish(boolean is_success, Bitmap img) {

	}

	@Override
	public void onGetHistoryBindDevices(Set<BluetoothDevice> history_deivces) {

	}

	@Override
	public void noteIsScaning() {

	}

	@Override
	public void noteBtTurnOn() {

	}

	@Override
	public void noteBtIsOff() {

	}

	@Override
	public void noteScanResultInvalid() {

	}

	@Override
	public void onGetNewDevices(List<BluetoothDevice> devices) {

	}

	@Override
	public void onScanFinish() {

	}

	@Override
	public void onCoonected(boolean state) {
		UbtLog.d(TAG,"onCoonected = " + state);
		if(state){
			RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.CONNECT_SUCCESS);
			EventBus.getDefault().post(robotEvent);

//			AlphaApplication.getBaseActivity().showToast("ui_home_connect_success"); //dicy.cheng
		}else {
			mHandler.sendEmptyMessage(CONNECT_FAIL);
			//AlphaApplication.getBaseActivity().showToast("ui_home_connect_failed");
		}
	}

	@Override
	public void updateHistory() {

	}

	@Override
	public void noteUpdateBin() {

	}

	@Override
	public void onGetNewDevicesParams(List<BluetoothDevice> mDevicesList, Map<BluetoothDevice, Integer> mDevicesRssiMap) {

	}

	@Override
	public void noteCheckBin(BaseWebRunnable runnable) {

	}

	@Override
	public void onGetUserImg(boolean isSuccess, Bitmap img) {

	}

	@Override
	public void onUpdateBluetoothFinish(boolean is_success) {

	}

	@Override
	public void onUpdateEngineFinish(boolean is_success) {
		if(is_success){
			//升级完自动连接
			doConnect();
		}
	}

	@Override
	public void onGotoPCUpdate() {

	}
}

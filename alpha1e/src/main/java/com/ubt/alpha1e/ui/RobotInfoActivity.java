package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.data.model.UpgradeProgressInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.update.RobotSoftUpdateManager;
import com.ubt.alpha1e.utils.log.UbtLog;

public class RobotInfoActivity extends BaseActivity implements BaseDiaUI {

	private static final String TAG = "RobotInfoActivity";

	private static final int UPDATE_WIFI_STATUS = 1; //更新WIFI状态
	private static final int UPDATE_AUTO_UPGRADE = 2; //更新自动升级状态
	public static final int MSG_DO_REQUEST_FAIL = 3; //检查最新版本失败
	public static final int MSG_DO_NO_NEW_VERSION = 4; //无新版本
	public static final int MSG_DO_HAS_NEW_VERSION = 5; //有新版本
	public static final int MSG_DO_UPGRADE_PROGRESS = 6; //更新下载进度
	public static final int MSG_DO_UPGRADE_PROGRESS_DISPLAY = 7; //关闭更新进度框
	public static final int MSG_DO_BLUETOOTH_DISCONNECT = 8; //蓝牙断开

	private TextView tvRobotName;
	private TextView tvRobotMac;
	private TextView tvCurrentNetwork = null;
	private TextView tvCurrentInstructionNum = null;
	private TextView tvCurrentFirmwareVersion = null;
	private TextView tvCurrentHardwareVersion = null;
	private TextView tvCurrentNetworkIp = null;
	private TextView tvBattery = null;

	private ImageView ivBattery = null;
	private ImageView ivCharging = null;
	private ImageView ivBatteryLever = null;
	private ImageView ivDisconnect = null;

	private ImageButton imgBtnAutoUpdateFirmware = null;

	private RelativeLayout layBatteryInfo;
	private RelativeLayout layNetwork;
	private RelativeLayout layCustomInstruction;
	private RelativeLayout layAutoUpdate;
	private RelativeLayout layNetworkIp;

	private LinearLayout llBack;
	private ImageView ivSearch;
	private ImageView ivHelp;
	private TextView tvTitle;

	private View netLine,instructionsLine,firwareLine;

	private int mBatteryLeverWidth = 0;
	private boolean mCurrentAutoUpgrade = false;

	private Animation imgBatteryLeverAnmi;

	//初始默认横竖屛默认值 默认为竖屏
	private int mScreenOrientation = 1;

	private LoadingDialog mLoadingDialog;
	private boolean isConnectNetwork = false;//网络是否连接

	private static int REQUEST_CODE = 2000;

	//定义Handler处理对象
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case UPDATE_WIFI_STATUS:
					NetworkInfo networkInfo = (NetworkInfo) msg.obj;
					UbtLog.d(TAG,"networkInfo = " + networkInfo);
					if(networkInfo != null && networkInfo.status){
						tvCurrentNetwork.setText(networkInfo.name);
						tvCurrentNetworkIp.setText(networkInfo.ip);
						isConnectNetwork = true;
					}else {
						tvCurrentNetwork.setText(getStringResources("ui_network_un_con_net"));
						tvCurrentNetworkIp.setText("");
						isConnectNetwork = false;
					}
					break;
				case UPDATE_AUTO_UPGRADE:
					if(mCurrentAutoUpgrade){ //打开
						imgBtnAutoUpdateFirmware.setBackgroundResource(R.drawable.menu_setting_select);
					}else {//关闭
						imgBtnAutoUpdateFirmware.setBackgroundResource(R.drawable.menu_setting_unselect);
					}
					dismissDialog();

					break;
				case MSG_DO_REQUEST_FAIL:
					dismissDialog();
					Toast.makeText(RobotInfoActivity.this,getStringResources("ui_common_network_request_failed"),Toast.LENGTH_SHORT).show();
					break;
				case MSG_DO_NO_NEW_VERSION:
					dismissDialog();
					Toast.makeText(RobotInfoActivity.this,getStringResources("ui_upgrade_already_latest"),Toast.LENGTH_SHORT).show();
					break;
				case MSG_DO_HAS_NEW_VERSION:
					dismissDialog();
					break;
				case MSG_DO_UPGRADE_PROGRESS:
					UpgradeProgressInfo upgradeProgressInfo = (UpgradeProgressInfo)msg.obj;

					if(upgradeProgressInfo.status == 0){
						RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).setIsDownloading(false);
					}else if(upgradeProgressInfo.status == 2){
						RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).setIsDownloading(false);
					}else {
						RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).setIsDownloading(true);
					}

					RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler)
							.setCurrentProgress(Integer.parseInt(upgradeProgressInfo.progress));

					tvCurrentFirmwareVersion.setText(upgradeProgressInfo.progress+"%");
					break;
				case MSG_DO_UPGRADE_PROGRESS_DISPLAY:
					dismissDialog();
					if(RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).getIsDownloading()){
						int currentProgress = RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).getCurrentProgress();
						tvCurrentFirmwareVersion.setText(currentProgress+"%");
					}
					break;
				case MSG_DO_BLUETOOTH_DISCONNECT:
					((AlphaApplication) getApplicationContext()).doLostConn(RobotInfoActivity.this);
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 启动Activty方法
	 * @param activity 启动类
	 * @param screenOrientation 横竖屏
	 */
	public static void launchActivity(Activity activity, int screenOrientation)
	{
		Intent intent = new Intent();
		intent.setClass(activity,RobotInfoActivity.class);
		intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UbtLog.d(TAG,"--onCreate-- " );
		mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//mScreenOrientation = 0;
		if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setContentView(R.layout.activity_robot_info_landscape);
		}else{
			setContentView(R.layout.activity_robot_info);
		}
		mHelper = new MainHelper(this);

		initUI();
		initControlListener();
		initData();

	}

	/**
	 * 切换横竖屛
	 */
	private void switchScreemOrientation(){

		UbtLog.d(TAG,"mScreenOrientation == " + mScreenOrientation);
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				&& mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			//设置为横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				&& mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){

			//设置为竖屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switchScreemOrientation();

		if(!((AlphaApplication) getApplicationContext()).isAlpha1E()){
			layNetwork.setVisibility(View.GONE);
			layCustomInstruction.setVisibility(View.GONE);
			layAutoUpdate.setVisibility(View.GONE);
			layNetworkIp.setVisibility(View.GONE);

			findViewById(R.id.v_firmware_version).setVisibility(View.GONE);
			findViewById(R.id.v_instruction).setVisibility(View.GONE);
			findViewById(R.id.v_auto_update_firmware).setVisibility(View.GONE);
			findViewById(R.id.v_network_ip).setVisibility(View.GONE);
		}else{
			((MainHelper) mHelper).readNetworkStatus();
			((MainHelper) mHelper).readAutoUpgradeStatus();

			layNetwork.setVisibility(View.VISIBLE);
			layCustomInstruction.setVisibility(View.VISIBLE);
			layAutoUpdate.setVisibility(View.VISIBLE);
			layNetworkIp.setVisibility(View.VISIBLE);

			findViewById(R.id.v_firmware_version).setVisibility(View.VISIBLE);
			findViewById(R.id.v_instruction).setVisibility(View.VISIBLE);
			findViewById(R.id.v_auto_update_firmware).setVisibility(View.VISIBLE);
			findViewById(R.id.v_network_ip).setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 初始化UI
	 */
	@Override
	protected void initUI() {


		tvTitle = (TextView) findViewById(R.id.tv_base_title_name);
		llBack = (LinearLayout) findViewById(R.id.lay_base_back);
		ivSearch = (ImageView) findViewById(R.id.iv_re_scan);
		ivHelp = (ImageView) findViewById(R.id.iv_help);

		tvTitle.setText(getStringResources("ui_home_my_robot"));

		llBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					Intent intent = new Intent();
					intent.setClass(RobotInfoActivity.this, ScanBluetoothActivity.class);
					startActivityForResult(intent, REQUEST_CODE);
				} else {
//					intent.setClass(RobotInfoActivity.this, RobotConnectedActivity.class);
					RobotConnectedActivity.launchActivity(RobotInfoActivity.this, true, REQUEST_CODE);
				}


			}
		});

		ivHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String language = getStandardLocale(getAppCurrentLanguage());

				String url = HttpAddress
						.getRequestUrl(HttpAddress.Request_type.scan_robot_gest)
						+ HttpAddress.getParamsForGet(
						new String[] { language },
						HttpAddress.Request_type.scan_robot_gest);
				Intent intent = new Intent();
				intent.putExtra(WebContentActivity.SCREEN_ORIENTATION, mScreenOrientation);
				intent.putExtra(WebContentActivity.WEB_TITLE, "");
				intent.putExtra(WebContentActivity.WEB_URL, url);
				intent.setClass(RobotInfoActivity.this, WebContentActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});

	tvRobotName = (TextView) findViewById(R.id.tv_robot_name);
	tvRobotMac = (TextView) findViewById(R.id.tv_robot_mac);

	tvCurrentNetwork = (TextView) findViewById(R.id.tv_current_network);
	tvCurrentInstructionNum = (TextView) findViewById(R.id.tv_current_instruction_num);
	tvCurrentFirmwareVersion = (TextView) findViewById(R.id.tv_current_firmware_version);
	tvCurrentHardwareVersion = (TextView) findViewById(R.id.tv_current_hardware_version);
	tvCurrentNetworkIp = (TextView) findViewById(R.id.tv_current_network_ip);

	ivBattery = (ImageView) findViewById(R.id.iv_battery);
	ivCharging = (ImageView) findViewById(R.id.iv_charging);
	ivBatteryLever = (ImageView) findViewById(R.id.iv_battery_lever);
	ivDisconnect = (ImageView) findViewById(R.id.iv_disconnect);

	imgBtnAutoUpdateFirmware = (ImageButton) findViewById(R.id.imgbtn_auto_update_firmware);

	tvBattery = (TextView) findViewById(R.id.tv_battery);
	mBatteryLeverWidth = ivBatteryLever.getLayoutParams().width;

	layBatteryInfo = (RelativeLayout) findViewById(R.id.lay_battery_info);
	layNetwork = (RelativeLayout) findViewById(R.id.lay_network);
	layCustomInstruction = (RelativeLayout) findViewById(R.id.lay_custom_instruction);
	layAutoUpdate = (RelativeLayout) findViewById(R.id.lay_auto_update_firmware);
	layNetworkIp = (RelativeLayout) findViewById(R.id.lay_network_ip);

	imgBatteryLeverAnmi = AnimationUtils.loadAnimation(this, R.anim.battery_line_anim_head);

	mLoadingDialog = LoadingDialog.getInstance(RobotInfoActivity.this,RobotInfoActivity.this);

}

	/**
	 * 注册监听EventBus方法
	 * @param event
	 */
	@Override
	public void onEventRobot(RobotEvent event) {
		super.onEventRobot(event);

		if(event.getEvent() == RobotEvent.Event.CHARGING){
			updateCharging(event);
		}else if(event.getEvent() == RobotEvent.Event.UPDATING_POWER){
			updatePower(event);
		}else if(event.getEvent() == RobotEvent.Event.NETWORK_STATUS){
			updateNetworkStatus(event);
		}else if(event.getEvent() == RobotEvent.Event.AUTO_UPGRADE_STATUS
				|| event.getEvent() == RobotEvent.Event.SET_AUTO_UPGRADE_STATUS){

			updateAutoUpgradeStatus(event);
		}else if(event.getEvent() == RobotEvent.Event.UPGRADE_PROGRESS){
			updateUpgradeProgress(event);
		}else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
			onBluetoothDisconnect(event);
		}
	}

	/**
	 * 更新是否在充电中
	 * @param event
	 */
	public void updateCharging(RobotEvent event) {
		UbtLog.d(TAG,"onEventCharging event = " +event.getEvent());

		layBatteryInfo.setVisibility(View.VISIBLE);
		ivCharging.setVisibility(View.VISIBLE);
		tvBattery.setTextColor(this.getResources().getColor(R.color.sec_txt_blue));
		tvBattery.setText(getStringResources("ui_home_charging"));
		tvBattery.setVisibility(View.VISIBLE);

		// spring
		ivBatteryLever.setBackgroundResource(R.drawable.main_myrobot_battery_blue);
		ivBattery.setBackgroundResource(R.drawable.main_myrobot_battery_normal);

		android.view.ViewGroup.LayoutParams lay_pram = ivBatteryLever.getLayoutParams();
		lay_pram.width = mBatteryLeverWidth;
		ivBatteryLever.setLayoutParams(lay_pram);
		ivBatteryLever.startAnimation(imgBatteryLeverAnmi);
	}

	/**
	 * 更新电量值
	 * @param event
	 */
	public void updatePower(RobotEvent event) {
		int power = event.getPower();
		UbtLog.d(TAG,"updatePower = " + power);
		layBatteryInfo.setVisibility(View.VISIBLE);
		ivCharging.setVisibility(View.INVISIBLE);
		try {
			ivBatteryLever.clearAnimation();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//电量少于10显示低电量
		if (power <= 10) {
			ivBatteryLever.setBackgroundResource(R.drawable.main_myrobot_battery_red);
			ivBattery.setBackgroundResource(R.drawable.main_myrobot_battery_low);
			tvBattery.setTextColor(this.getResources().getColor(R.color.sec_txt_red));
			tvBattery.setText(power+"%");
			tvBattery.setVisibility(View.VISIBLE);
		} else {
			ivBatteryLever.setBackgroundResource(R.drawable.main_myrobot_battery_blue);
			ivBattery.setBackgroundResource(R.drawable.main_myrobot_battery_normal);
			tvBattery.setTextColor(this.getResources().getColor(R.color.sec_txt_blue));
			tvBattery.setText(power+"%");
			tvBattery.setVisibility(View.VISIBLE);
		}
		android.view.ViewGroup.LayoutParams lay_pram = ivBatteryLever.getLayoutParams();
		lay_pram.width = mBatteryLeverWidth * power / 100;
		ivBatteryLever.setLayoutParams(lay_pram);
	}

	/**
	 * 更新网络状态
	 * @param event
	 */
	private void updateNetworkStatus(RobotEvent event){
		Message msg = new Message();
		msg.what = UPDATE_WIFI_STATUS;
		msg.obj = event.getNetworkInfo();
		mHandler.sendMessage(msg);
	}

	/**
	 * 更新网络状态
	 * @param event
	 */
	private void updateAutoUpgradeStatus(RobotEvent event){
		int autoUpgradeStatus = event.getAutoUpgradeStatus();
		UbtLog.d(TAG,"autoUpgradeStatus = " + autoUpgradeStatus + "	 " + event.getEvent().name());
		if(autoUpgradeStatus == 0){//关闭
			mCurrentAutoUpgrade = false;
			mHandler.sendEmptyMessage(UPDATE_AUTO_UPGRADE);
		}else if(autoUpgradeStatus == 1){
			mCurrentAutoUpgrade = true;
			mHandler.sendEmptyMessage(UPDATE_AUTO_UPGRADE);
		}else {
			//2 设置中... 不做处理
		}
	}

	/**
	 * 更新升级进度（实为下载进度）
	 * @param event
	 */
	private void updateUpgradeProgress(RobotEvent event){
		UpgradeProgressInfo upgradeProgressInfo = event.getUpgradeProgressInfo();
		Message msg = new Message();
		msg.what = MSG_DO_UPGRADE_PROGRESS;
		msg.obj = upgradeProgressInfo;
		mHandler.sendMessage(msg);
	}

	/**
	 * 蓝牙断开
	 * @param event
	 */
	private void onBluetoothDisconnect(RobotEvent event){
		mHandler.sendEmptyMessage(MSG_DO_BLUETOOTH_DISCONNECT);
	}

	/**
	 * 初始化数据
	 */
	private void initData(){

		tvRobotName.setText(((AlphaApplication) this.getApplication()).getCurrentDeviceName());
		tvRobotMac.setText(((AlphaApplication) this.getApplication()).getCurrentBluetoothAddress());

		//tvCurrentNetwork.setText(getStringResources("ui_network_un_con_net"));
		tvCurrentInstructionNum.setText(getStringResources("ui_robot_custom_instruction_num").replace("#","0"));

		tvCurrentFirmwareVersion.setText(((AlphaApplication) this.getApplication()).getRobotSoftVersion());
		tvCurrentHardwareVersion.setText(((AlphaApplication) this.getApplication()).getRobotHardVersion());

		mHandler.sendEmptyMessage(UPDATE_AUTO_UPGRADE);
	}

	/**
	 * 初始化按键监听逻辑
	 */
	@Override
	protected void initControlListener() {

		//跳转到联网配置
		layNetwork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				RobotNetConnectActivity.launchActivity(RobotInfoActivity.this, getRequestedOrientation(),false);
			}
		});

		//跳转到自定义语义
		layCustomInstruction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//CustomInstructionActivity.launchActivity(RobotInfoActivity.this,getRequestedOrientation());
			}
		});

		//蓝牙断开连接
		ivDisconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainHelper)mHelper).doLostCoon(RobotInfoActivity.this);
				AutoScanConnectService.doManalDisConnect(true);
			}
		});

		//自动升级
		imgBtnAutoUpdateFirmware.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadingDialog.setDoCancelable(true);
				mLoadingDialog.show();
				if(mCurrentAutoUpgrade){
					mCurrentAutoUpgrade = false;
				}else {
					mCurrentAutoUpgrade = true;
				}
				((MainHelper)mHelper).doChangeAutoUpgrade(mCurrentAutoUpgrade);
				//mHandler.sendEmptyMessage(UPDATE_AUTO_UPGRADE);
			}
		});

		tvCurrentFirmwareVersion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isDownloading = RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).getIsDownloading();
				UbtLog.d(TAG,"isDownloading = " + isDownloading);
				if(!isDownloading){
					mLoadingDialog.setDoCancelable(true);
					mLoadingDialog.showMessage(getStringResources("ui_robot_info_checking"));
				}
				RobotSoftUpdateManager.getInstance(RobotInfoActivity.this, mHandler).doCheckUpdateSoft();
			}
		});
	}

	/**
	 * 显示转动对话框
	 */
	public void showDialog() {
		if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
		{
			mLoadingDialog.show();
		}
	}

	/**
	 * 消失对话框
	 */
	public void dismissDialog() {
		if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!this.isFinishing())
		{
			mLoadingDialog.cancel();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			UbtLog.d(TAG, "onActivityResult Robot!");
			if (requestCode == REQUEST_CODE){
				initData();
			}
		}

	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void noteWaitWebProcressShutDown() {

	}
}

package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;
import com.ubt.alpha1e.ui.dialog.NetConnectAlertDialog;
import com.ubt.alpha1e.ui.helper.NetworkHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

public class RobotNetConnectActivity extends BaseActivity {

	private static final String TAG = "RobotNetConnectActivity";

	//定义类常量
	private static final int NETWORK_CONNECT_SUCCESS = 1; //连接成功
	private static final int NETWORK_CONNECT_FAIL = 2;    //连接失败
	private static final int NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY = 3;    //连接对话框消失
	private static final int NETWORK_CONNECT_FAIL_DIALOG_DISPLAY = 4;    //连接对话框消失
	private static final int UPDATE_WIFI_NAME = 5;    //更新网络连接名称

	//定义UI变量
	private TextView tvDoNetConnect = null;
	private EditText edtWifiPwd = null;
	private EditText edtWifiName = null;
	private ImageView ivReplaceWifi = null;

	//初始默认横竖屛默认值 默认为竖屏
	private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	//是否连接升级
	private boolean mIsConnectToUpgrade = false;
	private String mFromActivity = "";

	//定义联网弹出框
	private NetConnectAlertDialog mConnectAlertDialog = null;


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case NETWORK_CONNECT_SUCCESS:
					if(mConnectAlertDialog != null){
						mConnectAlertDialog.setImageResoure(R.drawable.network_connect_success);
						mConnectAlertDialog.setMsg(getStringResources("ui_network_connect_success"));
						mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY,1500);
					}

					break;
				case NETWORK_CONNECT_FAIL:
					if(mConnectAlertDialog != null){
						mConnectAlertDialog.setImageResoure(R.drawable.network_connect_fail);
						mConnectAlertDialog.setMsg(getStringResources("ui_network_connect_failer"));
						mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_FAIL_DIALOG_DISPLAY,1500);
					}
					break;
				case NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY:
					displayDialog();
					toBack();
					break;
				case NETWORK_CONNECT_FAIL_DIALOG_DISPLAY:
					displayDialog();
					break;
				case UPDATE_WIFI_NAME:
					String remoteConnectName = (String) msg.obj;
					if(!TextUtils.isEmpty(remoteConnectName)){
						edtWifiName.setText(remoteConnectName);
						edtWifiPwd.requestFocus();
					}else {
						NetworkInfo mWifiInfo = getCurrentLocalConnectNetworkInfo();
						if (mWifiInfo.isConnected()) {
							UbtLog.d(TAG,"mWifiInfo.getExtraInfo() == " + mWifiInfo.getExtraInfo());
							edtWifiName.setText(mWifiInfo.getExtraInfo().replaceAll("\"",""));
							edtWifiPwd.requestFocus();
						}else {
							edtWifiName.setText("");
						}
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 定义调转方法
	 * @param activity 来自哪个Activity
     */
	public static void launchActivity(Activity activity,int screenOrientation,boolean isConnectToUpgrade)
	{
		Intent intent = new Intent();
		intent.setClass(activity,RobotNetConnectActivity.class);
		intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
		intent.putExtra(Constant.IS_CONNECT_TO_UPGRADE,isConnectToUpgrade);
		intent.putExtra(Constant.FROM_ACTIVITY_NAME,activity.getClass().getSimpleName());
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_robot_net_connect);
		mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mIsConnectToUpgrade = getIntent().getBooleanExtra(Constant.IS_CONNECT_TO_UPGRADE,false);
		mFromActivity = getIntent().getStringExtra(Constant.FROM_ACTIVITY_NAME);

		mHelper = new NetworkHelper(this);
		initUI();
		initControlListener();
		initData();

	}

	/**
	 * 监听Eventbus消息方法
	 * @param event
     */
	@Subscribe
	public void onEventNetwork(NetworkEvent event) {
		if(event.getEvent() == NetworkEvent.Event.CHANGE_SELECT_WIFI){
			edtWifiName.setText(event.getSelectWifiName());
			edtWifiPwd.requestFocus();
		}else if(event.getEvent() == NetworkEvent.Event.DO_CONNECT_WIFI){
			updateNetworkConnectStatus(event);
		}else if(event.getEvent() == NetworkEvent.Event.NETWORK_STATUS){
			updateNetworkConnectName(event);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setCurrentActivityLable(RobotNetConnectActivity.class.getSimpleName());
		super.onResume();
		switchScreemOrientation();
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

	/**
	 * 初始化UI
	 */
	@Override
	protected void initUI() {
		initTitle(getStringResources("ui_network_home"));

		setTitleBack(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				toBack();
			}
		});

		edtWifiName = (EditText) findViewById(R.id.edt_wifi_name);
		tvDoNetConnect = (TextView)findViewById(R.id.tv_do_net_connect);
		edtWifiPwd = (EditText) findViewById(R.id.edt_wifi_pwd);
		ivReplaceWifi = (ImageView)findViewById(R.id.iv_replace_wifi);

	}

	/**
	 * 返回事件
	 */
	private void toBack(){
		UbtLog.d(TAG,"mFromActivity = " + mFromActivity);
		if("RobotConnectedActivity".equals(mFromActivity)){
			RobotInfoActivity.launchActivity(this, getRequestedOrientation());
		}
		this.finish();
	}


	/**
	 * 初始化数据
	 */
	private void initData(){
		Message msg = new Message();
		msg.what = UPDATE_WIFI_NAME;
		msg.obj = "";
		mHandler.sendMessage(msg);


	}

	/**
	 * 获取当前手机连接WIFI名称
	 * @return
     */
	private NetworkInfo getCurrentLocalConnectNetworkInfo(){

		//当前连接WIFI对象
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi;
	}


	/**
	 * 更新网络连接逻辑
	 * @param event
     */
	private void updateNetworkConnectStatus(NetworkEvent event){
		int connectStatus = event.getConnectStatus();
		UbtLog.d(TAG,"connectStatus === " + connectStatus);
		if(connectStatus == 1){
			//联网中
		}else if(connectStatus == 2){
			mHandler.sendEmptyMessage(NETWORK_CONNECT_SUCCESS);
		}else if(connectStatus == 3){
			mHandler.sendEmptyMessage(NETWORK_CONNECT_FAIL);
		}
	}

	/**
	 * 更新网络连接名称
	 * @param event
	 */
	private void updateNetworkConnectName(NetworkEvent event){
		UbtLog.d(TAG,"event.getConnectWifiName() = " + event.getConnectWifiName());
		Message msg = new Message();
		msg.what = UPDATE_WIFI_NAME;
		msg.obj = event.getConnectWifiName();
		mHandler.sendMessage(msg);
	}

	/**
	 * 初始化按键点击事件
	 */
	@Override
	protected void initControlListener() {
		tvDoNetConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(TextUtils.isEmpty(edtWifiName.getText().toString())){
					new AlertDialog(RobotNetConnectActivity.this)
							.builder()
							.setTitle(getStringResources("ui_network_prompt"))
							.setMsg(getStringResources("ui_netwok_robot_need_network"))
							.setCancelable(false)
							.setPositiveButton(getStringResources("ui_network_go_connect_net"), new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									gotoSelectWifi();
								}
							}).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
					return;
				}

				//密码为null,弹出提示
				if(TextUtils.isEmpty(edtWifiPwd.getText().toString())){
					new AlertDialog(RobotNetConnectActivity.this)
							.builder()
							.setTitle(getStringResources("ui_network_no_password_tips"))
							.setMsg(getStringResources("ui_network_un_pass_tip"))
							.setCancelable(false)
							.setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									startNetwork();
								}
							}).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
				}else {
					startNetwork();
				}

			}
		});

		ivReplaceWifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSelectWifi();
			}
		});

		edtWifiName.addTextChangedListener(mTextWatcher);
	}

	/**
	 * 文本内容改变，按钮处理逻辑
	 * 内容为空时，不可点击，非空时，可以点击
	 */
	private void setNetworkButtonState(){

		if(edtWifiName.getText().toString().trim().length() > 0){
			tvDoNetConnect.setEnabled(true);
		}else{
			tvDoNetConnect.setEnabled(false);
		}
	}

	/**
	 * 文本内容改变监听器
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable editable) {
			setNetworkButtonState();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
			// TODO Auto-generated method stub
		}
	};
	/**
	 * 开始联网
	 */
	private void startNetwork(){

		mConnectAlertDialog = new NetConnectAlertDialog(RobotNetConnectActivity.this)
				.builder()
				.setMsg(getStringResources("ui_network_connecting"))
				.setCancelable(false);
		mConnectAlertDialog.show();

		((NetworkHelper)mHelper).doConnectNetwork(edtWifiName.getText().toString(), edtWifiPwd.getText().toString());
		//((NetworkHelper)mHelper).doConnectNetwork("UBT", "Ubtubtubt!@");
		//((NetworkHelper)mHelper).doConnectNetwork("UBT-users", "Ubtubtubt");
	}

	/**
	 * 消失对话框
	 */
	private void displayDialog(){

		if(mConnectAlertDialog != null && mConnectAlertDialog.isShowing()){
			mConnectAlertDialog.display();
			mConnectAlertDialog = null;
		}
	}

	/**
	 * 选择WIFI,进行联网
	 */
	private void gotoSelectWifi(){
		new WifiSelectAlertDialog(RobotNetConnectActivity.this)
				.setmCurrentSelectWifiName(edtWifiName.getText().toString())
				.builder()
				.setTitle(getStringResources("ui_network_can_connect"))
				.setCancelable(false)
				.setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		displayDialog();
		if(mHelper != null){
			//读取更新当前机器人网络状态
			((NetworkHelper)mHelper).readNetworkStatus();
		}

		super.onDestroy();
	}
}

package com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netsearchresult.NetSearchResultActivity;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.ui.helper.IScanUI;
import com.ubt.alpha1e.ui.helper.ScanHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/11/6
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothandnetconnectstateActivity extends MVPBaseActivity<BluetoothandnetconnectstateContract.View, BluetoothandnetconnectstatePresenter> implements BluetoothandnetconnectstateContract.View , View.OnClickListener,IScanUI {

    String TAG = "BluetoothandnetconnectstateActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_close)
    ImageButton ib_close;

    @BindView(R.id.ig_bluetooth)
    ImageView ig_bluetooth;

    @BindView(R.id.ig_wifi)
    ImageView ig_wifi;

    @BindView(R.id.ig_get_bluetooth_list)
    ImageView ig_get_bluetooth_list;

    @BindView(R.id.ig_get_wifi_list)
    ImageView ig_get_wifi_list;

    @BindView(R.id.rl_content_device_list)
    RelativeLayout rl_content_device_list;

    @BindView(R.id.buletooth_device_list)
    RecyclerView buletooth_device_list;

    @BindView(R.id.rl_devices_list)
    RelativeLayout rl_devices_list;

    @BindView(R.id.tv_devices_num)
    TextView tv_devices_num;

    @BindView(R.id.ed_bluetooth_name)
    EditText ed_bluetooth_name;

    @BindView(R.id.ed_wifi_name)
    EditText ed_wifi_name;

    @BindView(R.id.no_buletooth_devices)
    TextView no_buletooth_devices;

    @BindView(R.id.ig_phone)
    ImageView ig_phone;

    private static final int UPDATE_WIFI_STATUS = 1; //更新WIFI状态
    private static final int UPDATE_AUTO_UPGRADE = 2; //更新自动升级状态
    public static final int MSG_DO_REQUEST_FAIL = 3; //检查最新版本失败
    public static final int MSG_DO_NO_NEW_VERSION = 4; //无新版本
    public static final int MSG_DO_HAS_NEW_VERSION = 5; //有新版本
    public static final int MSG_DO_UPGRADE_PROGRESS = 6; //更新下载进度
    public static final int MSG_DO_UPGRADE_PROGRESS_DISPLAY = 7; //关闭更新进度框
    public static final int MSG_DO_BLUETOOTH_DISCONNECT = 8; //蓝牙断开

    private List<Map<String, Object>> lst_robots_result_datas;

    private BluetoothHelper mHelper;
    private DecimalFormat df = new DecimalFormat("0.##");
    private Map<String, Object> mCurrentRobotInfo = null;

    //定义Handler处理对象
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WIFI_STATUS:
                    NetworkInfo networkInfo = (NetworkInfo) msg.obj;
                    UbtLog.d(TAG,"networkInfo == " + networkInfo);
//                    if(networkInfo != null && networkInfo.status){
                    ed_wifi_name.setText(networkInfo.name);

                    if(networkInfo.status){
                        ig_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_nomal));
                    }else {
                        ig_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_abnomal));
                    }
                    break;
                case UPDATE_AUTO_UPGRADE:

                    break;
                case MSG_DO_REQUEST_FAIL:

                    break;
                case MSG_DO_NO_NEW_VERSION:
                    break;
                case MSG_DO_HAS_NEW_VERSION:
//                    dismissDialog();
                    break;
                case MSG_DO_UPGRADE_PROGRESS:

                    break;
                case MSG_DO_UPGRADE_PROGRESS_DISPLAY:

                    break;
                case MSG_DO_BLUETOOTH_DISCONNECT:
                    UbtLog.d(TAG,"MSG_DO_BLUETOOTH_DISCONNECT ..... " );
                    ((AlphaApplication) getApplicationContext()).doLostConnect();
                    ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.buletooth_con_fail));
                    ed_bluetooth_name.setText("");
                    ig_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_abnomal));
                    ed_wifi_name.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst_robots_result_datas = new ArrayList<>();
        mHelper = new BluetoothHelper(BluetoothandnetconnectstateActivity.this, BluetoothandnetconnectstateActivity.this);
        initUI();
    }

    @Override
    protected void initUI() {
        ib_return.setOnClickListener(this);
        ib_return.callOnClick();
        ib_close.setOnClickListener(this);
        ig_get_bluetooth_list.setOnClickListener(this);
        ig_get_wifi_list.setOnClickListener(this);

        rl_content_device_list.setOnClickListener(this);
        rl_devices_list.setOnClickListener(this);
        no_buletooth_devices.setOnClickListener(this);

        rl_devices_list.setVisibility(View.INVISIBLE);


        if(!mHelper.isLostCoon()){
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_sucess));
            BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
            String name = b.getName();
            String macAddr = b.getAddress();
            UbtLog.d(TAG,"当前连接设备："+name +" mac地址："+macAddr);
            ed_bluetooth_name.setText(name);
            ed_wifi_name.requestFocus();

        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetooth_and_net_connect_state;

    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.SCAN_ROBOT){
            UbtLog.d(TAG,"收到蓝牙设备");
//            if(rlConnecting.getVisibility() != View.VISIBLE){
//            dealScanResult(event.getBluetoothDevice(),event.getRssi());
//            }
        }else if(event.getEvent() == RobotEvent.Event.CHARGING){
//            updateCharging(event);
        }else if(event.getEvent() == RobotEvent.Event.UPDATING_POWER){
//            updatePower(event);
        }else if(event.getEvent() == RobotEvent.Event.NETWORK_STATUS){
            updateNetworkStatus(event);
        }else if(event.getEvent() == RobotEvent.Event.AUTO_UPGRADE_STATUS
                || event.getEvent() == RobotEvent.Event.SET_AUTO_UPGRADE_STATUS){

//            updateAutoUpgradeStatus(event);
        }else if(event.getEvent() == RobotEvent.Event.UPGRADE_PROGRESS){
//            updateUpgradeProgress(event);
        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            onBluetoothDisconnect(event);
        }
    }

    /**
     * 蓝牙断开
     * @param event
     */
    private void onBluetoothDisconnect(RobotEvent event){
        mHandler.sendEmptyMessage(MSG_DO_BLUETOOTH_DISCONNECT);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mHelper != null) {
            UbtLog.d(TAG, "--onPause--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.UnRegisterHelper();
        }
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG,"---onDestroy----");
        try {
            this.mHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(mHelper != null){
            mHelper.RegisterHelper();
        }
        if(!mHelper.isLostCoon()){
                ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_sucess));
                BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
                String name = b.getName();
                String macAddr = b.getAddress();
                UbtLog.d(TAG,"当前连接设备："+name +" mac地址："+macAddr);
                ed_bluetooth_name.setText(name);
                ed_wifi_name.requestFocus();

                mHelper.readNetworkStatus();
        }else {
            rl_content_device_list.setVisibility(View.GONE);
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.buletooth_con_fail));
            ed_bluetooth_name.setText("");

            ig_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_abnomal));
            ed_wifi_name.setText("");
        }
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0,R.anim.activity_close_down_up);
    }


    void startBluetoothConnect(){
        UbtLog.d(TAG,"获取蓝牙列表");
        Intent intent = new Intent();
        intent.putExtra("isFirst","no");
        intent.setClass(BluetoothandnetconnectstateActivity.this,BluetoothconnectActivity.class);
        this.startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_return:
//                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ib_close:
                /*Intent backIntent = new Intent();
                backIntent.putExtra("isConnect", isBulueToothConnected());
                setResult(101,backIntent);*/
                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ig_get_bluetooth_list:

                BluetoothAdapter mBtAdapter;
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBtAdapter.isEnabled()) {
                    UbtLog.d(TAG, "bluetoothEnable false ");
                    boolean bluetoothEnable = mBtAdapter.enable();
                    if(bluetoothEnable){
                        UbtLog.d(TAG, "bluetooth Enable true 判断是否授权");
                        if(PermissionUtils.getInstance(this).hasPermission(Permission.LOCATION)){
                            UbtLog.d(TAG, "bluetoothEnable true 有授权");//ok
                            startBluetoothConnect();
                        }else {
                            UbtLog.d(TAG, "bluetoothEnable true 没有授权");//ok
                            PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION);
                        }
                    }else {
                        UbtLog.d(TAG, "bluetoothEnable false 提醒去打开蓝牙");//ok
                        new ConfirmDialog(this).builder()
                                .setTitle("提示")
                                .setMsg("请在手机的“设置->蓝牙”中打开蓝牙")
                                .setCancelable(true)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "bluetoothEnable false onClick 1 ");
                                    }
                                }).show();
                    }
                }else {
                    UbtLog.d(TAG, "bluetooth enable  判断是否授权");
                    if(PermissionUtils.getInstance(this).hasPermission(Permission.LOCATION)){
                        UbtLog.d(TAG, "bluetoothEnable true 有授权");//ok
                        startBluetoothConnect();
                    }else {
                        UbtLog.d(TAG, "bluetoothEnable true 没有授权"); //ok
                        PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION);
                    }
                }

                break;
            case R.id.ig_get_wifi_list:
                if(!mHelper.isLostCoon()){
                    Intent i = new Intent();
                    i.putExtra("wifiName",ed_wifi_name.getText().toString());
                    UbtLog.d(TAG,"ed_wifi_name===="+ed_wifi_name.getText().toString());
                    i.setClass(BluetoothandnetconnectstateActivity.this,NetSearchResultActivity.class);
                    this.startActivity(i);

                }else {
                    UbtLog.d(TAG,"请先连接蓝牙");
                    ToastUtils.showShort("请先连接机器人");
                }
                break;

            case R.id.rl_content_device_list:
                rl_devices_list.setVisibility(View.VISIBLE);
                rl_content_device_list.setVisibility(View.INVISIBLE);
                mHelper.cancelScan();
                break;
            case R.id.rl_devices_list:
                UbtLog.d(TAG,"rl_devices_list 点击");
                break;
            case R.id.no_buletooth_devices:
                UbtLog.d(TAG,"no_buletooth_devices 点击");
                break;

            default:

        }
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
        UbtLog.d(TAG,"网络连接状态 onCoonected =  " + state);
        if (state) {
            rl_content_device_list.setVisibility(View.GONE);
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_sucess));
            if(mCurrentRobotInfo != null){
                UbtLog.d(TAG,"mCurrentRobotInfo != null  ");
                String name = (String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name);
                if(name == null ||name.equals("")){
                    name = (String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_mac);
                }
                ed_bluetooth_name.setText(name);
                ed_wifi_name.requestFocus();
                if(!mHelper.isLostCoon()){
                    mHelper.readNetworkStatus();
                }

            }
        }else {
            rl_content_device_list.setVisibility(View.GONE);
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.buletooth_con_fail));
            ed_bluetooth_name.setText("");

            ig_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_abnomal));
            ed_wifi_name.setText("");
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

    }

    @Override
    public void onGotoPCUpdate() {

    }

}

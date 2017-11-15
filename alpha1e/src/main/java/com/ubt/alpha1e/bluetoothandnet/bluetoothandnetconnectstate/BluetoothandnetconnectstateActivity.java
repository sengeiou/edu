package com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate;


import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netsearchresult.NetSearchResultActivity;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.ui.helper.IMainUI;
import com.ubt.alpha1e.ui.helper.IScanUI;
import com.ubt.alpha1e.ui.helper.ScanHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

public class BluetoothandnetconnectstateActivity extends MVPBaseActivity<BluetoothandnetconnectstateContract.View, BluetoothandnetconnectstatePresenter> implements BluetoothandnetconnectstateContract.View , View.OnClickListener,IScanUI , IMainUI {

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

    private BaseQuickAdapter mdevicesAdapter;
    private List<Map<String, Object>> lst_robots_result_datas;

    private BluetoothHelper mHelper;
    private DecimalFormat df = new DecimalFormat("0.##");
    private Map<String, Object> mCurrentRobotInfo = null;

    private boolean isConnecting = false;


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
//                    dismissDialog();
//                    Toast.makeText(RobotInfoActivity.this,getStringResources("ui_common_network_request_failed"),Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DO_NO_NEW_VERSION:
//                    dismissDialog();
//                    Toast.makeText(RobotInfoActivity.this,getStringResources("ui_upgrade_already_latest"),Toast.LENGTH_SHORT).show();
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
                    ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_fail));
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

        buletooth_device_list = (RecyclerView) findViewById(R.id.buletooth_device_list);
        buletooth_device_list.setLayoutManager(new LinearLayoutManager(this));

        mdevicesAdapter = new BluetoothandnetconnectstateActivity.BluetoothDeviceListAdapter(R.layout.bluetooth_device_list_layout, lst_robots_result_datas);
        mdevicesAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Map<String, Object> modle = (Map<String, Object>)adapter.getItem(position);
                if((boolean)modle.get(ScanHelper.map_val_robot_connect_state)){
                    UbtLog.d(TAG,"蓝牙正在连接点击按钮无效");
                    return;
                }
                isConnecting = true;

                mHelper.doCancelCoon();
                ((AlphaApplication) getApplicationContext()).cleanBluetoothConnectData();

                tv_devices_num.setText("蓝牙连接中");
                mCurrentRobotInfo = lst_robots_result_datas.get(position);
                mHelper.doReCoonect(mCurrentRobotInfo);

                modle.put(ScanHelper.map_val_robot_connect_state,true);
                lst_robots_result_datas.clear();
                lst_robots_result_datas.add(modle);
                buletooth_device_list.setAdapter(mdevicesAdapter);

            }
        });

        buletooth_device_list.setAdapter(mdevicesAdapter);
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

    private void dealScanResult(BluetoothDevice bluetoothDevice,short rssi){

        if(bluetoothDevice != null){
            UbtLog.d(TAG,"搜索到蓝牙地址 ："+bluetoothDevice.getAddress());
        }

        if(!(bluetoothDevice.getName().toLowerCase().contains("alpha"))){
            return;
        }

        Map<String,Object> receiveRobot = null;
        boolean isNew = true;
        for(Map robot : lst_robots_result_datas){
            if(robot.get(ScanHelper.map_val_robot_mac).equals(bluetoothDevice.getAddress()) ){
                UbtLog.d(TAG,"mac : " + bluetoothDevice.getAddress() + "    isNew = " + isNew + "   rssi = " + rssi + " df = " + df.format(getDistance(rssi)));
                robot.put(ScanHelper.map_val_robot_name, /*-rssi + "_" +*/ AlphaApplicationValues.dealBluetoothName(bluetoothDevice.getName()) );
                robot.put(ScanHelper.map_val_robot_mac, bluetoothDevice.getAddress());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                isNew = false;
                receiveRobot = robot;
                break;
            }
        }

        if(isNew){
            UbtLog.d(TAG,"mac : " + bluetoothDevice.getAddress() + "    isNew = " + isNew + "   rssi = " + rssi + " df = " + df.format(getDistance(rssi)));
            Map robot = new HashMap<>();
            robot.put(ScanHelper.map_val_robot_name,/*-rssi + " _ " +*/ AlphaApplicationValues.dealBluetoothName(bluetoothDevice.getName()) );
            robot.put(ScanHelper.map_val_robot_mac, bluetoothDevice.getAddress());
            robot.put(ScanHelper.map_val_robot_connect_state, false);
            if(!isConnecting){
                lst_robots_result_datas.add(robot);
            }
            receiveRobot = robot;
        }

        if(lst_robots_result_datas.size() > 0  && !isConnecting){
//            tvDeviceNum.setVisibility(View.VISIBLE);
            tv_devices_num.setText("发现 "+lst_robots_result_datas.size()+" 台机器人");

            mdevicesAdapter.notifyDataSetChanged();
//            setListViewHeightBasedOnChildren(lst_robots_result);
        }else{
            tv_devices_num.setText("正在搜索机器人......");
        }

        if(getDistance(rssi) < 0.8 && !isConnecting){

            mCurrentRobotInfo = receiveRobot;
            Map<String, Object> modle = mCurrentRobotInfo ;
            isConnecting = true;
            mHelper.doCancelCoon();
            ((AlphaApplication) getApplicationContext()).cleanBluetoothConnectData();
            tv_devices_num.setText("蓝牙连接中");
            modle.put(ScanHelper.map_val_robot_connect_state,true);
            lst_robots_result_datas.clear();
            lst_robots_result_datas.add(modle);
            buletooth_device_list.setAdapter(mdevicesAdapter);

            mHelper.doReCoonect(mCurrentRobotInfo);

        }
    }

    /**
     * 更加rssi信号转换成距离
     * d=10^((ABS(RSSI)-A)/(10*n))、A 代表在距离一米时的信号强度(45 ~ 49), n 代表环境对信号的衰减系数(3.25 ~ 4.5)
     * @param rssi
     * @return
     */
    public float getDistance(short rssi) {
        //return (float) Math.pow(10, (Math.abs(rssi) - 45) / (10 * 3.25));

        float A_Value = 49;
        float n_Value = 3.5f;
        int iRssi = Math.abs(rssi);
        float power = (iRssi-A_Value)/(10*n_Value);
        return (float) Math.pow(10,power);
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

//    /**
//     * 监听Eventbus消息方法
//     * @param event
//     */
    @Subscribe
    public void onEventNetwork(NetworkEvent event) {
        if(event.getEvent() == NetworkEvent.Event.CHANGE_SELECT_WIFI){
//            ed_wifi_name.setText(event.getSelectWifiName());
            UbtLog.d(TAG,"选择的wifi ："+event.getSelectWifiName());

        }
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
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_fail));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_return:
//                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ib_close:
                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ig_get_bluetooth_list:
                UbtLog.d(TAG,"获取蓝牙列表");
                Intent intent = new Intent();
                intent.putExtra("isFirst","no");
                intent.setClass(BluetoothandnetconnectstateActivity.this,BluetoothconnectActivity.class);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.activity_open_up_down,R.anim.activity_close_down_up);

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
                    ToastUtils.showShort("请先连接蓝牙");
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
            ig_bluetooth.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_connect_fail));
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

    @Override
    public void noteCharging() {

    }

    @Override
    public void updateBattery(int power) {

    }

    @Override
    public void noteDiscoonected() {

    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void onNoteVol(int mCurrentVol) {

    }

    @Override
    public void onNoteVolState(boolean mCurrentVolState) {

    }

    public class BluetoothDeviceListAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

        public BluetoothDeviceListAdapter(@LayoutRes int layoutResId, @Nullable List<Map<String, Object>> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Map<String, Object> item) {
            helper.addOnClickListener(R.id.btn_buletooth_connect);
            helper.setText(R.id.tv_robot_bluetooth_name, (String)item.get(ScanHelper.map_val_robot_name));
            if ((boolean)item.get(ScanHelper.map_val_robot_connect_state)) {
                if(item.get("isConnect") == null){
                    helper.setText(R.id.btn_buletooth_connect, "连接中");
                }else {
                    helper.setText(R.id.btn_buletooth_connect, "已连接");
                }
                helper.setBackgroundRes(R.id.btn_buletooth_connect,R.drawable.action_button_disable);
            } else {
                helper.setText(R.id.btn_buletooth_connect, "连接");

            }
        }
    }
}

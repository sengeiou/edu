package com.ubt.alpha1e.bluetoothandnet.bluetoothconnect;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.bluetoothandnet.BluetoothHelp;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.ui.helper.IMainUI;
import com.ubt.alpha1e.ui.helper.IScanUI;
import com.ubt.alpha1e.ui.helper.ScanHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;


/**
 * @author: dicy.cheng
 * @description:  蓝牙搜索
 * @create: 2017/11/2
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothconnectActivity extends MVPBaseActivity<BluetoothconnectContract.View, BluetoothconnectPresenter> implements BluetoothconnectContract.View, View.OnClickListener,IScanUI, IMainUI {

    String TAG = "BluetoothconnectActivity";

    @BindView(R.id.ib_close)
    ImageButton ib_close;

    @BindView(R.id.ib_help)
    ImageButton ib_help;

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.rl_search)
    RelativeLayout rl_search;

    @BindView(R.id.buletooth_device_list)
    RecyclerView buletooth_device_list;

    @BindView(R.id.rl_devices_list)
    RelativeLayout rl_devices_list;

    @BindView(R.id.btn_goto_connect)
    Button btn_goto_connect;

    @BindView(R.id.tv_devices_num)
    TextView tv_devices_num;

    //蓝牙连接成功
    @BindView(R.id.rl_content_bluetooth_connect_sucess)
    RelativeLayout rl_content_bluetooth_connect_sucess;

    //搜索得到的蓝牙列表
    @BindView(R.id.rl_content_device_list)
    RelativeLayout rl_content_device_list;

    //点击搜索蓝牙
    @BindView(R.id.rl_content_search)
    RelativeLayout rl_content_search;

    //蓝牙连接失败
    @BindView(R.id.rl_content_bluetooth_connect_fail)
    RelativeLayout rl_content_bluetooth_connect_fail;

    @BindView(R.id.rl_research)
    RelativeLayout rl_research;

    //30s没有搜索到设备
    @BindView(R.id.rl_content_no_device)
    RelativeLayout rl_content_no_device;

    //搜索中
    @BindView(R.id.rl_content_device_researching)
    RelativeLayout rl_content_device_researching;
    //连接中
    @BindView(R.id.rl_content_bluetooth_connecting)
    RelativeLayout rl_content_bluetooth_connecting;

    @BindView(R.id.rl_devices_no_device)
    RelativeLayout rl_devices_no_device;


    private BaseQuickAdapter mdevicesAdapter;

    private BluetoothHelper mHelper;
    private List<Map<String, Object>> lst_robots_result_datas;
    private DecimalFormat df = new DecimalFormat("0.##");
    private Map<String, Object> mCurrentRobotInfo = null;

    private boolean isConnecting = false;
    boolean isFirst = false ; //是否是首次打开
    private boolean fromBlockRequest = false; //从Blockly跳转到蓝牙连接


    public Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst_robots_result_datas = new ArrayList<>();
        initUI();

        Intent i =getIntent();
        String first = i.getStringExtra("isFirst");
        if(first != null&& first.equals("yes")){
            isFirst = true ;
        }else {
            ib_return.setVisibility(View.VISIBLE);
        }
        fromBlockRequest = getIntent().getBooleanExtra(Constant.BLUETOOTH_REQUEST, false);
        AutoScanConnectService.doEntryManalConnect(true);
        mHelper = new BluetoothHelper(BluetoothconnectActivity.this, BluetoothconnectActivity.this);

        if(task != null){
            task.cancel();
            task = null;
        }
        task = new MyTimerTask();
        if(timer!= null){
            timer.cancel();
            timer = null ;
        }
        timer = new Timer();
        timer.schedule(task, 30 * 1000);
        mHelper.RegisterHelper();
        mHelper.doNearFieldScan();
        UbtLog.d(TAG, "onCreate finish");
    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetoothconnect;
    }

    @Override
    protected void onResume() {
        initControlListener();
        AutoScanConnectService.doEntryManalConnect(true);
        super.onResume();
    }

    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHelper != null) {
            UbtLog.d(TAG, "--onPause--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.UnRegisterHelper();
        }
        AutoScanConnectService.doEntryManalConnect(false);
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG,"---onDestroy----");
        if(mHandler != null){
            mHandler.removeCallbacks(overTimeDo);
        }
        EventBus.getDefault().unregister(this);
        if(mHelper != null){
            mHelper.cancelScan();
        }
        if(task != null){
            task.cancel();
            task = null;
        }
        if(timer!= null){
            timer.cancel();
            timer = null ;
        }

        try {
            this.mHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    MyTimerTask task = new MyTimerTask();

    //超时处理
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            UbtLog.d(TAG, "30S 时间到");
            if (lst_robots_result_datas.size() == 0) {
                UbtLog.d(TAG, "没有搜索到蓝牙!");
                if(mHelper != null){
                    mHelper.cancelScan();
                }
                if(isConnecting){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                            rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
                            rl_content_device_list.setVisibility(View.INVISIBLE);
                            rl_content_no_device.setVisibility(View.VISIBLE);
                            rl_content_device_researching.setVisibility(View.INVISIBLE);
                            rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    @Subscribe
    public void onEventRobot(RobotEvent event) {

        if( event != null && event.getEvent() == RobotEvent.Event.SCAN_ROBOT){
            UbtLog.d(TAG,"收到蓝牙设备");
            if(isConnecting){
                return;
            }
            dealScanResult(event.getBluetoothDevice(),event.getRssi());
        }
    }

    boolean isSearchDevice = true ;

    synchronized  void dealScanResult(BluetoothDevice bluetoothDevice,short rssi){
        if(isConnecting){
            return;
        }
        if(bluetoothDevice != null){
           UbtLog.d(TAG,"搜索到蓝牙地址 ："+bluetoothDevice.getAddress());
        }

        if(!(bluetoothDevice.getName().toLowerCase().contains("alpha1e"))){
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

        if( !isConnecting && lst_robots_result_datas.size()> 0){
            if(isSearchDevice){
                if(task != null){
                    task.cancel();
                    task = null;
                }
                if(timer!= null){
                    timer.cancel();
                    timer = null ;
                }
                isSearchDevice = false ;
                try {
                    rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                    rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
                    rl_content_device_list.setVisibility(View.VISIBLE);
                    rl_content_no_device.setVisibility(View.INVISIBLE);
                    rl_content_device_researching.setVisibility(View.INVISIBLE);
                    rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }


            }
            if(tv_devices_num == null){
                return;
            }
            tv_devices_num.setText("发现 "+lst_robots_result_datas.size()+" 台机器人");

            mdevicesAdapter.notifyDataSetChanged();
        }else{
            if(tv_devices_num == null){
                return;
            }
            tv_devices_num.setText("正在搜索机器人......");
        }

        if(!isConnecting && getDistance(rssi) < 0.8 ){
            if(tv_devices_num == null){
                return;
            }
            if (((AlphaApplication) BluetoothconnectActivity.this.getApplicationContext())
                    .getCurrentBluetooth() != null) {
                UbtLog.d(TAG, "-蓝牙已经连上，则不使用自动连接--");
                return ;
            }

            tv_devices_num.setText("进行自动连接蓝牙......");
            UbtLog.d(TAG,"距离近 进行自动连接");
            mCurrentRobotInfo = receiveRobot;
            isConnecting = true;
            if(mHandler != null){
                mHandler.removeCallbacks(overTimeDo);
            }
            mHelper.doCancelCoon();
            ((AlphaApplication) getApplicationContext()).cleanBluetoothConnectData();
            lst_robots_result_datas.clear();
            if(mHandler != null){
                mHandler.postDelayed(overTimeDo,30000);
            }
            mHelper.doReCoonect(mCurrentRobotInfo);
            try {
                rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
                rl_content_device_list.setVisibility(View.INVISIBLE);
                rl_content_no_device.setVisibility(View.INVISIBLE);
                rl_content_device_researching.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connecting.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    }

    Runnable overTimeDo = new Runnable() {
        @Override
        public void run() {
//            ToastUtils.showShort("蓝牙连接失败");
            UbtLog.d(TAG,"蓝牙连接超时");
            isConnecting = false;
            if(mHelper != null){
                mHelper.doCancelCoon();
            }
            try {
                rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connect_fail.setVisibility(View.VISIBLE);
                rl_content_device_list.setVisibility(View.INVISIBLE);
                rl_content_no_device.setVisibility(View.INVISIBLE);
                rl_content_device_researching.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    };

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
    protected void initUI() {
        ib_close.setOnClickListener(this);
        ib_help.setOnClickListener(this);
        ib_return.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        btn_goto_connect.setOnClickListener(this);
        rl_research.setOnClickListener(this);
        rl_content_no_device.setOnClickListener(this);
        rl_devices_no_device.setOnClickListener(this);

        buletooth_device_list = (RecyclerView) findViewById(R.id.buletooth_device_list);
        buletooth_device_list.setLayoutManager(new LinearLayoutManager(this));

        mdevicesAdapter = new BluetoothconnectActivity.BluetoothDeviceListAdapter(R.layout.bluetooth_device_list_layout, lst_robots_result_datas);
        mdevicesAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Map<String, Object> modle = (Map<String, Object>)adapter.getItem(position);
                if((boolean)modle.get(ScanHelper.map_val_robot_connect_state)){
                    UbtLog.d(TAG,"蓝牙正在连接点击按钮无效");
                    return;
                }
                if(isConnecting){
                    return;
                }
                isConnecting = true;
                if(mHandler != null){
                    mHandler.removeCallbacks(overTimeDo);
                }
                mHelper.doCancelCoon();
                ((AlphaApplication) getApplicationContext()).cleanBluetoothConnectData();
                mCurrentRobotInfo = lst_robots_result_datas.get(position);
                if(mHandler != null){
                    mHandler.postDelayed(overTimeDo,30000);
                }
                mHelper.doReCoonect(mCurrentRobotInfo);
                lst_robots_result_datas.clear();
                try {
                    rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                    rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
                    rl_content_device_list.setVisibility(View.INVISIBLE);
                    rl_content_no_device.setVisibility(View.INVISIBLE);
                    rl_content_device_researching.setVisibility(View.INVISIBLE);
                    rl_content_bluetooth_connecting.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }
        });
        buletooth_device_list.setAdapter(mdevicesAdapter);
    }

    @Override
    protected void initControlListener() {


    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                BluetoothconnectActivity.this.setResult(RESULT_OK);
                BluetoothconnectActivity.this.finish();
                break;
            case R.id.ib_return:
                BluetoothconnectActivity.this.finish();
                break;
            case R.id.ib_help:
                UbtLog.d(TAG,"ib_help click");
                Intent help = new Intent(BluetoothconnectActivity.this, BluetoothHelp.class);
                startActivity(help);
                break;
            case R.id.rl_search:

                break;
            case R.id.btn_goto_connect:
                Intent i = new Intent();
                i.setClass(BluetoothconnectActivity.this, NetconnectActivity.class);
                BluetoothconnectActivity.this.startActivity(i);
                BluetoothconnectActivity.this.finish();
                break;
            case R.id.rl_research:
                researchBluetooth();
                break;
            case R.id.rl_devices_no_device:
                researchBluetooth();
                break;

            default:

        }
    }

    //重新搜索蓝牙设备
    void researchBluetooth(){
        isConnecting = false;
        isSearchDevice = true ;
        lst_robots_result_datas.clear();
        if(tv_devices_num == null){
            return;
        }
        tv_devices_num.setText("正在搜索机器人......");
        buletooth_device_list.setAdapter(mdevicesAdapter);

        try {
            rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
            rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
            rl_content_device_list.setVisibility(View.INVISIBLE);
            rl_content_no_device.setVisibility(View.INVISIBLE);
            rl_content_device_researching.setVisibility(View.VISIBLE);
            rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        if(mHelper != null ){
            mHelper.doNearFieldScan();
        }
        if(task != null){
            task.cancel();
            task = null;
        }
        task = new MyTimerTask();
        if(timer!= null){
            timer.cancel();
            timer = null ;
        }
        timer = new Timer();
        timer.schedule(task, 30*1000);
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
        if(mHandler != null){
            mHandler.removeCallbacks(overTimeDo);
        }
        if (state) {
            if(isFirst){
                try {
                    rl_content_bluetooth_connect_sucess.setVisibility(View.VISIBLE);
                    rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
                    rl_content_device_list.setVisibility(View.INVISIBLE);
                    rl_content_no_device.setVisibility(View.INVISIBLE);
                    rl_content_device_researching.setVisibility(View.INVISIBLE);
                    rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                if(fromBlockRequest){
                    setResult(RESULT_OK);
                }
                 BluetoothconnectActivity.this.finish();
            }

        }else {
            try {
                rl_content_bluetooth_connect_sucess.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connect_fail.setVisibility(View.VISIBLE);
                rl_content_device_list.setVisibility(View.INVISIBLE);
                rl_content_no_device.setVisibility(View.INVISIBLE);
                rl_content_device_researching.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connecting.setVisibility(View.INVISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
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
                helper.setText(R.id.btn_buletooth_connect, "连接中");
                helper.setBackgroundRes(R.id.btn_buletooth_connect,R.drawable.action_button_disable);
            } else {
                helper.setText(R.id.btn_buletooth_connect, "连接");

            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(isFirst){
            //关闭窗体动画显示
            this.overridePendingTransition(0,R.anim.activity_close_down_up);
        }
    }
}

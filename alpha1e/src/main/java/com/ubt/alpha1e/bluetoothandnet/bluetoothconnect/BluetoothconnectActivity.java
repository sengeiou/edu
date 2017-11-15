package com.ubt.alpha1e.bluetoothandnet.bluetoothconnect;


import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
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

//    @BindView(R.id.ig_bg)
//    GifImageView gif_scan_logo;
    @BindView(R.id.ig_phone)
    ImageView ig_phone;


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

    @BindView(R.id.rl_devices_no_device)
    RelativeLayout rl_devices_no_device;


    private BaseQuickAdapter mdevicesAdapter;
    private List<BluetoothDeviceModel> mBluetoothDeviceModels = new ArrayList<>();
    private Handler handler = new Handler();


    private BluetoothHelper mHelper;
    private List<Map<String, Object>> lst_robots_result_datas;
    private DecimalFormat df = new DecimalFormat("0.##");
    private Map<String, Object> mCurrentRobotInfo = null;
    private int mBlueConnectNum = 0;

    private static final int START_CONNECT_BLUETOOTH = 100;
    private static final int CONNECT_BLUETOOTH_TIME = 101;
    private static final int CONNECT_BLUETOOTH_FINISH = 102;
    private int mConnectTime = 0;

    private boolean isConnecting = false;

    public Timer timer = new Timer();

    //定义Handler处理对象
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                default:
                    break;

            }
        }
    };


    boolean isFirst = false ; //是否是首次打开
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst_robots_result_datas = new ArrayList<>();
        initUI();
//        BluetoothAdapter mBtAdapter;
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (!mBtAdapter.isEnabled()) {
//            boolean s = mBtAdapter.enable();
//            if(s){
//                UbtLog.d(TAG, "s true");
//            }else {
//                UbtLog.d(TAG, "s false");
//            }
////            ((Activity)mContext).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1333);
//        }else {
//            UbtLog.d(TAG, "bluetooth enable");
//        }

        Intent i =getIntent();
        String first = i.getStringExtra("isFirst");
        if(first != null&& first.equals("yes")){
            isFirst = true ;
        }
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
    protected void onResume() {
        initControlListener();
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
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG,"---onDestroy----");
        handler.removeCallbacks(runnable);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(rl_content_device_researching!= null && rl_content_no_device != null){
                            rl_content_device_researching.setVisibility(View.INVISIBLE);
                            rl_content_no_device.setVisibility(View.VISIBLE);
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
//            if(rlConnecting.getVisibility() != View.VISIBLE){
                dealScanResult(event.getBluetoothDevice(),event.getRssi());
//            }
        }
    }

    boolean isSearchDevice = true ;

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

        if(lst_robots_result_datas.size() > 0 && !isConnecting){
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
                rl_content_device_researching.setVisibility(View.INVISIBLE);
                rl_content_device_list.setVisibility(View.VISIBLE);
                rl_devices_list.setVisibility(View.INVISIBLE);
                handler.postDelayed(runnable,200);
            }
            tv_devices_num.setText("发现 "+lst_robots_result_datas.size()+" 台机器人");

            mdevicesAdapter.notifyDataSetChanged();
        }else{
            tv_devices_num.setText("正在搜索机器人......");
        }

        if(getDistance(rssi) < 0.8 && !isConnecting){
            tv_devices_num.setText("进行自动连接蓝牙......");

            mCurrentRobotInfo = receiveRobot;
//            tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
            mBlueConnectNum = 1;

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
//            mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(rl_devices_list !=null && ig_phone != null ){
                translateAnimRun(rl_devices_list);
                imageviewTranslateAnimRun(ig_phone);
            }
        }
    };

    public String getStringRes(String str)
    {
        return this.getStringResources(str);
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
                isConnecting = true;
                mHelper.doCancelCoon();
                ((AlphaApplication) getApplicationContext()).cleanBluetoothConnectData();
                tv_devices_num.setText("蓝牙连接中");
                mCurrentRobotInfo = lst_robots_result_datas.get(position);
                mBlueConnectNum = 1;
                mHelper.doReCoonect(mCurrentRobotInfo);

                modle.put(ScanHelper.map_val_robot_connect_state,true);
                lst_robots_result_datas.clear();
                lst_robots_result_datas.add(modle);
                buletooth_device_list.setAdapter(mdevicesAdapter);
            }
        });
        buletooth_device_list.setAdapter(mdevicesAdapter);
        rl_devices_list.setVisibility(View.INVISIBLE);
    }

    public void translateAnimRun(View view)
    {
        if(rl_devices_list == null){
            return;
        }
        rl_devices_list.setVisibility(View.VISIBLE);
        float curTranslationY = view.getTranslationY();
        float curTranslationX = view.getTranslationX();
        UbtLog.d(TAG,"curTranslationY="+curTranslationY);
        UbtLog.d(TAG,"curTranslationX="+curTranslationX);
        UbtLog.d(TAG,"curhigh="+view.getHeight());
        UbtLog.d(TAG,"curwidth="+view.getWidth());
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0);
        animator.setDuration(500);
        animator.start();

    }

    //图片平移
    public void imageviewTranslateAnimRun(View view)
    {
        ObjectAnimator valueAnimator;
        valueAnimator = ObjectAnimator.ofFloat(view, "translationX",0.0f,100.0f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(8800);
        valueAnimator.start();
    }



    @Override
    protected void initControlListener() {


    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetoothconnect;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                BluetoothconnectActivity.this.finish();
                break;
            case R.id.ib_return:
                BluetoothconnectActivity.this.finish();
                break;
            case R.id.ib_help:
                UbtLog.d(TAG,"ib_help click");
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
        tv_devices_num.setText("正在搜索机器人......");
        buletooth_device_list.setAdapter(mdevicesAdapter);
        rl_content_device_list.setVisibility(View.INVISIBLE);
        rl_devices_list.setVisibility(View.INVISIBLE);
        rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
        rl_content_device_researching.setVisibility(View.VISIBLE);
        rl_content_no_device.setVisibility(View.INVISIBLE);
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
        if (state) {
            if(isFirst){
                rl_content_device_list.setVisibility(View.INVISIBLE);
                rl_content_bluetooth_connect_sucess.setVisibility(View.VISIBLE);
                rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
            }else {
                 BluetoothconnectActivity.this.finish();

            }
        }else {
            rl_content_device_list.setVisibility(View.INVISIBLE);
            rl_content_bluetooth_connect_fail.setVisibility(View.VISIBLE);
            rl_content_bluetooth_connect_fail.setVisibility(View.INVISIBLE);
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

//    @Override
//    public void finish() {
//        super.finish();
//
//        //关闭窗体动画显示
//        this.overridePendingTransition(0,R.anim.activity_close_down_up);
//    }
}

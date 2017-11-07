package com.ubt.alpha1e.bluetoothandnet.bluetoothconnect;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.RobotInfoActivity;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.fragment.ScanFragment;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.ui.helper.IMainUI;
import com.ubt.alpha1e.ui.helper.IScanUI;
import com.ubt.alpha1e.ui.helper.ScanHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.AlphaInfo;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import pl.droidsonroids.gif.GifImageView;


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

    @BindView(R.id.ig_bg)
    GifImageView gif_scan_logo;

    @BindView(R.id.btn_goto_connect)
    Button btn_goto_connect;

    @BindView(R.id.tv_devices_num)
    TextView tv_devices_num;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst_robots_result_datas = new ArrayList<>();
        initUI();
        AutoScanConnectService.doEntryManalConnect(true);
        mHelper = new BluetoothHelper(BluetoothconnectActivity.this, BluetoothconnectActivity.this);

        mHelper.RegisterHelper();
        mHelper.doNearFieldScan();
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
        EventBus.getDefault().unregister(this);
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.cancel();
        }
//        if(mHandler.hasMessages(CONNECT_BLUETOOTH_TIME)){
//            mHandler.removeMessages(CONNECT_BLUETOOTH_TIME);
//        }
        try {
            this.mHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {

        if(event.getEvent() == RobotEvent.Event.SCAN_ROBOT){
            UbtLog.d(TAG,"收到蓝牙设备");
//            if(rlConnecting.getVisibility() != View.VISIBLE){
                dealScanResult(event.getBluetoothDevice(),event.getRssi());
//            }
        }
    }


    private void dealScanResult(BluetoothDevice bluetoothDevice,short rssi){

        if(bluetoothDevice != null){
           UbtLog.d(TAG,"搜索到蓝牙地址 ："+bluetoothDevice.getAddress());
        }

        if(!(bluetoothDevice.getName().toLowerCase().contains("alpha"))){
            return;
        }

//        llNoRobot.setVisibility(View.GONE);
//        gif_scan_logo.setVisibility(View.GONE);
//        lay_scan_start.setVisibility(View.VISIBLE);

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
            lst_robots_result_datas.add(robot);
            receiveRobot = robot;
        }

        if(lst_robots_result_datas.size() > 0){
//            tvDeviceNum.setVisibility(View.VISIBLE);
            tv_devices_num.setText("发现 "+lst_robots_result_datas.size()+" 台机器人");

            mdevicesAdapter.notifyDataSetChanged();
//            setListViewHeightBasedOnChildren(lst_robots_result);
        }else{
//            tvDeviceNum.setText("");
//            tvDeviceNum.setVisibility(View.INVISIBLE);
            tv_devices_num.setText("正在搜索机器人......");
        }

        if(getDistance(rssi) < 0.8 ){

//            llScan.setVisibility(View.GONE);
//            rlConnecting.setVisibility(View.VISIBLE);

            if (mCoonLoadingDia == null){
//                mCoonLoadingDia = LoadingDialog.getInstance(BluetoothconnectActivity.this,ScanFragment.this);
            }

//            mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
//                @Override
//                public void disableTask() {
//                    mHelper.doCancelCoon();
//                }
//            };
//            mCoonLoadingDia.setDoCancelable(true, 7);
            //mCoonLoadingDia.show();

//            mCurrentRobotInfo = receiveRobot;
//            tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
//            mBlueConnectNum = 1;
//            mHelper.doReCoonect(mCurrentRobotInfo);
//            mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);

        }
    }

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

                mHelper.doCancelCoon();

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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                translateAnimRun(rl_devices_list);
            }
        },1000);
    }

    public void translateAnimRun(View view)
    {
        rl_devices_list.setVisibility(View.VISIBLE);
        float curTranslationY = view.getTranslationY();
        float curTranslationX = view.getTranslationX();
        UbtLog.d(TAG,"curTranslationY="+curTranslationY);
        UbtLog.d(TAG,"curTranslationX="+curTranslationX);
        UbtLog.d(TAG,"curhigh="+view.getHeight());
        UbtLog.d(TAG,"curwidth="+view.getWidth());
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0);
        animator.setDuration(1000);
        animator.start();

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
                translateAnimRun(rl_devices_list);
                break;
            case R.id.rl_search:

                break;
            case R.id.btn_goto_connect:
                Intent i = new Intent();
                i.setClass(BluetoothconnectActivity.this, NetconnectActivity.class);
                BluetoothconnectActivity.this.startActivity(i);
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
        UbtLog.d(TAG,"onCoonected =- " + state);
//        if(!state && mBlueConnectNum < 2 && mHelper.getNextConnectState()){
//            mBlueConnectNum++;
//            mHelper.doReCoonect(mCurrentRobotInfo);
//            return;
//        }
//        UbtLog.d(TAG,"onCoonected == " + state);
//        if((ScanFragment.this.getActivity() == null) || (ScanFragment.this.getActivity()).isFinishing()){
//            return;
//        }
//        //UbtLog.d(TAG,"onCoonected => " + state);
//        mHandler.sendEmptyMessage(CONNECT_BLUETOOTH_FINISH);
//
//        dismissDialog();
//
        if (state) {
//            if(mComeConnectForBack){
//                Intent mIntent = new Intent();
//                getActivity().setResult(Activity.RESULT_OK, mIntent);
//                getActivity().finish();
//            }else{
//                RobotInfoActivity.launchActivity(getActivity(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                this.getActivity().finish();
//            }
//        } else {
//            llScan.setVisibility(View.VISIBLE);
//            rlConnecting.setVisibility(View.GONE);
//
//            ((BaseActivity) ScanFragment.this.getActivity()).showToast("ui_home_connect_failed");
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
}

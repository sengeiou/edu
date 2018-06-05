package com.ubt.alpha1e_edu.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.AlphaApplicationValues;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.event.RobotEvent;
import com.ubt.alpha1e_edu.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.IGestureUI;
import com.ubt.alpha1e_edu.ui.RobotInfoActivity;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.helper.IMainUI;
import com.ubt.alpha1e_edu.ui.helper.IScanUI;
import com.ubt.alpha1e_edu.ui.helper.MainHelper;
import com.ubt.alpha1e_edu.ui.helper.ScanHelper;
import com.ubt.alpha1e_edu.update.BluetoothUpdateManager;
import com.ubt.alpha1e_edu.update.EngineUpdateManager;
import com.ubt.alpha1e_edu.utils.log.MyLog;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubtechinc.base.AlphaInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

import static com.ubt.alpha1e_edu.R.id.txt_name;


public class ScanFragment extends BaseFragment implements IMainUI,
        IScanUI, BaseDiaUI, IGestureUI {

    private static final String TAG = "ScanFragment";
    private static MainHelper mMainHelper;
    private ScanHelper mHelper;
    private View mMainView;
    private com.ubt.alpha1e_edu.ui.fragment.IMainUI mCurrentActivity;

    private GifImageView gif_scan_logo;


    private ListView lst_robots_result;
    private SimpleAdapter lst_robots_result_adapter;
    private List<Map<String, Object>> lst_robots_result_datas;

    private ListView lst_robots_history;
    private SimpleAdapter lst_robots_history_adapter;
    private List<Map<String, Object>> lst_robots_history_datas;


    private RelativeLayout lay_scan_start;

    private LoadingDialog mCoonLoadingDia;

    private Map<String, Object> mCurrentRobotInfo = null;

    private boolean mComeConnectForBack = false;
    private int mBlueConnectNum = 0;

    private boolean mNoScanHasDisPlay = false;

    private RelativeLayout rlScan;
    private TextView tvDeviceNum;
    private LinearLayout llNoRobot;
    private ImageView ivReScan;
    private LinearLayout llScan;
    private RelativeLayout rlConnecting;
    private TextView tvConnecting;

    private DecimalFormat df = new DecimalFormat("0.##");

    private Date lastScanTime = null;

    private static final int START_CONNECT_BLUETOOTH = 100;
    private static final int CONNECT_BLUETOOTH_TIME = 101;
    private static final int CONNECT_BLUETOOTH_FINISH = 102;
    private int mConnectTime = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_CONNECT_BLUETOOTH:
                    mConnectTime = 0;
                    //mHandler.sendEmptyMessage(CONNECT_BLUETOOTH_TIME);
                    break;
                case CONNECT_BLUETOOTH_TIME:
                    UbtLog.d(TAG,"mConnectTime = " + mConnectTime++);
                    if(mConnectTime < 60){
                        //mHandler.sendEmptyMessageDelayed(CONNECT_BLUETOOTH_TIME,1000);
                    }
                    break;
                case CONNECT_BLUETOOTH_FINISH:
                    mConnectTime = 0;
                    if(mHandler.hasMessages(CONNECT_BLUETOOTH_TIME)){
                        //mHandler.removeMessages(CONNECT_BLUETOOTH_TIME);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("ValidFragment")
    public ScanFragment(MainHelper helper) {
        mMainHelper = helper;
    }

    public ScanFragment() {

    }

    public void setMainUI(com.ubt.alpha1e_edu.ui.fragment.IMainUI mUI) {
        mCurrentActivity = mUI;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0){
            return null;
        }
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(),nextAnim);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (mCurrentActivity != null){
                    mCurrentActivity.toOtherPageStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (mCurrentActivity != null){
                    mCurrentActivity.toOtherPageEnd();
                }
            }
        });

        return anim;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_scan, null);
        mHelper = new ScanHelper(this, (BaseActivity) this.getActivity());

        initUI();
        initControlListener();
        EventBus.getDefault().register(this);
        mNoScanHasDisPlay = false;
        return mMainView;
    }

    @Override
    public void onResume() {
        mHelper.RegisterHelper();
        if(mMainHelper!=null)
        {
            mMainHelper.doRegisterListenerUI(this);
            mMainHelper.doReadState();
            if(isBulueToothConnected()){
                lay_scan_start.setVisibility(View.VISIBLE);
            }
        }

        List<AlphaInfo> list = mHelper.getHistoryDevices();
        if ((list != null && list.size() != 0) || mNoScanHasDisPlay) {
            lst_robots_history_datas.clear();
            for (int i = 0; i < list.size(); i++) {
                Map robot = new HashMap<String, Object>();
                robot.put(ScanHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(list.get(i).getName()));
                robot.put(ScanHelper.map_val_robot_mac, list.get(i).getMacAddr());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                lst_robots_history_datas.add(robot);
            }
            lst_robots_history_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lst_robots_history);
        }else{
            mNoScanHasDisPlay = true;
            lay_scan_start.setVisibility(View.INVISIBLE);
        }

        mHelper.doNearFieldScan();
        super.onResume();
    }

    @Override
    protected void initUI() {
        tvDeviceNum = (TextView) mMainView.findViewById(R.id.tv_device_num);
        tvConnecting = (TextView) mMainView.findViewById(R.id.tv_connecting);
        UbtLog.d(TAG,"tvConnecting = " +tvConnecting);
        llNoRobot = (LinearLayout) mMainView.findViewById(R.id.ll_no_robot);
        llScan = (LinearLayout) mMainView.findViewById(R.id.ll_scan);
        rlConnecting = (RelativeLayout) mMainView.findViewById(R.id.rl_connecting);

        ivReScan = (ImageView) mMainView.findViewById(R.id.iv_re_scan);

        lay_scan_start = (RelativeLayout) mMainView.findViewById(R.id.lay_scan_start);

        gif_scan_logo = (GifImageView) mMainView.findViewById(R.id.gif_scan_logo);

        lst_robots_result = (ListView) mMainView.findViewById(R.id.lst_robots_result);
        lst_robots_result_datas = new ArrayList<>();
        int id = R.layout.layout_robot_item_record_with_using;

        lst_robots_result_adapter = new SimpleAdapter(this.getActivity(),
                lst_robots_result_datas, id, new String[]{
                ScanHelper.map_val_robot_name,
                ScanHelper.map_val_robot_mac}, new int[]{
                txt_name, R.id.txt_mac}) {

            @Override
            public View getView(final int position, View convertView,
                                ViewGroup parent) {
                View thiz = super.getView(position, convertView, parent);
                return thiz;
            }

        };
        lst_robots_result.setAdapter(lst_robots_result_adapter);

        lst_robots_history = (ListView) mMainView
                .findViewById(R.id.lst_robots_history);
        lst_robots_history_datas = new ArrayList<Map<String, Object>>();
        int id_us = R.layout.layout_robot_item_record_history_with_using;

        lst_robots_history_adapter = new SimpleAdapter(this.getActivity(),
                lst_robots_history_datas, id_us, new String[]{
                ScanHelper.map_val_robot_name,
                ScanHelper.map_val_robot_mac}, new int[]{
                txt_name, R.id.txt_mac});
        lst_robots_history.setAdapter(lst_robots_history_adapter);


        rlScan = (RelativeLayout) mMainView.findViewById(R.id.lay_scan);

    }


    @Subscribe
    public void onEventRobot(RobotEvent event) {

        if(event.getEvent() == RobotEvent.Event.SCAN_ROBOT){
            if(rlConnecting.getVisibility() != View.VISIBLE){
                dealScanResult(event.getBluetoothDevice(),event.getRssi());
            }
        }
    }

    private void dealScanResult(BluetoothDevice bluetoothDevice,short rssi){

        if(!(bluetoothDevice.getName().toLowerCase().contains("alpha"))){
            return;
        }

        llNoRobot.setVisibility(View.GONE);
        gif_scan_logo.setVisibility(View.GONE);
        lay_scan_start.setVisibility(View.VISIBLE);

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
            tvDeviceNum.setVisibility(View.VISIBLE);
            tvDeviceNum.setText(getStringRes("ui_scan_robot_count").replace("#", ""+lst_robots_result_datas.size()) + "," + getStringRes("ui_connect_approach_tips"));
            lst_robots_result_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lst_robots_result);
        }else{
            tvDeviceNum.setText("");
            tvDeviceNum.setVisibility(View.INVISIBLE);
        }

        if(getDistance(rssi) < 0.8 ){

            llScan.setVisibility(View.GONE);
            rlConnecting.setVisibility(View.VISIBLE);

            if (mCoonLoadingDia == null){
                mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanFragment.this);
            }

            mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                @Override
                public void disableTask() {
                    mHelper.doCancelCoon();
                }
            };
            mCoonLoadingDia.setDoCancelable(true, 7);
            //mCoonLoadingDia.show();

            mCurrentRobotInfo = receiveRobot;
            tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
            mBlueConnectNum = 1;
            mHelper.doReCoonect(mCurrentRobotInfo);
            mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);

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
    protected void initControlListener() {
        // TODO Auto-generated method stub

        lst_robots_result.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                llScan.setVisibility(View.GONE);
                rlConnecting.setVisibility(View.VISIBLE);

                if (mCoonLoadingDia == null){
                    mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanFragment.this);
                }

                mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                    @Override
                    public void disableTask() {
                        mHelper.doCancelCoon();
                    }
                };
                mCoonLoadingDia.setDoCancelable(true, 7);
                //mCoonLoadingDia.show();

                mCurrentRobotInfo = lst_robots_result_datas.get(position);
                tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
                mBlueConnectNum = 1;
                mHelper.doReCoonect(mCurrentRobotInfo);
                mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);
            }
        });

        ivReScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doReScan();
            }
        });


        lst_robots_history.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                mCurrentRobotInfo = lst_robots_history_datas.get(arg2);
                if(isBulueToothConnected()){
                    if(mCurrentRobotInfo.get(ScanHelper.map_val_robot_mac)
                            .equals(((AlphaApplication)getActivity().getApplicationContext()).getCurrentBluetooth().getAddress())){
                        UbtLog.d(TAG,"this devices has connected: " + mCurrentRobotInfo.get(ScanHelper.map_val_robot_mac));
                        return;
                    }
                }

                llScan.setVisibility(View.GONE);
                rlConnecting.setVisibility(View.VISIBLE);

                if (mCoonLoadingDia == null){
                    mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanFragment.this);
                }
                mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                    @Override
                    public void disableTask() {
                        mHelper.doCancelCoon();
                    }
                };
                mCoonLoadingDia.setDoCancelable(true, 7);
                //mCoonLoadingDia.show();

                tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
                mBlueConnectNum = 1;
                mHelper.doReCoonect(mCurrentRobotInfo);
                mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);
            }
        });


    }

    /**
     * 重新搜索
     */
    public void doReScan() {
        if(rlConnecting.getVisibility() == View.VISIBLE){
            //连接中
            mHelper.doCancelCoon();
            rlConnecting.setVisibility(View.GONE);
            llScan.setVisibility(View.VISIBLE);
        }

        lastScanTime = new Date(System.currentTimeMillis());
        lst_robots_result_datas.clear();
        llNoRobot.setVisibility(View.GONE);
        lay_scan_start.setVisibility(View.GONE);
        gif_scan_logo.setVisibility(View.VISIBLE);
        mHelper.doNearFieldScan();
    }

    /**
     * 返回按键
     */
    public void onBack(){
        if(rlConnecting.getVisibility() == View.VISIBLE){
            //连接中
            mHelper.doCancelCoon();
            rlConnecting.setVisibility(View.GONE);
            llScan.setVisibility(View.VISIBLE);
        }else {
            getActivity().finish();
        }

    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        try {
            this.mHelper.UnRegisterHelper();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mMainHelper.doUnRegisterListenerUI(this);

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG,"---onDestroy----");
        EventBus.getDefault().unregister(this);
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.cancel();
        }
        if(mHandler.hasMessages(CONNECT_BLUETOOTH_TIME)){
            mHandler.removeMessages(CONNECT_BLUETOOTH_TIME);
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        try {
            this.mHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public void noteCharging() {
        // TODO Auto-generated method stub

        // spring


    }

    @Override
    public void updateBattery(int power) {
    }

    @Override
    public void noteDiscoonected() {
        UbtLog.d(TAG,"isEngineUpdateRemainReboot:"+ EngineUpdateManager.isEngineUpdateRemainReboot + "     isBluetoothUpdateRemainReboot:"+ BluetoothUpdateManager.isBluetoothUpdateRemainReboot);
        if(EngineUpdateManager.isEngineUpdateRemainReboot || BluetoothUpdateManager.isBluetoothUpdateRemainReboot){
            return;
        }

        ((BaseActivity)this.getActivity()).onLostBtCoon();
        this.getActivity().finish();
    }

    @Override
    public void onReadHeadImgFinish(boolean is_success, Bitmap img) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetHistoryBindDevices(Set<BluetoothDevice> history_deivces) {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteIsScaning() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteBtTurnOn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteBtIsOff() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteScanResultInvalid() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetNewDevices(List<BluetoothDevice> devices) {

        //
        /*UbtLog.d(TAG, "onGetNewDevices:" + devices.size());
        if(devices.size() > 0){
            llNoRobot.setVisibility(View.GONE);
            gif_scan_logo.setVisibility(View.GONE);
            lay_scan_start.setVisibility(View.VISIBLE);
            lst_robots_result_datas.clear();

            for (int i = 0; i < devices.size(); i++) {
                Map robot = new HashMap<String, Object>();

                robot.put(ScanHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(devices.get(i).getName()));
                robot.put(ScanHelper.map_val_robot_mac, devices.get(i).getAddress());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                lst_robots_result_datas.add(robot);

            }
            tvDeviceNum.setVisibility(View.VISIBLE);
            tvDeviceNum.setText(getStringRes("ui_block_search_robot_num").replace("#", ""+devices.size()));
            lst_robots_result_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lst_robots_result);
        }else{
            tvDeviceNum.setText("");
            tvDeviceNum.setVisibility(View.INVISIBLE);
        }*/

    }

    //该方法用于动态设置listview的高度，以解决scrollview嵌套listview时的滑动冲突问题
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int count = listAdapter.getCount();
        //UbtLog.d(TAG,"getId = " + listView.getId());
        if(count > 1 && listView.getId() == R.id.lst_robots_history){
            //最多显示三个的高度
            count = 1;
        }
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (count - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onScanFinish() {

        //添加近场连接后，无需处理搜索完成逻辑
        /*long timeDifference = 1000;
        if(lastScanTime != null){
            Date currentData = new Date(System.currentTimeMillis());
            timeDifference = currentData.getTime() - lastScanTime.getTime();
        }
        //UbtLog.d(TAG, "onScanFinish timeDifference = " + timeDifference);
        if(timeDifference < 300){
            //主动重新搜索，停止的的返回，不予处理
            return;
        }*/

        /*if(lst_robots_result_datas.size() <= 0){
            gif_scan_logo.setVisibility(View.GONE);
            tvDeviceNum.setVisibility(View.INVISIBLE);
            llNoRobot.setVisibility(View.VISIBLE);
        }else{
            tvDeviceNum.setVisibility(View.VISIBLE);
            tvDeviceNum.setText(getStringRes("ui_scan_search_finish").replace("#", ""+lst_robots_result_datas.size()));
        }*/

    }

    @Override
    public void onCoonected(boolean state) {
        UbtLog.d(TAG,"onCoonected =- " + state);
        if(!state && mBlueConnectNum < 2 && mHelper.getNextConnectState()){
            mBlueConnectNum++;
            mHelper.doReCoonect(mCurrentRobotInfo);
            return;
        }
        UbtLog.d(TAG,"onCoonected == " + state);
        if((ScanFragment.this.getActivity() == null) || (ScanFragment.this.getActivity()).isFinishing()){
            return;
        }
        //UbtLog.d(TAG,"onCoonected => " + state);
        mHandler.sendEmptyMessage(CONNECT_BLUETOOTH_FINISH);

        dismissDialog();

        if (state) {
            if(mComeConnectForBack){
                Intent mIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, mIntent);
                getActivity().finish();
            }else{
                RobotInfoActivity.launchActivity(getActivity(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                this.getActivity().finish();
            }
        } else {
            llScan.setVisibility(View.VISIBLE);
            rlConnecting.setVisibility(View.GONE);

            ((BaseActivity) ScanFragment.this.getActivity()).showToast("ui_home_connect_failed");
        }

    }

    public void setComeConnectForBack(boolean comeConnectForBack){
        mComeConnectForBack = comeConnectForBack;
    }

    public void dismissDialog() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.cancel();
        }
    }

    @Override
    public void updateHistory() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteLightOn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteLightOff() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteUpdateBin() {
        MyLog.writeLog("升级测试", "ScanFragment.noteUpdateBin");
        dismissDialog();
    }

    @Override
    public void onNoteVol(int mCurrentVol) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNoteVolState(boolean mCurrentVolState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetNewDevicesParams(List<BluetoothDevice> mDevicesList,
                                      Map<BluetoothDevice, Integer> mDevicesRssiMap) {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteCheckBin(BaseWebRunnable runnable) {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()) {
            mCoonLoadingDia.mCurrentTask = runnable;
            mCoonLoadingDia.setDoCancelable(true);
            //mCoonLoadingDia.showMessage(((BaseActivity) ScanFragment.this.getActivity()).getStringResources("ui_robot_info_checking"));
        }
        tvConnecting.setText(((BaseActivity) ScanFragment.this.getActivity()).getStringResources("ui_robot_info_checking"));
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollLeft(View v) {

    }

    @Override
    public void onScrollRight(View v) {

    }

    @Override
    public void onClick(int view_index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetUserImg(boolean isSuccess, Bitmap img) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateBluetoothFinish(boolean is_success) {
        autoReconnectRobot(is_success);
    }

    @Override
    public void onUpdateEngineFinish(boolean is_success) {
        autoReconnectRobot(is_success);
    }

    @Override
    public void onGotoPCUpdate() {
        UbtLog.d(TAG,"-onGotoPCUpdate-");
        mMainHelper.doGotoPcUpdate(getActivity());
    }

    @Override
    public void onClick(Map<String, Object> item) {
        // TODO Auto-generated method stub

    }

    private void autoReconnectRobot(boolean is_success){
        if(is_success){
            if (mCoonLoadingDia == null){
                mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanFragment.this);
            }

            mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                @Override
                public void disableTask() {
                    mHelper.doCancelCoon();
                }
            };
            mCoonLoadingDia.setDoCancelable(true, 7);
            //mCoonLoadingDia.showMessage(((BaseActivity) ScanFragment.this.getActivity()).getStringResources("ui_home_connecting"));

            tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
            mBlueConnectNum = 1;
            mHelper.doReCoonect(mCurrentRobotInfo);
            mHandler.sendEmptyMessage(START_CONNECT_BLUETOOTH);
        }
    }

}

class Robot_OnTouchListener implements OnTouchListener {
    private View mView;
    private IGestureUI mUI;
    private int mView_index;
    private GestureDetector mDetector;

    public Robot_OnTouchListener(View view, IGestureUI ui) {
        this.mView = view;
        this.mUI = ui;
        this.mDetector = new GestureDetector(new Robots_OnGestureListener(mView,
                mUI, mView_index));

    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        return mDetector.onTouchEvent(arg1);
    }

}

class Robots_OnGestureListener implements OnGestureListener {
    private static Date lastTime = null;
    private View mView;
    private int mView_index;
    private IGestureUI mUI;

    public Robots_OnGestureListener(View view, IGestureUI ui, int index) {
        mView = view;
        mView_index = index;
        mUI = ui;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Date curDate = new Date(System.currentTimeMillis());
        float time_interval = 1000;
        if (lastTime != null) {
            time_interval = curDate.getTime() - lastTime.getTime();
        }
        if (time_interval > 500) {

            if (distanceX < -10) {
                // 左滑
                mUI.onScrollRight(mView);

            }
            if (distanceX > 10) {

                mUI.onScrollLeft(mView);

            }
        }
        lastTime = curDate;
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
        // 按下事件
        mUI.onClick(mView_index);
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return true;
    }

}

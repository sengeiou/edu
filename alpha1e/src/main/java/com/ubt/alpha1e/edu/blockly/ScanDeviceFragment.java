package com.ubt.alpha1e.edu.blockly;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplicationValues;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.fragment.BaseFragment;
import com.ubt.alpha1e.edu.ui.helper.IScanUI;
import com.ubt.alpha1e.edu.ui.helper.ScanHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.AlphaInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

import static com.ubt.alpha1e.edu.blockly.ScanBluetoothActivity.REUEST_CODE;

/**
 * @className ScanDeviceFragment
 *
 * @author wmma
 * @description Blockly device search fragment
 * @date 2017/04/05
 * @update
 */


public class ScanDeviceFragment extends BaseFragment implements IScanUI, BaseDiaUI{

    private String TAG = ScanDeviceFragment.class.getSimpleName();

    private GifImageView gifIvSearch;
    private GifImageView gifNearRobot;
    private TextView tvTips;
    private LinearLayout llNoRobot;
    private ImageView ivSearch;
    private RelativeLayout rlConnecting;
    private TextView tvConnecting;

    private RecyclerView mHistoryRecycleView;
    private List<Map<String, Object>> mHistoryDeviceList;
    private RobotRecyclerViewAdapter mHistoryRobotAdapter;

    private RecyclerView mResultRecycleView;
    private List<Map<String, Object>> mResultDeviceList;
    private RobotRecyclerViewAdapter mResultRobotAdapter;

    private ScanHelper mScanHelper;

    private LoadingDialog mCoonLoadingDia;
    private Map<String, Object> mCurrentRobotInfo = null; //点击连接的机器人信息
    private int mBlueConnectNum = 0;  //主动连接蓝牙的次数

    private DecimalFormat df = new DecimalFormat("0.##");

    private static final int SCAN_FINISH_CODE = 1000;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == SCAN_FINISH_CODE){
                /*UbtLog.d(TAG, "scan finish:" + mResultDeviceList.size());
                if(mResultDeviceList.size() <= 0){
                    gifIvSearch.setVisibility(View.GONE);
                    tvTips.setVisibility(View.INVISIBLE);
                    llNoRobot.setVisibility(View.VISIBLE);
                }else{
                    tvTips.setText(getStringRes("ui_scan_search_finish").replace("#", ""+mResultDeviceList.size()));
                }*/
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_device_histroy, null);

        mScanHelper = new ScanHelper(this, (BaseActivity) this.getActivity());

        initView(view);
        EventBus.getDefault().register(this);
        return  view;
    }

    private void initView(View view) {
        gifIvSearch = (GifImageView) view.findViewById(R.id.gif_search);
        gifNearRobot = (GifImageView) view.findViewById(R.id.gif_near_robot);
        tvTips = (TextView) view.findViewById(R.id.tv_tip);
        llNoRobot = (LinearLayout) view.findViewById(R.id.ll_no_robot);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        rlConnecting = (RelativeLayout) view.findViewById(R.id.rl_connecting);
        tvConnecting = (TextView) view.findViewById(R.id.tv_connecting);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doReScan();
            }
        });

        mHistoryRecycleView = (RecyclerView) view.findViewById(R.id.history_recycler_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHistoryRecycleView.setLayoutManager(layoutManager);

        mHistoryDeviceList = new ArrayList<Map<String, Object>>();
        mHistoryRobotAdapter = new RobotRecyclerViewAdapter(this.getActivity(), mHistoryDeviceList);
        mHistoryRecycleView.setAdapter(mHistoryRobotAdapter);
        mHistoryRobotAdapter.setOnItemClickListener(new RobotRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Map<String, Object> data) {
                mCurrentRobotInfo = data;
                if (mCoonLoadingDia == null){
                    mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanDeviceFragment.this);
                }

                ((LoadingDialog) mCoonLoadingDia).mCurrentTask = new BaseWebRunnable() {
                    @Override
                    public void disableTask() {
                        ((ScanHelper) mScanHelper).doCancelCoon();
                    }
                };
                ((LoadingDialog) mCoonLoadingDia).setDoCancelable(true, 7);
                mCoonLoadingDia.show();
                mBlueConnectNum = 1;
                mScanHelper.doReCoonect(mCurrentRobotInfo);
            }
        });

        mResultRecycleView = (RecyclerView) view.findViewById(R.id.result_recycler_list);
        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(getActivity());
        resultLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mResultRecycleView.setLayoutManager(resultLayoutManager);

        mResultDeviceList = new ArrayList<Map<String, Object>>();
        mResultRobotAdapter = new RobotRecyclerViewAdapter(getActivity(), mResultDeviceList);
        mResultRecycleView.setAdapter(mResultRobotAdapter);
        mResultRobotAdapter.setOnItemClickListener(new RobotRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Map<String, Object> data) {
                mCurrentRobotInfo = data;

                tvTips.setVisibility(View.GONE);
                mResultRecycleView.setVisibility(View.GONE);
                gifNearRobot.setVisibility(View.GONE);
                rlConnecting.setVisibility(View.VISIBLE);

                if (mCoonLoadingDia == null){
                    mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanDeviceFragment.this);
                }

                mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                    @Override
                    public void disableTask() {
                        mScanHelper.doCancelCoon();
                    }
                };
                mCoonLoadingDia.setDoCancelable(true, 7);
                //mCoonLoadingDia.show();

                tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
                mBlueConnectNum = 1;
                mScanHelper.doReCoonect(mCurrentRobotInfo);
            }
        });

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

        gifIvSearch.setVisibility(View.GONE);
        gifNearRobot.setVisibility(View.VISIBLE);
        mResultRecycleView.setVisibility(View.VISIBLE);

        Map<String,Object> receiveRobot = null;
        boolean isNew = true;
        for(Map robot : mResultDeviceList){
            if(robot.get(ScanHelper.map_val_robot_mac).equals(bluetoothDevice.getAddress()) ){
                UbtLog.d(TAG,"mac :: " + bluetoothDevice.getAddress() + "    isNew = " + isNew + "   rssi = " + rssi + " df = " + df.format(getDistance(rssi)));
                robot.put(ScanHelper.map_val_robot_name, /*-rssi + "_" +*/ AlphaApplicationValues.dealBluetoothName(bluetoothDevice.getName()) );
                robot.put(ScanHelper.map_val_robot_mac, bluetoothDevice.getAddress());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                isNew = false;
                receiveRobot = robot;
                break;
            }
        }

        if(isNew){
            Map robot = new HashMap<>();
            robot.put(ScanHelper.map_val_robot_name,/*-rssi + " _ " +*/ AlphaApplicationValues.dealBluetoothName(bluetoothDevice.getName()) );
            robot.put(ScanHelper.map_val_robot_mac, bluetoothDevice.getAddress());
            robot.put(ScanHelper.map_val_robot_connect_state, false);
            mResultDeviceList.add(robot);
            receiveRobot = robot;
        }

        if(mResultDeviceList.size() > 0){

            tvTips.setText(getStringRes("ui_scan_robot_count").replace("#", ""+mResultDeviceList.size())+ "," + getStringRes("ui_connect_approach_tips"));
            mResultRobotAdapter.notifyDataSetChanged();
        }

        if(getDistance(rssi) < 0.5 ){

            tvTips.setVisibility(View.GONE);
            mResultRecycleView.setVisibility(View.GONE);
            gifNearRobot.setVisibility(View.GONE);
            rlConnecting.setVisibility(View.VISIBLE);

            if (mCoonLoadingDia == null){
                mCoonLoadingDia = LoadingDialog.getInstance(getActivity(),ScanDeviceFragment.this);
            }

            mCoonLoadingDia.mCurrentTask = new BaseWebRunnable() {
                @Override
                public void disableTask() {
                    mScanHelper.doCancelCoon();
                }
            };
            mCoonLoadingDia.setDoCancelable(true, 7);
            //mCoonLoadingDia.show();

            mCurrentRobotInfo = receiveRobot;
            tvConnecting.setText(getStringRes("ui_scan_connecting").replaceAll("#",(String) mCurrentRobotInfo.get(ScanHelper.map_val_robot_name)));
            mBlueConnectNum = 1;
            mScanHelper.doReCoonect(mCurrentRobotInfo);

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
        float n_Value = 2.5f;
        int iRssi = Math.abs(rssi);
        float power = (iRssi-A_Value)/(10*n_Value);
        return (float) Math.pow(10,power);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {
    }


    @Override
    public void onResume() {
        mScanHelper.RegisterHelper();
        List<AlphaInfo> list = mScanHelper.getHistoryDevices();
        if ((list != null && list.size() != 0)) {
            mHistoryDeviceList.clear();
            for (int i = 0; i < list.size(); i++) {
                Map robot = new HashMap<String, Object>();
                robot.put(ScanHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(list.get(i).getName()));
                robot.put(ScanHelper.map_val_robot_mac, list.get(i).getMacAddr());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                mHistoryDeviceList.add(robot);
            }
            mHistoryRobotAdapter.notifyDataSetChanged();
        }

        //mScanHelper.doScan();
        mScanHelper.doNearFieldScan();

        super.onResume();
    }

    public void doReScan() {

        if(rlConnecting.getVisibility() == View.VISIBLE){
            //连接中
            mScanHelper.doCancelCoon();
            rlConnecting.setVisibility(View.GONE);

            tvTips.setVisibility(View.VISIBLE);
            mResultRecycleView.setVisibility(View.VISIBLE);
            gifNearRobot.setVisibility(View.VISIBLE);
        }

        mResultDeviceList.clear();
        llNoRobot.setVisibility(View.GONE);
        gifNearRobot.setVisibility(View.GONE);
        mResultRecycleView.setVisibility(View.GONE);

        gifIvSearch.setVisibility(View.VISIBLE);
        tvTips.setVisibility(View.VISIBLE);
        tvTips.setText(getStringRes("ui_scan_tip"));
        mHandler.removeMessages(SCAN_FINISH_CODE);

        //mScanHelper.doScan();
        mScanHelper.doNearFieldScan();
    }

    /**
     * 返回按键
     */
    public void onBack(){
        if(rlConnecting.getVisibility() == View.VISIBLE){
            //连接中
            mScanHelper.doCancelCoon();
            rlConnecting.setVisibility(View.GONE);

            tvTips.setVisibility(View.VISIBLE);
            mResultRecycleView.setVisibility(View.VISIBLE);
            gifNearRobot.setVisibility(View.VISIBLE);
        }else {
            getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        if(mScanHelper != null) {
            mScanHelper.UnRegisterHelper();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        if(mHandler != null){
            mHandler.removeMessages(SCAN_FINISH_CODE);
        }

        try {
            this.mScanHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
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

        /*if(devices.size()>0){
            gifIvSearch.setVisibility(View.GONE);
            gifNearRobot.setVisibility(View.VISIBLE);
            mResultRecycleView.setVisibility(View.VISIBLE);
            mResultDeviceList.clear();
            for (int i = 0; i < devices.size(); i++) {
                Map robot = new HashMap<String, Object>();

                robot.put(ScanHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(devices.get(i).getName()));
                robot.put(ScanHelper.map_val_robot_mac, devices.get(i).getAddress());
                robot.put(ScanHelper.map_val_robot_connect_state, false);
                mResultDeviceList.add(robot);

            }
            UbtLog.d(TAG, "devices=" + devices.size());
            tvTips.setText(getStringRes("ui_block_search_robot_num").replace("#", ""+devices.size()));
            mResultRobotAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getActivity(), "NO DEVICE", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public void onScanFinish() {

        mHandler.sendEmptyMessage(SCAN_FINISH_CODE);

    }


    @Override
    public void onCoonected(boolean state) {

        if(!state && mBlueConnectNum < 2 && mScanHelper.getNextConnectState()){
            mBlueConnectNum++;
            mScanHelper.doReCoonect(mCurrentRobotInfo);
            return;
        }

        if((ScanDeviceFragment.this.getActivity() == null) || (ScanDeviceFragment.this.getActivity()).isFinishing()){
            return;
        }

        UbtLog.d(TAG, "onCoonected!");
        dismissDialog();

        if (state) {
            Intent mIntent = new Intent();
            mIntent.putExtra(REUEST_CODE,((ScanBluetoothActivity)getActivity()).getRequestCode());
            getActivity().setResult(Activity.RESULT_OK, mIntent);
            getActivity().finish();

        } else {

            rlConnecting.setVisibility(View.GONE);
            tvTips.setVisibility(View.VISIBLE);
            mResultRecycleView.setVisibility(View.VISIBLE);
            gifNearRobot.setVisibility(View.VISIBLE);

            ((BaseActivity) ScanDeviceFragment.this.getActivity()).showToast("ui_home_connect_failed");
        }

    }

    public void dismissDialog() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.cancel();
        }
    }


    @Override
    public void updateHistory() {

    }

    @Override
    public void noteUpdateBin() {
        UbtLog.d(TAG, "noteUpdateBin");
        dismissDialog();
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
    public void noteWaitWebProcressShutDown() {

    }


    @Override
    protected void initBoardCastListener() {

    }
}

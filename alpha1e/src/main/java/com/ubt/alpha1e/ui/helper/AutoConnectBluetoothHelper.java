package com.ubt.alpha1e.ui.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.tencent.ai.tvs.env.ELoginPlatform;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.business.FileSendManager;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.login.LoginManger;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.services.ActivationService;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.services.RecordSoftService;
import com.ubt.alpha1e.services.SendClientIdService;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.update.BluetoothUpdateManager;
import com.ubt.alpha1e.update.EngineUpdateManager;
import com.ubt.alpha1e.update.IBluetoothUpdateManagerListener;
import com.ubt.alpha1e.update.IEngineUpdateManagerListener;
import com.ubt.alpha1e.update.RobotSoftUpdateManager;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.AlphaInfo;
import com.ubtechinc.base.BlueToothManager;
import com.ubtechinc.base.BluetoothUtil;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.sqlite.DBAlphaInfoManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.ubt.alpha1e.base.Constant.SP_CLIENT_ID;

public class AutoConnectBluetoothHelper extends BaseHelper implements IJsonListener,
        IEngineUpdateManagerListener,IBluetoothUpdateManagerListener,FileSendManager.IFileSendManager {

    private static final String TAG = "AutoConnectBluetoothHelper";
    public Boolean thiz_state;

    public static final String map_val_rebot_head_resource = "map_val_head_resource";
    public static final String map_val_robot_name = "map_val_robot_info_name";
    public static final String map_val_robot_mac = "map_val_robot_mac";
    public static final String map_val_robot_connect_state = "map_val_robot_connect_state";
    public static final String map_val_robot_rssi = "map_val_robot_rssi";
    // -------------------------------
    private IScanUI mUI;
    private BluetoothAdapter mBtAdapter;
    private BluetoothDevice mCurrentTryDevices;
    private List<BluetoothDevice> mDevicesList;
    private static boolean is_read_state_for_update = false;
    private static boolean has_sdcard = true;
    private String localBluetoothVersion = "";

    private List<AlphaInfo> history_list;
    // -------------------------------

    private long do_check_bin_version_request = 11001; //检查1s 1p 固件bin
    private long do_check_bluetooth_version_request = 11002; //检查1sp 1p+ 蓝牙bin
    private final int do_get_access_token = 11003; //获取
    private final int do_check_soft_version_request = 11004; //检查1E 机器人OTA 软件
    // -------------------------------

    public static final int MSG_DO_COON_BT_SUCCESS = 1001;
    private static final int MSG_DO_COON_BT_FAIL = 1002;
    private static final int MSG_DO_UPDATE_BIN = 1003;
    private static final int MSG_DO_CHECK_BIN = 1004;
    private static final int MSG_DO_SCAN_FINISH = 1005;
    private static final int MSG_DO_UPDATE_BLUETOOTH = 1006;
    private static final int MSG_DO_SEND_HANDSHAKE = 1007;
    private static final int MSG_DO_READ_SOFT_VERSION = 1008;
    private static final int MSG_DO_CHECK_BLUETOOTH = 1009;
    private static final int MSG_DO_DOWNLOAD_UPDATE_BLUETOOTH = 1010;
    private static final int MSG_DO_GOTO_PC_UPDATE = 1011;
    private static final int MSG_ON_SEND_FILE_FINIAH = 1012;
    private static final int MSG_DO_CHECK_UPDATE_SOFT = 1013;
    public static final int MSG_DO_UPDATE_SOFT = 1014;
    public static final int MSG_DO_CONNECT_NETWORK_UPGRADE = 1015;


    private static final int MSG_DO_NOTE_DISCONNECT = 1020;
    private static final int MSG_DO_FINISHE_CONNECT = 1021;

    // -------------------------------
    private String mCurrentLocalSoftVersion = null;
    private String mCurrentLocalHardVersion = null;
    private Timer mReadHardVersionTimer;
    private static Timer mHandShakeTimer;
    private Timer StopScanBLETimer = null;
    private Date lastTime_DV_HANDSHAKE = null;
    private Date lastTime_DV_HANDSHAKE_B_SEVEN = null;
    private Date lastTime_onConnectState = null;

    private CountDownTimer mCountDownTimer;

    //与B7握手次数 (1SP 1P+ 添加 ,目前无此产品)
    private static int handshakeBsevenNum = 0;
    private static int handshakeBsevenNumMax = 0;//

    private boolean isNextConnect = true;

    private int clientIdSendWhich = 0 ; //clientId发送到哪一段
    String clientid[] = null;

    // -------------------------------
    private BaseWebRunnable mCurrentWait;
    // -------------------------------
    private BroadcastReceiver bvr_bt_discovery_new_devices = new BroadcastReceiver() {

        @Override
        public synchronized void onReceive(Context arg0, Intent arg1) {

            BluetoothDevice newDevices = arg1
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            String new_name = newDevices.getName();
            //MyLog.writeLog(TAG, "接收到的蓝牙地址：" + newDevices.getAddress() + "   new_name = " + new_name);

            //去掉alpha 名称的过滤，主要是因为有一批出厂的机器，名称没有alpha
            if (getDeviceById(newDevices.getAddress()) == null
                    && new_name != null
                    && ((new_name.toLowerCase().contains("alpha") || new_name.equalsIgnoreCase("Dual-SPP")))
                    ) {
                BluetoothClass cod = newDevices.getBluetoothClass();
                //UbtLog.d(TAG,"接收到的蓝牙名称:"+new_name+"     蓝牙地址:"+newDevices.getAddress()+"  蓝牙类型："+cod.toString() + "    getBondState  " + newDevices.getBondState());
                //if(!cod.toString().equals("1f00")){// CoD为"1f00"为BLE设备,过滤掉
                mDevicesList.add(newDevices);
                //MyLog.writeLog(TAG, "接收到的蓝牙总数量：" + mDevicesList.size());
                mUI.onGetNewDevices(mDevicesList);

                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.SCAN_ROBOT);
                robotEvent.setBluetoothDevice(newDevices);
                robotEvent.setRssi(arg1.getExtras().getShort(BluetoothDevice.EXTRA_RSSI));
                EventBus.getDefault().post(robotEvent);

                if (newDevices.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //信号强度。
                    short rssi = arg1.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                    //UbtLog.d(TAG,"接收到的蓝牙名称:"+new_name+"     蓝牙地址:"+newDevices.getAddress()+"  rssi:: " + rssi);
                }
                //}
            }

        }
    };

    private BroadcastReceiver bvr_bt_state_change = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {


            int state = arg1.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            int pre_state = arg1.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            UbtLog.d(TAG, "getAction = " + arg1.getAction() + " state = " + state);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_ON:
                    UbtLog.d(TAG,"mUI noteBtTurnOn = " + mUI);
                    RobotEvent btTurnOnEvent = new RobotEvent(RobotEvent.Event.BLUETOOTH_TURN_ON);
                    EventBus.getDefault().post(btTurnOnEvent);
                    mUI.noteBtTurnOn();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    break;
                case BluetoothAdapter.STATE_CONNECTING:
                    break;
                case BluetoothAdapter.STATE_DISCONNECTING:
                    break;
                case BluetoothAdapter.STATE_CONNECTED:
                    break;
                case (BluetoothAdapter.STATE_DISCONNECTED):
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver bvr_bt_discovery_start = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String state = arg1.getAction();
            //UbtLog.d(TAG,"bvr_bt_discovery_start = " + state);
            if (state.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {

            } else {

            }
        }
    };
    private BroadcastReceiver bvr_bt_discovery_state = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String state = arg1.getAction();
            //UbtLog.d(TAG,"bvr_bt_discovery_state = " + state);
            if (state.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                //
                mUI.onScanFinish();
                UbtLog.d(TAG," ccy SCAN_ROBOT_FINISH 2   " );
                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.SCAN_ROBOT_FINISH);
                EventBus.getDefault().post(robotEvent);
            } else {
                //
            }

        }
    };
    // -------------------------------
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == MSG_DO_SEND_HANDSHAKE){

                String mac = (String) msg.obj;
                doSendComm(mac, ConstValue.DV_HANDSHAKE, null);
            }else if (msg.what == MSG_DO_COON_BT_SUCCESS) {
//                doDealConnectSuccessResult();
//                mUI.onCoonected(true);
//                UbtLog.d(TAG,"    发送 获取 product 和 dsn  命令");
//                byte[] param_read = new byte[1];
//                doSendComm(ConstValue.DV_PRODUCT_AND_DSN, null);

            }else if(msg.what == MSG_DO_FINISHE_CONNECT){
                UbtLog.d(TAG,"    蓝牙连接过程完成");
                doDealConnectSuccessResult();
                mUI.onCoonected(true);
            }else if(msg.what == MSG_DO_CONNECT_NETWORK_UPGRADE){

                doDealConnectSuccessResult();
                if(mContext instanceof BaseActivity){
                    ((BaseActivity)mContext).finish();
                }
            }else if (msg.what == MSG_DO_COON_BT_FAIL) {
                mUI.onCoonected(false);
            }else if (msg.what == MSG_DO_CHECK_BIN) {

                mUI.noteCheckBin((BaseWebRunnable) msg.obj);
            }else if (msg.what == MSG_DO_UPDATE_BIN) {

                final String objStr = (String) msg.obj;
                new AlertDialog(AlphaApplication.getBaseActivity()).builder().setMsg(AlphaApplication.getBaseActivity().getStringResources("ui_common_upgrade_warning")).setCancelable(false).
                        setPositiveButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EngineUpdateManager.getInstance(AlphaApplication.getBaseActivity(),
                                        objStr, AutoConnectBluetoothHelper.this).Update();
                            }
                        }).setNegativeButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        doUpdateUI();//need finish activity
                        ((AlphaApplication)mContext.getApplicationContext()).doExitApp(false);
                    }
                }).show();
                mUI.noteUpdateBin();
            }else if(msg.what == MSG_DO_CHECK_BLUETOOTH){

                mUI.noteCheckBin((BaseWebRunnable) msg.obj);
            }else if(msg.what == MSG_DO_DOWNLOAD_UPDATE_BLUETOOTH){

                final String objStr = (String) msg.obj;
                new AlertDialog(mContext).builder().setMsg(AlphaApplication.getBaseActivity().getStringResources("ui_common_upgrade_warning")).setCancelable(true).
                        setPositiveButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mUI.noteUpdateBin();
                                BluetoothUpdateManager.getInstance(mContext,
                                        objStr, AutoConnectBluetoothHelper.this).Update();
                            }
                        }).setNegativeButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
                    }
                }).show();
            }else if(msg.what == MSG_DO_UPDATE_BLUETOOTH){

                BluetoothUpdateManager.getInstance().startSendBluetooth();
            }else if (msg.what == MSG_DO_SCAN_FINISH) {

                mUI.onScanFinish();
            }else if(msg.what == MSG_DO_READ_SOFT_VERSION ){

                byte[] param_read = new byte[1];
                doSendComm(ConstValue.DV_READ_SOFTWARE_VERSION, param_read);
            }else if(msg.what == MSG_DO_GOTO_PC_UPDATE){

                mUI.onGotoPCUpdate();
            }else if(msg.what == MSG_ON_SEND_FILE_FINIAH){

                boolean isAutoConnect = false;
                //UbtLog.d(TAG,"isAutoConnect = " + isAutoConnect + " mContext = " + mContext);
                if(mContext instanceof AutoScanConnectService){
                    isAutoConnect = true;
                }

                if (has_sdcard && !isAutoConnect){
                    doRequestForUpdateBin(mCurrentLocalSoftVersion,mCurrentLocalHardVersion);
                } else {
                    AutoConnectBluetoothHelper.this.doUpdateUI();
                }
            }else if(msg.what == MSG_DO_CHECK_UPDATE_SOFT){

                RobotSoftUpdateManager.getInstance(mContext,mHandler).doCheckUpdateSoft();
                mHandler.sendEmptyMessage(MSG_DO_CHECK_BIN);
            }else if(msg.what == MSG_DO_UPDATE_SOFT){

                mUI.noteUpdateBin();
            }else if (msg.what == MSG_DO_NOTE_DISCONNECT) {
//                if (mUI != null){
//                    mUI.noteDiscoonected();
//                }
                UbtLog.d(TAG,"--MSG_DO_NOTE_DISCONNECT-1- " );
                if(EventBus.getDefault().hasSubscriberForEvent(RobotEvent.class)) {
                    RobotEvent disconnectEvent = new RobotEvent(RobotEvent.Event.DISCONNECT);
                    EventBus.getDefault().post(disconnectEvent);
                    UbtLog.d(TAG,"--MSG_DO_NOTE_DISCONNECT-2- " );
                }
            }

        }
    };

    // -------------------------------
    private BluetoothDevice getDeviceById(String mac) {
        BluetoothDevice result = null;
        if (mDevicesList != null) {
            for (int i = 0; i < mDevicesList.size(); i++) {
                if (mDevicesList.get(i).getAddress().equals(mac))
                    result = mDevicesList.get(i);
            }
        }
        return result;
    }

    /**
     * 读取 1E 联网状态
     */
    public void readNetworkStatus(){
        UbtLog.d(TAG,"--readNetworkStatus-- " );
        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
    }


    @Override
    public void UnRegisterHelper() {

        MyLog.writeLog(TAG, "UnRegisterHelper" + mCountDownTimer);

        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }


        thiz_state = false;
        super.UnRegisterHelper();
    }

    Context mAtuoBluetoothContext = null;

    public AutoConnectBluetoothHelper(IScanUI _ui, Context context) {
        super(context);
        mAtuoBluetoothContext = context ;
        MyLog.writeLog(TAG, "create ScanHelper");
        this.mUI = _ui;

        registerBoardCastReceiver();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
//            mBtAdapter.enable();
//            ((Activity)mContext).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1333);
        }
        mDevicesList = new ArrayList<BluetoothDevice>();

        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() == null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .setBlueToothManager(new BlueToothManager(mContext));

            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().startProcess();
        }

        if (((AlphaApplication) mContext.getApplicationContext())
                .getDBAlphaInfoManager() == null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .setDBAlphaInfoManager(new DBAlphaInfoManager(mContext));
        }
        thiz_state = true;
    }

    public List<AlphaInfo> getHistoryDevices() {
        history_list = ((AlphaApplication) mContext
                .getApplicationContext()).getDBAlphaInfoManager().getAllInfo();
        return history_list;
    }

    public void getBindHistoryDerivce() {
        mUI.onGetHistoryBindDevices(mBtAdapter.getBondedDevices());
    }

    public void cancelScan(){
        if (mBtAdapter.isDiscovering()) {
            UbtLog.d(TAG,"取消蓝牙搜索");
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
            }
            mBtAdapter.cancelDiscovery();
        }
    }

    public void doScan() {

        if (1 == 0) {
            //doBLEScan();
            doNearFieldScan();
        } else {

            if (!mBtAdapter.isEnabled()) {
                mUI.noteBtIsOff();
                return;
            }

            UbtLog.d(TAG,"mBtAdapter.isDiscovering() == " + mBtAdapter.isDiscovering() + "  = " + mContext);
            if (mBtAdapter.isDiscovering()) {
                //mBtAdapter.cancelDiscovery();
                //mUI.noteIsScaning();
                return;
            }
            if (mDevicesList != null){
                mDevicesList.clear();
            }

            mBtAdapter.startDiscovery();
        }
    }

    public void doNearFieldScan(){

        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        //持续监听搜索蓝牙
        mCountDownTimer = new CountDownTimer(24 * 60 * 60 * 1000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }

                if (mDevicesList != null){
                    mDevicesList.clear();
                }

                UbtLog.d(TAG,"doNearFieldScan startDiscovery-->");
                mBtAdapter.startDiscovery();
            }
            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void doBLEScan() {
        UbtLog.d(TAG,"-doBLEScan-");
        if (!mBtAdapter.isEnabled()) {
            mUI.noteBtIsOff();
            return;
        }
        if (mBtAdapter.isDiscovering()) {
            mUI.noteIsScaning();
            return;
        }
        if (mDevicesList != null){
            mDevicesList.clear();
        }

        mBtAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                                   @Override
                                   public void onLeScan(BluetoothDevice newDevices, int rssi, byte[] scanRecord) {

                                       if (getDeviceById(newDevices.getAddress()) == null) {

                                           String new_name = newDevices.getName();
                                           if (getDeviceById(newDevices.getAddress()) == null
                                                   && new_name != null
                                                   /*&& new_name.toLowerCase().contains("alpha")*/) {

                                               UbtLog.d(TAG,"newDevices = " +newDevices + " new_name == " + new_name +  " rssi = " + rssi );
                                               mDevicesList.add(newDevices);
                                               mUI.onGetNewDevices(mDevicesList);


                                           }
                                       }
                                   }
                               }
        );

    }

    private void registerBoardCastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(bvr_bt_state_change, filter);

        mContext.registerReceiver(bvr_bt_discovery_start,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        mContext.registerReceiver(bvr_bt_discovery_state,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        mContext.registerReceiver(bvr_bt_discovery_new_devices,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));

    }

    private void unRegisterBoardCastReceiver() {
        try {
            mContext.unregisterReceiver(bvr_bt_discovery_new_devices);
            mContext.unregisterReceiver(bvr_bt_state_change);
            mContext.unregisterReceiver(bvr_bt_discovery_start);
            mContext.unregisterReceiver(bvr_bt_discovery_state);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doDelHistory(int index) {
        AlphaInfo i = history_list.get(index);
        if (i.isConnect()) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().releaseAllConnected();
        }
        ((AlphaApplication) mContext.getApplicationContext())
                .getDBAlphaInfoManager().deleteDevice(i.getMacAddr());
        mUI.updateHistory();
    }

    public void doReCoonect(Map<String, Object> robot_info) {

        if(robot_info == null){
            UbtLog.e(TAG,"蓝牙连接，robot_info为null");
            Message msg = new Message();
            msg.what = MSG_DO_COON_BT_FAIL;
            mHandler.sendMessage(msg);
            return;
        }

        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        setNextConnectState(true);
        is_read_state_for_update = true;
        BluetoothDevice device = mBtAdapter.getRemoteDevice((String) robot_info
                .get(map_val_robot_mac));
        MyLog.writeLog(TAG, "重新连接蓝牙:" + device.getAddress());
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().releaseAllConnected();
        if (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth() != null){
            onDeviceDisConnected(((AlphaApplication) mContext
                    .getApplicationContext()).getCurrentBluetooth()
                    .getAddress());
        }
        mCurrentTryDevices = device;
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().connectDevice(device);

    }

    public void doCoonect(Map<String, Object> robot_info) {
        setNextConnectState(true);
        is_read_state_for_update = true;
        MyLog.writeLog(TAG, "doCoonect-->is_need_update:" + is_read_state_for_update);
        BlueToothManager manager = ((AlphaApplication) mContext
                .getApplicationContext()).getBlueToothManager();
        if (manager.getmBluetoothUtil().getState() != BluetoothUtil.STATE_CONNECTING) {
            if (!(Boolean) robot_info.get("map_val_robot_connect_state")) {
                mCurrentTryDevices = mBtAdapter.getRemoteDevice((String) robot_info.get(map_val_robot_mac));
                manager.connectDevice(mCurrentTryDevices);
            }
        }

    }

    private void doUpdateUI() {
        // connect bluetooth success to update UI
        Message msg = new Message();
        msg.what = MSG_DO_COON_BT_SUCCESS;
        mHandler.sendMessage(msg);

    }

    private void finishBluetoothConnect() {
        // connect bluetooth success to update UI
        Message msg = new Message();
        msg.what = MSG_DO_FINISHE_CONNECT;
        mHandler.sendMessage(msg);

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
//        UbtLog.d(TAG,"cmd==1===="+cmd);
        if ((mCurrentTryDevices == null
                || mac != mCurrentTryDevices.getAddress()) && cmd != ConstValue.DV_READ_NETWORK_STATUS) {
            return;
        }
        UbtLog.d(TAG,"cmd==2===="+cmd);
        if(cmd == ConstValue.DV_HANDSHAKE_B_SEVEN){
            hasHandshakeBseven = true;
            lastTime_DV_HANDSHAKE_B_SEVEN = new Date(System.currentTimeMillis());
            if(mHandler.hasMessages(MSG_DO_SEND_HANDSHAKE)){
                mHandler.removeMessages(MSG_DO_SEND_HANDSHAKE);
            }
            Message msg = new Message();
            msg.what = MSG_DO_SEND_HANDSHAKE;
            msg.obj = mac;
            mHandler.sendMessage(msg);

        }else if (cmd == ConstValue.DV_HANDSHAKE) {
            cancelScan();
            UbtLog.d(TAG, "发送01握手成功-" +  mUI);
            UbtLog.d(TAG,"handshakeBsevenNum:" + handshakeBsevenNum + "    lastTime_DV_HANDSHAKE_B_SEVEN = "+lastTime_DV_HANDSHAKE_B_SEVEN);
            if(lastTime_DV_HANDSHAKE_B_SEVEN == null && handshakeBsevenNum < handshakeBsevenNumMax){//
                handshakeBsevenNum++;
                doSendComm(mac, ConstValue.DV_HANDSHAKE_B_SEVEN, null);

                Message msg = new Message();
                msg.what = MSG_DO_SEND_HANDSHAKE;
                msg.obj = mac;
                mHandler.sendMessageDelayed(msg,500);
                return;
            }

            try {
                mHandShakeTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 校验握手时间接受多次数据-----------start
            Date curDate = new Date(System.currentTimeMillis());
            float time_difference = 1000;
            if (lastTime_DV_HANDSHAKE != null) {
                time_difference = curDate.getTime()
                        - lastTime_DV_HANDSHAKE.getTime();
            }
            lastTime_DV_HANDSHAKE = curDate;

            if (time_difference < 1000) {
                MyLog.writeLog(TAG, "1S 接收到多次握手成功次数");
                return;
            }
            // 校验握手时间接受多次数据-----------end
            // hand bluetooth name
            String name = BluetoothParamUtil.bytesToString(param);
            ((AlphaApplication) mContext.getApplicationContext())
                    .setCurrentDeviceName(name);
            ((AlphaApplication) mContext.getApplicationContext())
                    .setCurrentBluetooth(mBtAdapter.getRemoteDevice(mac));

//            MyLog.writeLog(TAG, "时间校准");
            byte[] timeParam = TimeTools.getCurrentDateTimeBytes();

//            doSendComm(ConstValue.DV_ADJUST_TIME, timeParam);

            this.doSendReadStateComm();
            //update AlphaInfo
            AlphaInfo info = new AlphaInfo();
            info.setBlueToothName(((AlphaApplication) mContext
                    .getApplicationContext()).getCurrentBluetooth().getName());
            info.setConnect(true);
            if (name == null){
                info.setName("Alpha1");
            }else{
                info.setName(name);
            }

            info.setMacAddr(((AlphaApplication) mContext
                    .getApplicationContext()).getCurrentBluetooth()
                    .getAddress());
            ((AlphaApplication) mContext.getApplicationContext())
                    .getDBAlphaInfoManager().addAlphaInfo(info);


            SendClientIdService.send();
            UbtLog.d(TAG, "蓝牙连接完成-");
            finishBluetoothConnect();

        } else if (cmd == ConstValue.DV_ADJUST_TIME) {
            /** 收到时间校准，读取机器人状态 **/
            this.doSendReadStateComm();

        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {

            if (param[0] == 4) {
                if (param[1] == 0) {
                    has_sdcard = false;
                }
                else {
                    has_sdcard = true;
                }
            }

            //MyLog.writeLog(TAG, "是否需要读取软件版本is_need_update:" + is_read_state_for_update);
            if (is_read_state_for_update && param[0] == 4) {
                is_read_state_for_update = false;
                MyLog.writeLog(TAG,"读取软件版本DV_READ_SOFTWARE_VERSION开始");

                String currentBluetoothName = mCurrentTryDevices.getName().toLowerCase();
                UbtLog.d(TAG, "currentBluetoothName:"+ currentBluetoothName);
                if(currentBluetoothName.contains("Alpha1P+".toLowerCase())
                        || currentBluetoothName.contains("Alpha1S_".toLowerCase())){
                    hasSupportA2DP = true;
                    UbtLog.d(TAG, "need update bluetooth");
                    doSendComm(ConstValue.DV_READ_BLUETOOTH_VERSION, null);
                }else{
                    UbtLog.d(TAG, "need not update bluetooth");
                    hasSupportA2DP = false;
                    mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
                }
            }

        } else if (cmd == ConstValue.DV_READ_SOFTWARE_VERSION) {
            mCurrentLocalSoftVersion = new String(param);
            MyLog.writeLog(TAG, "本地软件版本 DV_READ_SOFTWARE_VERSION:" + mCurrentLocalSoftVersion);

            // 读取硬件版本定时器
            if (mReadHardVersionTimer != null){
                mReadHardVersionTimer.cancel();
            }
            mReadHardVersionTimer = new Timer();
            mReadHardVersionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mCurrentLocalHardVersion = EngineUpdateManager.Alphas_Old;
                    MyLog.writeLog(TAG, "LOCAL DV_READ_HARDWARE_VERSION："
                            + mCurrentLocalHardVersion);
                    doRequestForUpdateBin(mCurrentLocalSoftVersion,
                            mCurrentLocalHardVersion);
                }
            }, 5000);
            // 读取硬件版本
            byte[] read_param = new byte[1];
            param[0] = 0;
            MyLog.writeLog(TAG,"读取硬件版本DV_READ_HARDWARE_VERSION开始");

            doSendComm(ConstValue.DV_READ_HARDWARE_VERSION, read_param);

        } else if (cmd == ConstValue.DV_READ_HARDWARE_VERSION) {

            String version = new String(param);
            this.mReadHardVersionTimer.cancel();
            mCurrentLocalHardVersion = version;
            MyLog.writeLog(TAG, "DV_READ_HARDWARE_VERSION 硬件版本：" + mCurrentLocalHardVersion);

            //V3.2.1.1版本以后不要发送自我介绍功能
            boolean needSend = false;
            if(needSend){
                final String filePath = FileTools.tmp_file_cache + "/self introduction.hts";
                FileTools.writeAssetsToSd("Actions/self introduction.hts", mContext.getApplicationContext(), filePath);
                File file = new File(filePath);
                String ifSend = BasicSharedPreferencesOperator
                        .getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(mCurrentTryDevices.getAddress());

                String systemLanguage = AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage());

                if(file.isFile() && hasSdcard
                        && BasicSharedPreferencesOperator.NO_VALUE.equals(ifSend)
                        && "CN".equalsIgnoreCase(systemLanguage)
                        && !mCurrentLocalHardVersion.toLowerCase().contains(EngineUpdateManager.Alpha1e.toLowerCase())){
                    FileTools.pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            doSendFileToRobot(filePath,"自我介绍.hts");
                        }
                    });
                }else{
                    Message msg = new Message();
                    msg.what = MSG_ON_SEND_FILE_FINIAH;
                    msg.obj = true;
                    mHandler.sendMessage(msg);
                }
            }else {
                Message msg = new Message();
                msg.what = MSG_ON_SEND_FILE_FINIAH;
                msg.obj = true;
                mHandler.sendMessage(msg);
            }

        }else if(cmd == ConstValue.DV_READ_BLUETOOTH_VERSION){
            try {
                localBluetoothVersion = new String(param,"GB2312");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            UbtLog.d(TAG,"本地蓝牙版本 DV_READ_BLUETOOTH_VERSION:"+localBluetoothVersion);
            doRequestForUpdateBluetooth();

        }else if(cmd == ConstValue.DV_BLUETOOTH_UPGRADE){

            UbtLog.d(TAG,"DV_BLUETOOTH_UPGRADE:"+param[0]);
            if(param[0] == 3){
                ((AlphaApplication) mContext.getApplicationContext())
                        .getBlueToothManager().intoMonopoly();
                mHandler.sendEmptyMessageDelayed(MSG_DO_UPDATE_BLUETOOTH,500);
            }else{
                mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
            }

        } else if (cmd == ConstValue.SET_PALYING_CHARGING) {
            if (param[0] == 0) {
                MyLog.writeLog(TAG, "设置禁止边充边玩成功");
            } else {
                MyLog.writeLog(TAG, "设置开启边充边玩成功");
            }

        }else if(cmd == ConstValue.DV_READ_NETWORK_STATUS){

            String networkInfoJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG,"cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);

            NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);

            RobotEvent event = new RobotEvent(RobotEvent.Event.NETWORK_STATUS);
            event.setNetworkInfo(networkInfo);
            EventBus.getDefault().post(event);

        }
    }

    /**
     * 处理连接成功结果逻辑，保存版本号等
     */
    private void doDealConnectSuccessResult(){
        ((AlphaApplication) mContext.getApplicationContext())
                .setRobotHardVersion(mCurrentLocalHardVersion);
        ((AlphaApplication) mContext.getApplicationContext())
                .setRobotSoftVersion(mCurrentLocalSoftVersion);
        // 激活机器人
        AutoConnectBluetoothHelper.this.doActivate();
        // 记录机器人版本号
        AutoConnectBluetoothHelper.this.doRecordSoftVersion();
        // 设置机器边充边玩状态
        byte[] params = new byte[1];
        if (SettingHelper.isPlayCharging(mContext)) {
            params[0] = 1;
        } else {
            params[0] = 0;
        }
//        doSendComm(ConstValue.SET_PALYING_CHARGING, params);
        AutoScanConnectService.doManalDisConnect(false);
    }

    private void doSendFileToRobot(String filePath,String fileName) {

        FileSendManager.getInstance(this, mContext.getApplicationContext()).start().doSendFileToRobot(filePath,fileName);
    }

    private void doActivate() {
        Intent mIntent = new Intent(mContext,ActivationService.class);
        mContext.startService(mIntent);
    }

    private void doRecordSoftVersion(){
        Intent mIntent = new Intent(mContext,RecordSoftService.class);
        mContext.startService(mIntent);
    }

    private void doRequestForUpdateBluetooth() {
        String version_prod = "1";
        String version_prod_type = "6";

        Message msg = new Message();
        msg.what = MSG_DO_CHECK_BLUETOOTH;
        msg.obj = GetDataFromWeb.getJsonByPost(do_check_bluetooth_version_request,
                HttpAddress.getRequestUrl(HttpAddress.Request_type.check_update),
                HttpAddress.getParamsForPost(new String[]{version_prod,
                                version_prod_type, "0"}, HttpAddress.Request_type.check_update,
                        mContext), this);
        mHandler.sendMessage(msg);
    }

    private void doRequestForUpdateBin(String soft_version, String hard_version) {

        UbtLog.d(TAG, "soft_version-->" + soft_version + ",hard_version-->" + hard_version);

        String version_prod = "1";
        String version_prod_type = "";

        if (hard_version.toLowerCase().contains(
                EngineUpdateManager.Alphas_Old.toLowerCase())) {
            version_prod_type = "1";
        } else if (hard_version.toLowerCase().contains(
                EngineUpdateManager.Alpha1s.toLowerCase())) {
            version_prod_type = "2";
        } else if (hard_version.toLowerCase().contains(
                EngineUpdateManager.Alpha1p.toLowerCase())) {
            version_prod_type = "3";
        } else if (hard_version.toLowerCase().contains(
                EngineUpdateManager.Alpha1_General.toLowerCase())) {
            version_prod_type = "4";
        }

        if(hard_version.toLowerCase().contains(
                EngineUpdateManager.Alpha1e.toLowerCase())){
            ((AlphaApplication) mContext.getApplicationContext())
                    .setRobotSoftVersion(mCurrentLocalSoftVersion);
            mHandler.sendEmptyMessage(MSG_DO_CHECK_UPDATE_SOFT);
        }else {
            Message msg = new Message();
            msg.what = MSG_DO_CHECK_BIN;
            msg.obj = GetDataFromWeb.getJsonByPost(do_check_bin_version_request,
                    HttpAddress.getRequestUrl(HttpAddress.Request_type.check_update),
                    HttpAddress.getParamsForPost(new String[]{version_prod,
                                    version_prod_type, "0"}, HttpAddress.Request_type.check_update,
                            mContext), this);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        UbtLog.d(TAG, "蓝牙连接状态：" + bsucceed + "    mCurrentTryDevices = " + mCurrentTryDevices + " mac == " + mac + "    mUI = " + mUI);

        if(!bsucceed){
            if (mCurrentTryDevices == null/* || mac == null*/
                    /*|| !mac.equals(mCurrentTryDevices.getAddress())*/){
                return;
            }
        }

        if(AutoScanConnectService.isManalConnectMode() && mUI instanceof AutoScanConnectService){
            //当进入手动连接时，自动连接注册的Helper不相应
            return;
        }

        // 收到蓝牙连接状态命令时间相隔-----------start
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 1000;
        if (lastTime_onConnectState != null) {
            time_difference = curDate.getTime() - lastTime_onConnectState.getTime();
        }
        lastTime_onConnectState = curDate;
        if (time_difference < 500) {
            return;
        }
        // 收到蓝牙连接状态命令时间相隔-----------end

        if (!bsucceed) {
            Message msg = new Message();
            msg.what = MSG_DO_COON_BT_FAIL;
            mHandler.sendMessage(msg);
            return;
        }

        UbtLog.d(TAG, "蓝牙连接状态:" + bsucceed);
        //退出传文件模式
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().outMonopoly();

        /* 以下为1P+ 1SP 添加，目前无此产品，暂时注释 -----start */
        /*
        handshakeBsevenNum = 1;
        hasHandshakeBseven = false;
        lastTime_DV_HANDSHAKE_B_SEVEN = null;
        doSendComm(mac, ConstValue.DV_HANDSHAKE_B_SEVEN, null);
        */
        /* 以下为1P+ 1SP 添加，目前无此产品，暂时注释 -----end */

        Message msg = new Message();
        msg.what = MSG_DO_SEND_HANDSHAKE;
        msg.obj = mac;
        mHandler.sendMessage(msg);

        if (mHandShakeTimer != null){
            mHandShakeTimer.cancel();
        }

        mHandShakeTimer = new Timer();
        mHandShakeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 与机器人握手
                setNextConnectState(false);
                Message msg = new Message();
                msg.what = MSG_DO_COON_BT_FAIL;
                mHandler.sendMessage(msg);
                UbtLog.e(TAG, "与机器人握手超时，主动断开连接 ");
                doCancelCoon();
            }
        }, 5000);
    }

    private void setNextConnectState(boolean state){
        isNextConnect = state;
    }

    public boolean getNextConnectState(){
        return isNextConnect;
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.e(TAG, "onDeviceDisConnected..... ");
        if (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth() != null
                && ((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth().getAddress().equals(mac)) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .setCurrentBluetooth(null);
        }

        Message msg = new Message();
        msg.what = MSG_DO_NOTE_DISCONNECT;
        mHandler.sendMessage(msg);
//        UnRegisterHelper();
    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        // TODO Auto-generated method stub
        if (do_check_bin_version_request == request_code) {
            UbtLog.d(TAG,"json = " + json);
            if("2016082214".equals(mCurrentLocalSoftVersion)){
                mHandler.sendEmptyMessage(MSG_DO_GOTO_PC_UPDATE);
                return;
            }

            if (!isSuccess) {
                // 跳转主页
                doUpdateUI();
                return;
            }

            if (!JsonTools.getJsonStatus(json)) {
                // 跳转主页
                doUpdateUI();
                return;
            }

            try {
                String remote_soft_version_str = JsonTools.getJsonModel(json)
                        .getString("versionName");
                int remote_soft_version = Integer
                        .parseInt(remote_soft_version_str.substring(1));
                int local_soft_version = Integer
                        .parseInt(mCurrentLocalSoftVersion);
                MyLog.writeLog(TAG,"remote_soft_version-->"
                        + remote_soft_version + ",local_soft_version"
                        + local_soft_version);

                //remote_soft_version = 0;
                if (remote_soft_version > local_soft_version) {
                    // 有新固件
                    Message msg = new Message();
                    msg.what = MSG_DO_UPDATE_BIN;
                    msg.obj = JsonTools.getJsonModel(json).getString(
                            "versionPath");
                    mHandler.sendMessage(msg);
                } else {
                    //无新固件
                    doUpdateUI();
                }
            } catch (JSONException e) {
                // ������
                doUpdateUI();
            }

        }else if(request_code == do_check_bluetooth_version_request){
            if (!isSuccess) {
                mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
                return;
            }

            if (!JsonTools.getJsonStatus(json)) {
                mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
                return;
            }

            try {
                String remote_soft_version = JsonTools.getJsonModel(json)
                        .getString("versionName");
                String mRemoteBluetoothVersion = remote_soft_version.substring(1);

                MyLog.writeLog(TAG,"mRemoteBluetoothVersion:"
                        + mRemoteBluetoothVersion + ",mLocalBluetoothVersion:"
                        + localBluetoothVersion);

                UbtLog.d(TAG,"mRemoteBluetoothVersion:"
                        + mRemoteBluetoothVersion + ",  mLocalBluetoothVersion:"
                        + localBluetoothVersion);
                //mRemoteBluetoothVersion = "69.0.5";
                if(!mRemoteBluetoothVersion.equals(localBluetoothVersion)){
                    // 有新蓝牙
                    Message msg = new Message();
                    msg.what = MSG_DO_DOWNLOAD_UPDATE_BLUETOOTH;
                    msg.obj = JsonTools.getJsonModel(json).getString("versionPath");
                    mHandler.sendMessage(msg);
                } else {
                    //无新蓝牙
                    mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
                }
            } catch (JSONException e) {
                //读蓝牙版本
                mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
            }
        }
    }

    @Override
    public void onUpdateFinish(boolean is_success) {
        // TODO Auto-generated method stub
        if (is_success) {
            mUI.onUpdateEngineFinish(is_success);
        } else{
            doUpdateUI();
        }
    }

    @Override
    public void DistoryHelper() {

        unRegisterBoardCastReceiver();
        super.UnRegisterHelper();
    }

    public void doCancelCoon() {

        mCurrentTryDevices = null;
        ((AlphaApplication) mContext.getApplicationContext()).getBlueToothManager().releaseAllConnected();
        ((AlphaApplication) mContext.getApplicationContext()).setCurrentBluetooth(null);

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        if (request_code == readUserHeadImgRequest)
            mUI.onGetUserImg(isSuccess, bitmap);
    }

    @Override
    public void onUpdateBluetoothFinish(boolean is_success) {
        mUI.onUpdateBluetoothFinish(is_success);
    }

    @Override
    public void onDownLoadBluetoothFinish(boolean is_success) {
        if(is_success){
            doSendComm(ConstValue.DV_BLUETOOTH_UPGRADE, null);
        } else {
            mHandler.sendEmptyMessage(MSG_DO_READ_SOFT_VERSION);
        }
    }

    @Override
    public void onStartSend() {
        UbtLog.d(TAG,"---onStartSend---");
    }

    @Override
    public void onSendProgressReport(int value) {
        UbtLog.d(TAG,"---onSendProgressReport---"+value);
    }

    @Override
    public void onSendFileFinish(boolean isSuccess, SEND_FAIL_TYPE type) {
        UbtLog.d(TAG,"---onSendFileFinish---"+isSuccess + "    type:"+type);
        if(isSuccess && mCurrentTryDevices != null){
            BasicSharedPreferencesOperator
                    .getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                    .doWrite(mCurrentTryDevices.getAddress(),"true",null,-1);
        }
        Message msg = new Message();
        msg.what = MSG_ON_SEND_FILE_FINIAH;
        msg.obj = isSuccess;
        mHandler.sendMessage(msg);
        FileSendManager.getInstance(this, mContext.getApplicationContext()).release();
    }
}
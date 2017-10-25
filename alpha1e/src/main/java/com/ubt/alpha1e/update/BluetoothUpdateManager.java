package com.ubt.alpha1e.update;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.AlphaApplicationValues.EdtionCode;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.ui.dialog.ProgressDialog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.ubtechinc.file.FileUploadProgressListener;
import com.ubtechinc.file.FileUploader;

import java.io.File;

public class BluetoothUpdateManager implements FileUploadProgressListener {

    private static final String TAG = "BluetoothUpdateManager";

    private static final int MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE = 1001;
    private static final int MESSAGE_UPDATE_LOADING_FILE = 1003;
    private static final int MESSAGE_UPDATE_LOADING_FILE_FINISH = 1004;
    private static final int MESSAGE_ROBRT_IS_BUSY = 1005;
    private static final int MESSAGE_FILE_LOADING_ERROR = 1006;
    private static final int MESSAGE_FILE_LANDING_CANCEL = 1007;

    private static final int MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_START = 1008;
    private static final int MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ING = 1009;
    private static final int MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ERROR = 1010;
    private static final int MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_FINISH = 1011;
    private static final int MESSAGE_START_BACK_TO_RESCAN = 1012;

    private ProgressDialog mProgressDialog;
    private static BluetoothUpdateManager thiz;
    private FileUploader mFileLoader;
    private IBluetoothUpdateManagerListener mListener;

    private Context mContext;
    private String mUrl = "";
    private String mFile_name = "";
    private int mMaxFrame = 0;
    private int bluetoothUpgadeFileSize = 0;

    private int remainingTime = 10;
    public static boolean isBluetoothUpdateRemainReboot = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (mProgressDialog == null) {
                mProgressDialog = mProgressDialog.getInstance(mContext);
                mProgressDialog.show();
            }
            switch (msg.what) {
                case MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE:
                    mProgressDialog.showMsg(mContext
                            .getString(R.string.ui_robot_info_upgrading));
                    break;
                case MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_START:
                    mProgressDialog.showMsg(mContext
                            .getString(R.string.ui_robot_info_upgrading));
                    //mProgressDialog.show();
                    break;
                case MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ING:

                    //mProgressDialog.showMsg("更新：");
                    int upgadePercent = (int)msg.obj;
                    mProgressDialog.updateProgress(String.valueOf(upgadePercent));
                    if(!mProgressDialog.isShowing()){
                        mProgressDialog.show();
                    }
                    if(upgadePercent == 100){
                        mHandler.sendEmptyMessageDelayed(MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_FINISH,1000);
                    }

                    break;
                case MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_FINISH:

                    isBluetoothUpdateRemainReboot = true;
                    mProgressDialog.updateTitleMsg(mContext.getString(R.string.ui_upgrade_bluetooth_success));
                    mProgressDialog.updateCountDown(String.valueOf(remainingTime));

                    if(remainingTime > 0){
                        remainingTime--;
                        mHandler.sendEmptyMessageDelayed(MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_FINISH,1000);
                    }else {
                        mHandler.sendEmptyMessage(MESSAGE_START_BACK_TO_RESCAN);
                    }

                    break;
                case MESSAGE_START_BACK_TO_RESCAN:
                    isBluetoothUpdateRemainReboot = false;
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }

                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    mListener.onUpdateBluetoothFinish(true);
                    break;
                case MESSAGE_UPDATE_LOADING_FILE:

                    mProgressDialog.updateProgress(msg.obj + "");
                    break;
                case MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ERROR:
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(
                                    R.string.ui_robot_info_bin_fail), Toast.LENGTH_SHORT).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }

                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();

                    mListener.onDownLoadBluetoothFinish(false);
                    break;
                default:
                    break;
            }

        }

    };

    private BlueToothInteracter mSendListener = new BlueToothInteracter() {

        @Override
        public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

            if(cmd == ConstValue.DV_BLUETOOTH_UPGRADE_PERCENT){
                int sendSize = Integer.parseInt(new String(param));
                int upgadePercent = sendSize*100/bluetoothUpgadeFileSize;

                UbtLog.d(TAG,"sendSize:"+sendSize+"   bluetoothUpgadeFileSize="+bluetoothUpgadeFileSize + "   upgadePercent="+upgadePercent+"%");
                Message msg = new Message();
                msg.what = MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ING;
                msg.obj = upgadePercent;
                mHandler.sendMessage(msg);

            }
        }

        @Override
        public void onSendData(String mac, byte[] datas, int nLen) {

        }

        @Override
        public void onConnectState(boolean bsucceed, String mac) {

        }

        @Override
        public void onDeviceDisConnected(String mac) {

            UbtLog.d(TAG,"onDeviceDisConnected...");
        }
    };

    private BluetoothUpdateManager() {
    }

    public static BluetoothUpdateManager getInstance(Context cont, String url,
                                                     IBluetoothUpdateManagerListener listener) {
        if (thiz == null){
            thiz = new BluetoothUpdateManager();
        }

        thiz.mContext = cont;
        thiz.mUrl = url;
        String[] strs = thiz.mUrl.split("/");
        thiz.mFile_name = strs[strs.length - 1];
        thiz.mListener = listener;
        return thiz;
    }

    public static BluetoothUpdateManager getInstance() {
        if (thiz == null){
            thiz = new BluetoothUpdateManager();
        }
        return thiz;
    }

    public void Update() {

        if (AlphaApplicationValues.getCurrentEdit() == EdtionCode.for_factory_edit) {
            mListener.onUpdateBluetoothFinish(true);
            return;
        }

        MyLog.writeLog("蓝牙升级功能",
                "com.ubt.alpha1e.update.BluetoothUpdateManager.Update");

        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE;
        mHandler.sendMessage(msg);

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().intoMonopoly();
        UbtLog.d(TAG,"downloadUrl:"+mUrl+"     localUrl:"+FileTools.update_cache + "/" + mFile_name);
        GetDataFromWeb.getFileFromHttp(-1, mUrl, FileTools.update_cache + "/"
                + mFile_name, new FileDownloadListener() {

            @Override
            public void onGetFileLenth(long request_code, double file_lenth) {

            }

            @Override
            public void onStopDownloadFile(long request_code, State state) {

            }

            @Override
            public void onReportProgress(long request_code, double progess) {

            }

            @Override
            public void onDownLoadFileFinish(long request_code, State state) {
                if (state == State.success) {
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    mListener.onDownLoadBluetoothFinish(true);
                } else {
                    Message msg = new Message();
                    msg.what = MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    public void startSendBluetooth() {
        remainingTime= 10;
        isBluetoothUpdateRemainReboot = true;

        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BLUETOOTH_FILE_START;
        mHandler.sendMessage(msg);

        // 注册蓝牙监听
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().addBlueToothInteraction(mSendListener);
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().intoMonopoly();

        // 准备发送文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = FileTools.update_cache + File.separator + mFile_name;
                //String filePath = FileTools.file_path+ File.separator+"update_MV.MVA";
                //String filePath = FileTools.file_path + File.separator + "OTA_V69.0.6_0812.MVA";

                /*if(ScanHelper.mLocalBluetoothVersion.endsWith("1.3")){
                    filePath = FileTools.file_path + File.separator +"o18image_14.MVA";
                }else{
                    filePath = FileTools.file_path + File.separator +"o18image_13.MVA";
                }*/

                File file = new File(filePath);
                UbtLog.d(TAG,"lihai------------file_exists--"+file.exists() + " "+file.getPath());

                if(file.exists()){
                    bluetoothUpgadeFileSize = (int)file.length();

                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager()
                            .sendFile(
                                    ((AlphaApplication) mContext
                                            .getApplicationContext()).getCurrentBluetooth()
                                            .getAddress(), filePath,true);

                }

            }
        }).start();
    }

    @Override
    public void onDownloadSize(int size) {

        /*MyLog.writeLog("bin文件升级", "更新进度：" + 100 * size / mMaxFrame);

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = (100 * size / mMaxFrame);
        mHandler.sendMessage(msg);*/
    }

    @Override
    public void sendCMD(byte flag, byte[] data) {

        MyLog.writeLog("bin文件升级", "发送文件：" + data.length + "(bit)");

        /*((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().sendCommand(
                ((AlphaApplication) mContext.getApplicationContext())
                        .getCurrentBluetooth().getAddress(), flag,
                data, data.length, true);*/
    }

    @Override
    public void UploadFail() {

    }
}

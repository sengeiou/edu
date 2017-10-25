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
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.dialog.UpdateProgressDialog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.ui.dialog.ProgressDialog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.ubtechinc.file.FileUploadProgressListener;
import com.ubtechinc.file.FileUploader;

import java.io.File;

public class EngineUpdateManager implements FileUploadProgressListener {

    private static final String TAG = "EngineUpdateManager";

    // 1s、1p通用版
    public static final String Alpha1_General = "alpha1_v2.0";
    public static final String Alpha1s = "alpha1s_v";
    public static final String Alpha1p = "alpha1p_v";
    public static final String Alphas_Old = "alpha1s_def";
    public static final String Alpha1e = "alpha1e_";

    private static final int MESSAGE_START_DOW_LOAD_BIN_FILE = 1001;
    private static final int MESSAGE_START_DOW_LOAD_BIN_FILE_ERROR = 1002;
    private static final int MESSAGE_UPDATE_LOADING_FILE = 1003;
    private static final int MESSAGE_UPDATE_LOADING_FILE_FINISH = 1004;
    private static final int MESSAGE_ROBRT_IS_BUSY = 1005;
    private static final int MESSAGE_FILE_LOADING_ERROR = 1006;
    private static final int MESSAGE_FILE_LANDING_CANCEL = 1007;
    private static final int MESSAGE_START_BACK_TO_RESCAN = 1008;

    private ProgressDialog mProgressDialog;
    private UpdateProgressDialog mUpdateProgressDialog;
    private static EngineUpdateManager thiz;
    private FileUploader mFileLoader;
    private IEngineUpdateManagerListener mListener;

    private Context mContext;
    private String mUrl = "";
    private String mFile_name = "";
    private int mMaxFrame = 0;
    private int defaultMaxRemainingTime = 22;
    private int remainingTime = 0;
    public static boolean isEngineUpdateRemainReboot = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (mProgressDialog == null) {
                mProgressDialog = mProgressDialog.getInstance(mContext);
                mProgressDialog.show();
            }

            if (mUpdateProgressDialog == null) {
                mUpdateProgressDialog = UpdateProgressDialog.getInstance(mContext);
            }

            switch (msg.what) {
                case MESSAGE_START_DOW_LOAD_BIN_FILE:
                    mProgressDialog.showMsg(mContext
                            .getString(R.string.ui_robot_info_upgrading));
                    break;

                case MESSAGE_ROBRT_IS_BUSY:

                    Toast.makeText(mContext,
                            mContext.getString(R.string.ui_robot_info_upgrade_failed_busy), Toast.LENGTH_SHORT)
                            .show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    mListener.onUpdateFinish(false);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    break;
                case MESSAGE_FILE_LOADING_ERROR:
                    UbtLog.d("EngineUpdateManager","lihai---------MESSAGE_FILE_LOADING_ERROR->>");
                    Toast.makeText(mContext,
                            mContext.getString(R.string.ui_robot_info_upgrade_failed), Toast.LENGTH_SHORT)
                            .show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }

                    if(mUpdateProgressDialog != null){
                        mUpdateProgressDialog.cancel();
                        mUpdateProgressDialog = null;
                    }

                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    mListener.onUpdateFinish(false);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    break;
                case MESSAGE_START_DOW_LOAD_BIN_FILE_ERROR:
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
                    mListener.onUpdateFinish(false);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    break;
                case MESSAGE_UPDATE_LOADING_FILE_FINISH:

                    /*Toast.makeText(mContext,
                            mContext.getString(R.string.ui_robot_info_upgrade_success), Toast.LENGTH_SHORT)
                            .show();
                    */
                    isEngineUpdateRemainReboot = true;

                    mUpdateProgressDialog.updateTitleMsg(mContext.getString(R.string.ui_upgrade_firmware_success));
                    mUpdateProgressDialog.updateCountDown(String.valueOf(remainingTime));


                    if(!mUpdateProgressDialog.isShowing()){
                        mUpdateProgressDialog.show();
                    }

                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }

                    /*mProgressDialog.updateTitleMsg(mContext.getString(R.string.ui_robot_info_bin_remaining_title));
                    mProgressDialog.updateCountDown(String.valueOf(remainingTime));*/



                    if(remainingTime > 0){
                        remainingTime--;
                        AutoScanConnectService.doEntryUgrade(true);
                        mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_LOADING_FILE_FINISH,1000);
                    }else {
                        AutoScanConnectService.doEntryUgrade(false);
                        mHandler.sendEmptyMessage(MESSAGE_START_BACK_TO_RESCAN);

                    }

                    break;
                case MESSAGE_START_BACK_TO_RESCAN:
                    isEngineUpdateRemainReboot = false;
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }

                    if (mUpdateProgressDialog.isShowing()) {
                        mUpdateProgressDialog.cancel();
                        mUpdateProgressDialog = null;
                    }

                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    mListener.onUpdateFinish(true);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();

                    break;
                case MESSAGE_FILE_LANDING_CANCEL:

                    Toast.makeText(mContext,
                            mContext.getString(R.string.ui_remote_select_robot_synchoronize_cancel), Toast.LENGTH_SHORT)
                            .show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(
                            mSendListener);
                    mListener.onUpdateFinish(false);
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    break;
                case MESSAGE_UPDATE_LOADING_FILE:

                    mProgressDialog.updateProgress(msg.obj + "");

                    break;
                default:
                    break;
            }

        }

    };

    private BlueToothInteracter mSendListener = new BlueToothInteracter() {

        @Override
        public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

            if (cmd == ConstValue.DV_FILE_UPLOAD_START) {
                MyLog.writeLog("bin文件升级功能", "DV_FILE_UPLOAD_START");
                UbtLog.d("bin文件升级功能", "DV_FILE_UPLOAD_START");
                parseUploadStart(param);
            } else if (cmd == ConstValue.DV_FILE_UPLOADING) {
                MyLog.writeLog("bin文件升级功能", "DV_FILE_UPLOADING");
                parseUploading(param);
            } else if (cmd == ConstValue.DV_FILE_UPLOAD_END) {
                MyLog.writeLog("bin文件升级功能", "DV_FILE_UPLOAD_END");
                parseUploadEnd(param);
            } else if (cmd == ConstValue.DV_FILE_UPLOAD_CANCEL) {
                MyLog.writeLog("bin文件升级功能", "DV_FILE_UPLOAD_CANCEL");
                parseUploadCancel(param);
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

            ((AlphaApplication) mContext.getApplicationContext())
                    .setCurrentBluetooth(null);
            UbtLog.d(TAG,"isEngineUpdateRemainReboot:"+isEngineUpdateRemainReboot);
            if(!isEngineUpdateRemainReboot){
                Message msg = new Message();
                msg.what = MESSAGE_FILE_LOADING_ERROR;
                msg.obj = 0 + "";
                mHandler.sendMessage(msg);
            }
        }
    };

    private EngineUpdateManager() {
    }

    public static EngineUpdateManager getInstance(Context cont, String url,
                                                  IEngineUpdateManagerListener listener) {
        if (thiz == null)
            thiz = new EngineUpdateManager();
        thiz.mContext = cont;
        thiz.mUrl = url;
        String[] strs = thiz.mUrl.split("/");
        thiz.mFile_name = strs[strs.length - 1];
        thiz.mListener = listener;
        return thiz;
    }

    public void Update() {

        if (AlphaApplicationValues.getCurrentEdit() == EdtionCode.for_factory_edit) {
            mListener.onUpdateFinish(true);
            return;
        }

        MyLog.writeLog("bin文件升级功能",
                "com.ubt.alpha1e.update.EngineUpdateManager.Update");

        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE;
        mHandler.sendMessage(msg);

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().intoMonopoly();

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
                    startSendBin();
                } else {
                    Message msg = new Message();
                    msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private void startSendBin() {

        remainingTime = defaultMaxRemainingTime;
        isEngineUpdateRemainReboot = false;
        // 注册蓝牙监听
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().addBlueToothInteraction(mSendListener);
        // 准备发送文件
        String Path = FileTools.update_cache + "/" + mFile_name;
        String[] savePaths = Path.split("/");
        String savePath = savePaths[savePaths.length - 1];
        mFileLoader = new FileUploader(this, Path, savePath);
        // 发送传文件命令
        final int maxFrame = openFile(Path);
        byte[] data = FileTools.packData(savePath, maxFrame);
        if (data != null) {
            MyLog.writeLog("bin文件升级功能", "尝试发送升级文件：Path-->" + Path
                    + ",savePaths-->" + savePath);
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mContext
                                    .getApplicationContext())
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }
    }

    private int openFile(String path) {
        File f = new File(path);
        long size = f.length();
        int maxFrame = 0;
        if (size % FileUploader.defFrameLen == 0) {
            maxFrame = (int) (size / FileUploader.defFrameLen);
        } else {
            maxFrame = (int) (size / FileUploader.defFrameLen + 1);
        }
        if (maxFrame > 65535) {
            return -1;
        }
        mMaxFrame = maxFrame;
        return maxFrame;
    }

    @Override
    public void onDownloadSize(int size) {

        MyLog.writeLog("bin文件升级", "更新进度：" + 100 * size / mMaxFrame);

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = (100 * size / mMaxFrame);
        mHandler.sendMessage(msg);
    }

    @Override
    public void sendCMD(byte flag, byte[] data) {

        MyLog.writeLog("bin文件升级", "发送文件：" + data.length + "(bit)");

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().sendCommand(
                ((AlphaApplication) mContext.getApplicationContext())
                        .getCurrentBluetooth().getAddress(), flag,
                data, data.length, true);
    }

    @Override
    public void UploadFail() {
        mFileLoader = null;
        Message msg_error = new Message();
        msg_error.what = MESSAGE_FILE_LOADING_ERROR;
        mHandler.sendMessage(msg_error);
    }

    private void parseUploadStart(byte[] param) {

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = 0 + "";
        mHandler.sendMessage(msg);

        UbtLog.d("bin文件升级功能",
                "进入parseUploadStart" + mFileLoader == null ? "null"
                        : "not null" + ",param[0]-->" + param[0]);
        // ---------------------------------------------------------------
        switch (param[0]) {
            case 0x00:
                mFileLoader.download();
                break;
            case 0x03:
                mFileLoader = null;
                Message msg_busy = new Message();
                msg_busy.what = MESSAGE_ROBRT_IS_BUSY;
                mHandler.sendMessage(msg_busy);
                break;
            default:
                mFileLoader = null;
                Message msg_error = new Message();
                msg_error.what = MESSAGE_FILE_LOADING_ERROR;
                mHandler.sendMessage(msg_error);
                break;
        }

    }

    private void parseUploading(byte[] param) {
        switch (param[0]) {
            case 0x00:
                mFileLoader.notityThread();
                break;
            default:
                mFileLoader = null;
                Message msg = new Message();
                msg.what = MESSAGE_FILE_LOADING_ERROR;
                mHandler.sendMessage(msg);
                break;
        }
    }// 收到发送完成信号

    private void parseUploadEnd(byte[] param) {

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE_FINISH;
        mHandler.sendMessage(msg);
        // ---------------------------------------------------------------
        switch (param[0]) {
            case 0x00:
                break;
            default:
                break;
        }
        mFileLoader = null;
    }

    private void parseUploadCancel(byte[] param) {
        Message msg = new Message();
        msg.what = MESSAGE_FILE_LANDING_CANCEL;
        mHandler.sendMessage(msg);
        switch (param[0]) {
            case 0x00:
                break;
            default:
                break;
        }
        mFileLoader = null;
    }
}

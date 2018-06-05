package com.ubt.alpha1e_edu.business;

import android.content.Context;
import android.util.Log;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.utils.log.MyLog;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface;
import com.ubtechinc.file.FileUploadProgressListener;
import com.ubtechinc.file.FileUploader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/14.
 */
public class FileSendManager implements PublicInterface.BlueToothInteracter, FileUploadProgressListener {

    private static final String TAG = "FileSendManager";

    public interface IFileSendManager {

        enum SEND_FAIL_TYPE {BUSY, ERROR, CANCEL}

        void onStartSend();

        void onSendProgressReport(int value);

        void onSendFileFinish(boolean isSuccess, SEND_FAIL_TYPE type);

    }

    private static FileSendManager thiz;
    private static FileUploader mFileLoader;
    private static Context mContext;
    private IFileSendManager mListener;
    private Boolean isContinueSendLook = false;
    private String mCurrentSourcePath;
    private String mCurrentObjPath;
    private boolean doFinish = false;
    private List<String> mFileNames;
    private List<Map<String,Integer>> sendFileNum = null;
    private boolean isBluetoothDisconnect = false;

    private FileSendManager() {
    }

    public static FileSendManager getInstance(IFileSendManager listener, Context _context) {
        if (thiz == null) {
            thiz = new FileSendManager();
        }
        thiz.mListener = listener;
        thiz.mContext = _context;
        return thiz;
    }

    public FileSendManager start() {
        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().addBlueToothInteraction(this);
        }
        return this;
    }

    public void release() {
        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().removeBlueToothInteraction(this);
        }
    }

    public void setListener(IFileSendManager listener) {
        mListener = listener;
    }


    private void doReSendFile(){

        final int maxFrame = openFile(mCurrentSourcePath);
        byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
        MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
        if (data != null) {
            ((AlphaApplication) mContext)
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mContext)
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }
    }

    public synchronized void doSendFile(List<String> _names) {
        isBluetoothDisconnect = false;
        doFinish = false;
        mFileNames = _names;
        sendFileNum = new ArrayList<Map<String,Integer>>();
        ((AlphaApplication) mContext)
                .getBlueToothManager().intoMonopoly();
        //通知开始文件传输-------------start
        mListener.onStartSend();
        //通知开始文件传输-------------end
        MyLog.writeLog("发送文件测试", "doFinish:" + doFinish + ",");
        UbtLog.d(TAG,"lihai---------mFileNames.size():"+mFileNames.size() + "  doFinish:"+doFinish);
        for (int i = 0; i < mFileNames.size() && doFinish == false; i++) {
            mCurrentSourcePath = FileTools.tmp_file_cache + "/" + mFileNames.get(i);
            boolean isFileCreateSuccess = false;
            isFileCreateSuccess = FileTools.writeAssetsToSd("Actions/" + mFileNames.get(i), mContext, mCurrentSourcePath);
            if (!isFileCreateSuccess)
                continue;
            //mCurrentObjPath = "action/" + mFileNames.get(i);
            mCurrentObjPath = FileTools.actions_gamepad_robot_path + "/" + mFileNames.get(i);
            final int maxFrame = openFile(mCurrentSourcePath);
            byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
            MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
            UbtLog.d(TAG,"lihai---------mCurrentSourcePath:"+mCurrentSourcePath + "    mCurrentObjPath:"+mCurrentObjPath);
            if (data != null) {
                ((AlphaApplication) mContext)
                        .getBlueToothManager()
                        .sendCommand(
                                ((AlphaApplication) mContext)
                                        .getCurrentBluetooth().getAddress(),
                                ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                                true);
            }

            try {
                isContinueSendLook = true;
                synchronized (isContinueSendLook) {
                    isContinueSendLook.wait(10*1000);
                }
            } catch (Exception e) {
                mListener.onSendFileFinish(false, null);
                Log.e(TAG,"onReceiveData:"+e.getMessage());
                MyLog.writeLog("发送文件测试", "isContinueSendLook.wait()失败：" + e.getMessage());
            }
            if (mFileNames != null) {
                mListener.onSendProgressReport((i + 1) * 100 / mFileNames.size());
                UbtLog.d(TAG,"onReceiveData:"+mFileNames.get(i) + "发送完毕，doFinish：" + doFinish);
            }
        }

        //发送完毕
        mFileNames = null;
        ((AlphaApplication) mContext).getBlueToothManager().outMonopoly();
        mFileLoader = null;
        if(!isBluetoothDisconnect){
            mListener.onSendFileFinish(true, null);
        }

        MyLog.writeLog("发送文件测试", "outMonopoly");
    }

    public synchronized void doSendWalkFile(List<String> _names) {
        isBluetoothDisconnect = false;
        doFinish = false;
        mFileNames = _names;
        sendFileNum = new ArrayList<Map<String,Integer>>();
        ((AlphaApplication) mContext)
                .getBlueToothManager().intoMonopoly();
        //通知开始文件传输-------------start
        mListener.onStartSend();
        //通知开始文件传输-------------end
        MyLog.writeLog("发送文件测试", "doFinish:" + doFinish + ",");
        UbtLog.d(TAG,"lihai---------mFileNames.size():"+mFileNames.size() + "  doFinish:"+doFinish);
        for (int i = 0; i < mFileNames.size() && doFinish == false; i++) {
            mCurrentSourcePath = FileTools.tmp_file_cache + "/" + mFileNames.get(i);
            boolean isFileCreateSuccess = false;
            isFileCreateSuccess = FileTools.writeAssetsToSd("Actions/walk/" + mFileNames.get(i), mContext, mCurrentSourcePath);
            if (!isFileCreateSuccess)
                continue;
            //mCurrentObjPath = "action/" + mFileNames.get(i);
            mCurrentObjPath = FileTools.actions_walk_robot_path + "/" + mFileNames.get(i);
            final int maxFrame = openFile(mCurrentSourcePath);
            byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
            MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
            UbtLog.d(TAG,"lihai---------mCurrentSourcePath:"+mCurrentSourcePath + "    mCurrentObjPath:"+mCurrentObjPath);
            if (data != null) {
                ((AlphaApplication) mContext)
                        .getBlueToothManager()
                        .sendCommand(
                                ((AlphaApplication) mContext)
                                        .getCurrentBluetooth().getAddress(),
                                ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                                true);
            }

            try {
                isContinueSendLook = true;
                synchronized (isContinueSendLook) {
                    isContinueSendLook.wait(10*1000);
                }
            } catch (Exception e) {
                mListener.onSendFileFinish(false, null);
                Log.e(TAG,"onReceiveData:"+e.getMessage());
                MyLog.writeLog("发送文件测试", "isContinueSendLook.wait()失败：" + e.getMessage());
            }
            if (mFileNames != null) {
                mListener.onSendProgressReport((i + 1) * 100 / mFileNames.size());
                UbtLog.d(TAG,"onReceiveData:"+mFileNames.get(i) + "发送完毕，doFinish：" + doFinish);
                MyLog.writeLog("发送文件测试", mFileNames.get(i) + "发送完毕，doFinish：" + doFinish);
            }
        }
        //发送完毕
        mFileNames = null;
        ((AlphaApplication) mContext)
                .getBlueToothManager().outMonopoly();
        mFileLoader = null;
        if(!isBluetoothDisconnect){
            mListener.onSendFileFinish(true, null);
        }
        MyLog.writeLog("发送文件测试", "outMonopoly");
    }

    public synchronized void doSendCourseFile(String fileDir, List<String> _names) {
        isBluetoothDisconnect = false;
        doFinish = false;
        mFileNames = _names;
        sendFileNum = new ArrayList<Map<String,Integer>>();
        ((AlphaApplication) mContext).getBlueToothManager().intoMonopoly();
        //通知开始文件传输-------------start
        mListener.onStartSend();
        //通知开始文件传输-------------end
        MyLog.writeLog("发送文件测试", "doFinish:" + doFinish + ",");
        UbtLog.d(TAG,"lihai---------mFileNames.size():"+mFileNames.size() + "  doFinish:"+doFinish);
        for (int i = 0; i < mFileNames.size() && doFinish == false; i++) {
            mCurrentSourcePath = fileDir + "/" + mFileNames.get(i);

            mCurrentObjPath = FileTools.action_robot_file_path + "/" + mFileNames.get(i);

            final int maxFrame = openFile(mCurrentSourcePath);
            byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
            MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
            UbtLog.d(TAG,"lihai---------mCurrentSourcePath:"+mCurrentSourcePath + "    mCurrentObjPath:"+mCurrentObjPath);
            if (data != null) {
                ((AlphaApplication) mContext)
                        .getBlueToothManager()
                        .sendCommand(
                                ((AlphaApplication) mContext)
                                        .getCurrentBluetooth().getAddress(),
                                ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                                true);
            }

            try {
                isContinueSendLook = true;
                synchronized (isContinueSendLook) {
                    isContinueSendLook.wait(10*1000);
                }
            } catch (Exception e) {
                mListener.onSendFileFinish(false, null);
                Log.e(TAG,"onReceiveData:"+e.getMessage());
                MyLog.writeLog("发送文件测试", "isContinueSendLook.wait()失败：" + e.getMessage());
            }
            if (mFileNames != null) {
                mListener.onSendProgressReport((i + 1) * 100 / mFileNames.size());
                UbtLog.d(TAG,"onReceiveData:"+mFileNames.get(i) + "发送完毕，doFinish：" + doFinish);
                MyLog.writeLog("发送文件测试", mFileNames.get(i) + "发送完毕，doFinish：" + doFinish);
            }
        }
        //发送完毕
        mFileNames = null;
        ((AlphaApplication) mContext).getBlueToothManager().outMonopoly();
        mFileLoader = null;
        if(!isBluetoothDisconnect){
            mListener.onSendFileFinish(true, null);
        }
        MyLog.writeLog("发送文件测试", "outMonopoly");
    }

    public synchronized void doSendCostomFile(String filePath,String fileName) {
        isBluetoothDisconnect = false;
        sendFileNum = new ArrayList<Map<String,Integer>>();
        ((AlphaApplication) mContext)
                .getBlueToothManager().intoMonopoly();
        //通知开始文件传输-------------start
        mListener.onStartSend();
        //通知开始文件传输-------------end
        mCurrentSourcePath = filePath;
        mCurrentObjPath = FileTools.actions_gamepad_robot_path + "/" + fileName;

        final int maxFrame = openFile(filePath);
        byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
        MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
        if (data != null) {
            ((AlphaApplication) mContext)
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mContext)
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }

        try {
            isContinueSendLook = true;
            synchronized (isContinueSendLook) {
                isContinueSendLook.wait(10*1000);
            }
        } catch (Exception e) {
            mListener.onSendFileFinish(false, null);
            UbtLog.d(TAG,"onReceiveData:"+e.getMessage());
            MyLog.writeLog("发送文件测试", "isContinueSendLook.wait()失败：" + e.getMessage());
        }

        //发送完毕
        ((AlphaApplication) mContext).getBlueToothManager().outMonopoly();
        mFileLoader = null;
        if(!isBluetoothDisconnect){
            mListener.onSendFileFinish(true, null);
        }
        MyLog.writeLog("发送文件测试", "outMonopoly");
    }

    public synchronized void doSendFileToRobot(String filePath,String fileName) {
        isBluetoothDisconnect = false;
        sendFileNum = new ArrayList<Map<String,Integer>>();
        ((AlphaApplication) mContext)
                .getBlueToothManager().intoMonopoly();
        //通知开始文件传输-------------start
        mListener.onStartSend();
        //通知开始文件传输-------------end
        mCurrentSourcePath = filePath;
        mCurrentObjPath = FileTools.action_robot_file_path + "/" + fileName;

        final int maxFrame = openFile(filePath);
        byte[] data = FileTools.packData(mCurrentObjPath, maxFrame);
        MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START发送");
        if (data != null) {
            ((AlphaApplication) mContext)
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mContext)
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }

        try {
            isContinueSendLook = true;
            synchronized (isContinueSendLook) {
                isContinueSendLook.wait(10*1000);
            }
        } catch (Exception e) {
            mListener.onSendFileFinish(false, null);
            UbtLog.d(TAG,"onReceiveData:"+e.getMessage());
            MyLog.writeLog("发送文件测试", "isContinueSendLook.wait()失败：" + e.getMessage());
        }

        //发送完毕
        ((AlphaApplication) mContext).getBlueToothManager().outMonopoly();
        mFileLoader = null;
        if(!isBluetoothDisconnect){
            mListener.onSendFileFinish(true, null);
        }
        MyLog.writeLog("发送文件测试", "outMonopoly");
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
        return maxFrame;
    }


    private void onErrorExit(IFileSendManager.SEND_FAIL_TYPE type) {
        mFileLoader = null;
        mListener.onSendFileFinish(false, type);
        doFinish = true;
        if (isContinueSendLook) {
            synchronized (isContinueSendLook) {
                isContinueSendLook.notify();
            }
            isContinueSendLook = false;
        }
    }

    private void parseUploadStart(byte[] param) {
        MyLog.writeLog("发送文件测试", "parseUploadStart-->" + param[0] + ";mCurrentSourcePath-->" + mCurrentSourcePath + ";mCurrentObjPath-->" + mCurrentObjPath);
        UbtLog.d("发送文件测试", "parseUploadStart-->" + param[0] + ";mCurrentSourcePath-->" + mCurrentSourcePath + ";mCurrentObjPath-->" + mCurrentObjPath);
        switch (param[0]) {
            case 0x08:
                if (isContinueSendLook) {
                    synchronized (isContinueSendLook) {
                        isContinueSendLook.notify();
                    }
                    isContinueSendLook = false;

                }
                break;
            case 0x00:
                if (mCurrentSourcePath != null
                        && mCurrentObjPath != null) {
                    mFileLoader = new FileUploader(this, mCurrentSourcePath,
                            mCurrentObjPath);
                    mFileLoader.download();
                }
                break;
            case 0x03:
                onErrorExit(IFileSendManager.SEND_FAIL_TYPE.BUSY);
                break;
            default:
                onErrorExit(IFileSendManager.SEND_FAIL_TYPE.ERROR);
                break;
        }

    }

    private void parseUploading(byte[] param) {
        MyLog.writeLog("发送文件测试", "parseUploading");
        switch (param[0]) {
            case 0x08:
                if (isContinueSendLook) {
                    synchronized (isContinueSendLook) {
                        isContinueSendLook.notify();
                    }
                    isContinueSendLook = false;
                }
                break;
            case 0x00:
                mFileLoader.notityThread();
                break;
            default:
                onErrorExit(IFileSendManager.SEND_FAIL_TYPE.ERROR);
                break;
        }
    }

    private void parseUploadEnd(byte[] param) {
        switch (param[0]) {
            case 0x08:
                break;
            case 0x00:
                for(Map<String,Integer> map : sendFileNum){
                    if(map.containsKey(mCurrentObjPath)){
                        sendFileNum.remove(map);
                        break;
                    }
                }
                break;
            case 0x0b:
                //所传动作文件为0kb需重传
                boolean flag = false;
                for(Map<String,Integer> map : sendFileNum){
                    if(map.containsKey(mCurrentObjPath)){
                        flag = true;
                        int num = map.get(mCurrentObjPath);
                        UbtLog.d(TAG,"所传动作文件为0kb需重传,文件名："+mCurrentObjPath + "  重复次数：" + (num + 1));
                        if(num < 3){
                            map.put(mCurrentObjPath,++num);
                        }else {
                            return;
                        }
                        break;
                    }
                }

                if(!flag){
                    UbtLog.d(TAG,"所传动作文件为0kb需重传,文件名："+mCurrentObjPath + "  重复次数：1");
                    Map<String,Integer> map = new HashMap<String, Integer>();
                    map.put(mCurrentObjPath,1);
                    sendFileNum.add(map);
                }

                doReSendFile();
                break;
            default:
                break;
        }
        if(param[0] == 0x0b ){
            return;
        }
        if (isContinueSendLook) {
            synchronized (isContinueSendLook) {
                isContinueSendLook.notify();
            }
            isContinueSendLook = false;
        }
    }

    private void parseUploadCancel(byte[] param) {
        onErrorExit(IFileSendManager.SEND_FAIL_TYPE.CANCEL);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        UbtLog.d(TAG,"onReceiveData::"+"mac:"+mac+",cmd:"+cmd+",param:"+param[0]+",len:"+len);

        if (cmd == ConstValue.DV_FILE_UPLOAD_START) {
            MyLog.writeLog("发送文件测试", "DV_FILE_UPLOAD_START收到");
            parseUploadStart(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOADING) {
            parseUploading(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_END) {
            parseUploadEnd(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_CANCEL) {
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
        UbtLog.d(TAG,"--onDeviceDisConnected--");
        isBluetoothDisconnect = true;
        onErrorExit(IFileSendManager.SEND_FAIL_TYPE.ERROR);
    }

    @Override
    public void onDownloadSize(int size) {
        MyLog.writeLog("发送文件测试", "onDownloadSize-->" + size);
    }

    @Override
    public void sendCMD(byte flag, byte[] data) {
        MyLog.writeLog("发送文件测试", "sendCMD");
        ((AlphaApplication) mContext).getBlueToothManager().sendCommand(((AlphaApplication) mContext).getCurrentBluetooth().getAddress(), flag, data, data.length, true);
    }

    @Override
    public void UploadFail() {
        onErrorExit(IFileSendManager.SEND_FAIL_TYPE.ERROR);
    }
}

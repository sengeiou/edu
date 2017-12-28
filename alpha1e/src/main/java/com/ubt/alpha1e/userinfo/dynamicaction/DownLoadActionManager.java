package com.ubt.alpha1e.userinfo.dynamicaction;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author：liuhai
 * @date：2017/12/21 10:40
 * @modifier：ubt
 * @modify_date：2017/12/21 10:40
 * [A brief description] 动作下载逻辑
 * version
 */

public class DownLoadActionManager {
    private static final String TAG = DownLoadActionManager.class.getSimpleName();

    private static DownLoadActionManager singleton;
    private static Context mContext;

    // 所有正在下载的任务
    private List<DynamicActionModel> mRobotDownList;
    private List<DynamicActionModel> downLoadCompleteList;
    private DynamicActionModel playingInfo;
    PlayState mPlayState = PlayState.action_init;

    private BlockingQueue<ActionInfo> mQueue;

    private List<DownLoadActionListener> mDownLoadActionListeners;

    private List<String> robotActionList = new ArrayList<>();
    Handler mhandler;
    public static int STATU_INIT = 1;//初始化
    public static int STATU_DOWNLOADING = 2;//正在下载
    public static int STATU_PLAYING = 3;//正在播放
    public static int STATU_PLAY_FINISH = 4;//播放结束

    public static enum PlayState {
        action_init, action_playing, action_pause, action_finish
    }

    public enum Action_type {
        Story_type, Dance_type, Base_type, Custom_type, My_download, My_new, My_download_local, My_new_local, My_gamepad, All, Unkown, MY_WALK, MY_COURSE
    }

    public static Action_type getDataType = Action_type.Unkown;
    // 所有监听者
    private List<ActionsDownLoadManagerListener> mDownListenerLists;

    private DownLoadActionManager() {
        mRobotDownList = new ArrayList<>();
        downLoadCompleteList = new ArrayList<>();
        mDownLoadActionListeners = new ArrayList<>();
        mQueue = new LinkedBlockingDeque<ActionInfo>();
        mhandler = new Handler(Looper.getMainLooper());
        addBluetoothListener();
    }

    private void printAll() {
        ActionInfo value;
        Iterator iter = mQueue.iterator();
        while (iter.hasNext()) {
            value = (ActionInfo) iter.next();
            UbtLog.d(TAG, "ActionInfo====" + value.toString());
        }

    }

    public static DownLoadActionManager getInstance(Context context) {
        mContext = context.getApplicationContext();
        if (singleton == null) {
            synchronized (DownLoadActionManager.class) {
                if (singleton == null) {
                    singleton = new DownLoadActionManager();
                }
            }
        }
        return singleton;
    }

    private PublicInterface.BlueToothInteracter mBluetoothListener = new PublicInterface.BlueToothInteracter() {

        @Override
        public void onReceiveData(String mac, final byte cmd, final byte[] param, int len) {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (cmd == ConstValue.DV_DO_DOWNLOAD_ACTION) {
                        String downloadProgressJson = BluetoothParamUtil.bytesToString(param);
                        UbtLog.d(TAG, "downloadProgressJson : " + downloadProgressJson);
                        DownloadProgressInfo downloadProgressInfo = GsonImpl.get().toObject(downloadProgressJson, DownloadProgressInfo.class);
                        DynamicActionModel actionInfo = getRobotDownloadActionById(downloadProgressInfo.actionId);
                        if (actionInfo == null) {
                            UbtLog.d(TAG, "actionInfo : null ");
                            return;
                        }
                        for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                            if (mActionListener != null) {
                                mActionListener.getDownLoadProgress(actionInfo, downloadProgressInfo);
                            }
                        }
                        if (downloadProgressInfo.status == 1) {
                            //下载中
                            UbtLog.d(TAG, "机器人下载进度, actionName : " + actionInfo.getActionName() + " " + downloadProgressInfo.progress);
                        } else {
                            //2 下载成功 3 未联网 0 下载失败
                            //UbtLog.d(TAG,"actionInfo : " + actionInfo );
                            FileDownloadListener.State state;
                            if (downloadProgressInfo.status == 3) {
                                state = FileDownloadListener.State.connect_fail;
                            } else if (downloadProgressInfo.status == 2) {
                                state = FileDownloadListener.State.success;
                                UbtLog.d(TAG, "机器人下载成功：hts_file_name = " + actionInfo.getActionName());
                                //机器人下载成功，加入缓存
                                if (null != downLoadCompleteList && !TextUtils.isEmpty(actionInfo.getActionName())) {
                                    UbtLog.d(TAG, "机器人下载成功：hts_file_name = " + actionInfo.getActionName());
                                    downLoadCompleteList.add(actionInfo);
                                }
                            } else {
                                state = FileDownloadListener.State.fail;
                            }
                            UbtLog.d(TAG, "机器人下载结束, actionName : " + actionInfo.getActionName() + " state : " + state + "  ");
                            mRobotDownList.remove(actionInfo);
                        }


                    } else if (cmd == ConstValue.DV_READ_NETWORK_STATUS) {

                    } else if ((cmd & 0xff) == (ConstValue.DV_DO_CHECK_ACTION_FILE_EXIST & 0xff)) {
                        UbtLog.d(TAG, "播放文件是否存在：" + param[0]);
                        downRobotAction(playingInfo);
                        UbtLog.d(TAG, "播放文件：" + FileTools.actions_download_robot_path + "/" + playingInfo.getActionName() + ".hts");
                    } else if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {
                        if (getDataType == Action_type.MY_WALK) {
                            return;
                        }
                        String name = BluetoothParamUtil.bytesToString(param);
                        UbtLog.d(TAG, "获取文件：" + name);
                        robotActionList.add(name);
                    } else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
                        UbtLog.d(TAG, "获取文件结束");
                        for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                            if (null != mActionListener) {
                                mActionListener.getRobotActionLists(robotActionList);
                            }
                        }
                    } else if (cmd == ConstValue.DV_ACTION_FINISH) {
                        String finishPlayActionName = BluetoothParamUtil.bytesToString(param);
                        UbtLog.d(TAG, "finishPlayActionName = " + finishPlayActionName);
                        if (null != playingInfo && finishPlayActionName.contains(playingInfo.getActionName())) {
                            playingInfo = null;
                        }
                        if (!TextUtils.isEmpty(finishPlayActionName) && finishPlayActionName.contains("初始化")) {
                        } else {
                            for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                                if (null != mActionListener) {
                                    mActionListener.playActionFinish(finishPlayActionName);
                                }
                            }
                        }
                    }
                }
            });

        }

        @Override
        public void onSendData(String mac, byte[] datas, int nLen) {

        }

        @Override
        public void onConnectState(boolean bsucceed, String mac) {

        }

        @Override
        public void onDeviceDisConnected(String mac) {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    mRobotDownList.clear();
                    downLoadCompleteList.clear();
                    playingInfo = null;
                    for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                        if (null != mActionListener) {
                            mActionListener.onBlutheDisconnected();
                        }
                    }
                }
            });
            UbtLog.d(TAG, "onDeviceDisConnected...");

        }
    };


    // 添加监听者
    public void addDownLoadActionListener(DownLoadActionListener listener) {
        if (!mDownLoadActionListeners.contains(listener)) {
            mDownLoadActionListeners.add(listener);
        }
    }


    // 移除监听者
    public void removeDownLoadActionListener(DownLoadActionListener listener) {
        if (mDownLoadActionListeners.contains(listener)) {
            mDownLoadActionListeners.remove(listener);
        }
    }
//
//    // 添加监听者
//    public void addListener(ActionsDownLoadManagerListener listener) {
//        if (!mDownListenerLists.contains(listener)) {
//            mDownListenerLists.add(listener);
//        }
//    }
//
//
//    // 移除监听者
//    public void removeListener(ActionsDownLoadManagerListener listener) {
//        if (mDownListenerLists.contains(listener)) {
//            mDownListenerLists.remove(listener);
//        }
//    }

    /**
     * 注册蓝牙监听
     */
    public void addBluetoothListener() {
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().addBlueToothInteraction(mBluetoothListener);
    }


    /**
     * 获取机器人动作列表
     */
    public void getRobotAction() {
        robotActionList.clear();
        try {
            doSendComm(ConstValue.DV_GETACTIONFILE, (new String(FileTools.actions_download_robot_path)).getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放动作文件
     *
     * @param isFromDetail 是否从详情页播放
     * @param actionInfo
     */
    public void playAction(boolean isFromDetail, DynamicActionModel actionInfo) {
        doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(FileTools.actions_download_robot_path + actionInfo.getActionOriginalId() + ".hts"));
        if (isFromDetail) {
            for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                if (null != mActionListener) {
                    mActionListener.doActionPlay(actionInfo.getActionId(), 1);
                }
            }
        }
        playingInfo = actionInfo;
    }


    /**
     * 发送机器人下载
     *
     * @param dynamicActionModel 实体类
     */
    public void downRobotAction(DynamicActionModel dynamicActionModel) {
        // mQueue.add(actionInfo);

        DynamicActionModel c_info = getRobotDownloadActionById(dynamicActionModel.getActionId());
        if (c_info == null) {
            c_info = dynamicActionModel;
            //倒序
            mRobotDownList.add(0, c_info);
        }
        String params = BluetoothParamUtil.paramsToJsonString(new String[]{dynamicActionModel.getActionId() + "",
                dynamicActionModel.getActionOriginalId(), dynamicActionModel.getActionUrl()}, ConstValue.DV_DO_DOWNLOAD_ACTION);

        /*String params = BluetoothParamUtil.paramsToJsonString(new String[]{ actionInfo.actionId + "",
                actionInfo.actionName,"https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip" }, ConstValue.DV_DO_DOWNLOAD_ACTION);*/

        UbtLog.d(TAG, "params =========== " + params);

        doSendComm(ConstValue.DV_DO_DOWNLOAD_ACTION, BluetoothParamUtil.stringToBytes(params));

    }

    private void doSendComm(byte cmd, byte[] param) {

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager()
                .sendCommand(((AlphaApplication) mContext
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress(), cmd,
                        param, param == null ? 0 : param.length,
                        false);
    }

    public void resetData() {
        if (singleton != null) {
            singleton.mRobotDownList.clear();
        }
    }

    /**
     * 获取正在播放的动作
     *
     * @return
     */
    public DynamicActionModel getPlayingInfo() {
        return playingInfo;
    }

    /**
     * 结束动作
     *
     * @param isFromDetail 是否从详情页结束
     */
    public void stopAction(boolean isFromDetail) {
        ((AlphaApplication) mContext
                .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth().getAddress(), ConstValue.DV_STOPPLAY, null, 0, false);
        if (isFromDetail) {
            for (DownLoadActionListener mActionListener : mDownLoadActionListeners) {
                if (null != mActionListener) {
                    mActionListener.doActionPlay(playingInfo.getActionId(), 0);
                }
            }
        }
        this.playingInfo = null;
    }

    /**
     * 根据id获取ActionInfo
     *
     * @param id
     * @return
     */
    public DynamicActionModel getRobotDownloadActionById(long id) {
        DynamicActionModel info = null;
        for (int i = 0; i < mRobotDownList.size(); i++) {
            if (mRobotDownList.get(i).getActionId() == id) {
                info = mRobotDownList.get(i);
            }
        }
        return info;
    }

    /**
     * 判断是否在下载中
     *
     * @param action_id
     * @return
     */
    public boolean isRobotDownloading(long action_id) {
        boolean result = false;
        for (int i = 0; i < mRobotDownList.size(); i++) {
            if (mRobotDownList.get(i).getActionId() == action_id) {
                result = true;
            }
        }
        return result;
    }


    /**
     * 获取本地下载列表
     *
     * @return
     */
    public List<DynamicActionModel> getRobotDownList() {
        return mRobotDownList;
    }

    /**
     * 根据id获取ActionInfo
     *
     * @param id
     * @return
     */
    public boolean isCompletedActionById(long id) {
        boolean result = false;
        for (int i = 0; i < downLoadCompleteList.size(); i++) {
            if (downLoadCompleteList.get(i).getActionId() == id) {
                result = true;
                break;
            }
        }
        return result;
    }

    public interface DownLoadActionListener {
        void getRobotActionLists(List<String> list);

        void getDownLoadProgress(DynamicActionModel info, DownloadProgressInfo progressInfo);

        void playActionFinish(String actionName);

        void onBlutheDisconnected();

        void doActionPlay(long actionId, int statu);
    }

}

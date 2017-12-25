package com.ubt.alpha1e.userinfo.dynamicaction;

import android.content.Context;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    private List<ActionInfo> mRobotDownList;
    private List<ActionInfo> downLoadCompleteList;
    private ActionInfo playingInfo;
    PlayState mPlayState = PlayState.action_init;

    GetRobotActionListener mActionListener;
    private List<String> robotActionList = new ArrayList<>();

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
        addBluetoothListener();
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
        public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

            if (cmd == ConstValue.DV_DO_DOWNLOAD_ACTION) {
                String downloadProgressJson = BluetoothParamUtil.bytesToString(param);

                DownloadProgressInfo downloadProgressInfo = GsonImpl.get().toObject(downloadProgressJson, DownloadProgressInfo.class);
                ActionInfo actionInfo = getRobotDownloadActionById(downloadProgressInfo.actionId);

                UbtLog.d(TAG, "downloadProgressJson : " + downloadProgressJson);
                if (actionInfo == null) {
                    UbtLog.d(TAG, "actionInfo : null ");
                    return;
                }
                if (mActionListener != null) {
                    mActionListener.getDownLoadProgress(actionInfo, downloadProgressInfo);
                }
                if (downloadProgressInfo.status == 1) {
                    //下载中
                    UbtLog.d(TAG, "机器人下载进度, actionName : " + actionInfo.actionName + " " + downloadProgressInfo.progress);
                } else {
                    //2 下载成功 3 未联网 0 下载失败
                    //UbtLog.d(TAG,"actionInfo : " + actionInfo );
                    FileDownloadListener.State state;
                    if (downloadProgressInfo.status == 3) {
                        state = FileDownloadListener.State.connect_fail;
                    } else if (downloadProgressInfo.status == 2) {
                        state = FileDownloadListener.State.success;
                        UbtLog.d(TAG, "机器人下载成功：hts_file_name = " + actionInfo.hts_file_name);
                        //机器人下载成功，加入缓存
                        if (null != downLoadCompleteList && !TextUtils.isEmpty(actionInfo.hts_file_name)) {
                            UbtLog.d(TAG, "机器人下载成功：hts_file_name = " + actionInfo.hts_file_name);
                            downLoadCompleteList.add(actionInfo);
                        }
                    } else {
                        state = FileDownloadListener.State.fail;
                    }

                    UbtLog.d(TAG, "机器人下载结束, actionName : " + actionInfo.actionName + " state : " + state + "  ");

                    mRobotDownList.remove(actionInfo);
                }


            } else if (cmd == ConstValue.DV_READ_NETWORK_STATUS) {

            } else if ((cmd & 0xff) == (ConstValue.DV_DO_CHECK_ACTION_FILE_EXIST & 0xff)) {
                UbtLog.d(TAG, "播放文件是否存在：" + param[0]);
                downRobotAction(playingInfo);
                //   doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(FileTools.actions_download_robot_path + "/" + playingInfo.actionName + ".hts"));
                UbtLog.d(TAG, "播放文件：" + FileTools.actions_download_robot_path + "/" + playingInfo.actionName + ".hts");
            } else if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {
                if (getDataType == Action_type.MY_WALK) {
                    return;
                }

                String name = BluetoothParamUtil.bytesToString(param);
                UbtLog.d(TAG, "获取文件：" + name);
                robotActionList.add(name);


            } else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
                UbtLog.d(TAG, "获取文件结束");
                if (null != mActionListener) {
                    mActionListener.getRobotActionLists(robotActionList);
                }
            } else if (cmd == ConstValue.DV_ACTION_FINISH) {
                String finishPlayActionName = BluetoothParamUtil.bytesToString(param);
                UbtLog.d(TAG, "finishPlayActionName = " + finishPlayActionName);
                if (finishPlayActionName.contains(playingInfo.actionName)) {
                    playingInfo = null;
                }
                if (!TextUtils.isEmpty(finishPlayActionName) && finishPlayActionName.contains("初始化")) {
                    //return;
                } else {
                    if (null != mActionListener) {
                        mActionListener.playActionFinish(finishPlayActionName);
                    }

                }
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

            UbtLog.d(TAG, "onDeviceDisConnected...");
            mRobotDownList.clear();
            downLoadCompleteList.clear();
            playingInfo = null;
            if (null != mActionListener) {
                mActionListener.onBlutheDisconnected();
            }
        }
    };


    // 添加监听者
    public void addListener(ActionsDownLoadManagerListener listener) {
        if (!mDownListenerLists.contains(listener)) {
            mDownListenerLists.add(listener);
        }
    }

    // 移除监听者
    public void removeListener(ActionsDownLoadManagerListener listener) {
        if (mDownListenerLists.contains(listener)) {
            mDownListenerLists.remove(listener);
        }
    }

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
    public void getRobotAction(GetRobotActionListener listener) {
        this.mActionListener = listener;
        robotActionList.clear();
        try {
            doSendComm(ConstValue.DV_GETACTIONFILE, (new String(FileTools.actions_download_robot_path)).getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 总的处理接口，首先判断文件是否下载过，
     *
     * @param isDownload
     * @param actionInfo
     * @return 返回状态
     */
    public int dealAction(boolean isDownload, ActionInfo actionInfo) {
        if (isRobotDownloading(actionInfo.actionId)) {
            return STATU_INIT;
        }
        if (isCompletedActionById(actionInfo.actionId)) {//本地下载过，直接播放
            playingInfo = actionInfo;
            doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(actionInfo.actionName));
            return STATU_PLAYING;
        } else {
            if (isDownload) {
                playingInfo = actionInfo;
                doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(actionInfo.actionName));
                return STATU_PLAYING;
            } else {
                downRobotAction(actionInfo);
                return STATU_DOWNLOADING;
            }
        }
    }


    /**
     * 播放动作文件
     *
     * @param actionInfo
     */
    public void playAction(ActionInfo actionInfo) {
        playingInfo = actionInfo;
        doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(actionInfo.actionName));
    }


    /**
     * 发送机器人下载
     *
     * @param actionInfo
     */
    public void downRobotAction(ActionInfo actionInfo) {

        ActionInfo c_info = getRobotDownloadActionById(actionInfo.actionId);
        if (c_info == null) {
            c_info = actionInfo;
            //倒序
            mRobotDownList.add(0, c_info);
        }
        String params = BluetoothParamUtil.paramsToJsonString(new String[]{actionInfo.actionId + "",
                actionInfo.actionName, actionInfo.actionPath}, ConstValue.DV_DO_DOWNLOAD_ACTION);

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
    public ActionInfo getPlayingInfo() {
        return playingInfo;
    }

    /**
     * 结束动作
     */
    public void stopAction() {
        ((AlphaApplication) mContext
                .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth().getAddress(), ConstValue.DV_STOPPLAY, null, 0, false);
    }

    /**
     * 根据id获取ActionInfo
     *
     * @param id
     * @return
     */
    public ActionInfo getRobotDownloadActionById(long id) {
        ActionInfo info = null;
        for (int i = 0; i < mRobotDownList.size(); i++) {
            if (mRobotDownList.get(i).actionId == id) {
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
            if (mRobotDownList.get(i).actionId == action_id) {
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
    public List<ActionInfo> getRobotDownList() {
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
            if (downLoadCompleteList.get(i).actionId == id) {
                result = true;
                break;
            }
        }
        return result;
    }

    public interface GetRobotActionListener {
        void getRobotActionLists(List<String> list);

        void getDownLoadProgress(ActionInfo info, DownloadProgressInfo progressInfo);

        void playActionFinish(String actionName);

        void onBlutheDisconnected();
    }

}

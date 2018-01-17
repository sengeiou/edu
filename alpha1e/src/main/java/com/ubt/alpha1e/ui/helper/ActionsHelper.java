package com.ubt.alpha1e.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.FillLocalContent;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.ActionPlayer.Play_state;
import com.ubt.alpha1e.business.ActionPlayer.Play_type;
import com.ubt.alpha1e.business.ActionPlayerListener;
import com.ubt.alpha1e.business.ActionsCollocationManager;
import com.ubt.alpha1e.business.ActionsDownLoadManager;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.business.NewActionPlayer;
import com.ubt.alpha1e.business.NewActionPlayer.PlayerState;
import com.ubt.alpha1e.business.NewActionsManager;
import com.ubt.alpha1e.business.NewActionsManagerListener;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener.State;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.MyActionsSyncActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.fragment.BaseMyActionsFragment;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.file.FileUploadProgressListener;
import com.ubtechinc.file.FileUploader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ActionsHelper extends BaseHelper implements
        ActionsDownLoadManagerListener, FileUploadProgressListener,
        NewActionsManagerListener {

    public static List<String> mCurrentSeletedNameList = new ArrayList<String>();
    public static final String Return_select_action_name = "Return_select_action_name";

    public static final String action_type = "action_type";
    public static final String action_info = "action_info";
    public static final String action_info_all = "action_info_all";

    public  enum Action_type {
        Story_type, Dance_type, Base_type, Custom_type, My_download, My_new, All,Unkown
    }

    public  enum Command_type {
        Do_play, Do_pause_or_continue, Do_Stop, Do_lost_power, Do_default
    }

    public  enum Action_download_state {
        not_download, downing, download_finish, send_finish
    }

    public  enum StartType {
        nomal_type, select_type_single, select_type_multiple_cycle
    }

    public static final String map_val_action = "map_val_action";
    public static final String map_val_action_logo_res = "map_val_action_logo_res";
    public static final String map_val_action_name = "map_val_action_name";
    public static final String map_val_action_type_logo_res = "map_val_action_type_logo_res";
    public static final String map_val_action_type_name = "map_val_action_type_name";
    public static final String map_val_action_time = "map_val_action_time";
    public static final String map_val_action_disc = "map_val_action_disc";
    public static final String map_val_action_scan_times = "map_val_action_scan_times";
    public static final String map_val_action_is_playing = "map_val_action_is_playing";
    public static final String map_val_action_download_state = "map_val_action_download_state";
    public static final String map_val_action_download_progress = "map_val_action_download_progress";
    public static final String map_val_action_selected = "map_val_action_selected";
    // -------------------------------
    public static final String map_val_type_logo_res = "map_val_type_logo_res";
    public static final String map_val_type_name = "map_val_type_name";
    public static final String map_val_type_num = "map_val_type_num";
    // -------------------------------
    public static final String map_val_new_action_logo_res = "map_val_new_action_logo_res";
    public static final String map_val_new_action_name = "map_val_new_action_name";
    public static final String map_val_new_action_repet = "map_val_new_action_repet";

    public static final String TRANSFOR_PARCEBLE = "TRANSFOR_PARCEBLE";
    public static final String ACTIONS_SYNC_TYPE = "ACTIONS_SYNC_TYPE";
    // -------------------------------
    private static final int MSG_DO_READ_ACTIONS = 1001;
    private static final int MSG_DO_NOTE_VOL = 1002;
    private static final int MSG_DO_NOTE_VOL_STATE = 1003;
    private static final int MSG_DO_READ_DOWNLOAD_ACTIONS_FINISH = 1004;
    private static final int MSG_DO_NOTE_LIGHT_ON = 1005;
    private static final int MSG_DO_NOTE_LIGHT_OFF = 1006;
    private static final int MSG_ON_TF_PULLED = 1007;
    private static final int MSG_DO_CHNNGE_NEW_ACTION_FINISH = 1008;
    private static final int MSG_DO_READ_NEW_ACTION_FINISH = 1009;
    // -------------------------------
    private static final int MESSAGE_START_DOW_LOAD_BIN_FILE = 11001;
    private static final int MESSAGE_UPDATE_LOADING_FILE = 11002;
    private static final int MESSAGE_UPDATE_LOADING_FILE_FINISH = 11003;
    private static final int MESSAGE_ROBRT_IS_BUSY = 11004;
    private static final int MESSAGE_FILE_LOADING_ERROR = 11005;
    private static final int MESSAGE_FILE_LANDING_CANCEL = 11006;

    // -------------------------------


    private NewActionPlayer mNewPlayer;
    public String mCurrentBaseActionsPath;
    private Activity mActivity;
    // -------------------------------

    private IActionsUI mUI;
    private ActionPlayer mPlayer;
    private ActionsCollocationManager mCollcationManager;
    private ActionsDownLoadManager mDownLoadManager;
    private static FileUploader mFileLoader;
    private String mSendFileSourcePath;
    private String mSendDFileRobotSavePath;

    private boolean mCurrentVolState;
    private int mCurrentVol;
    private int mMaxFrame = 0;
    private List<String> mActionsNames;
    private NewActionInfo mNewActionInfo;
    private NewActionsManager mNewActionsManager;

    private Boolean light_state = null;
    private static Date lastTime_doPauseMp3ForMyDownload = null;

    private static AudioManager mAudioManager;
    private static MediaPlayer mp3_player;
    private static Timer mp3_delay;

    private static Action_type mCurrentPlayType = null;

    public  int mCurrentPlayPos = -1;
    public  int mCurrentIntendtoPlay = -1;
    public  int mCurrentRemovedItem = -1;

    public Action_type getCurrentPlayType() {
        return mCurrentPlayType;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == MSG_DO_READ_ACTIONS) {
                    mUI.onReadActionsFinish(mActionsNames);
                } else if (msg.what == MSG_DO_NOTE_VOL) {
                    // mUI.onNoteVol((mCurrentVol * 100) / 255);
                    int value = mCurrentVol;
                    value *= 100;
                    int add_val = 0;
                    if (value % 255 >= 127.5) {
                        add_val = 1;
                    }
                    value /= 255;
                    value += add_val;
                    mUI.onNoteVol(value);

                } else if (msg.what == MSG_DO_NOTE_VOL_STATE) {
                    mUI.onNoteVolState(mCurrentVolState);
                } else if (msg.what == MSG_DO_READ_DOWNLOAD_ACTIONS_FINISH) {
                    List<ActionRecordInfo> history = null;
                    if (msg.obj == null)
                        history = new ArrayList<ActionRecordInfo>();
                    else
                        history = (List<ActionRecordInfo>) msg.obj;
                    mUI.onReadMyDownLoadHistory(ActionsHelper.this.hashCode()
                            + "", history);
                } else if (msg.what == MESSAGE_START_DOW_LOAD_BIN_FILE) {
                    mUI.onSendFileStart();
                } else if (msg.what == MESSAGE_ROBRT_IS_BUSY) {

                    mUI.onSendFileBusy();
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_FILE_LOADING_ERROR) {
                    mUI.onSendFileError();

                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_UPDATE_LOADING_FILE_FINISH) {
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    mUI.onSendFileFinish(mCurrentIntendtoPlay);
                } else if (msg.what == MESSAGE_FILE_LANDING_CANCEL) {
                    mUI.onSendFileCancel();

                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_UPDATE_LOADING_FILE) {
                    mUI.onSendFileUpdateProgress(msg.obj + "");
                } else if (msg.what == MSG_DO_NOTE_LIGHT_ON) {

                    mUI.noteLightOn();

                } else if (msg.what == MSG_DO_NOTE_LIGHT_OFF) {

                    mUI.noteLightOff();

                } else if (msg.what == MSG_ON_TF_PULLED) {
                    mUI.noteTFPulled();
                } else if (msg.what == MSG_DO_CHNNGE_NEW_ACTION_FINISH) {
                    mUI.onChangeNewActionsFinish();
                } else if (msg.what == MSG_DO_READ_NEW_ACTION_FINISH) {
                    mUI.onReadNewActionsFinish((List<NewActionInfo>) msg.obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    public ActionsHelper(IActionsUI _ui, BaseActivity baseActivity) {

        super(baseActivity);
        this.mActivity = baseActivity;
        this.mUI = _ui;
        mCollcationManager = ActionsCollocationManager
                .getInstance(mBaseActivity);
        mDownLoadManager = ActionsDownLoadManager.getInstance(baseActivity);
        mNewActionsManager = NewActionsManager.getInstance(baseActivity);
//        mCollcationManager.addListener(mUI);
//        mDownLoadManager.addListener(mUI);
//        mDownLoadManager.addListener(this);
//        mNewActionsManager.addListener(this);
        mActionsNames = new ArrayList<String>();

        if (mBaseActivity.isBulueToothConnected()) {
//            initActionPlayer();
//            initNewActionPlayer();
        }
    }


    public void initActionPlayer( )
    {
        if (mPlayer == null) {

            mPlayer = ActionPlayer.getInstance(((AlphaApplication) mBaseActivity
                            .getApplicationContext()).getBlueToothManager(),
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getCurrentBluetooth().getAddress());
            mPlayer.addListener(mUI);
        }
    }

    public void initNewActionPlayer()
    {

        if (mNewPlayer == null) {
            mNewPlayer = NewActionPlayer
                    .getPlayer(((AlphaApplication) mBaseActivity
                            .getApplicationContext()).getCurrentBluetooth()
                            .getAddress());

        }
        if (mNewPlayer != null) {
            mNewPlayer.setListener(mUI);
        }
    }

    public BaseWebRunnable doReadCollcationRecord() {
        return mCollcationManager.getCollcationRecord();
    }

    public boolean isDownloading(long action_id) {

        return mDownLoadManager.isDownloading(action_id);
    }

    public void doDelCollocation(ActionColloInfo mInfo) {
        // TODO Auto-generated method stub
        mCollcationManager.removeWebRecord(mInfo.collectRelationId);
    }


    /**
     * 同步我的创建,下载记录先写文件
     */
    public void doWriteDownLoadFile(List<NewActionInfo> infos) {

        mNewActionsManager.doWriteSyncListToFile(infos);

    }

    public void removePlayerListeners(ActionPlayerListener listener)
    {
        if(mPlayer!=null)
            mPlayer.removeListener(listener);

    }
    public void addPlayerListeners(ActionPlayerListener listener)
    {
        if(mPlayer!=null)
            mPlayer.addListener(listener);

    }
    public void UnRegisterHelper() {
        super.UnRegisterHelper();

//        mDownLoadManager.removeListener(mUI);
//        mDownLoadManager.removeListener(this);
//        mNewActionsManager.removeListener(this);
//        mCollcationManager.removeListener(mUI);
//        if(mPlayer!=null)
//            mPlayer.removeListener(mUI);
    }

    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
//        mCollcationManager.addListener(mUI);
//        mDownLoadManager.addListener(mUI);
//        mDownLoadManager.addListener(this);
//        mNewActionsManager.addListener(this);
//        if(mPlayer!=null)
//            mPlayer.addListener(mUI);
    }

    public void doReadMyNewAction() {

        mNewActionsManager.doRead();

    }

    public Play_state getPlayerState() {
        if (this.mPlayer == null) {
            return Play_state.action_finish;
        } else {
            return this.mPlayer.getState();
        }
    }

    public String getPlayerName() {
        if (this.mPlayer == null) {
            return "";
        } else {
            String name = this.mPlayer.getPlayActonName();
            if (name.equals(ActionPlayer.CYCLE_ACTION_NAME)) {
                name = mBaseActivity.getResources().getString(
                        R.string.ui_action_cycle_name);
            } else if ("#@%".contains(name.toCharArray()[0] + "")) {
                name = name.substring(1);
            }
            return name;
        }
    }

    public PlayerState getNewPlayerState() {
        if (this.mNewPlayer == null) {
            return PlayerState.STOPING;
        } else {
            return this.mNewPlayer.getState();
        }
    }

    public String getNewPlayerName() {
        if (this.mNewPlayer == null) {
            return "";
        } else {
            String name = this.mNewPlayer.getPlayActonName();
            try {
                if ("#@%".contains(name.toCharArray()[0] + "")) {
                    name = name.substring(1);
                }
            } catch (Exception e) {
                name = "";
            }
            return name;
        }
    }

    public Play_type getPlayerType() {
        if (this.mPlayer == null) {
            return null;
        } else {
            return this.mPlayer.getPlayType();
        }
    }

    public int getCycleNum() {
        return mPlayer.getCycleNum();
    }

    private void doPlayMyNewAction(NewActionInfo info) {
        mNewPlayer.PlayAction(info, mBaseActivity);

    }

    public void doActionCommand(Command_type comm_type, Object action_obj,
                                Action_type act_type) {

        if (!mBaseActivity.checkCoon()) {
            return;
        }

        if (comm_type == Command_type.Do_play) {
            MyLog.writeLog("播放功能", "Do_play");
            mCurrentPlayType = act_type;
            if (mCurrentPlayType == Action_type.My_new) {
                mPlayer.doStopPlay();

                NewActionInfo info = (NewActionInfo) action_obj;

                doPlayMyNewAction(info);

            } else {
                mNewPlayer.StopPlayer();
                if (mCurrentPlayType == Action_type.My_download) {
                    mPlayer.doPlayAction((ActionInfo) action_obj);
                } else {
                    ActionInfo info = new ActionInfo();
                    info.actionName = action_obj.toString();
                    mPlayer.doPlayAction(info);
                }

            }
        } else if (comm_type == Command_type.Do_pause_or_continue) {
            if (mNewPlayer.getState() != PlayerState.STOPING) {
                if (mNewPlayer.getState() == PlayerState.PAUSING) {
                    mNewPlayer.ContinuePlayer();
                } else if (mNewPlayer.getState() == PlayerState.PLAYING) {
                    mNewPlayer.PausePalyer();
                }
            } else {
                mPlayer.doPauseOrContinueAction();
            }

        } else if (comm_type == Command_type.Do_Stop) {
            if (mNewPlayer.getState() != PlayerState.STOPING)
                mNewPlayer.StopPlayer();
            else {
                mPlayer.doStopPlay();
            }

        } else if (comm_type == Command_type.Do_default) {
            if (mNewPlayer.getState() != PlayerState.STOPING) {
                mNewPlayer.StopPlayer();
            }
            mPlayer.doDefault();

        } else if (comm_type == Command_type.Do_lost_power) {

            if (mNewPlayer.getState() != PlayerState.STOPING) {
                mNewPlayer.StopPlayer();
            }

            MyLog.writeLog("掉电功能测试", "mPlayer.doLostPower");

            mPlayer.doLostPower(((AlphaApplication) mBaseActivity
                    .getApplicationContext()).getCurrentBluetooth()
                    .getAddress());

        }

    }

    public static void ChangeMisucVol(int _pow) {

        if (mAudioManager != null) {
            try {
                int pow_music = mAudioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                pow_music = pow_music * _pow / 100;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        pow_music, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doChangeVol(int _pow) {

        int pow = _pow * 255;
        int add_val = 0;
        if (_pow % 100 >= 50) {
            add_val = 1;
        }
        pow /= 100;
        pow += add_val;

        MyLog.writeLog("音量检测", "发送调整指令" + pow);

        byte[] param = new byte[1];
        param[0] = (byte) (pow & 0xff);

        doSendComm(ConstValue.DV_VOLUME, param);
        ChangeMisucVol(_pow);
    }

    public void doTurnVol() {

        byte[] papram = new byte[1];
        if (mCurrentVolState) {
            papram[0] = 0;
        } else {
            papram[0] = 1;
        }

        doSendComm(ConstValue.DV_VOICE, papram);

    }

    public List<String> doGetActions(Action_type type) {

        if (type.equals( Action_type.All)) {
            return mActionsNames;
        }

        List<String> actions = new ArrayList<String>();
        String title = "";
        if (type == Action_type.Dance_type)
            title = "#";
        else if (type == Action_type.Base_type)
            title = "@";
        else if (type == Action_type.Story_type)
            title = "%";

        for (int i = 0; i < mActionsNames.size(); i++) {
            if (mActionsNames.get(i).startsWith(title)) {
                if (type == Action_type.Custom_type) {
                    if (!"#@%".contains(mActionsNames.get(i).subSequence(0, 1))) {
                        actions.add(mActionsNames.get(i));
                    }
                } else {
                    actions.add(mActionsNames.get(i));
                }
            }

        }

        return actions;
    }

    public void doReadActions() {

        MyLog.writeLog(
                "蓝牙掉线测试",
                "_ActionsHelper.doReadActions-->"
                        + (((AlphaApplication) mBaseActivity
                        .getApplicationContext()).getCurrentBluetooth() == null ? "null"
                        : "not-null"));

        if (!mActionsNames.isEmpty()) {
            mActionsNames.clear();
        }
        MyLog.writeLog("命令发送", "DV_GETACTIONFILE");
        doSendComm(ConstValue.DV_GETACTIONFILE, null);

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if((cmd+0)!=10)
        Log.e("wilsonCMD","onReceiveData:"+cmd+",parms:"+param[0]);
        if (cmd == ConstValue.DV_FILE_UPLOAD_START) {
            parseUploadStart(param);
        } else if (cmd == ConstValue.DV_MODIFY_FILENAME) {// 修改文件名
            parseModifyFileName(param, len);
        } else if (cmd == ConstValue.DV_DELETE_FILE) {// 删除文件
            parseDeleteFile(param, len);
        } else if (cmd == ConstValue.DV_FILE_UPLOADING) {
            parseUploading(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_END) {
            parseUploadEnd(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_CANCEL) {
            parseUploadCancel(param);
        } else if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {

            String name = BluetoothParamUtil.bytesToString(param);

            mActionsNames.add(name);

        } else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
            if (mPlayer != null) {
                mPlayer.setRobotActions(mActionsNames);
            }
            Message msg = new Message();
            msg.what = MSG_DO_READ_ACTIONS;
            mHandler.sendMessage(msg);
        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {
            // MyLog.writeLog("定时器测试", "_ActionsHelper-->收到状态信息");
            // 声音状态
            if (param[0] == 0) {
                // 静音
                if (param[1] == 1) {
                    mCurrentVolState = false;
                }
                // 有声音
                else {
                    mCurrentVolState = true;
                }
                Message msg = new Message();
                msg.what = MSG_DO_NOTE_VOL_STATE;
                mHandler.sendMessage(msg);

            }
            // 播放状态
            else if (param[0] == 1) {
                // 暂停
                if (param[1] == 0) {

                }
                // 非暂停
                else {

                }
            }
            // 音量状态
            else if (param[0] == 2) {

                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }

                mCurrentVol = nCurrentVolume;

                MyLog.writeLog("音量检测", "当前音量:" + mCurrentVol);

                if (mCurrentVolState) {
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_VOL;
                    mHandler.sendMessage(msg);
                }
            }
            // 舵机灯状态
            else if (param[0] == 3) {
                // 灭
                if (param[1] == 0) {
                    light_state = false;
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_LIGHT_OFF;
                    mHandler.sendMessage(msg);
                }
                // 亮
                else {
                    light_state = true;
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_LIGHT_ON;
                    mHandler.sendMessage(msg);
                }
            } else if (param[0] == 4) {
                // 拔出
                if (param[1] == 0) {
                    Message msg = new Message();
                    msg.what = MSG_ON_TF_PULLED;
                    mHandler.sendMessage(msg);
                }
                // 插入
                else {

                }
            }
        }

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    public void doPauseMyNewAction() {

        mNewPlayer.PausePalyer();
    }

    public void doContinueMyNewAction() {
        mNewPlayer.ContinuePlayer();
    }

    public void doStopMyNewAction() {
        mNewPlayer.StopPlayer();
    }

    public PlayerState doGetNewPlayerState() {
        return mNewPlayer.getState();
    }

    public void doReadMyDownLoadActions() {
        mDownLoadManager.getDownHistoryList();
    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {

    }

    @Override
    public void onSyncHistoryFinish() {
        MyLog.writeLog("读取历史数据测试", this.hashCode()
                + "：onUpdateHistoryFinish-->getDownHistoryList");
        mDownLoadManager.getDownHistoryList();
    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

        MyLog.writeLog("读取历史数据测试", this.hashCode()
                + "：onReadHistoryFinish-->MSG_DO_READ_DOWNLOAD_ACTIONS_FINISH");
        Message msg = new Message();
        msg.what = MSG_DO_READ_DOWNLOAD_ACTIONS_FINISH;
        msg.obj = history;
        mHandler.sendMessage(msg);
    }

    /**
     * 发送新建动作到机器人
     * */
    public void sendActionFileToRobot(NewActionInfo info)
    {
        if (!mBaseActivity.checkCoon()) {
            return;
        }
        // 传文件
        // 0、处理压缩文件
        // 3、立即进独占模式
        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE;
        mHandler.sendMessage(msg);
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .getBlueToothManager().intoMonopoly();
        // 4、传文件
        mSendFileSourcePath = info.actionPath_local;
        String[] savePaths = mSendFileSourcePath.split("/");
        mSendDFileRobotSavePath = "action/" + savePaths[savePaths.length - 1];
        final int maxFrame = openFile(mSendFileSourcePath);
        byte[] data = FileTools.packData(mSendDFileRobotSavePath, maxFrame);
        /**传动作需添加是否覆盖命令，直接加在data末尾*/
        if (data != null) {
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mBaseActivity
                                    .getApplicationContext())
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }


    }
    public void sendActionFileToRobot(ActionInfo info) {

        if (!mBaseActivity.checkCoon()) {
            return;
        }
        // 传文件
        // 0、处理压缩文件
        File zip_release_path = new File(FileTools.actions_download_cache + "/"
                + info.actionId);
        String file_name = info.hts_file_name;
        if (info.hts_file_name != null && !info.hts_file_name.equals("")) {
        } else {
            // 1、解压文件
            ZipTools.unZip(FileTools.actions_download_cache + "/" + info.actionId
                    + ".zip", zip_release_path.getPath());
            String[] action_files = zip_release_path.list(new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return name.endsWith(".hts");
                }
            });
            if (action_files == null || action_files.length < 1) {
                mUI.onSendFileError();
                return;
            }
            // 2、写入下载历史
            file_name = action_files[0];
            info.hts_file_name = file_name;
            mDownLoadManager.doUpdateAction(info);
        }
        // 3、立即进独占模式
        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE;
        mHandler.sendMessage(msg);
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .getBlueToothManager().intoMonopoly();
        // 4、传文件
        mSendFileSourcePath = zip_release_path + "/" + file_name;
        String[] savePaths = mSendFileSourcePath.split("/");
        mSendDFileRobotSavePath = "action/" + savePaths[savePaths.length - 1];

        final int maxFrame = openFile(mSendFileSourcePath);
        byte[] data = FileTools.packData(mSendDFileRobotSavePath, maxFrame);

        if (data != null) {

            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .getBlueToothManager()
                    .sendCommand(
                            ((AlphaApplication) mBaseActivity
                                    .getApplicationContext())
                                    .getCurrentBluetooth().getAddress(),
                            ConstValue.DV_FILE_UPLOAD_START, data, data.length,
                            true);
        }
    }

    @Override
    public void onDownloadSize(int size) {
        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = (100 * size / mMaxFrame);
        mHandler.sendMessage(msg);
    }

    @Override
    public void sendCMD(byte flag, byte[] data) {

        doSendComm(flag, data, true);

    }

    @Override
    public void UploadFail() {

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

    private void parseUploadStart(byte[] param) {

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = 0 + "";
        mHandler.sendMessage(msg);
        // ---------------------------------------------------------------
        switch (param[0]) {
            case 0x00:
                if (mFileLoader == null) {

                    if (mSendFileSourcePath != null
                            && mSendDFileRobotSavePath != null) {
                        mFileLoader = new FileUploader(this, mSendFileSourcePath,
                                mSendDFileRobotSavePath);

                        mFileLoader.download();
                    } else {
                        break;
                    }
                }
                break;
            case 0x03:

                mFileLoader = null;
                Message msg_busy = new Message();
                msg_busy.what = MESSAGE_ROBRT_IS_BUSY;
                mHandler.sendMessage(msg_busy);
                break;
            case 0x08:

                Message msg_exist = new Message();
                msg_exist.what = MESSAGE_UPDATE_LOADING_FILE_FINISH;
                mHandler.sendMessage(msg_exist);
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
    }

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

    public void doCycle(JSONArray jsonArray) {
        // TODO Auto-generated method stub
        String[] actions = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                actions[i] = jsonArray.get(i).toString();
            } catch (JSONException e) {
            }
        }
        mPlayer.doCycle(actions);

    }

    public void doTurnLight() {
        if (light_state == null)
            return;
        byte[] papram = new byte[1];
        if (light_state) {
            papram[0] = 0;// 关闭
        } else {
            papram[0] = 1;// 打开
        }

        doSendComm(ConstValue.DV_LIGHT, papram);

    }

    public void doPlayMp3ForMyDownload(ActionInfo action) {
        MyLog.writeLog("MP3播放", action.actionName);

        try {
            mp3_player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mp3_player = null;
        mAudioManager = null;
        final File mp3_path = new File(FileTools.actions_download_cache + "/"
                + action.actionId);
        if (mp3_path.exists()) {
            final String[] mp3_name = mp3_path.list(new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return name.endsWith(".mp3");
                }
            });

            if (mp3_name != null && mp3_path.length() > 0
                    && mp3_name.length > 0) {

                if (mp3_player == null) {
                    mp3_player = new MediaPlayer();
                }
                if (mAudioManager == null) {
                    try {
                        mAudioManager = (AudioManager) mBaseActivity
                                .getSystemService(Context.AUDIO_SERVICE);
                    } catch (Exception e) {
                        mAudioManager = null;
                    }
                }
                mp3_player.reset();

                try {
                    mp3_delay.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp3_delay = new Timer();
                mp3_delay.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mp3_player.setDataSource(mp3_path + "/"
                                    + mp3_name[0]);
                            mp3_player.prepare();
                            mp3_player.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);

            }
        }
    }

    public void doPauseMp3ForMyDownload() {

        // 防止连续响应-----------start
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 200;
        if (lastTime_doPauseMp3ForMyDownload != null) {
            time_difference = curDate.getTime()
                    - lastTime_doPauseMp3ForMyDownload.getTime();
        }
        lastTime_doPauseMp3ForMyDownload = curDate;
        if (time_difference < 200) {
            return;
        }
        // 防止连续响应-----------end
        if (mp3_player != null) {
            MyLog.writeLog("MP3播放", this.hashCode()
                    + ":doPauseMp3ForMyDownload-->" + mp3_player.isPlaying());
            try {
                if (mp3_player.isPlaying()) {
                    mp3_player.pause();
                } else {
                    mp3_player.start();
                }
            } catch (Exception e) {
                MyLog.writeLog("MP3播放", e.getMessage());
            }
        }
    }

    public static void doStopMp3ForMyDownload() {
        if (mp3_delay != null) {
            try {
                mp3_delay.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mp3_player != null) {
            try {
                mp3_player.stop();
                mAudioManager = null;
            } catch (Exception e) {
                MyLog.writeLog("MP3播放", e.getMessage());
            }
        }
    }

    public Bitmap getActionLogo(Action_type type, ActionInfo actionInfo,
                                float h, float w) {
        Bitmap bitmap = null;
        if (type == Action_type.My_download) {

            if (h > 0 && w > 0) {
                float new_w = w;
                float new_h = h;
                // if (h > 400) {
                // new_w = (w * 400 / h);
                // new_h = 400;
                // }
                bitmap = FileTools.readImageFromSDCacheSync(actionInfo, new_w,
                        new_h);
                // bitmap = ImageTools.compressImage(bitmap, 2);
            }

        }
        return bitmap;

    }

    public void doRename(Object action_obj, Action_type act_type,
                         String new_name) {
        if (act_type == Action_type.My_new) {

            NewActionInfo info = (NewActionInfo) action_obj;
            info.actionName = new_name;

            mNewActionsManager.doRename(info);

        } else if (act_type == Action_type.My_download) {
            //我的下载不许重命名
        } else {
            String info = action_obj.toString();
            byte[] oldNameDate;
            byte[] newNameDate;
            byte[] param;
            try {
                oldNameDate = info.getBytes("GBK");
                newNameDate = new_name.getBytes("GBK");
                byte len1 = (byte) oldNameDate.length;
                byte len2 = (byte) newNameDate.length;
                param = new byte[2 + len1 + len2];

                param[0] = len1;
                param[1] = len2;
                System.arraycopy(oldNameDate, 0, param, 2, len1);
                System.arraycopy(newNameDate, 0, param, len1 + 2, len2);
                // 发送命令到设备

                doSendComm(ConstValue.DV_MODIFY_FILENAME, param);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void parseModifyFileName(byte[] param, int len) {

        byte b = param[0];
        switch (b) {
            case 0x00:// 0k
                mUI.noteChangeFinish(
                        true,
                        mBaseActivity.getResources().getString(
                                R.string.ui_action_rename_success));
                break;
            case 0x01:// 文件不存在
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_action_name_not_exist));
                break;
            case 0x02:// DEL OTHER ERR
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_remote_synchoronize_unknown_error));
                break;
            case 0x03:// 03 BUSY
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getStringResources("ui_remote_synchoronize_busy")
                );
                break;
            case 0x08:// 08 文件已存在
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_action_name_exist));
                break;
            case 0x07:// 07 文件名不支持
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_action_name_error));
                break;

            default:
                break;
        }

    }

    private void sendDeleteFileCMD(String info) {
        // TODO Auto-generated method stub
        byte[] actions;
        try {
            actions = info.getBytes("GBK");

            doSendComm(ConstValue.DV_DELETE_FILE, actions);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doDelAction(Object action_obj, Action_type act_type) {
        if (act_type == Action_type.My_new) {
            updateItemImmediately(mCurrentRemovedItem);
            mPlayer.doStopPlay();
            NewActionInfo info = (NewActionInfo) action_obj;
            mNewActionsManager.doDelete(info);
        } else {
            if (mNewPlayer != null)
                mNewPlayer.StopPlayer();
            if (act_type == Action_type.My_download) {
                ActionInfo info = (ActionInfo) action_obj;
                mDownLoadManager.doDelAction(info);
            } else {
                String info = action_obj.toString();
                sendDeleteFileCMD(info);
            }
        }
    }

    public void parseDeleteFile(byte[] param, int len) {
        byte b = param[0];
        switch (b) {
            case 0x00:// 0k
                mUI.noteChangeFinish(true, "");
                break;
            case 0x01:// 文件不存在
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_action_name_not_exist));
                break;
            case 0x02:// DEL OTHER ERR
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_remote_synchoronize_unknown_error));
                break;
            case 0x03:// 03 BUSY
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getStringResources("ui_remote_synchoronize_busy"));
                break;
            default:
                mUI.noteChangeFinish(
                        false,
                        mBaseActivity.getResources().getString(
                                R.string.ui_remote_synchoronize_unknown_error));
                break;
        }

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo){
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {
        // TODO Auto-generated method stub
        Message msg = new Message();
        msg.what = MSG_DO_READ_NEW_ACTION_FINISH;
        msg.obj = actions;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onChangeNewActionsFinish() {
        Message msg = new Message();
        msg.what = MSG_DO_CHNNGE_NEW_ACTION_FINISH;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {
        // TODO Auto-generated method stub

    }

    public boolean isFirstEditMain() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION_FALSE))
            return false;
        return true;
    }

    public boolean isNewNewAction() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.IS_NEW_NEW_ACTION)
                .equals(BasicSharedPreferencesOperator.IS_NEW_NEW_ACTION_TRUE))
            return true;
        return false;
    }

    public boolean isNewDownloadAction() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_NEW_DOWNLOAD_ACTION)
                .equals(BasicSharedPreferencesOperator.IS_NEW_DOWNLOAD_ACTION_TRUE))
            return true;
        return false;
    }

    public void changeNewNewActionState() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_NEW_NEW_ACTION, "", null, -1);
    }

    public void changeDownloadActionState() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_NEW_DOWNLOAD_ACTION, "",
                null, -1);
    }

    public void doGetImages(List<Long> ids, List<String> urls) {
        // TODO Auto-generated method stub
        if (ids == null || urls == null)
            return;
        if (ids.size() == 0 || urls.size() == 0)
            return;
        if (ids.size() != urls.size())
            return;
        for (int i = 0; i < ids.size(); i++) {
            FileTools.readImageFromSDCacheASync(ids.get(i), urls.get(i), -1,
                    -1, mUI);
        }
    }


    public String getCurrentPlayName() {
        String name = "";
        if (getPlayerState() != Play_state.action_finish) {
            name = getPlayerName();
        } else {
            name = getNewPlayerName();
        }
        return name;
    }
    public int getCurrentPlayPosition(List<Map<String,Object>> mDatas,ActionInfo actionInfo) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).get(
                    map_val_action).equals(actionInfo)
                   ) {
                mDatas.get(i).put(
                        ActionsHelper.map_val_action_is_playing, true);
                return i;
            }
        }
        return -1;
    }
    public int getCurrentPlayPosition(List<Map<String,Object>> mDatas) {
        for (int i = 0; i < mDatas.size(); i++) {
            if ((Boolean) mDatas.get(i).get(
                    map_val_action_is_playing)
                    && !(mDatas.get(i).get(
                    map_val_action_name))
                    .equals(getCurrentPlayName())) {
                mDatas.get(i).put(
                        ActionsHelper.map_val_action_is_playing, false);
                return i;
            }
        }
        return -1;
    }

    public boolean doDownLoad(ActionInfo actionInfo) {

        UserInfo info = ((AlphaApplication) mBaseActivity
                .getApplicationContext()).getCurrentUserInfo();
        if (info == null) {
            mUI.onNoteNoUser();
            mUI.onDownLoadFileFinish(actionInfo,State.fail);
            return false;
        }

        mDownLoadManager.DownLoadAction(actionInfo);

        return true;
    }


    /**
     * 处理机器人内置动作
     * @return
     */
    public List<Map<String, Object>> loadDatas()
    {
        JSONArray actions_list = new JSONArray(
                mActionsNames);
        List<Map<String, Object>> mDatas = new ArrayList<>();
        for (int i = 0; i < actions_list.length(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
//            action_item.put(ActionsHelper.map_val_action_logo_res,
//                    R.drawable.action_local_logo_grid);
            try {
                action_item.put(ActionsHelper.map_val_action,
                        actions_list.get(i));
                String name = actions_list.get(i).toString();
//                if (mCurrentActionType != Action_type.Custom_type) {
//                    name = name.substring(1);
//                }
//                if (!name.toLowerCase().contains(key.toLowerCase())) {
//                    continue;
//                }
                action_item.put(ActionsHelper.map_val_action_name, name);
            } catch (JSONException e) {
//                action_item.put(
//                        ActionsHelper.map_val_action_name,
//                        this.getResources().getString(
//                                R.string.ui_action_no_action));
            }
            action_item.put(ActionsHelper.map_val_action_type_logo_res,
                    R.drawable.actions_item_unkown);
            action_item.put(ActionsHelper.map_val_action_type_name,
                    "未知");
            action_item.put(ActionsHelper.map_val_action_time, 0);
            action_item.put(ActionsHelper.map_val_action_disc, "暂无描述");
            action_item.put(ActionsHelper.map_val_action_is_playing, false);
            action_item.put(ActionsHelper.map_val_action_selected, false);
            mDatas.add(action_item);
        }
     return mDatas;

    }

    /**
     * my download
     * */
    public  List<Map<String, Object>>  loadDatas(List<ActionRecordInfo> mMyDownLoadHistory, List<ActionInfo> mMyDownLoading,List<String> syncList)
    {
        JSONArray actions_list = new JSONArray(
                syncList);
        List<Map<String, Object>> mDatas = new ArrayList<>();
        int[] type_logos = new int[]{R.drawable.gamepad_actions_basic_s_icon,
                R.drawable.sec_dance_icon_s, R.drawable.sec_story_icon_s};

        String[] type_names = new String[]{
                ((BaseActivity)mActivity).getStringResources("ui_action_type_basic"),
                ((BaseActivity)mActivity).getStringResources("ui_action_type_dance"),
                ((BaseActivity)mActivity).getStringResources("ui_action_type_story")};
//正在下载的动作
        for (int i = 0; i < mMyDownLoading.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            action_item.put(ActionsHelper.map_val_action,
                    mMyDownLoading.get(i));
            action_item.put(ActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);

            action_item.put(ActionsHelper.map_val_action_name,
                    mMyDownLoading.get(i).actionName);
            try {
                action_item
                        .put(ActionsLibHelper.map_val_action_type_logo_res,
                                type_logos[((int) mMyDownLoading.get(i).actionType) - 1]);
                action_item
                        .put(ActionsHelper.map_val_action_type_name,
                                type_names[((int) mMyDownLoading.get(i).actionType) - 1]);
            } catch (Exception e) {
                e.printStackTrace();
                action_item.put(
                        ActionsLibHelper.map_val_action_type_logo_res,
                        type_logos[1]);
            }

            action_item
                    .put(ActionsHelper.map_val_action_time,
                            TimeTools.getMMTime((int) mMyDownLoading
                                    .get(i).actionTime * 1000));

            if (mMyDownLoading.get(i).actionDesciber != null
                    && !mMyDownLoading.get(i).actionDesciber.equals("")) {
                action_item.put(ActionsLibHelper.map_val_action_disc,
                        mMyDownLoading.get(i).actionDesciber);
            } else {
                action_item.put(
                        ActionsLibHelper.map_val_action_disc,
                        ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
            }
            if(mMyDownLoadHistory.size()!=0)
            {
                String file_name = mMyDownLoadHistory.get(i).action.hts_file_name;
                String current_name = getCurrentPlayName();
                if (file_name != null && current_name.equals(file_name.split("\\.")[0])) {
                    action_item.put(ActionsHelper.map_val_action_is_playing,
                            true);
                } else {
                    action_item.put(ActionsHelper.map_val_action_is_playing,
                            false);
                }
            }
            action_item.put(
                    ActionsHelper.map_val_action_download_state,
                    ActionsHelper.Action_download_state.downing);
            action_item.put(
                    ActionsHelper.map_val_action_download_progress,
                    new Double(0));
            mDatas.add(action_item);
        }
//已下载的动作
        for (int i = 0; i < mMyDownLoadHistory.size(); i++) {
//            if (!mMyDownLoadHistory.get(i).action.actionName.contains(key))
//                continue;
            Map<String, Object> action_item = new HashMap<String, Object>();
            action_item.put(ActionsHelper.map_val_action,
                    mMyDownLoadHistory.get(i).action);
            action_item.put(ActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);

            String action_name = mMyDownLoadHistory.get(i).action.actionName;

            action_item.put(ActionsHelper.map_val_action_name, action_name);

            try {
                action_item
                        .put(ActionsLibHelper.map_val_action_type_logo_res,
                                type_logos[((int) mMyDownLoadHistory.get(i).action.actionType) - 1]);
                action_item
                        .put(ActionsHelper.map_val_action_type_name,
                                type_names[((int) mMyDownLoadHistory.get(i).action.actionType) - 1]);
            } catch (Exception e) {
                action_item.put(
                        ActionsLibHelper.map_val_action_type_logo_res,
                        type_logos[1]);
            }

            action_item
                    .put(ActionsHelper.map_val_action_time,
                            TimeTools.getMMTime((int) mMyDownLoadHistory
                                    .get(i).action.actionTime * 1000));

            if (mMyDownLoadHistory.get(i).action.actionDesciber != null
                    && !mMyDownLoadHistory.get(i).action.actionDesciber
                    .equals("")) {
                action_item.put(ActionsLibHelper.map_val_action_disc,
                        mMyDownLoadHistory.get(i).action.actionDesciber);
            } else {
                action_item.put(
                        ActionsLibHelper.map_val_action_disc,
                        ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
            }
            action_item.put(
                    ActionsHelper.map_val_action_download_state,
                    Action_download_state.download_finish);
            action_item.put(
                    ActionsHelper.map_val_action_download_progress,
                    new Double(100));

            String file_name = mMyDownLoadHistory.get(i).action.hts_file_name;
            String current_name = getCurrentPlayName();
            if (file_name != null && current_name.equals(file_name.split("\\.")[0])) {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        false);
            }
            boolean is_send = false;
            if (actions_list != null) {
                for (int j = 0; j < actions_list.length(); j++) {
                    try {
                        String robot_action_name = actions_list.get(j)
                                .toString() + ".hts";
                        if (robot_action_name.equals(file_name)) {
                            is_send = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (is_send) {
                action_item.put(
                        ActionsHelper.map_val_action_download_state,
                        ActionsHelper.Action_download_state.send_finish);
            } else {
                action_item.put(
                        ActionsHelper.map_val_action_download_state,
                        ActionsHelper.Action_download_state.download_finish);
            }
            mDatas.add(action_item);
        }
        return  mDatas;
    }


    /**
     * my collection
     * */
    public  List<Map<String,Object>> loadDatas(List<ActionColloInfo> mActions)
    {

        List<Map<String, Object>> mDatas = new ArrayList<>();
        int[] type_logos = new int[]{R.drawable.gamepad_actions_basic_s_icon,
                R.drawable.sec_dance_icon_s, R.drawable.sec_story_icon_s};

        String[] type_names = new String[]{
                ((BaseActivity)mActivity).getStringResources("ui_action_type_basic"),
                ((BaseActivity)mActivity).getStringResources("ui_action_type_dance"),
                ((BaseActivity)mActivity).getStringResources("ui_action_type_story")};
        if (mActions == null)
            return mDatas;

        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(ActionsColloHelper.map_val_action, mActions.get(i));

            item.put(ActionsColloHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);

            item.put(ActionsColloHelper.map_val_action_name,
                    mActions.get(i).collectName);
            String[] str = mActions.get(i).extendInfo.split("#");
            String browseTime = str[str.length - 1];
            item.put(ActionsColloHelper.map_val_action_browse_time, browseTime);

            try {
                int type_num = Integer.parseInt(mActions.get(i).extendInfo
                        .split("#")[0]);
                item.put(ActionsColloHelper.map_val_action_type_logo_res,
                        type_logos[type_num - 1]);
                item.put(ActionsHelper.map_val_action_type_name,
                        type_names[type_num - 1]);

            } catch (Exception e) {
                item.put(ActionsColloHelper.map_val_action_type_logo_res,
                        type_logos[0]);
            }

            try {
                int time = Integer.parseInt(mActions.get(i).extendInfo
                        .split("#")[2]);
                item.put(ActionsColloHelper.map_val_action_time,
                        TimeTools.getMMTime(time * 1000));
            } catch (Exception e) {
                item.put(ActionsColloHelper.map_val_action_time,
                        TimeTools.getMMTime(0));
            }

            if (mActions.get(i).collectDescriber != null
                    && !mActions.get(i).collectDescriber.equals("")) {
                item.put(ActionsColloHelper.map_val_action_disc,
                        mActions.get(i).collectDescriber);
            } else {
                item.put(
                        ActionsColloHelper.map_val_action_disc,
                        ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
            }

            item.put(ActionsColloHelper.map_val_action_download_progress,
                    new Double(0));

            mDatas.add(item);
        }

        return mDatas;
    }


    public void doRemoveCollectWeb(ActionColloInfo mAction) {

        updateItemImmediately(mCurrentRemovedItem);
        UserInfo info = ((AlphaApplication) mBaseActivity
                .getApplicationContext()).getCurrentUserInfo();
        if (info == null) {
            mUI.onNoteNoUser();
            return;
        }
        mCollcationManager.removeWebRecord(mAction.collectRelationId);

    }

    public  List<Map<String,Object>> loadCreatedDatas(List<NewActionInfo> mActions)
    {
        List<Map<String, Object>> mDatas = new ArrayList<>();
        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            action_item.put(ActionsHelper.map_val_action,
                    mActions.get(i));
            action_item.put(ActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);
            action_item.put(ActionsHelper.map_val_action_name,
                    mActions.get(i).actionName);
            action_item.put(ActionsHelper.map_val_action_type_name, this
                    .mBaseActivity.getString(R.string.ui_action_type_basic));
            action_item.put(ActionsHelper.map_val_action_type_logo_res,
                    R.drawable.gamepad_actions_basic_s_icon);
            action_item.put(ActionsHelper.map_val_action_time, TimeTools
                    .getMMTime(mActions.get(i).getTitleTime()));
            action_item.put(ActionsHelper.map_val_action_disc,
                    mActions.get(i).actionDesciber);
            action_item.put(ActionsHelper.map_val_action_selected,false);
            if (getCurrentPlayName().equals(mActions.get(i).actionName)) {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        false);
            }
            action_item.put(ActionsHelper.map_val_action_is_playing, false);
            action_item.put(ActionsHelper.map_val_action_download_state, ActionsHelper.Action_download_state.download_finish);
            mDatas.add(action_item);
        }

      return mDatas;
    }
    public  List<Map<String,Object>> loadSyncDatas(List<ActionInfo> mActions,int type)

    {
        List<Map<String, Object>> mDatas = new ArrayList<>();
        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            action_item.put(ActionsHelper.map_val_action,
                    mActions.get(i));
            action_item.put(ActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);
            action_item.put(ActionsHelper.map_val_action_name,
                    mActions.get(i).actionName);
            action_item.put(ActionsHelper.map_val_action_type_name, this
                    .mBaseActivity.getString(R.string.ui_action_type_basic));
            action_item.put(ActionsHelper.map_val_action_type_logo_res,
                    R.drawable.gamepad_actions_basic_s_icon);
            if(type== FillLocalContent.CREATE_ACTIONS)
            action_item.put(ActionsHelper.map_val_action_time, TimeTools
                    .getMMTime((int)mActions.get(i).actionTime));
            else
                action_item.put(ActionsHelper.map_val_action_time, TimeTools
                        .getMMTime((int)mActions.get(i).actionTime*1000));
            action_item.put(ActionsHelper.map_val_action_disc,
                    mActions.get(i).actionDesciber);
            action_item.put(ActionsHelper.map_val_action_selected,false);
            if (getCurrentPlayName().equals(mActions.get(i).actionName)) {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(ActionsHelper.map_val_action_is_playing,
                        false);
            }
            action_item.put(ActionsHelper.map_val_action_is_playing, false);
            action_item.put(ActionsHelper.map_val_action_download_state, ActionsHelper.Action_download_state.download_finish);
            mDatas.add(action_item);
        }

      return mDatas;
    }



    public void startPlayAction( Map<String, Object> item,int mCurrentPlayPos,ActionsHelper.Action_type type) {
        doActionCommand(ActionsHelper.Command_type.Do_play,
                item.get(ActionsHelper.map_val_action), type);
        this.mCurrentPlayPos = mCurrentPlayPos;
//        item.put(ActionsHelper.map_val_action_is_playing, true);
    }

    public  void stopPlayAction()
    {
        if (getPlayerState() == ActionPlayer.Play_state.action_finish
                && getNewPlayerState() == NewActionPlayer.PlayerState.STOPING)
            return;
       doActionCommand(ActionsHelper.Command_type.Do_Stop,
                "", ActionsHelper.Action_type.My_download);
    }

    public void noteCollectDeleted(Context mContext, final ActionColloInfo actionColloInfo,final int pos)
    {

        this.mCurrentRemovedItem = pos;
        new AlertDialog(mContext).builder().setMsg("对不起，您收藏的动作已被删除，是否移除本条收藏").setCancelable(true).
                setPositiveButton("移除", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doRemoveCollectWeb(actionColloInfo);
                    }
                }).setNegativeButton("取消",null).show();

    }

    public void updateItemImmediately(int pos)
    {
        MyActionsActivity instance = (MyActionsActivity)mActivity;
        instance.fragment.mDatas.remove(pos);
        instance.fragment.sendMessage(pos, BaseMyActionsFragment.DELETE_ITEMS);
    }

    public void notifyAdapters(int pos)
    {
        MyActionsSyncActivity instance = (MyActionsSyncActivity)mActivity;
        instance.notifyAdapters(pos);
    }

}

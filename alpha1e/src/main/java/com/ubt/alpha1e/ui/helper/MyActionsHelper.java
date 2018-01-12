package com.ubt.alpha1e.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.ubt.alpha1e.data.DB.ActionsOnlineCacheOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.ActionEvent;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener.State;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.MyActionsSyncActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.fragment.BaseMyActionsFragment;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.ResourceUtils;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.file.FileUploadProgressListener;
import com.ubtechinc.file.FileUploader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import okhttp3.Call;

public class MyActionsHelper extends BaseHelper implements
        ActionsDownLoadManagerListener, FileUploadProgressListener,
        NewActionsManagerListener {

    private static final String TAG = "MyActionsHelper";

    public static MyActionsHelper instance = null;
    public static List<String> mCurrentSeletedNameList = new ArrayList<String>();
    public static Map<String,ActionInfo> mCurrentSeletedActionInfoMap = new HashMap<String,ActionInfo>();
    public static final String Return_select_action_name = "Return_select_action_name";

    public static final String action_type = "action_type";
    public static final String action_info = "action_info";
    public static final String action_info_all = "action_info_all";

    public  enum Action_type {
        Story_type, Dance_type, Base_type, Custom_type, My_download, My_new,My_download_local,My_new_local,My_gamepad ,All,Unkown,MY_WALK,MY_COURSE
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
    public static final String map_val_action_browse_time = "map_val_action_browse_time";
    public static final String map_val_action_is_playing = "map_val_action_is_playing";
    public static final String map_val_action_download_state = "map_val_action_download_state";
    public static final String map_val_action_download_progress = "map_val_action_download_progress";
    public static final String map_val_action_selected = "map_val_action_selected";
    public static final String map_val_action_head_url = "map_val_action_head_url";
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
    private static final int MSG_DO_SYNC_SERVER_ACTION_FINISH = 1010;
    private static final int MSG_DO_GETACTIONFILE = 1011;

    // -------------------------------
    private static final int MESSAGE_START_DOW_LOAD_BIN_FILE = 11001;
    private static final int MESSAGE_UPDATE_LOADING_FILE = 11002;
    private static final int MESSAGE_UPDATE_LOADING_FILE_FINISH = 11003;
    private static final int MESSAGE_ROBRT_IS_BUSY = 11004;
    private static final int MESSAGE_FILE_LOADING_ERROR = 11005;
    private static final int MESSAGE_FILE_LANDING_CANCEL = 11006;
    private static final int MESSAGE_UPDATE_LOADING_FILE_FULL = 11007;
    private static final int MESSAGE_DO_PLAY_ACTION = 11008;
    private static final int MESSAGE_DO_SET_AUDIO_SOURCE = 11009;
    private static final int MESSAGE_DO_DELAY_PLAY_ACTION = 11010;
    private static final int MESSAGE_DO_DELETE_ACTION_START = 11011;

    // -------------------------------


    private NewActionPlayer mNewPlayer;
    public String mCurrentBaseActionsPath;
    private Activity mActivity;
    // -------------------------------

//    private IActionsUI mUI;
    private List<IActionsUI> mActionListeners = new ArrayList<>();
    private ActionPlayer mPlayer;
    private ActionsCollocationManager mCollcationManager;
    private ActionsDownLoadManager mDownLoadManager;
    private static FileUploader mFileLoader;
    private String mSendFileSourcePath;
    private String mSendDFileRobotSavePath;

    private int mMaxFrame = 0;
    private List<String> mActionsNames;
    private NewActionInfo mNewActionInfo;
    private NewActionsManager mNewActionsManager;

    private Date lastTime_doPauseMp3ForMyDownload = null;

    private static AudioManager mAudioManager;
    private static MediaPlayer mp3_player;
    private static Timer mp3_delay;

    public static Action_type mCurrentPlayType = null;
    public static Action_type mCurrentLocalPlayType = null;

    public  int mCurrentPlayPos = -1;
    public  int mCurrentIntendtoPlay = -1;
    public  int mCurrentRemovedItem = -1;
    private Object mCurrentPlayActions = null;
    private MyActionsHelper.Action_type mCurrentPlayActionType = null;

    public static Action_type getDataType = Action_type.Unkown;
    public static int localSize = 0;
    public static int myDownloadSize = 0;
    public static int myNewSize = 0;
    public static List<String> mCacheActionsNames = new ArrayList<String>();
    private int sendNum = 0; //传送次数

    private String mSchemeId = "";
    private String mSchemeName = "";
    private boolean isLooping=false;

    public Action_type getCurrentPlayType() {
        return mCurrentPlayType;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == MSG_DO_READ_ACTIONS) {
                    for(IActionsUI ui:mActionListeners){
                        ui.onReadActionsFinish(mActionsNames);
                    }
                    ActionEvent event = new ActionEvent(ActionEvent.Event.READ_ACTIONS_FINISH);
                    event.setActionsNames(mActionsNames);
                    EventBus.getDefault().post(event);

                } else if (msg.what == MSG_DO_NOTE_VOL) {


                } else if (msg.what == MSG_DO_NOTE_VOL_STATE) {

                } else if (msg.what == MSG_DO_READ_DOWNLOAD_ACTIONS_FINISH) {
                    List<ActionRecordInfo> history = null;
                    if (msg.obj == null)
                        history = new ArrayList<ActionRecordInfo>();
                    else
                        history = (List<ActionRecordInfo>) msg.obj;
                    for(IActionsUI ui:mActionListeners)
                        ui.onReadMyDownLoadHistory(MyActionsHelper.this.hashCode()
                                + "", history);
//                    mUI.onReadMyDownLoadHistory(ActionsHelper.this.hashCode()
//                            + "", history);
                } else if (msg.what == MESSAGE_START_DOW_LOAD_BIN_FILE) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileStart();
//                    mUI.onSendFileStart();
                } else if (msg.what == MESSAGE_ROBRT_IS_BUSY) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileBusy();
//                    mUI.onSendFileBusy();
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_FILE_LOADING_ERROR
                        || msg.what == MESSAGE_UPDATE_LOADING_FILE_FULL) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileError();
//                    mUI.onSendFileError();

                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_UPDATE_LOADING_FILE_FINISH) {
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileFinish(mCurrentIntendtoPlay);
//                    mUI.onSendFileFinish(mCurrentIntendtoPlay);
                } else if (msg.what == MESSAGE_FILE_LANDING_CANCEL) {
//                    mUI.onSendFileCancel();
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileCancel();
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager().outMonopoly();
                } else if (msg.what == MESSAGE_UPDATE_LOADING_FILE) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onSendFileUpdateProgress(msg.obj + "");
//                    mUI.onSendFileUpdateProgress(msg.obj + "");
                } else if (msg.what == MSG_DO_NOTE_LIGHT_ON) {
                    for(IActionsUI ui:mActionListeners)
                        ui.noteLightOn();
//                    mUI.noteLightOn();

                } else if (msg.what == MSG_DO_NOTE_LIGHT_OFF) {

                    for(IActionsUI ui:mActionListeners)
                        ui.noteLightOff();
//                    mUI.noteLightOff();

                } else if (msg.what == MSG_ON_TF_PULLED) {
                    for(IActionsUI ui:mActionListeners)
                        ui.noteTFPulled();
//                    mUI.noteTFPulled();
                } else if (msg.what == MSG_DO_CHNNGE_NEW_ACTION_FINISH) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onChangeNewActionsFinish();
//                    mUI.onChangeNewActionsFinish();
                } else if (msg.what == MSG_DO_READ_NEW_ACTION_FINISH) {
                    for(IActionsUI ui:mActionListeners)
                        ui.onReadNewActionsFinish((List<NewActionInfo>) msg.obj);
//                    mUI.onReadNewActionsFinish((List<NewActionInfo>) msg.obj);
                }else if(msg.what == MSG_DO_SYNC_SERVER_ACTION_FINISH){
                    List<Map<String, Object>> mData = (List<Map<String, Object>>) msg.obj;
                    for(IActionsUI ui:mActionListeners)
                        ui.syncServerDataEnd(mData);
                }else if(msg.what == MSG_DO_GETACTIONFILE){
                    doSendComm(ConstValue.DV_GETACTIONFILE, ((String)msg.obj).getBytes("GBK"));
                }else if(msg.what == MESSAGE_DO_PLAY_ACTION){
                    doActionCommand(MyActionsHelper.Command_type.Do_play,mCurrentPlayActions,mCurrentPlayActionType);
                }else if(msg.what == MESSAGE_DO_SET_AUDIO_SOURCE){
                    setAudioSource((byte) msg.obj);
                }else if(msg.what == MESSAGE_DO_DELAY_PLAY_ACTION){

                    mPlayer.doPlayAction((ActionInfo)msg.obj);
                }else if(msg.what == MESSAGE_DO_DELETE_ACTION_START){
                    final String deleteFilePath = (String) msg.obj;
                    new AlertDialog(mBaseActivity).builder().setMsg(mBaseActivity.getStringResources("ui_actionfile_delete_warning")).setCancelable(false).
                            setPositiveButton(mBaseActivity.getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    for(IActionsUI ui:mActionListeners){
                                        ui.noteDeleteActionStart(mCurrentRemovedItem);
                                    }
                                    sendDeleteFileCMD(deleteFilePath);
                                }
                            }).setNegativeButton(mBaseActivity.getStringResources("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     *
     * @param baseActivity
     * @return single instance
     */
    public static MyActionsHelper getInstance(BaseActivity baseActivity)
    {
        if(instance == null){
            instance = new MyActionsHelper(baseActivity);
        }
        instance.mActivity = baseActivity;
        instance.setBaseActivity(baseActivity);
        return  instance;
    }


    public void setManagerListeners(BaseActivity baseActivity)
    {
        mCollcationManager = ActionsCollocationManager.getInstance(baseActivity);
        mDownLoadManager = ActionsDownLoadManager.getInstance(baseActivity);
        mNewActionsManager = NewActionsManager.getInstance(baseActivity);
    }

    public MyActionsHelper(BaseActivity baseActivity) {

        super(baseActivity);
        this.mActivity = baseActivity;
        mCollcationManager = ActionsCollocationManager.getInstance(baseActivity);
        mDownLoadManager = ActionsDownLoadManager.getInstance(baseActivity);
        mNewActionsManager = NewActionsManager.getInstance(baseActivity);
        mActionsNames = new ArrayList<String>();
    }

    public void registerListeners(IActionsUI listener)
    {
        UbtLog.d(TAG,"registerListeners----"+listener.getClass());
        if(!mActionListeners.contains(listener))
        {
            mActionListeners.add(listener);
        }
        mCollcationManager.addListener(listener);
        mDownLoadManager.addListener(listener);
        mDownLoadManager.addListener(this);
        mNewActionsManager.addListener(this);
        if(mPlayer!=null){
            mPlayer.addListener(listener);
        }
        if (mBaseActivity.isBulueToothConnected()) {
            initActionPlayer(listener);
            initNewActionPlayer(listener);
        }
    }

    public void unRegisterListeners(IActionsUI listener)
    {
        mCollcationManager.removeListener(listener);
        mDownLoadManager.removeListener(listener);
        if(mPlayer!=null){
            mPlayer.removeListener(listener);
        }

        if(mNewPlayer!=null){
            mNewPlayer.removeListener(listener);
        }

        if(mActionListeners.contains(listener)){
            mActionListeners.remove(listener);
        }

//        mActionListeners.clear();
    }

    public void resetPlayer(){
        if(mPlayer != null){
            mPlayer.doStopPlay();
            ActionPlayer.reset();
            mPlayer = null;
        }
        if(mNewPlayer != null){
            mNewPlayer.StopPlayer();
            NewActionPlayer.reset();
            mNewPlayer = null;
        }
    }

    public void initActionPlayer(IActionsUI listener )
    {
        if (mPlayer == null || !(((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentBluetooth().getAddress()).equals(mPlayer.getBtMac())) {
            mPlayer = ActionPlayer.getInstance(((AlphaApplication) mBaseActivity
                            .getApplicationContext()).getBlueToothManager(),
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getCurrentBluetooth().getAddress());
            mPlayer.addListener(listener);
        }
    }

    public void initNewActionPlayer(IActionsUI listener)
    {
        if (mNewPlayer == null || !(((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentBluetooth().getAddress()).equals(mNewPlayer.getBtMac())) {
            mNewPlayer = NewActionPlayer
                    .getPlayer(((AlphaApplication) mBaseActivity
                            .getApplicationContext()).getCurrentBluetooth()
                            .getAddress());

        }
        if (mNewPlayer != null) {
            mNewPlayer.addListener(listener);
            //mNewPlayer.setListener(listener);
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

    /**
     * 下载我的创建同步文件
     */
    public void doDownloadSyncFiles(String url, final String filePath, final String fileName)
    {
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(filePath,fileName+".zip") {
                    @Override
                    public void inProgress(float v, long l,int i) {

                    }

                    @Override
                    public void onError(Call call, Exception e,int i) {

                    }

                    @Override
                    public void onResponse(File file,int i) {
                        ZipTools.unZip(file.getAbsolutePath(), filePath+File.separator+fileName);

                    }
                });


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

        mDownLoadManager.removeListener(this);
        mNewActionsManager.removeListener(this);

//        mDownLoadManager.removeListener(mUI);
//        mCollcationManager.removeListener(mUI);
//        if(mPlayer!=null)
//            mPlayer.removeListener(mUI);
    }

    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
//        mCollcationManager.addListener(mUI);
//        mDownLoadManager.addListener(mUI);
        mDownLoadManager.addListener(this);
        mNewActionsManager.addListener(this);
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

    public  void setPlayerState() {
        mPlayer.setState();
    }

    public String getPlayerName() {
        if (this.mPlayer == null) {
            return "";
        } else {
            String name = this.mPlayer.getPlayActonName();
            if(TextUtils.isEmpty(name)){
                return "";
            }else if (name.equals(ActionPlayer.CYCLE_ACTION_NAME)) {
                name = mBaseActivity.getResources().getString(
                        R.string.ui_action_cycle_name);
            } else if ("#@%".contains(name.toCharArray()[0] + "")) {
                name = name.substring(1);
            }
            return name;
        }
    }

    public long getPlayerActionId() {
        if(this.mPlayer == null){
            return -1;
        }
        return this.mPlayer.getActionOriginalId();
    }

    public PlayerState getNewPlayerState() {
        if (this.mNewPlayer == null) {
            return PlayerState.STOPING;
        } else {
            return this.mNewPlayer.getState();
        }
    }

    public long getNewPlayerActionId() {
        if(this.mNewPlayer == null){
            return -1;
        }
        return this.mNewPlayer.getPlayActonId();
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

        if (!mBaseActivity.isBulueToothConnected()) {
            return;
        }
        if (comm_type == Command_type.Do_play) {
            MyLog.writeLog("播放功能", "Do_play");
            mCurrentPlayType = act_type;
            if (mCurrentPlayType == Action_type.My_new) {
                /*mPlayer.doStopPlay();
                mNewPlayer.StopPlayer();*/

                NewActionInfo info = (NewActionInfo) action_obj;
                if((info.actionId+"").length() != 13){//下载
                    mNewPlayer.StopPlayer();
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_new_local;
                    ActionInfo actionInfo = (ActionInfo) action_obj;
                    actionInfo.hts_file_name = info.actionOriginalId+".hts";

                    Message msg = new Message();
                    msg.what = MESSAGE_DO_DELAY_PLAY_ACTION;
                    msg.obj = actionInfo;

                    mHandler.sendMessageDelayed(msg,200);
                    //mPlayer.doPlayAction(actionInfo);
                }else {
                    mPlayer.doStopPlay();
                    doPlayMyNewAction(info);
                }

            } else {
                mNewPlayer.StopPlayer();

                if (mCurrentPlayType == Action_type.My_download) {
                    Message msg = new Message();
                    msg.what = MESSAGE_DO_DELAY_PLAY_ACTION;
                    msg.obj = (ActionInfo) action_obj;

                    mHandler.sendMessageDelayed(msg,200);
                    //mPlayer.doPlayAction((ActionInfo) action_obj);
                } else {
                    //ActionInfo info = new ActionInfo();
                    //info.actionName = action_obj.toString();

                    Message msg = new Message();
                    msg.what = MESSAGE_DO_DELAY_PLAY_ACTION;
                    msg.obj = (ActionInfo) action_obj;
                    mHandler.sendMessageDelayed(msg,200);
                    //mPlayer.doPlayAction((ActionInfo) action_obj);
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
        mCurrentVolume = pow;

        MyLog.writeLog("音量检测", "发送调整指令" + pow);

        byte[] param = new byte[1];
        param[0] = (byte) (pow & 0xff);
        doSendComm(ConstValue.DV_VOLUME, param);
        ChangeMisucVol(_pow);
    }

    public void doTurnVol() {
        byte[] papram = new byte[1];
        if (mCurrentVoiceState) {
            papram[0] = 0;
            ChangeMisucVol(0);
            //有声音
            mCurrentVoiceState = false;
        } else {
            papram[0] = 1;
            //无声音
            mCurrentVoiceState = true;
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

        UbtLog.d(TAG,"lihai-------------mCacheActionsNames->"+mCacheActionsNames.size()+"---"+mCacheActionsNames.isEmpty());
        if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
            //mCacheActionsNames.clear();
        }

        if(mCacheActionsNames.isEmpty()){
            MyLog.writeLog("命令发送", "DV_GETACTIONFILE");

            getDataType = Action_type.Unkown;
            Message msg = new Message();
            msg.what = MSG_DO_GETACTIONFILE;
            msg.obj  =  FileTools.action_robot_file_path;
            mHandler.sendMessage(msg);
        }else{
            mActionsNames.addAll(mCacheActionsNames);
            Message msg = new Message();
            msg.what = MSG_DO_READ_ACTIONS;
            mHandler.sendMessage(msg);
        }

        /*doSendComm(Constalue.DV_GETACTIONFILE, FileTools.action_robot_file_path.getBytes("GBK"));
        doSendComm(ConstValue.DV_GETACTIONFILE, FileTools.actions_download_robot_path.getBytes("GBK"));
        doSendComm(ConstValue.DV_GETACTIONFILE, FileTools.actions_creation_robot_path.getBytes("GBK"));
        */

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if((cmd+0)!=10){
            //UbtLog.d(TAG,"onReceiveData:"+cmd+",parms:"+param[0]);
        }

   /*     if(getDataType == Action_type.MY_WALK){
            return;
        }*/

        if (cmd == ConstValue.DV_FILE_UPLOAD_START) {
            UbtLog.d(TAG, "dub start");
            parseUploadStart(param);
        } else if (cmd == ConstValue.DV_MODIFY_FILENAME) {// 修改文件名
            parseModifyFileName(param, len);
        } else if (cmd == ConstValue.DV_DELETE_FILE) {// 删除文件
            parseDeleteFile(param, len);
        } else if (cmd == ConstValue.DV_FILE_UPLOADING) {
            UbtLog.d(TAG,"getDataType = " + getDataType + " mFileLoader = " + mFileLoader);
            if(getDataType == Action_type.MY_WALK || getDataType == Action_type.MY_COURSE || mFileLoader == null){
                return;
            }
            parseUploading(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_END) {
            UbtLog.d(TAG, "dub end");
            parseUploadEnd(param);
        } else if (cmd == ConstValue.DV_FILE_UPLOAD_CANCEL) {
            parseUploadCancel(param);
        } else if(cmd == ConstValue.DV_SET_AUDIO_SOURCE){
            mHandler.sendEmptyMessage(MESSAGE_DO_PLAY_ACTION);
        } else if(cmd == ConstValue.DV_READ_AUDIO_SOURCE_STATE){

            if(param[0] == 0){//robot audio
                if(mCurrentPlayActionType == MyActionsHelper.Action_type.Unkown){
                    //no need switch audio source
                    mHandler.sendEmptyMessage(MESSAGE_DO_PLAY_ACTION);
                }else{
                    //need switch audio source
                    Message msg = new Message();
                    msg.what = MESSAGE_DO_SET_AUDIO_SOURCE;
                    msg.obj = (byte)1;
                    mHandler.sendMessage(msg);
                }
            }else {// 1 is phone audio
                if(mCurrentPlayActionType == MyActionsHelper.Action_type.Unkown){
                    //need switch audio source
                    Message msg = new Message();
                    msg.what = MESSAGE_DO_SET_AUDIO_SOURCE;
                    msg.obj = (byte) 0;
                    mHandler.sendMessage(msg);
                }else{
                    //no need switch audio source
                    mHandler.sendEmptyMessage(MESSAGE_DO_PLAY_ACTION);
                }
            }

        }else if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {
            if(getDataType == Action_type.MY_WALK){
                return;
            }

            String name = BluetoothParamUtil.bytesToString(param);

            if(!"初始化".equals(name)){
                mActionsNames.add(name);
            }

        } else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
            UbtLog.d(TAG,"lihai------------getDataType-=>>"+getDataType);
            if(getDataType == Action_type.MY_WALK){
                return;
            }

            if(getDataType == Action_type.Unkown){
                localSize = mActionsNames.size();
                getDataType = Action_type.My_download_local;

                Message msg = new Message();
                msg.what = MSG_DO_GETACTIONFILE;
                msg.obj = FileTools.actions_download_robot_path;
                mHandler.sendMessage(msg);
                return;
            }else if(getDataType == Action_type.My_download_local){
                myDownloadSize = mActionsNames.size() - localSize;
                getDataType = Action_type.My_new_local;

                Message msg = new Message();
                msg.what = MSG_DO_GETACTIONFILE;
                msg.obj = FileTools.actions_creation_robot_path;
                mHandler.sendMessage(msg);
                return;
            }else{
                myNewSize = mActionsNames.size() - localSize - myDownloadSize;
                getDataType = Action_type.Unkown;
            }

            if (mPlayer != null) {
                mPlayer.setRobotActions(mActionsNames);
            }
            mCacheActionsNames.clear();
            mCacheActionsNames.addAll(mActionsNames);

            Message msg = new Message();
            msg.what = MSG_DO_READ_ACTIONS;
            mHandler.sendMessage(msg);

        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {
             //MyLog.writeLog(TAG, "收到状态信息 : param[0] = " + param[0] + "  param[1] = " + param[1]);
            if (param[0] == 4) {
                // 拔出
                if (param[1] == 0) {
                    mCacheActionsNames.clear();
                    Message msg = new Message();
                    msg.what = MSG_ON_TF_PULLED;
                    mHandler.sendMessage(msg);
                }
                // 插入
                else {

                }
            }
        }else if ((cmd & 0xff) == (ConstValue.DV_DO_CHECK_ACTION_FILE_EXIST & 0xff)) {
            UbtLog.d(TAG, "播放文件是否存在：" + param[0]);
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

    private void doReSendFile(){

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
        File zip_release_path = new File(FileTools.actions_new_cache + "/"
                + info.actionId);

        if(!zip_release_path.exists()){   //发布之后NewActionInfo的actionID会变化，会存在路径找不到
            zip_release_path = new File(FileTools.actions_new_cache + "/"
                    + info.actionOriginalId);
        }

        String file_name = info.hts_file_name;
        if (info.actionPath_local != null && !info.actionPath_local.equals("")) {
        } else {
            File htsFile = new File(zip_release_path+"/"+info.actionOriginalId+".hts");
            if(htsFile.exists()){
                info.actionPath_local = zip_release_path+"/"+info.actionOriginalId+".hts";
            }else {
                // 1、解压文件
                ZipTools.unZip(FileTools.actions_new_cache + "/" + info.actionId
                        + ".zip", zip_release_path.getPath());
                String[] action_files = zip_release_path.list(new FilenameFilter() {
                    public boolean accept(File f, String name) {
                        return name.endsWith(".hts");
                    }
                });
                if (action_files == null || action_files.length < 1) {
                    for(IActionsUI ui:mActionListeners){
                        ui.onSendFileError();
                    }
                    return;
                }
                // 2、写入下载历史
                file_name = action_files[0];
                info.hts_file_name = file_name;
                info.actionPath_local = zip_release_path +"/"+info.hts_file_name;
            }
        }

        // 3、立即进独占模式
        Message msg = new Message();
        msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE;
        mHandler.sendMessage(msg);
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .getBlueToothManager().intoMonopoly();
        // 4、传文件
        mSendFileSourcePath = info.actionPath_local;
        String[] savePaths = mSendFileSourcePath.split("/");
        //mSendDFileRobotSavePath = "action/" + savePaths[savePaths.length - 1];
        mSendDFileRobotSavePath = FileTools.actions_creation_robot_path +"/"+ savePaths[savePaths.length - 1];
        UbtLog.d(TAG,"mSendDFileRobotSavePath->"+mSendDFileRobotSavePath+"   mSendFileSourcePath="+mSendFileSourcePath);
        sendNum = 1;
        final int maxFrame = openFile(mSendFileSourcePath);
        byte[] data = FileTools.packData(mSendDFileRobotSavePath, maxFrame);

        if (data != null) {
            UbtLog.d(TAG, "send DV_FILE_UPLOAD_START");
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
                for(IActionsUI ui:mActionListeners)
                    ui.onSendFileError();
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
        //mSendDFileRobotSavePath = "action/" + savePaths[savePaths.length - 1];
        mSendDFileRobotSavePath = FileTools.actions_download_robot_path + "/" + savePaths[savePaths.length - 1];

        UbtLog.d(TAG,"mSendDFileRobotSavePath="+mSendDFileRobotSavePath+"   mSendFileSourcePath="+mSendFileSourcePath);
        sendNum = 1;
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
        UbtLog.d(TAG,"----UploadFail----");
        mFileLoader = null;
        Message msg_error = new Message();
        msg_error.what = MESSAGE_FILE_LOADING_ERROR;
        mHandler.sendMessage(msg_error);
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
        UbtLog.d(TAG, "---parseUploadStart");

        Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE;
        msg.obj = 0 + "";
        mHandler.sendMessage(msg);
        UbtLog.d(TAG,"lihai--------parseUploadStart-->"+param+"----"+param[0] );
        // ---------------------------------------------------------------
        switch (param[0]) {
            case 0x00:
                UbtLog.d(TAG, "mSendFileSourcePath=" + mSendFileSourcePath + "--mSendDFileRobotSavePath" + mSendDFileRobotSavePath + "  mFileLoader:"+mFileLoader);
                if (mFileLoader == null) {
                    if (mSendFileSourcePath != null
                            && mSendDFileRobotSavePath != null) {
                        //UbtLog.d(TAG, "mSendFileSourcePath=" + mSendFileSourcePath + "--mSendDFileRobotSavePath" + mSendDFileRobotSavePath);
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
            case 0x0A:
                Message msg_full = new Message();
                msg_full.what = MESSAGE_UPDATE_LOADING_FILE_FULL;
                mHandler.sendMessage(msg_full);
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

        /*Message msg = new Message();
        msg.what = MESSAGE_UPDATE_LOADING_FILE_FINISH;
        mHandler.sendMessage(msg);*/
        mFileLoader = null;
        // ---------------------------------------------------------------
        boolean isNeedReSend = false;
        switch (param[0]) {
            case 0x00:
                break;
            case 0x0b:
                if(sendNum < 3){
                    UbtLog.d(TAG,"所传动作文件为0kb需重传,文件名：："+mSendFileSourcePath + "  重复次数："+sendNum);
                    sendNum++;
                    isNeedReSend = true;
                    doReSendFile();
                }
                break;
            default:
                break;
        }
        if(!isNeedReSend){
            Message msg = new Message();
            msg.what = MESSAGE_UPDATE_LOADING_FILE_FINISH;
            mHandler.sendMessage(msg);
        }

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
        byte[] papram = new byte[1];
        if (mLightState) {
            papram[0] = 0;// 关闭
            mLightState = false;
        } else {
            papram[0] = 1;// 打开
            mLightState = true;
        }

        UbtLog.d(TAG, "--wmma--doSendComm");
        doSendComm(ConstValue.DV_LIGHT, papram);

    }

    public void doPlayMp3ForMyDownload(ActionInfo action) {
        MyLog.writeLog("MP3播放", action.actionName);
        try {
            if(mp3_player != null){
                mp3_player.stop();
            }
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

        if (time_difference < 200) {
            return;
        }
        lastTime_doPauseMp3ForMyDownload = curDate;

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

    private File mp3_path = null;
    public void doPlayMp3ForMyCreate(ActionInfo action) {
        MyLog.writeLog("MP3播放", action.actionName);
        UbtLog.d(TAG, "MP3播放:" + action.toString());
        try {
            if(mp3_player != null){
                mp3_player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mp3_player = null;
        mAudioManager = null;
         mp3_path = new File(FileTools.actions_new_cache + "/"
                + action.actionId);
        if(!mp3_path.exists()){
            mp3_path = new File(FileTools.actions_new_cache + "/"
                    + action.actionOriginalId);
        }
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

    //新增一个不同参数的播放音乐方法，由于创建的动作在未发布的时候播放走onPlaying,所以重新创建一个方法，直接以actionId为参数
    public void doPlayMp3ForMyCreate(long actionId) {

        UbtLog.d(TAG, "MP3播放:" + actionId);
        try {
            if(mp3_player != null){
                mp3_player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mp3_player = null;
        mAudioManager = null;
        final File mp3_path = new File(FileTools.actions_new_cache + "/"
                + actionId);
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
                            UbtLog.d(TAG, "1018 path:" + mp3_path + "/"
                                    + mp3_name[0]);
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
        String str = "";
        boolean isOK = false;
        switch (b) {
            case 0x00:// 0k
                isOK = true;
                str =  mBaseActivity.getStringResources("ui_action_rename_success");
                break;
            case 0x01:// 文件不存在
                str =  mBaseActivity.getStringResources("ui_action_name_not_exist");
                break;
            case 0x02:// DEL OTHER ERR
                str =  mBaseActivity.getStringResources("ui_remote_synchoronize_unknown_error");
                break;
            case 0x03:// 03 BUSY
                str =  mBaseActivity.getStringResources("ui_remote_synchoronize_busy");
                break;
            case 0x08:// 08 文件已存在
                str =  mBaseActivity.getStringResources("ui_action_name_exist");
                break;
            case 0x07:// 07 文件名不支持
                str =  mBaseActivity.getStringResources("ui_action_name_error");
                break;

            default:
                break;
        }
        for(IActionsUI ui:mActionListeners)
            ui.noteChangeFinish(isOK,str);

    }

    private void sendDeleteFileCMD(String info) {

        byte[] actions = BluetoothParamUtil.stringToBytes(info) ;
        doSendComm(ConstValue.DV_DELETE_FILE, actions);
    }

    public void doDelAction(Object action_obj, Action_type act_type) {
        if (act_type == Action_type.My_new) {
            updateItemImmediately(mCurrentRemovedItem);
            if(mPlayer!=null)
            mPlayer.doStopPlay();
            NewActionInfo info = (NewActionInfo) action_obj;
            mNewActionsManager.doDelete(info);
            doCancelPublishActions(info);
        } else {
            if (mNewPlayer != null)
                mNewPlayer.StopPlayer();
            if (act_type == Action_type.My_download) {
                ActionInfo info = (ActionInfo) action_obj;
                mDownLoadManager.doDelAction(info);
            } else {
                Message msg = new Message();
                msg.what = MESSAGE_DO_DELETE_ACTION_START;
                msg.obj = action_obj.toString();
                mHandler.sendMessage(msg);

                /*
                String info = action_obj.toString();
                sendDeleteFileCMD(info);*/
            }
        }
    }

    public void parseDeleteFile(byte[] param, int len) {
        byte b = param[0];
        String str = "";
        boolean isOk = false;

        switch (b) {
            case 0x00:// 0k
                str = "";
                isOk = true;
                break;
            case 0x01:// 文件不存在
                str = mBaseActivity.getStringResources("ui_action_name_not_exist");
                break;
            case 0x02:// DEL OTHER ERR
                str = mBaseActivity.getStringResources("ui_remote_synchoronize_unknown_error");
                break;
            case 0x03:// 03 BUSY
                str = mBaseActivity.getStringResources("ui_remote_synchoronize_busy");
                break;
            default:
                str = mBaseActivity.getStringResources("ui_remote_synchoronize_unknown_error");
                break;
        }
        for(IActionsUI ui:mActionListeners){
            ui.noteDeleteActionFinish(isOk,str);
        }
    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo){
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub
        UnRegisterHelper();

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
//            FileTools.readImageFromSDCacheASync(ids.get(i), urls.get(i), -1,
//                    -1, mUI);
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
                        MyActionsHelper.map_val_action_is_playing, true);
                return i;
            }
        }
        return -1;
    }
    public int getCurrentPlayPosition(List<Map<String,Object>> mDatas) {
        for (int i = 0; i < mDatas.size(); i++) {
            if(mDatas.get(i) != null){
                if ((Boolean) mDatas.get(i).get(map_val_action_is_playing)
                        && !(mDatas.get(i).get(map_val_action_name))
                        .equals(getCurrentPlayName())) {
                    mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing, false);
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean doDownLoad(ActionInfo actionInfo,boolean hasDownLocal) {

        UserInfo info = ((AlphaApplication) mBaseActivity
                .getApplicationContext()).getCurrentUserInfo();
        if (info == null) {
            for(IActionsUI ui:mActionListeners)
            {
                ui.onNoteNoUser();
                ui.onDownLoadFileFinish(actionInfo,State.fail);
            }
            return false;
        }
        if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
            mDownLoadManager.doDownloadOnRobot(actionInfo,hasDownLocal);
        }else {
            mDownLoadManager.DownLoadAction(actionInfo);
        }

        return true;
    }

    /**
     * Alpha 1E
     * 检查动作文件是否存在
     * @param item 播放对象
     */
    public void checkActionFileExist(Map<String, Object> item){
        ActionInfo actionInfo = (ActionInfo)item.get(MyActionsHelper.map_val_action);
        String actionName = "";

        if (actionInfo.hts_file_name != null && !actionInfo.hts_file_name.equals("")){
            actionName = actionInfo.hts_file_name.split("\\.")[0];
        }else{
            actionName = actionInfo.actionName;
        }
        actionName = actionName+".hts";
        UbtLog.d(TAG,"checkActionFileExist = " + actionName);
        try {
            doSendComm(ConstValue.DV_DO_CHECK_ACTION_FILE_EXIST, actionName.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 处理机器人内置动作
     * @return
     */
    public List<Map<String, Object>> loadDatas()
    {
        JSONArray actions_list = new JSONArray(mActionsNames);
        StringBuilder sb = new StringBuilder();
        List<String> numberNameList = new ArrayList<String>();

        List<Map<String, Object>> mDatas = new ArrayList<>();

        for (int i = 0; i < actions_list.length(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
//            action_item.put(ActionsHelper.map_val_action_logo_res,
//                    R.drawable.action_local_logo_grid);

            try {
                String name = actions_list.get(i).toString();
                if(isStringNumber(name) && i >= (localSize + myDownloadSize)){
                    sb.append(name+",");
                    numberNameList.add(name);
                    continue;
                }
                action_item.put(MyActionsHelper.map_val_action,name);

//                if (mCurrentActionType != Action_type.Custom_type) {
//                    name = name.substring(1);
//                }
//                if (!name.toLowerCase().contains(key.toLowerCase())) {
//                    continue;
//                }
                action_item.put(MyActionsHelper.map_val_action_name, name);
            } catch (JSONException e) {
//                action_item.put(
//                        ActionsHelper.map_val_action_name,
//                        this.getResources().getString(
//                                R.string.ui_action_no_action));
            }
            action_item.put(MyActionsHelper.map_val_action_type_logo_res,
                    R.drawable.actions_item_unkown);
            action_item.put(MyActionsHelper.map_val_action_type_name,
                    ((BaseActivity)mActivity).getStringResources("ui_square_unknown"));
            action_item.put(MyActionsHelper.map_val_action_time, 0);
            action_item.put(MyActionsHelper.map_val_action_disc, ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
            action_item.put(MyActionsHelper.map_val_action_is_playing, false);
            action_item.put(MyActionsHelper.map_val_action_selected, false);
            action_item.put(MyActionsHelper.map_val_action_browse_time, 0);
            mDatas.add(action_item);
        }
        final List<Map<String, Object>> mNoNumberData = mDatas;
        final List<String> fnumberNameList = numberNameList;
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);

            String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.detailById);
            String params = HttpAddress.getParamsForPost(new String[]{sb.toString()},
                    HttpAddress.Request_type.detailById,
                    mBaseActivity);
            final StringBuilder numberSb = sb;

            OkHttpClientUtils
                    .getJsonByPostRequest(url,params)
                    .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d(TAG,"Exception:"+e.getMessage());
                        dealResPonseResult(null,mNoNumberData,fnumberNameList);
                    }

                    @Override
                    public void onResponse(String resultData,int i) {
                        //UbtLog.d(TAG,"lihai------onResponse::"+resultData.toString());
                        dealResPonseResult(resultData,mNoNumberData,fnumberNameList);
                    }
                });
        }else{
            dealResPonseResult(null,mNoNumberData,fnumberNameList);
        }
        return mDatas;

    }

    /**
     * my download
     * */
    public  List<Map<String, Object>>  loadDatas(List<ActionRecordInfo> mMyDownLoadHistory, List<ActionInfo> mMyDownLoading,List<ActionInfo> mRobotDownloading,List<Map<String,Object>> syncList)
    {

        //1E 判断是否本地已经下载，但在机器人中下载
        Map<String,Object> mRobotDownloadingMap = new HashMap<>();
        if(mRobotDownloading != null && mRobotDownloading.size() > 0){
            for (ActionInfo robotDownloadAction : mRobotDownloading){
                mRobotDownloadingMap.put(String.valueOf(robotDownloadAction.actionId),robotDownloadAction.actionId);
            }
        }

        List<Map<String, Object>> mDatas = new ArrayList<>();
        //正在下载的动作
        for (int i = 0; i < mMyDownLoading.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            ActionInfo actionInfo = mMyDownLoading.get(i);
            actionInfo.actionSonType = actionInfo.actionType;

            action_item.put(MyActionsHelper.map_val_action,mMyDownLoading.get(i));
            action_item.put(MyActionsHelper.map_val_action_logo_res,R.drawable.sec_action_logo);
            action_item.put(MyActionsHelper.map_val_action_name,mMyDownLoading.get(i).actionName);
            try {
                action_item
                        .put(ActionsLibHelper.map_val_action_type_logo_res,
                                ResourceUtils.getActionTypeImage(actionInfo.actionSonType,mBaseActivity));
                action_item
                        .put(MyActionsHelper.map_val_action_type_name,
                                ResourceUtils.getActionType(actionInfo.actionSonType,mBaseActivity));
            } catch (Exception e) {
                e.printStackTrace();
                action_item.put(
                        ActionsLibHelper.map_val_action_type_logo_res,
                        ResourceUtils.getActionTypeImage(6,mBaseActivity));
            }

            action_item
                    .put(MyActionsHelper.map_val_action_time,
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
            action_item.put(MyActionsHelper.map_val_action_is_playing,
                    false);
            if(mMyDownLoadHistory.size()!=0)
            {
                String file_name = mMyDownLoadHistory.get(i).action.hts_file_name;
                String current_name = getCurrentPlayName();
                if (file_name != null && current_name.equals(file_name.split("\\.")[0])) {
                    action_item.put(MyActionsHelper.map_val_action_is_playing,
                            true);
                } else {
                    action_item.put(MyActionsHelper.map_val_action_is_playing,
                            false);
                }
            }
            action_item.put(
                    MyActionsHelper.map_val_action_download_state,
                    MyActionsHelper.Action_download_state.downing);
            action_item.put(
                    MyActionsHelper.map_val_action_download_progress,
                    new Double(0));
            mDatas.add(action_item);
        }
        //已下载的动作
        for (int i = 0; i < mMyDownLoadHistory.size(); i++) {
//            if (!mMyDownLoadHistory.get(i).action.actionName.contains(key))
//                continue;
            ActionRecordInfo info = mMyDownLoadHistory.get(i);
            info.action.actionSonType = info.action.actionType;
            Map<String, Object> action_item = new HashMap<String, Object>();
            action_item.put(MyActionsHelper.map_val_action,
                    mMyDownLoadHistory.get(i).action);
            action_item.put(MyActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);

            String action_name = mMyDownLoadHistory.get(i).action.actionName;

            action_item.put(MyActionsHelper.map_val_action_name, action_name);

            try {
                action_item
                        .put(ActionsLibHelper.map_val_action_type_logo_res,
                                ResourceUtils.getActionTypeImage(info.action.actionSonType,mBaseActivity));
                action_item
                        .put(MyActionsHelper.map_val_action_type_name,
                                ResourceUtils.getActionType(info.action.actionSonType,mBaseActivity));
            } catch (Exception e) {
                action_item.put(
                        ActionsLibHelper.map_val_action_type_logo_res,
                        ResourceUtils.getActionTypeImage(6,mBaseActivity));
            }

            action_item
                    .put(MyActionsHelper.map_val_action_time,
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
                    MyActionsHelper.map_val_action_download_state,
                    Action_download_state.download_finish);
            action_item.put(
                    MyActionsHelper.map_val_action_download_progress,
                    new Double(100));

            String file_name = mMyDownLoadHistory.get(i).action.hts_file_name;
            String current_name = getCurrentPlayName();
            if (file_name != null && current_name.equals(file_name.split("\\.")[0])) {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        false);
            }

            //判断是否在机器人中下载
            boolean isRobotDownloading = false;
            if(mRobotDownloadingMap.containsKey(String.valueOf(info.action.actionId))){
                isRobotDownloading = true;
            }

            //判断是否已经发送到机器人
            boolean isSend = false;
            if (syncList != null && !isRobotDownloading) {
                for (int j = MyActionsHelper.localSize; j < syncList.size(); j++) {
                    try {
                        String robot_action_name = syncList.get(j)
                                .get(MyActionsHelper.map_val_action_name) + ".hts";
                        if (robot_action_name.equals(file_name)) {
                            isSend = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if(isRobotDownloading){
                action_item.put(
                        MyActionsHelper.map_val_action_download_state,
                        Action_download_state.downing);
            }else if (isSend) {
                action_item.put(
                        MyActionsHelper.map_val_action_download_state,
                        Action_download_state.send_finish);
            } else {
                action_item.put(
                        MyActionsHelper.map_val_action_download_state,
                        Action_download_state.download_finish);
            }
            mDatas.add(action_item);
        }

        return  mDatas;
    }


    /**
     * my collection
     * */
    public  List<Map<String,Object>> loadDatas(List<ActionColloInfo> mActions,List<ActionRecordInfo> mMyDownLoadHistory)
    {
        List<Map<String, Object>> mDatas = new ArrayList<>();
        if (mActions == null){
            return mDatas;
        }

        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            ActionColloInfo actionColloInfo = mActions.get(i);
            actionColloInfo.actionSonType = actionColloInfo.actionType;
            item.put(MyActionsHelper.map_val_action, mActions.get(i));

            item.put(MyActionsHelper.map_val_action_logo_res,R.drawable.sec_action_logo);

            item.put(MyActionsHelper.map_val_action_name,mActions.get(i).collectName);
            item.put(MyActionsHelper.map_val_action_browse_time,mActions.get(i).actionBrowseTime);
            try {
                String[] str = mActions.get(i).extendInfo.split("#");
                String browseTime = str[str.length - 1];
                item.put(MyActionsHelper.map_val_action_browse_time, Long.valueOf(browseTime));
                int type_num = Integer.parseInt(mActions.get(i).extendInfo.split("#")[0]);
                actionColloInfo.actionSonType = type_num;
                item.put(MyActionsHelper.map_val_action_type_logo_res,
                        ResourceUtils.getActionTypeImage(actionColloInfo.actionSonType,mBaseActivity));
                item.put(MyActionsHelper.map_val_action_type_name,
                        ResourceUtils.getActionType(actionColloInfo.actionSonType,mBaseActivity));

            } catch (Exception e) {
                item.put(MyActionsHelper.map_val_action_type_logo_res,
                        ResourceUtils.getActionTypeImage(6,mBaseActivity));
                item.put(MyActionsHelper.map_val_action_browse_time,0);
                item.put(MyActionsHelper.map_val_action_type_name,mBaseActivity.getStringResources("ui_square_unknown"));
            }

            try {
                int time = Integer.parseInt(mActions.get(i).extendInfo.split("#")[2]);
                item.put(MyActionsHelper.map_val_action_time,TimeTools.getMMTime(time * 1000));
            } catch (Exception e) {
                item.put(MyActionsHelper.map_val_action_time,TimeTools.getMMTime(0));
            }

            if (mActions.get(i).collectDescriber != null
                    && !mActions.get(i).collectDescriber.equals("")) {
                item.put(MyActionsHelper.map_val_action_disc,
                        mActions.get(i).collectDescriber);
            } else {
                item.put(MyActionsHelper.map_val_action_disc,
                        ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
            }

            if (isDownLoadFinish(actionColloInfo.collectRelationId,mMyDownLoadHistory)) {
                item.put(ActionsLibHelper.map_val_action_download_state,
                        MyActionsHelper.Action_download_state.download_finish);
            } else {
                item.put(ActionsLibHelper.map_val_action_download_state,
                        MyActionsHelper.Action_download_state.not_download);
            }

            item.put(MyActionsHelper.map_val_action_download_progress,new Double(0));

            mDatas.add(item);
        }

        return mDatas;
    }

    public void dealResPonseResult(String jsonString,List<Map<String, Object>> mNoNumberData,List<String> numberNameList)  {
        List<Map<String, Object>> mDatas = new ArrayList<>();
        mDatas.addAll(mNoNumberData);
        try {
            boolean status = false;
            if(jsonString != null){
                status = JsonTools.getJsonStatus(jsonString);
            }
            if(status){
               JSONArray jsonArray = JsonTools.getJsonModels(jsonString);
                for(int len = 0; len < jsonArray.length() ; len++){
                    JSONObject jsonObject = jsonArray.getJSONObject(len);
                    String actionOriginalId = jsonObject.getString("actionOriginalId");
                    String actionName = jsonObject.getString("actionName");
                    int actionSonType = jsonObject.getInt("actionSonType");
                    String actionDesciber = jsonObject.getString("actionDesciber");
                    int actionBrowseTime = jsonObject.getInt("actionBrowseTime");
                    int actionTime = jsonObject.getInt("actionTime");
                    String actionHeadUrl = jsonObject.getString("actionHeadUrl");

                    Map<String, Object> action_item = new HashMap<String, Object>();

                    Iterator<String> iter = numberNameList.iterator();
                    while(iter.hasNext()){
                        String numberName = iter.next();
                        if(numberName.equals(actionOriginalId)){
                            iter.remove();
                        }
                    }

                    action_item.put(MyActionsHelper.map_val_action,actionOriginalId);
                    action_item.put(MyActionsHelper.map_val_action_name, actionName);
                    action_item.put(MyActionsHelper.map_val_action_type_logo_res,R.drawable.actions_item_unkown);
                    action_item.put(MyActionsHelper.map_val_action_time, actionTime);
                    action_item.put(MyActionsHelper.map_val_action_disc, actionDesciber);
                    action_item.put(MyActionsHelper.map_val_action_browse_time,actionBrowseTime);
                    action_item.put(MyActionsHelper.map_val_action_head_url,actionHeadUrl);
                    action_item.put(MyActionsHelper.map_val_action_type_logo_res,
                            ResourceUtils.getActionTypeImage(actionSonType,mBaseActivity));
                    action_item.put(MyActionsHelper.map_val_action_type_name,
                            ResourceUtils.getActionType(actionSonType,mBaseActivity));

                    action_item.put(MyActionsHelper.map_val_action_time,
                                    TimeTools.getMMTime((int) actionTime));

                    if (actionDesciber != null && !actionDesciber.equals("")) {
                        action_item.put(ActionsLibHelper.map_val_action_disc,actionDesciber);
                    } else {
                        action_item.put(
                                ActionsLibHelper.map_val_action_disc,
                                ((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
                    }

                    action_item.put(MyActionsHelper.map_val_action_is_playing, false);
                    action_item.put(MyActionsHelper.map_val_action_selected, false);
                    mDatas.add(action_item);

                }

                for(int num = 0; num < numberNameList.size(); num++){
                    Map<String, Object> action_item = new HashMap<String, Object>();

                    String name = numberNameList.get(num);
                    action_item.put(MyActionsHelper.map_val_action,name);
                    action_item.put(MyActionsHelper.map_val_action_name, name);
                    action_item.put(MyActionsHelper.map_val_action_type_logo_res,R.drawable.actions_item_unkown);
                    action_item.put(MyActionsHelper.map_val_action_type_name,mBaseActivity.getStringResources("ui_square_unknown"));
                    action_item.put(MyActionsHelper.map_val_action_time, 0);
                    action_item.put(MyActionsHelper.map_val_action_disc,mBaseActivity.getStringResources("ui_action_no_description") );
                    action_item.put(MyActionsHelper.map_val_action_is_playing, false);
                    action_item.put(MyActionsHelper.map_val_action_selected, false);
                    mDatas.add(action_item);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = MSG_DO_SYNC_SERVER_ACTION_FINISH;
        msg.obj = mDatas;
        mHandler.sendMessage(msg);
    }

    public void doRemoveCollectWeb(ActionColloInfo mAction) {

        updateItemImmediately(mCurrentRemovedItem);
        UserInfo info = ((AlphaApplication) mBaseActivity
                .getApplicationContext()).getCurrentUserInfo();
        if (info == null) {
            for(IActionsUI ui:mActionListeners)
            {
                ui.onNoteNoUser();
            }
            return;
        }
        mCollcationManager.removeWebRecord(mAction.collectRelationId);

        doRemoveCollectLocal(mAction);
    }

    public  List<Map<String,Object>> loadCreatedDatas(List<NewActionInfo> mActions,List<Map<String,Object>> syncList)
    {
        UbtLog.d(TAG,"lihai----loadCreatedDatas-1");
        List<Map<String, Object>> mDatas = new ArrayList<>();
        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            NewActionInfo info =  mActions.get(i);
            //注释，删掉动态后，类型变无知
            //info.actionSonType =info.actionType;
            action_item.put(MyActionsHelper.map_val_action,
                    info);
            action_item.put(MyActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);
            action_item.put(MyActionsHelper.map_val_action_name,
                    mActions.get(i).actionName);
            action_item.put(MyActionsHelper.map_val_action_browse_time,
                    0);
            action_item.put(MyActionsHelper.map_val_action_type_name, ResourceUtils.getActionType(info.actionSonType,mBaseActivity));
            action_item.put(MyActionsHelper.map_val_action_type_logo_res,
                   ResourceUtils.getActionTypeImage(info.actionSonType,mBaseActivity));
            int actionTime = (int)mActions.get(i).actionTime;
            action_item.put(MyActionsHelper.map_val_action_time, TimeTools
                    .getMMTime(actionTime>0?actionTime*1000:mActions.get(i).getTitleTime()));
            action_item.put(MyActionsHelper.map_val_action_disc,
                    mActions.get(i).actionDesciber);
            action_item.put(MyActionsHelper.map_val_action_selected,false);
            if (getCurrentPlayName().equals(mActions.get(i).actionName)) {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        false);
            }
            action_item.put(MyActionsHelper.map_val_action_is_playing, false);

            String fileName = String.valueOf(mActions.get(i).actionId);
            if(fileName.length() != 13){//下载
                fileName = String.valueOf(mActions.get(i).actionOriginalId);
            }
            String file_name = fileName+".hts";

            boolean is_send = false;
            if (syncList != null) {
                for (int j = (localSize + myDownloadSize); j < syncList.size(); j++) {
                    try {
                        String robot_action_name = syncList.get(j)
                                .get(MyActionsHelper.map_val_action) + ".hts";
                        if (robot_action_name.equals(file_name)) {
                            is_send = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(is_send){
                action_item.put(MyActionsHelper.map_val_action_download_state, Action_download_state.send_finish);
            }else{
                action_item.put(MyActionsHelper.map_val_action_download_state, Action_download_state.download_finish);
            }
            mDatas.add(action_item);
        }

      return mDatas;
    }
    public  List<Map<String,Object>> loadSyncDatas(List<ActionInfo> mActions,int type)

    {
        List<Map<String, Object>> mDatas = new ArrayList<>();
        for (int i = 0; i < mActions.size(); i++) {
            Map<String, Object> action_item = new HashMap<String, Object>();
            ActionInfo info = mActions.get(i);
            if(info == null)
                continue;
            info.actionSonType = info.actionType;
            UbtLog.d(TAG,"mActions.get(i) == " + mActions.get(i));
            action_item.put(MyActionsHelper.map_val_action,
                    mActions.get(i));
            action_item.put(MyActionsHelper.map_val_action_logo_res,
                    R.drawable.sec_action_logo);
            action_item.put(MyActionsHelper.map_val_action_name,
                    mActions.get(i).actionName);
            action_item.put(MyActionsHelper.map_val_action_type_name, ResourceUtils.getActionType(info.actionSonType,mBaseActivity));
            action_item.put(MyActionsHelper.map_val_action_type_logo_res,
                    ResourceUtils.getActionTypeImage(info.actionSonType,mBaseActivity));
            if(type== FillLocalContent.CREATE_ACTIONS)
            action_item.put(MyActionsHelper.map_val_action_time, TimeTools
                    .getMMTime((int)mActions.get(i).actionTime*1000));
            else
                action_item.put(MyActionsHelper.map_val_action_time, TimeTools
                        .getMMTime((int)mActions.get(i).actionTime*1000));
            action_item.put(MyActionsHelper.map_val_action_disc,
                    mActions.get(i).actionDesciber);
            action_item.put(MyActionsHelper.map_val_action_selected,false);
            if (getCurrentPlayName().equals(mActions.get(i).actionName)) {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        true);
            } else {
                action_item.put(MyActionsHelper.map_val_action_is_playing,
                        false);
            }
            action_item.put(MyActionsHelper.map_val_action_is_playing, false);
            action_item.put(MyActionsHelper.map_val_action_download_state, MyActionsHelper.Action_download_state.download_finish);
            mDatas.add(action_item);
        }

      return mDatas;
    }


    /**
     * 播放动作
     * @param item 播放对象
     * @param mCurrentPlayPos 播放对象索引
     * @param type 播放对象类型
     */
    public void startPlayAction( Map<String, Object> item,int mCurrentPlayPos,MyActionsHelper.Action_type type) {

        mCurrentPlayActions = item.get(MyActionsHelper.map_val_action);
        mCurrentPlayActionType = type;
        this.mCurrentPlayPos = mCurrentPlayPos;

        UbtLog.d(TAG,"hasSupportA2DP:"+hasSupportA2DP);
        if(hasSupportA2DP){
            readAudioSourceState();
        }else {
            mHandler.sendEmptyMessage(MESSAGE_DO_PLAY_ACTION);
        }

        /*doActionCommand(MyActionsHelper.Command_type.Do_play,
                item.get(MyActionsHelper.map_val_action), type);*/
        //this.mCurrentPlayPos = mCurrentPlayPos;

    }

    @Override
    public void onDeviceDisConnected(String mac) {
        mFileLoader = null;
        super.onDeviceDisConnected(mac);
    }

    /**
     * 读取音源状态
     */
    private void readAudioSourceState(){
        doSendComm(ConstValue.DV_READ_AUDIO_SOURCE_STATE,null);
    }

    /**
     * 设置后的音源状态
     * @param source
     * 1 音源为手机APP蓝牙音频
     * 0 音源为机器人的音频
     */
    private void setAudioSource(byte source){
        byte[] param = new byte[1];
        param[0] = source;
        doSendComm(ConstValue.DV_SET_AUDIO_SOURCE,param);
    }



    public  void stopPlayAction()
    {
        if (getPlayerState() == Play_state.action_finish
                && getNewPlayerState() == PlayerState.STOPING)
            return;
       doActionCommand(MyActionsHelper.Command_type.Do_Stop,
                "", MyActionsHelper.Action_type.My_download);
    }

    public void noteCollectDeleted(Context mContext, final ActionColloInfo actionColloInfo,final int pos)
    {

        this.mCurrentRemovedItem = pos;
        new AlertDialog(mContext).builder().setMsg(mBaseActivity.getStringResources("ui_myaction_invalid_collection")).setCancelable(true).
                setPositiveButton(mBaseActivity.getStringResources("ui_common_remove"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doRemoveCollectWeb(actionColloInfo);
                    }
                }).setNegativeButton(mBaseActivity.getStringResources("ui_common_cancel"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();

    }

    /**先更新UI*/
    public void updateItemImmediately(int pos)
    {
        MyActionsActivity instance = (MyActionsActivity)mActivity;
        instance.fragment.mDatas.remove(pos);
        if(instance.fragment.mDatas.size()==1&&instance.fragment.mDatas.get(0)==null
                ||instance.fragment.mDatas.size()<1)
        {
            instance.fragment.mDatas.clear();
            instance.fragment.updateViews(true,0);
        }else
        instance.fragment.sendMessage(pos, BaseMyActionsFragment.DELETE_ITEMS);
    }

    public void notifyAdapters(int pos)
    {
        MyActionsSyncActivity instance = (MyActionsSyncActivity)mActivity;
        instance.notifyAdapters(pos);
    }

    /**复位动作**/
    public void doDefaultActions()
    {
        byte[] param = new byte[1];
        param[0] = 0;
        doSendComm(ConstValue.DV_SET_ACTION_DEFAULT, param);
    }

    public void doCancelPublishActions(NewActionInfo newActionInfo)
    {
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.deleteCreationPublish);
        String params = HttpAddress.getParamsForPost(new String[]{newActionInfo.actionOriginalId+""}, HttpAddress.Request_type.deleteCreationPublish,mBaseActivity);

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d("wilson","onResponse:"+e.getMessage());
                    }

                    @Override
                    public void onResponse(String s,int i) {
                        UbtLog.d("wilson","onResponse:"+s);

                    }
                });
    }

    public void doRemoveCollectLocal(final ActionColloInfo mAction){

        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                //更新数据库
                ActionsOnlineCacheOperater.getInstance(mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).updateOnlineCacheForDelCollect(mAction.collectRelationId,mAction.actionCollectTime);
            }
        });

    }

    /**
     * 参与活动创建动作发布
     * @param schemeId
     * @param schemeName
     */
    public void setSchemeInfo(String schemeId,String schemeName){
        mSchemeId = schemeId;
        mSchemeName = schemeName;
    }

    /**
     * 获取参与活动ID
     * @return
     */
    public String getSchemeId(){
        return mSchemeId;
    }

    /**
     * 获取参与活动名称
     * @return
     */
    public String getSchemeName(){
        return mSchemeName;
    }

    public boolean isDownLoadFinish(long actionId,List<ActionRecordInfo> mMyDownLoadHistory) {
        boolean result = false;
        if (mMyDownLoadHistory != null) {
            for (int i = 0; i < mMyDownLoadHistory.size(); i++) {
                if (actionId == mMyDownLoadHistory.get(i).action.actionId) {
                    result = true;
                    break;
                }
            }
        } else {
            result = false;
        }
        return result;
    }


    public void doPlayForBlockly(String name, boolean isWalk){
        mPlayer.doPlayActionForBlockly(name, isWalk);
    }
    public void doPlay(ActionInfo name){
        mPlayer.doPlayAction(name);
    }


    public void setLooping(boolean flag){
        isLooping=flag;
        if(!flag){
            mCurrentSeletedNameList.clear();
        }
    }
    public boolean getLoopingFlag(){
        return isLooping;
    }

}

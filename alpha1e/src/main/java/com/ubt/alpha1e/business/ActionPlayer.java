package com.ubt.alpha1e.business;

import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.BlueToothManager;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.ubtechinc.sqlite.UBXDataBaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActionPlayer implements BlueToothInteracter {

    private static final String TAG = "ActionPlayer";

    // 当前正在放的动作（可能是动作列表）
    private List<String> mSourceActionNameList;
    // 主干信息
    private static ActionPlayer thiz;
    private BlueToothManager mBtManager;
    private String mBtMac;
    private List<ActionPlayerListener> mListeners;
    // 状态信息
    private Play_state mCurrentPlayState = Play_state.action_finish;
    public Play_type mCurrentPlayType;
    // 循环播放相关信息
    private CycleThread mCyclePlayThread;
    private Object mCyclePlaylock = new Object();
    private boolean mIsCycleContinuePlay = true;
    // 机器人里面的动作列表
    private static List<String> mRobotActions;
    private String mCurrentDefaultAction = "初始化";
    // 常量
    public final static String CYCLE_ACTION_NAME = "CYCLE_ACTION_NAME";

    private static final int UI_NOTE_PLAY_CYCLE_NEXT = 1001;
    private static final int UI_NOTE_PLAY_CYCLE_STOP = 1002;//循环20分钟 自动停止

    private static final int AUTO_STOP_PLAY_CYCLE_TIME = 20*60*1000;//20分钟

    private String cycleActionName = "";  //增加循环播放时动作的名称，用于在全局控件中更新显示动作名称
    private String mCurrentPlayActionName = "";
    private long actionOriginalId = 0;

    private Date mLastPlayTime = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UI_NOTE_PLAY_CYCLE_NEXT:
                    if (thiz.mListeners != null) {
                        for (int j = 0; j < mListeners.size(); j++) {
                            cycleActionName = (String)msg.obj;
                            thiz.mListeners.get(j).notePlayCycleNext((String)msg.obj);
                        }
                    }

                    break;
                case UI_NOTE_PLAY_CYCLE_STOP:
                    UbtLog.d(TAG,"20 分钟时间到，自动停止循环播放");
                    thiz.doStopCycle(false);
                    break;
                default:
                    break;
            }

        }
    };

    public static enum Play_type {
        single_action, cycle_action
    }

    public static enum Play_state {
        action_playing, action_pause, action_finish
    }

    private ActionPlayer() {

    }

    public Play_state getState() {
        // 获取当前播放状态
        return thiz.mCurrentPlayState;
    }

    public static void setState() {
        thiz.mCurrentPlayState = Play_state.action_finish;
    }

    public String getBtMac(){
        return thiz.mBtMac;
    }

    public String getPlayActonName() {
        if (mSourceActionNameList == null || mSourceActionNameList.size() < 1) {
            return "";
        } else if (mSourceActionNameList.size() > 1) {
            return cycleActionName;
        } else{
            return mSourceActionNameList.get(0);
        }
    }

    public long getActionOriginalId(){
        return actionOriginalId;
    }

    public static void setRobotActions(List<String> actions) {
        mRobotActions = actions;
    }

    public Play_type getPlayType() {
        return thiz.mCurrentPlayType;
    }

    public int getCycleNum() {
        if (mSourceActionNameList == null){
            return 0;
        }else{
            return mSourceActionNameList.size();
        }
    }

    public static ActionPlayer getInstance(BlueToothManager _btManager,
                                           String device_mac) {
        if (thiz == null){
            thiz = new ActionPlayer();
        }
        thiz.mBtManager = _btManager;
        thiz.mBtManager.addBlueToothInteraction(thiz);
        thiz.mBtMac = device_mac;
        return thiz;
    }

    public static ActionPlayer getInstance() {
        return thiz;
    }

    public static void reset() {
        thiz.mBtManager.removeBlueToothInteraction(thiz);
        thiz = null;
    }

    public void addListener(ActionPlayerListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<ActionPlayerListener>();
        }

        if (!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }

    public void removeListener(ActionPlayerListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<ActionPlayerListener>();
        }

        if (mListeners.contains(listener)){
            mListeners.remove(listener);
        }
    }

    public void doStopPlay() {
        doStopCurrentPlay(true);
    }

    private void doStopCurrentPlay(boolean needSendComm) {
        UbtLog.d(TAG, "doStopPlay尝试停止播放器:mCurrentPlayState-->"
                + mCurrentPlayState + ",mCurrentPlayType-->" + mCurrentPlayType);
        if (thiz.mCurrentPlayState != Play_state.action_finish) {
            if (thiz.mCurrentPlayType == Play_type.single_action) {
                mCyclePlayThread = null;
                UbtLog.d(TAG, "-->notePlayFinish doStopCurrentPlay");
                thiz.doStopSingleAction(needSendComm);
            } else {
                thiz.doStopCycle(false);
            }
        }
        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<String>();
        }
        mSourceActionNameList.clear();
    }

    public void doPlayAction(ActionInfo info) {
        UbtLog.d(TAG, "---wmma mCurrentPlayState=" + mCurrentPlayState + " actionOriginalId =" + info.actionOriginalId);
        actionOriginalId = info.actionOriginalId;
        if (thiz.mCurrentPlayState == Play_state.action_pause) {
            thiz.mCurrentPlayState = Play_state.action_finish;
            UbtLog.d(TAG, "---wmma mCurrentPlayType=" + mCurrentPlayType);
            if (mCurrentPlayType == Play_type.cycle_action) {
                thiz.doStopCycle(false);
            }else{
                doStopCurrentPlay(false);
            }
        } else {
            // 原来逻辑
            // doStopPlay();
            // 修改之后的逻辑
            doStopCurrentPlay(false);
        }
        mCurrentPlayType = Play_type.single_action;
        UbtLog.d(TAG, "doPlayAction-->" + thiz.mCurrentPlayState + " to action_playing");
        thiz.mCurrentPlayState = Play_state.action_playing;

        String action_name = "";

        if (info.hts_file_name != null && !info.hts_file_name.equals("")){
            action_name = info.hts_file_name.split("\\.")[0];
        }else{
            action_name = info.actionName;
        }

        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<>();
        }
        mSourceActionNameList.add(action_name);
        MyActionsHelper.Action_type mCurrentActionType= MyActionsHelper.mCurrentLocalPlayType;
        if(action_name.equals(mCurrentDefaultAction)){
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
        }
        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local
                || MyActionsHelper.mCurrentPlayType == MyActionsHelper.Action_type.My_download){
            action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
        }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
            action_name = FileTools.actions_creation_robot_path + "/"+action_name+".hts";
        }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_gamepad){
            action_name = FileTools.actions_gamepad_robot_path + "/"+action_name+".hts";
        }else{
            action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
        }

        UbtLog.d(TAG,"mCurrentPlayActionName = " + action_name + "--" + MyActionsHelper.mCurrentLocalPlayType + "---" + MyActionsHelper.mCurrentPlayType + "--" + mCurrentDefaultAction);

        doPlay(action_name);
        if (thiz.mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList,
                        info, mCurrentPlayType);
            }
        }

    }

    /*******************************add for block*****************************************/

    public void doPlayActionForBlockly(String actionName, boolean isWalk){
        UbtLog.d(TAG, "---wmma mCurrentPlayState=" + mCurrentPlayState + " actionName =" + actionName);
        if (thiz.mCurrentPlayState == Play_state.action_pause) {
            thiz.mCurrentPlayState = Play_state.action_finish;
            UbtLog.d(TAG, "---wmma mCurrentPlayType=" + mCurrentPlayType);
            if (mCurrentPlayType == Play_type.cycle_action) {
                thiz.doStopCycle(false);
            }else{
                doStopCurrentPlay(false);
            }
        } else {
            doStopCurrentPlay(false);
        }
        mCurrentPlayType = Play_type.single_action;
        MyLog.writeLog("播放功能", "doPlayAction-->" + thiz.mCurrentPlayState + " to action_playing");
        thiz.mCurrentPlayState = Play_state.action_playing;

        String action_name = "";
        action_name = actionName;

        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<>();
        }
        mSourceActionNameList.add(action_name);
        MyActionsHelper.Action_type mCurrentActionType= MyActionsHelper.mCurrentLocalPlayType;
        if(actionName.equals(mCurrentDefaultAction)){
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
        }

        if(isWalk){
            action_name = FileTools.actions_walk_robot_path + "/"+action_name+".hts";
        }else{
            if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local
                    || MyActionsHelper.mCurrentPlayType == MyActionsHelper.Action_type.My_download){
                action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
            }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
                action_name = FileTools.actions_creation_robot_path + "/"+action_name+".hts";
            }else{
                action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
            }
        }

        UbtLog.d(TAG,"mCurrentPlayActionName = " + action_name + "--" + MyActionsHelper.mCurrentLocalPlayType + "---" + MyActionsHelper.mCurrentPlayType + "--" + mCurrentDefaultAction);

        doPlay(action_name);
        if (thiz.mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList,
                        null, mCurrentPlayType);
            }
        }
    }

    /*******************************end*****************************************/

    public void doCycle(String[] _action_name) {
        UbtLog.d(TAG, "循环播放功能 ActionPlayer.doCycle:_action_name-->"+ _action_name.length);

        doStopPlay();
        StopCycleThread(false);
        doInitDefaultAction();

        mIsCycleContinuePlay = true;
        mCurrentPlayType = Play_type.cycle_action;
        thiz.mCurrentPlayState = Play_state.action_playing;

        for (int i = 0; i < _action_name.length; i++) {
            mSourceActionNameList.add(_action_name[i]);
        }

        String[] mActionNameList = new String[thiz.mSourceActionNameList.size() * 2];
        for (int i = 0; i < thiz.mSourceActionNameList.size(); i++) {
            mActionNameList[i * 2] = mCurrentDefaultAction;
            mActionNameList[i * 2 + 1] = mSourceActionNameList.get(i);
        }

        UbtLog.d(TAG, "播放列表长度:mActionNameList-->" + mActionNameList.length);

        if (mActionNameList == null || mActionNameList.length < 1){
            return;
        }

        if (mCyclePlayThread != null && mCyclePlayThread.isAlive()) {

        } else {
            mCyclePlayThread = new CycleThread(mActionNameList);
            MyLog.writeLog("循环播放功能", "线程启动");
            mCyclePlayThread.start();
        }

        thiz.mCurrentPlayState = Play_state.action_playing;

        if (thiz.mListeners != null)
        {
            for (int i = 0; i < mListeners.size(); i++) {
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList, null, mCurrentPlayType);
            }
        }

    }

    private void doStopSingleAction(boolean needSendComm) {

        if (needSendComm) {
            UbtLog.d(TAG,"doStopSingleAction : " + ConstValue.DV_STOPPLAY);
            mBtManager.sendCommand(mBtMac, ConstValue.DV_STOPPLAY, null, 0, false);
        }

        if(thiz == null){
            return;
        }

        UbtLog.d(TAG, "doStopSingleAction-->" + thiz.mCurrentPlayState + " to action_finish");

        notePlayFinish();

    }

    private void doStopCycle(boolean is_low_power) {

        if(mCyclePlayThread == null){
            return;
        }
        //UbtLog.d(TAG,"thiz:"+thiz);
        if(thiz == null){
            return;
        }

        if(mHandler.hasMessages(UI_NOTE_PLAY_CYCLE_STOP)){
            mHandler.removeMessages(UI_NOTE_PLAY_CYCLE_STOP);
        }

        mIsCycleContinuePlay = false;
        mCyclePlayThread.isShutDowm = true;
        if (!is_low_power)
            doStopSingleAction(true);
        else {
            UbtLog.d(TAG, "-->notePlayFinish from doStopCycle");
            notePlayFinish();
        }
        continueCycle();
    }

    private void notePlayFinish() {
        UbtLog.d(TAG, "-->notePlayFinish!");
        if(thiz == null){
            return;
        }
        thiz.mCurrentPlayState = Play_state.action_finish;

        if (thiz.mListeners != null){
            for (int i = 0; i < mListeners.size(); i++) {
                //UbtLog.d(TAG, "mListeners" + "_" + i + ":" + thiz.mListeners.get(i).hashCode());
                thiz.mListeners.get(i).notePlayFinish(mSourceActionNameList,
                        mCurrentPlayType,
                        thiz.mListeners.get(i).hashCode() + "");
            }
        }
    }

    private void continueCycle() {
        try {
            synchronized (mCyclePlaylock) {
                UbtLog.d(TAG, "---mCyclePlaylock.notify");
                mCyclePlaylock.notify();
            }
        } catch (Exception e) {

        }

    }

    public void doLostPower(String device_mac) {
        mBtManager.sendCommand(device_mac, ConstValue.DV_DIAODIAN, null, 0,false);
        if (thiz.mCurrentPlayType == Play_type.cycle_action) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ActionPlayer.this.doStopPlay();
                }
            }, 100);
        }
    }

    public void doPauseOrContinueAction() {

        if (thiz.mCurrentPlayState == Play_state.action_finish){
            return;
        }

        if (thiz.mCurrentPlayState == Play_state.action_playing) {
            byte[] papram = new byte[1];
            papram[0] = 0;
            mBtManager.sendCommand(mBtMac, ConstValue.DV_PAUSE, papram, papram.length, false);
            if (thiz.mListeners != null){
                for (int i = 0; i < mListeners.size(); i++) {
                    thiz.mListeners.get(i).notePlayPause(mSourceActionNameList,
                            mCurrentPlayType);
                }
            }

            UbtLog.d(TAG, "doPauseOrContinueAction-->" + thiz.mCurrentPlayState + " to action_pause");

            thiz.mCurrentPlayState = Play_state.action_pause;
        } else {
            byte[] papram = new byte[1];
            papram[0] = 1;
            mBtManager.sendCommand(mBtMac, ConstValue.DV_PAUSE, papram, papram.length, false);

            if (thiz.mListeners != null){
                for (int i = 0; i < mListeners.size(); i++) {
                    thiz.mListeners.get(i).notePlayContinue(
                            mSourceActionNameList, mCurrentPlayType);
                }
            }

            UbtLog.d(TAG, "doPauseOrContinueAction-->" + thiz.mCurrentPlayState + " to action_playing");
            thiz.mCurrentPlayState = Play_state.action_playing;
        }
    }

    // ------------------------------------------------------------------------

    private void doPlay(String info) {
        mCurrentPlayActionName = info;
        UbtLog.d(TAG,"play action = " + info);
        mLastPlayTime = new Date(System.currentTimeMillis());

        byte[] actions = BluetoothParamUtil.stringToBytes(info);
        mBtManager.sendCommand(mBtMac, ConstValue.DV_PLAYACTION, actions,actions.length, false);

    }

    // ------------------------------------------------------------------------

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

        if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {

            if (param[0] == 0) {
                // 声音状态
                if (param[1] == 1) {
                    // 静音
                } else {
                    // 有声音
                }
            } else if (param[0] == 1) {
                // 播放状态

                if (param[1] == 0) {
                    // 暂停
                } else {
                    // 非暂停
                }
            } else if (param[0] == 2) {
                // 音量状态
                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }
            } else if (param[0] == 3) {
                // 舵机灯状态

                if (param[1] == 0) {
                    // 灭
                } else {
                    // 亮
                }
            } else if (param[0] == 4) {

                if (param[1] == 0) {
                    // 拔出
                } else {
                    // 插入
                }
            }
        } else if (cmd == ConstValue.DV_ACTION_FINISH)// 动作播放完毕
        {
            String finishPlayActionName = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "finishPlayActionName = " + finishPlayActionName + "    mCurrentPlayActionName = " + mCurrentPlayActionName);

            boolean isStopLocal = false;
            if(finishPlayActionName.contains(mCurrentPlayActionName)){
                isStopLocal = true;
            }
            if(!isStopLocal){
                //如果回复播放完成的动作名称与本地当前播放动作名称不一致，则不予处理
                return;
            }
            UbtLog.d(TAG, "mCurrentPlayType = " + mCurrentPlayType);
            if (mCurrentPlayType == Play_type.cycle_action) {
                UbtLog.d(TAG, "-->notePlayFinish Play_type.cycle_action");
                if (thiz != null) {
                    try {
                        synchronized (mCyclePlaylock) {
                            MyLog.writeLog("循环播放功能", "解锁");
                            mCyclePlaylock.notify();
                        }
                    } catch (Exception e) {
                    }
                }

            } else if (mCurrentPlayType == Play_type.single_action) {
                UbtLog.d(TAG, "-->notePlayFinish Play_type.single_action");

                // 防止暂停循环播放后点击普通动作
                if (mCyclePlayThread == null) {
                    UbtLog.d(TAG, "-->notePlayFinish onReceiveData");
                    doStopCurrentPlay(false);
                } else {
                    if (!mCyclePlayThread.isShutDowm) {
                        UbtLog.d(TAG, "-->notePlayFinish onReceiveData 2");
                        doStopCurrentPlay(false);
                    }
                    doStopCurrentPlay(false);
                }
            }

        } else if (cmd == ConstValue.DV_PLAYACTION) {
            UbtLog.d(TAG, "-->notePlay ConstValue.DV_PLAYACTION " + param[0] + "    = " + mCurrentPlayType);
            if (param[0] == 2) {
                if (mCurrentPlayType == Play_type.cycle_action)
                    thiz.doStopCycle(true);
            }
        }
        // 如果禁止边充边玩
        else if (cmd == ConstValue.SET_PALYING_CHARGING) {
            if (param[0] == 0 && thiz.mCurrentPlayState == Play_state.action_playing) {
                if(mIsCycleContinuePlay){
                    UbtLog.d(TAG,"SET_PALYING_CHARGING  ");
                    mIsCycleContinuePlay = false;
                }

                if (thiz.mListeners != null){
                    for (int i = 0; i < mListeners.size(); i++) {
                        thiz.mListeners.get(i).notePlayChargingError();
                    }
                }
            }
        }else if(cmd == ConstValue.DV_STOPPLAY){

            if (mCurrentPlayType == Play_type.cycle_action){
                thiz.doStopCycle(true);
            }else {
                Date nowDate = new Date(System.currentTimeMillis());
                long mDiffTime = 1000;
                if(mLastPlayTime != null){
                    mDiffTime = nowDate.getTime() - mLastPlayTime.getTime();
                }
                //UbtLog.d(TAG,"DV_STOPPLAY = " + cmd + "  mCurrentPlayType = " + mCurrentPlayType + " mDiffTime = " + mDiffTime);
                if(mDiffTime >= 1000){
                    //1S之内返回的，是认为是主动切换停止返回的，不予处理，大于1S的，是机器人主动停止返回的
                    doStopCurrentPlay(false);
                }
            }
        }else if(cmd == ConstValue.DV_CURRENT_PLAY_NAME){
            String robotCurrentPlayName = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "robotCurrentPlayName : " + robotCurrentPlayName + "    mCurrentPlayActionName = " + mCurrentPlayActionName);

        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub
        UbtLog.d(TAG, "---onConnectState");
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG, "---mCyclePlaylock.onDeviceDisConnected");
        if(mHandler.hasMessages(UI_NOTE_PLAY_CYCLE_STOP)){
            mHandler.removeMessages(UI_NOTE_PLAY_CYCLE_STOP);
        }
        mIsCycleContinuePlay = false;
        mCurrentPlayType = null;

    }


    class CycleThread extends Thread {
        private String[] mActionNameList;
        public boolean isShutDowm = false;

        public CycleThread(String[] actions) {
            mActionNameList = actions;
        }

        public void run() {
            UbtLog.d(TAG, "---mCyclePlaylock.run");

            //最长播放时长
            mHandler.sendEmptyMessageDelayed(UI_NOTE_PLAY_CYCLE_STOP,AUTO_STOP_PLAY_CYCLE_TIME);

            int i = 0;
            while (mIsCycleContinuePlay && !isStopCycleThread ) {
                UbtLog.d(TAG, "循环播放功能，播放动作：" + mActionNameList[i] + "   isShutDowm：" + isShutDowm+"    mIsCycleContinuePlay:   "+mIsCycleContinuePlay+"  isStopCycleThread:  "+isStopCycleThread);
                if (!isShutDowm && thiz.mCurrentPlayState == Play_state.action_playing) {
                    String action_name = mActionNameList[i];

                    if(!mCurrentDefaultAction.equals(action_name)){
                        ActionInfo actionInfo = MyActionsHelper.mCurrentSeletedActionInfoMap.get(action_name);
                        if(actionInfo == null){
                            //线上友盟crash报null,但是找不到规律，判null处理
                            continue;
                        }
                        int pos = actionInfo.actionSize;
                        if(pos < MyActionsHelper.localSize){
                            action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
                        }else if(pos >= MyActionsHelper.localSize  &&  pos < (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                            action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
                        }else if(pos >= (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                            action_name = FileTools.actions_creation_robot_path + "/"+actionInfo.hts_file_name+".hts";
                        }

                    }else {
                        //默认动作
                        action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
                    }

                    UbtLog.d(TAG,"doCycle mCurrentPlayActionName = " + action_name);
                    doPlay(action_name);

                    Message msg = new Message();
                    msg.what = UI_NOTE_PLAY_CYCLE_NEXT;
                    msg.obj = mActionNameList[i];
                    mHandler.sendMessage(msg);

                }
                i++;
                if (i == mActionNameList.length){
                    i = 0;
                }
                synchronized (mCyclePlaylock) {
                    try {
                        UbtLog.d(TAG, "循环播放功能，等待解锁");
                        mCyclePlaylock.wait();
                    } catch (InterruptedException e) {

                    }
                }
            }

            doStopCycle(false);
            // mCyclePlayThread = null;
        }
    }

    public void doDefault() {

        doInitDefaultAction();

        ActionInfo info = new ActionInfo();
        info.actionName = mCurrentDefaultAction;
        doPlayAction(info);
    }

    /**
     * 初始化Default动作
     */
    private void doInitDefaultAction(){
        if (mRobotActions != null) {
            for (int i = 0; i < mRobotActions.size(); i++) {
                if (mRobotActions.get(i).equals("default")) {
                    mCurrentDefaultAction = "default";
                    break;
                }
                if (mRobotActions.get(i).equals("初始化")) {
                    mCurrentDefaultAction = "初始化";
                    break;
                }
            }
        }
    }

    public static boolean isStopCycleThread = false;
    public static void StopCycleThread(boolean stop){
        UbtLog.d(TAG, "StopCycleThread -1");
        isStopCycleThread = stop;
    }

}

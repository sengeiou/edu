/*
 * Copyright (C) 2008-2016 UBT Corporation.  All rights reserved.
 * Redistribution,modification, and use in source and binary forms are not permitted unless
 * otherwise authorized by UBT.
 */

package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.adapter.MyRoleAcitonAdapter;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.business.FileSendManager;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.edu.data.DB.RemoteRoleActionOperater;
import com.ubt.alpha1e.edu.data.DB.RemoteRoleOperater;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.RemoteItem;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.log.MyLog;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.file.FileUploader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class RemoteHelper extends BaseHelper implements FileSendManager.IFileSendManager,ISharedPreferensListenet {

    private static final String TAG = "RemoteHelper";
    //辅助常量----------------—-start
    public final static String MAP_KEY_ACTION_ITEM = "MAP_KEY_ACTION_ITEM";
    public final static String MAP_KEY_ACTION_ITEM_ICON = "MAP_KEY_ACTION_ITEM_ICON";
    public final static String MAP_KEY_ACTION_ITEM_NAME = "MAP_KEY_ACTION_ITEM_NAME";

    public static final String MAP_KEY_HEAD_IMAGE_ICON = "MAP_KEY_HEAD_ITEM_ICON";
    public static final String MAP_KEY_HEAD_IMAGE_ICON_SELECT = "MAP_KEY_HEAD_ITEM_ICON_SELECT";
    public static final String IS_SUCCESS = "isSuccess";
    public static final String REMOTE_ROLE_INFO_PARAM = "remoteroleInfo";
    private int do_colse_head_prompt = 1001;
    //辅助常量----------------—-end

    //初始化参数---------------start
    private IRemoteUI mUI;
    private ActionPlayer mPlayer;
    //初始化参数---------------end

    //临时参数-----------------start
    private List<String> mActionsNames;
    public static RemoteInfo mCurrentInfo;
    public static RemoteRecordOperater.ModelType mCurrentType;
    //临时参数-----------------end

    //同步信号-----------------start
    private static final int MSG_DO_READ_ACTIONS = 1001;
    private static final int MSG_ON_TF_PULLED = 1002;
    private static final int MSG_ON_SEND_FILE_FINIAH = 1003;
    private static final int MSG_ON_READ_SETTING_ITEMS = 1004;
    private static final int MSG_ON_UPDATE_REMOTE_FINISH = 1005;
    private static final int MSG_ON_READ_REMOTE_ROLE_FINISH = 1006;
    private static final int MSG_ON_ADD_REMOTE_ROLE = 1007;
    private static final int MSG_ON_DELETE_REMOTE_ROLE = 1008;
    private static final int MSG_ON_UPDATE_REMOTE_ROLE = 1009;
    private static final int MSG_ON_ADD_REMOTE_ROLE_ACTION = 1010;
    private static final int MSG_ON_SEND_FILE_START = 1011;
    private static final int MSG_ON_PLAY_ACTION_FILE_NOT_EXIST = 1012;
    private static final int MSG_ON_DEL_HEAD_PROMPT = 1013;
    //同步信号-----------------end
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DO_READ_ACTIONS) {
                mUI.onReadActionsFinish(mActionsNames);
//                EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_READ_WALK_FILE_FINISH, mActionsNames));
            }
            if (msg.what == MSG_ON_TF_PULLED) {
                if(mActionsNames != null){
                    mActionsNames.clear();
                }
                mUI.noteTFPulled();
            }
            if (msg.what == MSG_ON_SEND_FILE_FINIAH) {
                mUI.onSendFileFinish((Boolean) msg.obj);
            }
            if (msg.what == MSG_ON_READ_SETTING_ITEMS) {
                mUI.onReadSettingItem((List<RemoteItem>) msg.obj);
            }
            if (msg.what == MSG_ON_UPDATE_REMOTE_FINISH) {
                mUI.onSendFileFinish((Boolean) msg.obj);
            }
            if(msg.what == MSG_ON_READ_REMOTE_ROLE_FINISH){
                mUI.onReadRemoteRoleFinish((List<RemoteRoleInfo>) msg.obj);
            }
            if(msg.what == MSG_ON_DELETE_REMOTE_ROLE){
                RemoteRoleInfo roleInfo = (RemoteRoleInfo)msg.obj;
                Bundle bundle = msg.getData();
                boolean isSuccess = bundle.getBoolean(IS_SUCCESS);
                mUI.onDelRemoteRole(isSuccess,roleInfo);
            }
            if(msg.what == MSG_ON_ADD_REMOTE_ROLE){
                mUI.onAddRemoteRole((boolean)msg.obj,msg.arg1);
            }
            if(msg.what == MSG_ON_ADD_REMOTE_ROLE_ACTION){
                mUI.onAddRemoteRoleActions((boolean)msg.obj,msg.arg1);
            }
            if(msg.what == MSG_ON_SEND_FILE_START){
                mUI.onSendFileStart();
            }
            if(msg.what == MSG_ON_PLAY_ACTION_FILE_NOT_EXIST){
                mUI.onPlayActionFileNotExist();
            }
            if(msg.what == MSG_ON_DEL_HEAD_PROMPT){
                mUI.onDelRemoteHeadPrompt((boolean) msg.obj);
            }

        }
    };

    public void doAction(int index) {

        MyActionsHelper.doStopMp3ForMyDownload();
        //ActionsHelper.doStopMp3ForMyDownload();

        if (index == -1) {
            //mPlayer.doStopPlay();
            ActionInfo info = new ActionInfo();
            info.actionName = "Default foot";
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_gamepad;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
            if(mPlayer != null){
                mPlayer.doPlayAction(info);
            }
        } else {
            RemoteItem item = RemoteRecordOperater.getItemByIndex(index, mCurrentInfo);
            ActionInfo info = new ActionInfo();
            info.actionName = item.hts_name.split("\\.")[0];
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_gamepad;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
            if(mPlayer != null){
                mPlayer.doPlayAction(info);
            }
        }
    }

    /**
     * 执行步态算法（上下左右方向键）
     * @param direction 方向
     * @param speed 速度
     * @param count 步数
     */
    public void doWalkAction(int direction, int speed, int count){
        byte[] param = new byte[6];
        param[0] = (byte) direction;
        param[1] = (byte) speed;
        param[2] = (byte) 0;
        param[3] = (byte) 0;
        param[4] = (byte) 0;
        param[5] = (byte) 0;
        doSendComm(ConstValue.DV_WALK, param);

    }

    public void doStopWalkAction(){
        byte[] param = new byte[1];
        param[0] = (byte) 0;
        doSendComm(ConstValue.DV_STOP_WALK, param);
    }


    public void doCustomAction(final int index,final int roleId) {

        MyActionsHelper.doStopMp3ForMyDownload();
        if(mPlayer != null){
            mPlayer.doStopPlay();
        }

        if (index == -1) {
            //mPlayer.doStopPlay();
        } else {

            FileTools.pool.execute(new Runnable() {
                @Override
                public void run() {
                    RemoteItem item = RemoteRecordOperater.getItemByIndex(index, mCurrentInfo);
                    ActionInfo info = new ActionInfo();
                    info.actionName = item.hts_name.split("\\.")[0];
                    boolean isSend = false;
                    for(String actionName : mActionsNames){
                        if(actionName.equals(item.hts_name.split("\\.")[0])){
                            isSend = true;
                        }
                    }
                    MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
                    if(isSend){
                        MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_gamepad;
                        if(mPlayer != null){
                            mPlayer.doPlayAction(info);
                        }
                    }else{
                        if(index < 7){
                            List<String> list = new ArrayList();
                            list.add(item.hts_name);
                            sendFiles(list);
                            return;
                        }

                        RemoteRoleActionInfo roleActionInfo = RemoteRoleActionOperater.getInstance(mBaseActivity.getApplicationContext(),
                                FileTools.db_log_cache, FileTools.db_log_name).getRemoteRoleActionInfo(roleId+"",item.show_name,item.hts_name);

                        String filePath = null;
                        if(roleActionInfo.actionType == MyRoleAcitonAdapter.DOWNLOAD_ACTIONS){
                            if(roleActionInfo.actionFileName != null){
                                filePath = FileTools.actions_download_cache + File.separator+ roleActionInfo.actionId + File.separator+roleActionInfo.actionFileName;
                                File file = new File(filePath);
                                UbtLog.d(TAG,"play downloadFile=>"+file.getAbsolutePath()+"  isExist="+file.isFile());
                                if(file.isFile()){
                                    sendCustomFiles(filePath,roleActionInfo.actionFileName);
                                }else{
                                    mHandler.sendEmptyMessage(MSG_ON_PLAY_ACTION_FILE_NOT_EXIST);
                                }
                            }
                        }else{// MyRoleAcitonAdapter.CREATE_ACTIONS
                            filePath = roleActionInfo.actionPath;
                            File file = new File(filePath);
                            UbtLog.d(TAG,"play createFile="+filePath+"  isExist="+file.isFile());
                            if(file.isFile()){
                                sendCustomFiles(filePath,roleActionInfo.actionFileName);
                            }else {
                                mHandler.sendEmptyMessage(MSG_ON_PLAY_ACTION_FILE_NOT_EXIST);
                            }
                        }
                    }
                }
            });
        }
    }

    public List<String> getActionsNamesList(){
        return mActionsNames;
    }

    public void addActionName(String actionName){
        mActionsNames.add(actionName);
    }

    public RemoteHelper(BaseActivity _baseActivity){
        super(_baseActivity);
        if (mActionsNames == null){
            mActionsNames = new ArrayList<String>();
        }
        if (!mBaseActivity.checkCoon()) {
            return;
        }
        if (mPlayer == null){
            mPlayer = ActionPlayer.getInstance(
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager(),
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getCurrentBluetooth().getAddress());
        }
    }

    public RemoteHelper(IRemoteUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
        if (mActionsNames == null){
            mActionsNames = new ArrayList<String>();
        }
        if (!mBaseActivity.checkCoon()) {
            return;
        }
        if (mPlayer == null){
            mPlayer = ActionPlayer.getInstance(
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager(),
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getCurrentBluetooth().getAddress());
        }
        mPlayer.addListener(mUI);
    }

    public void doRemoterState(byte state){
        byte[] params = new byte[2];
        params[0] = state;
        params[1] = 0;
        UbtLog.d(TAG, "doChangeEditState params:" + ByteHexHelper.bytesToHexString(params));
        doSendComm(ConstValue.DV_INTO_EDIT, params);
    }

    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
        if (mPlayer != null)
            mPlayer.addListener(mUI);
    }

    @Override
    public void UnRegisterHelper() {
        super.UnRegisterHelper();
        if (mPlayer != null)
            mPlayer.removeListener(mUI);
    }

    public void doOutRemote() {
        byte[] param = new byte[1];
        param[0] = 0;
        doSendComm(ConstValue.DV_SET_ACTION_DEFAULT, param);

    }

    public void doIntoRemote() {
        byte[] param = new byte[1];
        param[0] = 1;
        doSendComm(ConstValue.DV_SET_ACTION_DEFAULT, param);
    }


    public void doSaveRemoteEdit(final RemoteInfo info,final String roleId) {
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                List<RemoteItem> items = new ArrayList<RemoteItem>();
                for (int i = 1; i < 13; i++) {
                    RemoteItem item = RemoteRecordOperater.getItemByIndex(i, info);
                    if (!RemoteRecordOperater.getItemByIndex(i, mCurrentInfo).hts_name.equalsIgnoreCase(item.hts_name)) {
                        items.add(item);
                    }
                }
                if (items.size() == 0) {
                    Message msg = new Message();
                    msg.obj = true;
                    msg.what = MSG_ON_UPDATE_REMOTE_FINISH;
                    mHandler.sendMessage(msg);
                } else {
                    RemoteRecordOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).UpdateRemoteInfo(mCurrentType, info, items,roleId);
                    if(mCurrentType != RemoteRecordOperater.ModelType.CUSTOM){
                        sendFiles(mCurrentType);
                    }else{
                        mCurrentInfo = RemoteRecordOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name)
                                .getRemoteInfoByModel(RemoteHelper.mCurrentType, false,Integer.parseInt(roleId)+"");

                        Message msg = new Message();
                        msg.what = MSG_ON_SEND_FILE_FINIAH;
                        msg.obj = true;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });

    }

    /**
     * 读取本地数据库中的动作
     * */
    public void doReadActionsSettingItem(final String roleId) {
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean isCN = mBaseActivity.getAppCurrentLanguage().equalsIgnoreCase("CN") || mBaseActivity.getAppCurrentLanguage().equalsIgnoreCase("zh");
                List<RemoteItem> result = RemoteRecordOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).getAllActions(mCurrentType, isCN,roleId);
                Message msg = new Message();
                msg.obj = result;
                msg.what = MSG_ON_READ_SETTING_ITEMS;
                mHandler.sendMessage(msg);
            }
        });
    }

    public void sendFiles(final RemoteRecordOperater.ModelType type) {
        mCurrentType = type;
        mCurrentInfo = null;
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean isCN = mBaseActivity.getAppCurrentLanguage().equalsIgnoreCase("CN") || mBaseActivity.getAppCurrentLanguage().equalsIgnoreCase("zh");
                mCurrentInfo = RemoteRecordOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).getRemoteInfoByModel(type, isCN,null);
                List<String> unSyncFileNames = new ArrayList<String>();
                for (int i = 0; i < 13; i++) {
                    RemoteItem item = null;
                    if(i == 0){
                        item = new RemoteItem();
                        item.hts_name = "Default foot1.hts";
                    }else {
                        item = RemoteRecordOperater.getItemByIndex(i, mCurrentInfo);
                    }

                    if (item.hts_name.equals("")) {
                        continue;
                    }
                    boolean isSend = false;
                    //1E 不需发送文件
                    /*for (int j = 0; j < mActionsNames.size(); j++) {
                        String file_name = item.hts_name.substring(0, item.hts_name.lastIndexOf("."));
                        if (file_name.equalsIgnoreCase(mActionsNames.get(j))) {
                            isSend = true;
                            break;
                        }
                    }*/
                    if (!isSend) {
                        unSyncFileNames.add(item.hts_name);
                    }
                }
                UbtLog.d(TAG,"lihai-------unSyncFileNames.size():"+unSyncFileNames.size() + "  unSyncFileNames:" + unSyncFileNames.toString());
                sendFiles(unSyncFileNames);
            }
        });
    }

    public void doStopPrePlay(){
        MyActionsHelper.doStopMp3ForMyDownload();
        if(mPlayer != null){
            mPlayer.doStopPlay();
        }
    }

    public void sendFiles(List<String> file_names) {
        //log-------------------------------------------------start
        String names = "";
        for (int i = 0; i < file_names.size(); i++) {
            names += file_names.get(i) + ";";
        }
        MyLog.writeLog("发送文件测试", "文件名：" + names);
        //log-------------------------------------------------end
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).start().doSendFile(file_names);
    }

    public void sendWalkFiles(List<String> file_names) {
        //log-------------------------------------------------start
        String names = "";
        for (int i = 0; i < file_names.size(); i++) {
            names += file_names.get(i) + ";";
        }
        MyLog.writeLog("发送文件测试", "文件名：" + names);
        //log-------------------------------------------------end
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).start().doSendWalkFile(file_names);
    }

    public void sendCourseFiles(String fileDir,List<String> file_names) {
        //log-------------------------------------------------start
        String names = "";
        for (int i = 0; i < file_names.size(); i++) {
            names += file_names.get(i) + ";";
        }
        MyLog.writeLog("发送文件测试", "文件名：" + names);
        //log-------------------------------------------------end
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).start().doSendCourseFile(fileDir,file_names);
    }

    private void sendCustomFiles(String filePath,String fileName) {

        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).start().doSendCostomFile(filePath,fileName);
    }

    public int getResId(String name) {
        if (name.equals(""))
            return -1;
        if (name.contains(".")) {
            name = name.split("\\.")[0];
        }
        return mBaseActivity.getResources().getIdentifier(name, "drawable", FileTools.package_name);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        //逐个动作表文件名
        if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {
                String names = BluetoothParamUtil.bytesToString(param);
                UbtLog.d(TAG,"names = " + names);
                mActionsNames.add(names);
        }
        //动作文件读取完毕
        else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
            if(mPlayer != null){
                mPlayer.setRobotActions(mActionsNames);
            }
            changeActionsLength();
            Message msg = new Message();
            msg.what = MSG_DO_READ_ACTIONS;
            mHandler.sendMessage(msg);

        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {
            //TF卡状态
            if (param[0] == 4) {
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
        }else if(cmd == ConstValue.DV_INTO_EDIT){
            if(param != null){
                UbtLog.d(TAG, "DV_INTO_EDIT:" + ByteHexHelper.bytesToHexString(param)  + "   = " + mUI);
            }
        }
    }

    @Override
    public void onSendFileFinish(boolean isSuccess, SEND_FAIL_TYPE type) {
        Message msg = new Message();
        msg.what = MSG_ON_SEND_FILE_FINIAH;
        msg.obj = isSuccess;
        mHandler.sendMessage(msg);
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).release();
    }


    /**
     * 读取机器人内动作
     * */
    public void doReadActions() {
        mActionsNames.clear();
        //doSendComm(ConstValue.DV_GETACTIONFILE, null);
        try{
            doSendComm(ConstValue.DV_GETACTIONFILE,FileTools.actions_gamepad_robot_path.getBytes("GBK"));
        } catch (Exception e) {

        }
    }

    public void doReadWalkAction() {
        mActionsNames.clear();
        try{
            MyActionsHelper.getDataType = MyActionsHelper.Action_type.MY_WALK;
            doSendComm(ConstValue.DV_GETACTIONFILE,FileTools.actions_walk_robot_path.getBytes("GBK"));
        } catch (Exception e) {

        }
    }

    public void doReadAllRemoteRole(){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                List<RemoteRoleInfo> roleInfoList = RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).getAllRemoteRole();
                Message msg = new Message();
                msg.what = MSG_ON_READ_REMOTE_ROLE_FINISH;
                msg.obj = roleInfoList;
                mHandler.sendMessage(msg);
            }
        });
    }

    public void dd(){
        byte[] params = new byte[1];
        params[0] = (byte)1;
        doSendComm(ConstValue.SET_PALYING_CHARGING, params);
    }

    public void doDelRemoteRole(final RemoteRoleInfo roleInfo){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {

                boolean isSuccess = RemoteRoleActionOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).deleteRemoteRoleAction(roleInfo.roleid);
                if(isSuccess){
                    isSuccess = RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).deleteRemoteRole(roleInfo);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_SUCCESS,isSuccess);

                Message msg = new Message();
                msg.what = MSG_ON_DELETE_REMOTE_ROLE;
                msg.obj = roleInfo;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }

    public void doUpdateRemoteRole(final RemoteRoleInfo roleInfo){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).updateRemoteRole(roleInfo);
            }
        });
    }

    public void doAddRemoteRole(final RemoteRoleInfo roleInfo){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                long rowid = RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).addRemoteRole(roleInfo);
                boolean isSuccess = true;
                if(rowid == -1){
                    isSuccess = false;
                }

                Message msg = new Message();
                msg.what = MSG_ON_ADD_REMOTE_ROLE;
                msg.obj = isSuccess;
                msg.arg1 = (int)rowid;
                mHandler.sendMessage(msg);
            }
        });
    }

    public void doAddRemoteRoleActions(final List<RemoteRoleActionInfo> roleActionInfos){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = RemoteRoleActionOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).addRemoteRoleActions(roleActionInfos);

                initCustomData(roleActionInfos);

                Message msg = new Message();
                msg.what = MSG_ON_ADD_REMOTE_ROLE_ACTION;
                msg.obj = isSuccess;
                msg.arg1 = roleActionInfos.get(0).roleid;
                mHandler.sendMessage(msg);
            }
        });
    }

    public List<RemoteRoleActionInfo> doReadRemoteRoleByRoleid(int roleid){
        List<RemoteRoleActionInfo> roleActionInfos = RemoteRoleActionOperater.getInstance(mBaseActivity.getApplicationContext(),
                FileTools.db_log_cache, FileTools.db_log_name).getAllRemoteRoleByRoleid(roleid);
        return roleActionInfos;
    }

    public void doAddRemoteRoleAndAction(final RemoteRoleInfo roleInfo,final List<RemoteRoleActionInfo> roleActionInfos){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                long roleid = RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).addRemoteRole(roleInfo);
                if(roleid != -1){
                    for(int i=0;i<roleActionInfos.size();i++){
                        roleActionInfos.get(i).roleid = (int)roleid;
                    }
                    doAddRemoteRoleActions(roleActionInfos);
                }

            }
        });
    }

    public void doUpdateRemoteRoleAndAction(final RemoteRoleInfo roleInfo,final List<RemoteRoleActionInfo> roleActionInfos){
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                RemoteRoleOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).updateRemoteRole(roleInfo);

                doAddRemoteRoleActions(roleActionInfos);
            }
        });
    }

    public boolean initCustomData(List<RemoteRoleActionInfo> roleInfos){
        boolean flag = RemoteRoleActionOperater.getInstance(mBaseActivity.getApplicationContext(), FileTools.db_log_cache, FileTools.db_log_name).initCustomData(roleInfos);
        return flag;
    }

    public void changeActionsLength(){
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.REMOTE_ACTIONS_LENGTH,
                mActionsNames.size()+"", null,
                -1);
    }

    public int getRobotActionsLength(){
        String lengthString = BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.REMOTE_ACTIONS_LENGTH);

        if(isStringNumber(lengthString)){
            return Integer.parseInt(lengthString);
        }else {
            return -1;
        }
    }

    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public void colseRemoteHeadPormgt(){
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.REMOTE_HEAD_PROMPT,
                BasicSharedPreferencesOperator.IS_REMOTE_HEAD_PROMPT, this,
                do_colse_head_prompt);
    }

    public boolean isShowRemoteHeadPormgt(){
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.REMOTE_HEAD_PROMPT)
                .equals(BasicSharedPreferencesOperator.IS_REMOTE_HEAD_PROMPT)) {

            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        //由于remoteHelper onDeviceDisConnected 先调用，所以如果正在传输文件的话，先停定for循环传输，不然，清空蓝牙后，for会包蓝牙为null异常
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).onDeviceDisConnected(mac);
        super.onDeviceDisConnected(mac);
    }

    @Override
    public void DistoryHelper() {

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }


    @Override
    public void onStartSend() {
        mHandler.sendEmptyMessage(MSG_ON_SEND_FILE_START);
    }

    @Override
    public void onSendProgressReport(final int value) {

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

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {
        if (do_colse_head_prompt == request_code) {
            Message msg = new Message();
            msg.obj = isSuccess;
            msg.what = MSG_ON_DEL_HEAD_PROMPT;
            mHandler.sendMessage(msg);
        }
    }
}

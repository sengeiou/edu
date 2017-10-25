package com.ubt.alpha1e.business;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.DB.ActionsRecordOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.ActionEvent;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.ui.RobotNetConnectActivity;
import com.ubt.alpha1e.ui.custom.DownloadSuccessDubView;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsDownLoadManager implements FileDownloadListener {

    private static final String TAG = "ActionsDownLoadManager";
    private static ActionsDownLoadManager thiz;
    private static Date lastTime = null;
    private Context mContext;
    private Handler mHandler;
    // 所有正在下载的任务
    private List<ActionInfo> mLocalDownList;

    // 所有正在下载的任务
    private List<ActionInfo> mRobotDownList;

    private Map<Long,State> mDownState;

    // 所有监听者
    private List<ActionsDownLoadManagerListener> mDownListenerLists;
    // 下载请求码与下载任务对应关系
    private Map<Long, ActionInfo> mDownRequestCodeMap;


    private ActionsDownLoadManager() {
    }

    private PublicInterface.BlueToothInteracter mBluetoothListener = new PublicInterface.BlueToothInteracter() {

        @Override
        public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

            if (cmd == ConstValue.DV_DO_DOWNLOAD_ACTION) {
                String downloadProgressJson= BluetoothParamUtil.bytesToString(param);

                DownloadProgressInfo downloadProgressInfo = GsonImpl.get().toObject(downloadProgressJson,DownloadProgressInfo.class);
                ActionInfo actionInfo = getRobotDownloadActionById(downloadProgressInfo.actionId);

                UbtLog.d(TAG,"downloadProgressJson : " + downloadProgressJson );
                if(actionInfo == null){
                    UbtLog.d(TAG,"actionInfo : null "   );
                    return;
                }

                if(downloadProgressInfo.status == 1){
                    //下载中
                    UbtLog.d(TAG,"机器人下载进度, actionName : " + actionInfo.actionName + " "+  downloadProgressInfo.progress );
                }else {
                    //2 下载成功 3 未联网 0 下载失败
                    //UbtLog.d(TAG,"actionInfo : " + actionInfo );
                    State state ;
                    if(downloadProgressInfo.status == 3){
                        state = State.connect_fail;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        mContext,
                                        mContext.getString(R.string.ui_network_robot_un_con_net),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else if(downloadProgressInfo.status == 2){
                        state = State.success;
                        UbtLog.d(TAG,"机器人下载成功：hts_file_name = " + actionInfo.hts_file_name);
                        //机器人下载成功，加入缓存
                        if(!MyActionsHelper.mCacheActionsNames.isEmpty() && !TextUtils.isEmpty(actionInfo.hts_file_name)){
                            String sendFileName = actionInfo.hts_file_name.split("\\.")[0];
                            //UbtLog.d(TAG,"机器人下载成功：hts_file_name = " + sendFileName);
                            MyActionsHelper.mCacheActionsNames.add((MyActionsHelper.localSize + MyActionsHelper.myDownloadSize),sendFileName);
                            MyActionsHelper.myDownloadSize++;
                        }
                    }else {
                        state = State.fail;
                    }

                    UbtLog.d(TAG,"机器人下载结束, actionName : " + actionInfo.actionName + " state : "+  state + "  " + mDownState.containsKey(actionInfo.actionId));
                    if (mDownListenerLists != null && mDownState.containsKey(actionInfo.actionId)) {
                        for (int i = 0; i < mDownListenerLists.size(); i++) {
                            mDownListenerLists.get(i).onDownLoadFileFinish(actionInfo, mDownState.get(actionInfo.actionId));
                        }

                        ActionEvent actionEvent = new ActionEvent(ActionEvent.Event.ROBOT_ACTION_DOWNLOAD);
                        actionEvent.setActionInfo(actionInfo);
                        actionEvent.setDownloadProgressInfo(downloadProgressInfo);
                        EventBus.getDefault().post(actionEvent);
                    }

                    mRobotDownList.remove(actionInfo);
                }

                /*ActionEvent actionEvent = new ActionEvent(ActionEvent.Event.ROBOT_ACTION_DOWNLOAD);
                actionEvent.setActionInfo(actionInfo);
                actionEvent.setDownloadProgressInfo(downloadProgressInfo);
                EventBus.getDefault().post(actionEvent);*/
            }else if(cmd == ConstValue.DV_READ_NETWORK_STATUS){
                /*String networkInfoJson = null;
                try {
                    networkInfoJson = new String(param,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);

                if(networkInfo.status){
                    //机器人未联网
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.ui_network_robot_un_con_net),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {

                }*/
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
            mDownState.clear();
            mRobotDownList.clear();

        }
    };

    public static ActionsDownLoadManager getInstance(Context _context) {
        if (thiz == null) {
            thiz = new ActionsDownLoadManager();
            thiz.mLocalDownList = new ArrayList<ActionInfo>();
            thiz.mRobotDownList = new ArrayList<ActionInfo>();
            thiz.mDownListenerLists = new ArrayList<ActionsDownLoadManagerListener>();
            thiz.mDownRequestCodeMap = new HashMap<Long, ActionInfo>();
            thiz.mDownState = new HashMap<>();

        }
        thiz.mContext = _context.getApplicationContext();
        thiz.mHandler = new Handler();
        return thiz;
    }

    public ActionInfo getActionById(long id) {
        ActionInfo info = null;
        for (int i = 0; i < thiz.mLocalDownList.size(); i++) {
            if (thiz.mLocalDownList.get(i).actionId == id)
                info = thiz.mLocalDownList.get(i);
        }
        return info;
    }

    public boolean isDownloading(long action_id) {
        boolean result = false;
        for (int i = 0; i < mLocalDownList.size(); i++) {
            if (mLocalDownList.get(i).actionId == action_id)
                result = true;
        }
        return result;
    }

    public ActionInfo getRobotDownloadActionById(long id) {
        ActionInfo info = null;
        for (int i = 0; i < thiz.mRobotDownList.size(); i++) {
            if (thiz.mRobotDownList.get(i).actionId == id)
                info = thiz.mRobotDownList.get(i);
        }
        return info;
    }

    public boolean isRobotDownloading(long action_id) {
        boolean result = false;
        for (int i = 0; i < mRobotDownList.size(); i++) {
            if (mRobotDownList.get(i).actionId == action_id)
                result = true;
        }
        return result;
    }

    private ActionInfo getActionByReqCode(long req_code) {
        return mDownRequestCodeMap.get(req_code);
    }

    // 添加监听者
    public void addListener(ActionsDownLoadManagerListener listener) {
        if (!mDownListenerLists.contains(listener))
            mDownListenerLists.add(listener);
    }

    // 移除监听者
    public void removeListener(ActionsDownLoadManagerListener listener) {
        if (mDownListenerLists.contains(listener))
            mDownListenerLists.remove(listener);
    }

    /**
     * 注册蓝牙监听
     */
    public void addBluetoothListener(){
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().addBlueToothInteraction(mBluetoothListener);
    }

    // 启动下载任务
    public void DownLoadAction(ActionInfo info) {

        if (isDownloading(info.actionId)){
            return;
        }

        ActionInfo c_info = getActionById(info.actionId);
        if (c_info == null) {
            c_info = info;
            //倒序
            thiz.mLocalDownList.add(0,c_info);
        }

        UserInfo userInfo = ((AlphaApplication) mContext).getCurrentUserInfo();
        long req_code = new Date().getTime();
        UbtLog.d("Actiondownload", "req_code==" + req_code + "c_info=" + info.actionId);
        mDownRequestCodeMap.put(req_code, info);

        GetDataFromWeb.getFileFromHttp(req_code, info.actionPath,
                FileTools.actions_download_cache + "/" + info.actionId + ".zip", this);

        //统计下载次数
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("actionId", info.actionId);
            jobj.put("loginUserId",userInfo.userId);
            GetDataFromWeb.getJsonByPost(-1,
                    HttpAddress.getRequestUrl(Request_type.add_download),
                    HttpAddress.getParamsForPost(jobj.toString(), mContext),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void doDownloadOnRobot(ActionInfo actionInfo, boolean hasDownLocal){

        if(!BaseHelper.hasConnectNetwork){
            //机器人未联网，先去联网
            for (int i = 0; i < mDownListenerLists.size(); i++) {
                mDownListenerLists.get(i).onDownLoadFileFinish(actionInfo, State.fail);
            }
            showConnectNetworkDialog();
            return;
        }

        addBluetoothListener();
        if(hasDownLocal){
            mDownState.put(actionInfo.actionId,State.success);
        }else {
            DownLoadAction(actionInfo);
        }

        if(isRobotDownloading(actionInfo.actionId)){
            return;
        }

        if(hasDownLocal){
            //我的下载进来，弹出下载弹出框
            /*Message msg = new Message();
            msg.what = MESSAGE_START_DOW_LOAD_BIN_FILE;
            mHandler.sendMessage(msg);*/
            ActionEvent actionEvent = new ActionEvent(ActionEvent.Event.ROBOT_ACTION_DOWNLOAD_START);
            actionEvent.setActionInfo(actionInfo);
            EventBus.getDefault().post(actionEvent);
        }

        ActionInfo c_info = getRobotDownloadActionById(actionInfo.actionId);
        if (c_info == null) {
            c_info = actionInfo;
            //倒序
            thiz.mRobotDownList.add(0,c_info);
        }

        String params = BluetoothParamUtil.paramsToJsonString(new String[]{ actionInfo.actionId + "",
                actionInfo.actionName,actionInfo.actionPath }, ConstValue.DV_DO_DOWNLOAD_ACTION);

        /*String params = BluetoothParamUtil.paramsToJsonString(new String[]{ actionInfo.actionId + "",
                actionInfo.actionName,"https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip" }, ConstValue.DV_DO_DOWNLOAD_ACTION);*/

        UbtLog.d(TAG,"params =========== " + params);

        doSendComm(ConstValue.DV_DO_DOWNLOAD_ACTION, BluetoothParamUtil.stringToBytes(params));
    }

    private void showConnectNetworkDialog(){
        new AlertDialog(AlphaApplication.getBaseActivity())
                .builder()
                .setTitle(AlphaApplication.getBaseActivity().getStringResources("ui_network_prompt"))
                .setMsg(AlphaApplication.getBaseActivity().getStringResources("ui_netwok_robot_need_network"))
                .setCancelable(false)
                .setPositiveButton(AlphaApplication.getBaseActivity().getStringResources("ui_network_go_connect_net"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        RobotNetConnectActivity.launchActivity(AlphaApplication.getBaseActivity(),
                                AlphaApplication.getBaseActivity().getRequestedOrientation(),false);
                    }
                }).setNegativeButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_cancel"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    private void doSendComm(byte cmd, byte[] param) {

        ((AlphaApplication) thiz.mContext.getApplicationContext())
                .getBlueToothManager()
                .sendCommand(((AlphaApplication) thiz.mContext
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress(), cmd,
                        param, param == null ? 0 : param.length,
                        false);
    }

    public static void resetData(){
        if(thiz != null){
            thiz.mDownState.clear();
            thiz.mRobotDownList.clear();
        }
    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {
        if (mDownListenerLists != null) {
            for (int i = 0; i < mDownListenerLists.size(); i++) {
                mDownListenerLists.get(i).onGetFileLenth(
                        getActionByReqCode(request_code), file_lenth);
            }
        }
    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {
        if (mDownListenerLists != null) {
            for (int i = 0; i < mDownListenerLists.size(); i++) {
                mDownListenerLists.get(i).onStopDownloadFile(
                        getActionByReqCode(request_code), state);
            }
        }
    }

    @Override
    public void onReportProgress(long request_code, double progess) {

        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 200;
        if(progess>=100)
        {

            if (mDownListenerLists != null ) {
                for (int i = 0; i < mDownListenerLists.size(); i++) {
                    UbtLog.d(TAG, "onReportProgress progess=" + progess);
                    mDownListenerLists.get(i).onReportProgress(
                            getActionByReqCode(request_code), progess);
                }
            }
        }
        if (lastTime != null) {
            time_difference = curDate.getTime() - lastTime.getTime();
        }

        if (mDownListenerLists != null && time_difference >= 50) {
            for (int i = 0; i < mDownListenerLists.size(); i++) {
                UbtLog.d(TAG, "onReportProgress progess=" + progess);
                mDownListenerLists.get(i).onReportProgress(
                        getActionByReqCode(request_code), progess);
            }
            lastTime = curDate;
        }
    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

        ActionInfo info = getActionByReqCode(request_code);
        UbtLog.d(TAG, "req_code=" + request_code + "本地下载结束 info id=" + info.actionId);
        mLocalDownList.remove(info);

        if(((AlphaApplication)mContext.getApplicationContext()).isAlpha1E()){
            mDownState.put(info.actionId,state);
            ActionInfo actionInfo = getRobotDownloadActionById(info.actionId);
            if(actionInfo == null){
                //等于null，说明机器人已经下载结束，无论是成功或失败
                if (mDownListenerLists != null) {
                    for (int i = 0; i < mDownListenerLists.size(); i++) {
                        mDownListenerLists.get(i).onDownLoadFileFinish(info, state);
                    }
                }
            }
        }else {
            if (mDownListenerLists != null) {
                for (int i = 0; i < mDownListenerLists.size(); i++) {
                    mDownListenerLists.get(i).onDownLoadFileFinish(info, state);
                }
            }
        }

        if (!state.equals(State.success)) {
            UbtLog.d(TAG,  "state=" + state);
            ActionInfo infos = getActionByReqCode(request_code);
            final String downloadName = "\""+infos.actionName + "\" ";
            mLocalDownList.remove(infos);

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(
                            mContext,
                            downloadName + mContext.getResources().getString(
                                    R.string.ui_action_download_fail), Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }else{
            boolean isShowDownloadSuccessDub = DownloadSuccessDubView.isShowDownloadSuccessDubView(AlphaApplication.getBaseActivity());
            UbtLog.d(TAG,"info.actionName:"+info.actionName + "   isShowDownloadSuccessDub:"+isShowDownloadSuccessDub);

            if(!isShowDownloadSuccessDub){
                final String downloadName = ""+info.actionName + " ";
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        new DownloadSuccessDubView(AlphaApplication.getBaseActivity(),downloadName);
                    }
                });
            }
        }



        // 修改新下载状态
        BasicSharedPreferencesOperator.getInstance(mContext,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_NEW_DOWNLOAD_ACTION,
                BasicSharedPreferencesOperator.IS_NEW_DOWNLOAD_ACTION_TRUE,
                null, -1);
        // 尝试解压
        File zip_release_path = new File(FileTools.actions_download_cache + "/"
                + info.actionId);
        ZipTools.unZip(
                FileTools.actions_download_cache + "/" + info.actionId + ".zip",
                zip_release_path.getPath());
        UbtLog.d(TAG, "unZip=" + ZipTools.unZip(
                FileTools.actions_download_cache + "/" + info.actionId + ".zip",
                zip_release_path.getPath()) +"---path=" + zip_release_path.getPath());
        String[] action_files = zip_release_path.list(new FilenameFilter() {
            public boolean accept(File f, String name) {
                return name.endsWith(".hts");
            }
        });

        UbtLog.d(TAG, "action_files length=" + action_files.length);

        // 添加到下载记录
        final ActionRecordInfo record = new ActionRecordInfo(info,
                state == State.success ? true : false);
        if (action_files == null || action_files.length < 1) {
        } else {
            record.action.hts_file_name = action_files[0];
        }

        UbtLog.d(TAG, "record.action.hts_file_name" + record.action.hts_file_name);
        //写入数据库
        ActionsRecordOperater.getInstance(mContext, FileTools.db_log_cache, "UbtLogs").addRecord(record);
    }


    // 获取正在下载的文件列表
    public List<ActionInfo> getDownList() {
        return mLocalDownList;
    }

    // 获取正在下载的文件列表
    public List<ActionInfo> getRobotDownList() {
        return mRobotDownList;
    }

    // 获取下载历史
    public void getDownHistoryList() {


        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {

                List<ActionRecordInfo> infos = ActionsRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name).getAllRecoed();

                infos = FileTools.getDownloadHtsFile(infos);

                for (int i = 0; i < mDownListenerLists.size(); i++) {
                    mDownListenerLists.get(i).onReadHistoryFinish(infos);
                }
            }
        });


    }

    public void doUpdateAction(final ActionInfo info) {

        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                ActionsRecordOperater.getInstance(mContext, FileTools.db_log_cache, "UbtLogs").updateRecord(info);
            }
        });

    }


    public void doDelAction(final ActionInfo info) {


        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                ActionsRecordOperater.getInstance(mContext, FileTools.db_log_cache, "UbtLogs").deleteRecord(info.actionId);
                FileTools.DeleteFile(new File( FileTools.actions_download_cache + "/" + info.actionId + ".zip"));
                FileTools.DeleteFile(new File( FileTools.actions_download_cache + "/" + info.actionId));
                for (int i = 0; i < mDownListenerLists.size(); i++) {
                    mDownListenerLists.get(i).onChangeFinish(info);
                }
            }
        });


    }


    public boolean isTooMore() {
        if (mLocalDownList.size() > 2)
            return true;
        else
            return false;
    }


}

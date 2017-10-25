package com.ubt.alpha1e.update;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.data.model.UpgradeProgressInfo;
import com.ubt.alpha1e.data.model.VersionInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.RobotConnectedActivity;
import com.ubt.alpha1e.ui.RobotInfoActivity;
import com.ubt.alpha1e.ui.RobotNetConnectActivity;
import com.ubt.alpha1e.ui.dialog.DownloadProgressDialog;
import com.ubt.alpha1e.ui.dialog.UpdateAlertDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.ScanHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class RobotSoftUpdateManager {

    private static final String TAG = "RobotSoftUpdateManager";

    private static final int MSG_DO_GET_TOKEN_SUCCESS = 1001; //获取token成功
    private static final int MSG_DO_REQUEST_FAIL = 1002;   //检查最新版本失败
    private static final int MSG_DO_HAS_NEW_VERSION = 1003; //有新版本
    private static final int MSG_DO_NO_NEW_VERSION = 1004; //无新版本
    private static final int MSG_DO_UPGRADE_SOFT = 1005; //更新软件
    private static final int MSG_DO_GO_NEXT = 1006;
    private static final int MSG_DO_UPDATE_PROGRESS = 1007;//更新进度
    private static final int MSG_DO_SHOW_UPGRADE_DIALOG = 1008;//更新进度
    private static final int MSG_DO_CONNECT_NETWORK_UPGRADE = 1009;// 去联网
    private static final int MSG_DO_UPGRADE_STATUS = 1010;// 是否进去升级

    private final int do_get_access_token = 11003; //获取token
    private final int do_check_soft_version_request = 11004; //检查1E 机器人OTA 软件

    private static RobotSoftUpdateManager thiz;

    //private BaseActivity mBaseActivity;
    private Context mContext;
    private DownloadProgressDialog mPropressDialog;

    private VersionInfo mNewVersionInfo = null;
    private boolean isDownloading = false; //是否下载中
    private boolean isUpgradeing = false;
    private int mCurrentProgress = 0;      //当前下载进度


    //传过来的Handler
    private Handler mHandler = null;

    //本地定义的Handler
    private Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case MSG_DO_GET_TOKEN_SUCCESS:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        String accessToken = (String) msg.obj;
                        String robotSoftVersion = ((AlphaApplication) mContext.getApplicationContext()).getRobotSoftVersion();
                        UbtLog.d(TAG,"robotSoftVersion == " + robotSoftVersion);
                        robotSoftVersion = robotSoftVersion.replace("alpha1e_V","");
                        UbtLog.d(TAG,"robotSoftVersion =>> " + robotSoftVersion);
                        //robotSoftVersion = "0.1.5";
                        //1-v0.1.2 版本格式
                        doCheckRobotSoftVersionForUpdate(accessToken,"1-v" + robotSoftVersion ,"CNE19123ECC7A26A341B2E51ACC08ECN","Alpha1E");
                    }
                    break;
                case MSG_DO_REQUEST_FAIL:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        // 取消注册蓝牙监听
                        ((AlphaApplication) mContext.getApplicationContext())
                                .getBlueToothManager().removeBlueToothInteraction(mSendListener);

                        if(mContext instanceof RobotConnectedActivity ||
                                mContext instanceof ScanBluetoothActivity ||
                                mContext instanceof AutoScanConnectService){
                            // 跳转主页
                            mHandler.sendEmptyMessage(ScanHelper.MSG_DO_COON_BT_SUCCESS);
                        }else {
                            //RobotInfoActivity
                            mHandler.sendEmptyMessage(RobotInfoActivity.MSG_DO_REQUEST_FAIL);
                        }
                    }
                    break;
                case MSG_DO_NO_NEW_VERSION:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        // 取消注册蓝牙监听
                        ((AlphaApplication) mContext.getApplicationContext())
                                .getBlueToothManager().removeBlueToothInteraction(mSendListener);

                        if(mContext instanceof RobotConnectedActivity ||
                                mContext instanceof ScanBluetoothActivity ||
                                mContext instanceof AutoScanConnectService){
                            // 跳转主页
                            mHandler.sendEmptyMessage(ScanHelper.MSG_DO_COON_BT_SUCCESS);
                        }else {
                            //RobotInfoActivity
                            mHandler.sendEmptyMessage(RobotInfoActivity.MSG_DO_NO_NEW_VERSION);
                        }
                    }
                    break;
                case MSG_DO_HAS_NEW_VERSION:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
                    }
                    break;
                case MSG_DO_SHOW_UPGRADE_DIALOG:
                    if(mContext instanceof RobotConnectedActivity ||
                            mContext instanceof ScanBluetoothActivity ||
                            mContext instanceof AutoScanConnectService){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else{
                        //RobotInfoActivity
                        mHandler.sendEmptyMessage(RobotInfoActivity.MSG_DO_HAS_NEW_VERSION);
                    }

                    if(thiz.isDownloading){
                        //已经在下载中，直接显示
                    }else {
                        showUpgradeDialog(mNewVersionInfo);
                    }
                    break;
                case MSG_DO_UPGRADE_SOFT:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        doSendComm(ConstValue.DV_DO_UPGRADE_SOFT, null);
                    }
                    break;
                case MSG_DO_GO_NEXT :

                    // 取消注册蓝牙监听
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(mSendListener);

                    UbtLog.d(TAG,"-MSG_DO_GO_NEXT-----------" + mContext);
                    if(mContext instanceof RobotConnectedActivity ||
                            mContext instanceof ScanBluetoothActivity ||
                            mContext instanceof AutoScanConnectService){
                        // 跳转主页
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_COON_BT_SUCCESS);
                    }else {
                        //RobotInfoActivity
                        mHandler.sendEmptyMessage(RobotInfoActivity.MSG_DO_UPGRADE_PROGRESS_DISPLAY);
                    }
                    break;
                case MSG_DO_UPDATE_PROGRESS:
                    UbtLog.d(TAG,"mPropressDialog => " + mPropressDialog);
                    if(mPropressDialog == null){
                        isDownloading = true;
                        showDownloadProgress();
                    }

                    UpgradeProgressInfo upgradeProgressInfo = (UpgradeProgressInfo)msg.obj;
                    mCurrentProgress = Integer.parseInt(upgradeProgressInfo.progress);

                    if(upgradeProgressInfo.status == 0){
                        mPropressDialog.updateMsg(AlphaApplication.getBaseActivity().getStringResources("ui_download_failed_toast"));
                        isDownloading = false;
                    }else if(upgradeProgressInfo.status == 2){
                        mPropressDialog.updateMsg(AlphaApplication.getBaseActivity().getStringResources("ui_download_success"));
                        mPropressDialog.updateProgress(upgradeProgressInfo.progress);
                        isDownloading = false;
                    }else if(upgradeProgressInfo.status == 1){
                        mPropressDialog.showMsg(AlphaApplication.getBaseActivity().getStringResources("ui_robot_info_upgrade_downloading"));
                        mPropressDialog.updateProgress(upgradeProgressInfo.progress);

                        if(mCurrentProgress == 100){
                            mPropressDialog.updateMsg(AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_calibrate"));
                        }

                    }

                    break;
                case MSG_DO_CONNECT_NETWORK_UPGRADE:
                    if(thiz.isDownloading){
                        mHandler.sendEmptyMessage(ScanHelper.MSG_DO_UPDATE_SOFT);
                    }else {
                        // 取消注册蓝牙监听
                        ((AlphaApplication) mContext.getApplicationContext())
                                .getBlueToothManager().removeBlueToothInteraction(mSendListener);

                        UbtLog.d(TAG,"-MSG_DO_CONNECT_NETWORK_UPGRADE-----------" + mContext);
                        if(mContext instanceof RobotConnectedActivity ||
                                mContext instanceof ScanBluetoothActivity ||
                                mContext instanceof AutoScanConnectService){
                            mHandler.sendEmptyMessage(ScanHelper.MSG_DO_CONNECT_NETWORK_UPGRADE);
                        }else {
                            //RobotInfoActivity
                            mHandler.sendEmptyMessage(RobotInfoActivity.MSG_DO_UPGRADE_PROGRESS_DISPLAY);
                        }
                        RobotNetConnectActivity.launchActivity(AlphaApplication.getBaseActivity(),AlphaApplication.getBaseActivity().getRequestedOrientation(),true);
                    }
                    break;
                case MSG_DO_UPGRADE_STATUS:
                    int upgradeStatus = ((int)msg.obj) ;
                    UbtLog.d(TAG,"收到升级指令 : " + upgradeStatus);
                    if(upgradeStatus == 1){
                        isUpgradeing = true;

                        Toast.makeText(mContext,AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_start_update") + ","
                                + AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_will_disconnect_robot"),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(mContext,AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_robot_low_battery"),Toast.LENGTH_LONG).show();
                    }

                    if(mPropressDialog != null){
                        mPropressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }

        }

    };

    private BlueToothInteracter mSendListener = new BlueToothInteracter() {

        @Override
        public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

            if(cmd == ConstValue.DV_DO_UPGRADE_PROGRESS){
                String upgradeProgressJson= BluetoothParamUtil.bytesToString(param);

                UpgradeProgressInfo upgradeProgressInfo = GsonImpl.get().toObject(upgradeProgressJson,UpgradeProgressInfo.class);

                UbtLog.d(TAG, "upgradeProgressInfo : " + upgradeProgressInfo );
                Message msg = new Message();
                msg.what = MSG_DO_UPDATE_PROGRESS;
                msg.obj = upgradeProgressInfo;
                mLocalHandler.sendMessage(msg);

            }else if(cmd == ConstValue.DV_READ_NETWORK_STATUS){

                String networkInfoJson = BluetoothParamUtil.bytesToString(param);

                NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);
                if(networkInfo.status){
                    BaseHelper.hasConnectNetwork = true;
                }else {
                    BaseHelper.hasConnectNetwork = false;
                }

                UbtLog.d(TAG,"wifiName = " + networkInfo.name);
                mLocalHandler.sendEmptyMessage(MSG_DO_SHOW_UPGRADE_DIALOG);

            }else if(cmd == ConstValue.DV_DO_UPGRADE_STATUS){
                Message msg = new Message();
                msg.what = MSG_DO_UPGRADE_STATUS;
                msg.obj = (int) param[0];
                mLocalHandler.sendMessage(msg);
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

            if(mPropressDialog != null){
                mPropressDialog.dismiss();
            }
            reset();

            UbtLog.d(TAG,"onDeviceDisConnected...");
        }
    };


    private RobotSoftUpdateManager() {
    }

    public static RobotSoftUpdateManager getInstance(Context cont,Handler handler) {
        if (thiz == null){
            thiz = new RobotSoftUpdateManager();
        }
        thiz.mHandler = handler;
        //thiz.mBaseActivity = (BaseActivity) cont;
        thiz.mContext = cont;
        return thiz;
    }



    private void showUpgradeDialog(final VersionInfo versionInfo){

        String negative = AlphaApplication.getBaseActivity().getStringResources("ui_common_ignore");

        //是否强制升级
        final boolean isForceUpgrade = "1".equals(versionInfo.upgradeType) ? true : false;
        if(isForceUpgrade){//强制升级
            negative = AlphaApplication.getBaseActivity().getStringResources("ui_common_cancel");
        }

        String positive = AlphaApplication.getBaseActivity().getStringResources("ui_about_update_now");

        if(!BaseHelper.hasConnectNetwork){
            positive = AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_connect_network");
        }

        new UpdateAlertDialog(AlphaApplication.getBaseActivity())
                .setScreenOrientation(AlphaApplication.getBaseActivity().getRequestedOrientation())
                .builder()
                .setTitle(AlphaApplication.getBaseActivity().getStringResources("ui_robot_info_upgrade_title"))
                .setUpgradeDes(AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_app_version_low"))
                .setVersionMsg(AlphaApplication.getBaseActivity().getStringResources("ui_about_version") + versionInfo.toVersion )
                .setMsg(versionInfo.remark)
                .setCancelable(false)
                .setPositiveButton(positive, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(BaseHelper.hasConnectNetwork){
                            mLocalHandler.sendEmptyMessage(MSG_DO_UPGRADE_SOFT);
                        }else {
                            mLocalHandler.sendEmptyMessage(MSG_DO_CONNECT_NETWORK_UPGRADE);
                        }
                    }
                })
                .setNegativeButton(negative,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isForceUpgrade){
                            //强制升级的话，直接退出
                            ((AlphaApplication)mContext.getApplicationContext()).doExitApp(false);
                        }else {
                            mLocalHandler.sendEmptyMessage(MSG_DO_GO_NEXT);
                        }
                    }
                }).show();
    }

    /**
     * 检查机器人软件版本更新
     */
    public void doCheckUpdateSoft(){
        // 注册蓝牙监听
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().addBlueToothInteraction(mSendListener);

        if(thiz.isDownloading){
            //如果已经在下载，则直接显示进度
            showDownloadProgress();
        }else {
            doRequestTokenForUpdateSoft();
        }
    }

    /**
     * 1E 升级机器人本体软件，先请求公共数据平台访问token
     */
    public void doRequestTokenForUpdateSoft(){

        //此参数是固定的
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.get_access_token)
                + HttpAddress.getParamsForGet(new String[]{"mobile_1", "secret_1", "password", "aa", "aa"},
                HttpAddress.Request_type.get_access_token);
        requestDataFromWeb(url,do_get_access_token);

    }

    /**
     * 1E 升级机器人本体软件，检查软件版本信息是否有更新
     * @param accessToken 访问token
     * @param currentVersion 当前机器人版本
     * @param robotId 机器人ID
     * @param robotType 机器人类型 固定是 Alpha1E
     */
    private void doCheckRobotSoftVersionForUpdate(String accessToken,String currentVersion,String robotId,String robotType){

        //此参数是固定的
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.check_robot_soft_version_info)
                + HttpAddress.getParamsForGet(new String[]{accessToken, currentVersion, robotId, robotType},
                HttpAddress.Request_type.check_robot_soft_version_info);

        requestDataFromWeb(url,do_check_soft_version_request);
    }

    private void requestDataFromWeb(String url,int id){

        UbtLog.d(TAG,"requestDataFromWeb params : " + url + "   id = " + id);

        OkHttpClientUtils
                .getJsonByGetRequest(url,id)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d(TAG,"requestDataFromWeb exception : " + e.getMessage() + "   id = " + id );
                        switch (id){
                            case do_get_access_token:
                            case do_check_soft_version_request:
                                mLocalHandler.sendEmptyMessage(MSG_DO_REQUEST_FAIL);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        //UbtLog.d(TAG,"CheckRobotSoftUpdate response : " + response + "  id = " + id);
                        try {
                            if(id == do_get_access_token){
                                JSONObject jsonObject = new JSONObject(response);

                                Message msg = new Message();
                                msg.what = MSG_DO_GET_TOKEN_SUCCESS;
                                msg.obj = jsonObject.getString("access_token");
                                mLocalHandler.sendMessage(msg);

                            }else if(id == do_check_soft_version_request){
                                JSONObject jsonObject = new JSONObject(response);
                                String msg = jsonObject.getString("msg");
                                mNewVersionInfo = null;
                                boolean hasNewVersion = false;
                                UbtLog.d(TAG,"msg = " + msg + " response = " + response);
                                if("success".equals(msg)){
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    JSONArray versionArray = dataObject.getJSONArray("version");
                                    if(versionArray.length() > 0){
                                        mNewVersionInfo = GsonImpl.get().toObject(versionArray.getJSONObject(0).toString(),VersionInfo.class);
                                        hasNewVersion = true;
                                        UbtLog.d(TAG,"versionInfo = " + mNewVersionInfo);
                                    }
                                }

                                //hasNewVersion = false;
                                if(hasNewVersion){
                                    mLocalHandler.sendEmptyMessage(MSG_DO_HAS_NEW_VERSION);
                                }else {
                                    mLocalHandler.sendEmptyMessage(MSG_DO_NO_NEW_VERSION);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showDownloadProgress(){

        UbtLog.d(TAG,"-showDownloadProgress-");
        mPropressDialog = DownloadProgressDialog.getInstance(AlphaApplication.getBaseActivity());
        mPropressDialog.setCancelable(true);
        mPropressDialog.showMsg(AlphaApplication.getBaseActivity().getStringResources("ui_robot_info_upgrade_downloading"));
        mPropressDialog.setTitle(AlphaApplication.getBaseActivity().getStringResources("ui_upgrade_robot_system_upgrade"));
        mPropressDialog.updateProgress(mCurrentProgress + "");
        mPropressDialog.show();

        isUpgradeing = false;
        mPropressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(isUpgradeing){
                    isUpgradeing = false;
                    ((AlphaApplication) mContext.getApplicationContext())
                            .getBlueToothManager().removeBlueToothInteraction(mSendListener);
                }else {
                    mLocalHandler.sendEmptyMessage(MSG_DO_GO_NEXT);
                }
                mPropressDialog = null;
            }
        });
    }

    private void reset(){
        if(thiz != null){
            thiz.isUpgradeing = false;
            thiz.isDownloading = false;
        }
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


    public void setIsDownloading(boolean isDownloading){
        thiz.isDownloading = isDownloading;
    }

    public boolean getIsDownloading(){
        return thiz.isDownloading;
    }

    public int getCurrentProgress() {
        return thiz.mCurrentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        thiz.mCurrentProgress = currentProgress;
    }

}

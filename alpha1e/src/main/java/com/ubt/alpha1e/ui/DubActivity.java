package com.ubt.alpha1e.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.fragment.BaseFragment;
import com.ubt.alpha1e.ui.fragment.DubFragment;
import com.ubt.alpha1e.ui.fragment.DubPreviewFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.AudioRecorder2Mp3Util;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * 类名
 *
 * @author wmma
 * @description 录音功能实现。
 * @date 2016.11.16
 * @update 修改者，修改日期，修改内容。
 */


public class DubActivity extends BaseActivity implements IActionsUI, BaseDiaUI {

    private static final String TAG = "DubActivity";

    private LinearLayout llBack;
    private TextView tvTitle;
    private LinearLayout llNext;
    private TextView btnBack;
    private TextView tvNext;

    private int type = -1;
    AudioRecorder2Mp3Util util = null;
    private NewActionInfo oldNewActionInfo;
    private NewActionInfo newNewActionInfo = null;

    //my download
    private ActionInfo oldActionInfo;
    private ActionInfo newActionInfo;

    private long actionTime;
    private String actionHeadUrl;
    private String actionName;
    private String htsFileName;
    private long actionId;
    private String actionSyncState;

    private String mSchemeId = "";
    private String mSchemeName = "";

    public static final String ACTION_TIME= "action_time";
    public static final String ACTION_HEAD_URL = "action_head_url";
    public static final String ACTION_NAME= "action_name";
    public static final String ACTION_ID = "action_id";
    public static final String ACTION = "action";

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DubFragment dubFragment;
    private DubPreviewFragment dubPreviewFragment;
    private BaseFragment mCurrentFragment;


    public static final int TYPE_CREATE = 0;
    public static final int TYPE_DOWNLOAD = 1;
    public static final String ACTION_TYPE = "action_type";
    public static final String ACTION_INFO = "action_info";
    public static final String ACTION_SYNC_STATE= "action_sync_state";
    public static void launchActivity(Context context, Parcelable parcelable, int type, String actionSyncState) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, DubActivity.class);
        intent.putExtra(ACTION_INFO,parcelable);
        intent.putExtra(ACTION_TYPE, type);
        intent.putExtra(ACTION_SYNC_STATE, actionSyncState);
        context.startActivity(intent);

    }

    public static String DUB_TAG = "dub_tag";
    private long dubTag = 1;

    public static String DUB_STATE = "dub_state";

    private MyActionsHelper myActionsHelper;
    private MyActionsHelper.Action_type action_type;

    private LoadingDialog mLoadingDialog;
    public boolean isShowTips = false;

    private int SYNC_ACTION = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == SYNC_ACTION){
                initHelper();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dub);
        actionSyncState = getIntent().getStringExtra(ACTION_SYNC_STATE);
        UbtLog.d(TAG, "actionSyncState=" + actionSyncState);
        type= getIntent().getIntExtra(ACTION_TYPE, -1);
        if(type == TYPE_CREATE){
            oldNewActionInfo = getIntent().getParcelableExtra(ACTION_INFO);
            actionTime = oldNewActionInfo.actionTime;
            actionHeadUrl = oldNewActionInfo.actionHeadUrl;
            actionName = oldNewActionInfo.actionName;
            actionId = oldNewActionInfo.actionId;

            htsFileName = actionId+"";
            if(htsFileName.length() != 13){
                htsFileName = oldNewActionInfo.actionOriginalId + "";
            }

            action_type = MyActionsHelper.Action_type.My_new;
            UbtLog.d(TAG, "oldNewActionInfo=" + oldNewActionInfo.toString());

        }else if(type == TYPE_DOWNLOAD){
            oldActionInfo = getIntent().getParcelableExtra(ACTION_INFO);
            htsFileName = oldActionInfo.hts_file_name;
            actionTime = oldActionInfo.actionTime;
            actionHeadUrl = oldActionInfo.actionImagePath;
            actionName = oldActionInfo.actionName;
            actionId = oldActionInfo.actionId;
            action_type =  MyActionsHelper.Action_type.My_download;
            UbtLog.d(TAG, "oldActionInfo=" + oldActionInfo.toString());
        }


        newNewActionInfo = new NewActionInfo();
        newNewActionInfo.actionTime = actionTime;

        initUI();
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        dubFragment = new DubFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ACTION_TIME, actionTime);
        bundle.putString(ACTION_HEAD_URL, actionHeadUrl);
        bundle.putString(ACTION_NAME, actionName);
        bundle.putLong(ACTION_ID, actionId);
        bundle.putInt(ACTION_TYPE, type);
//        bundle.putParcelable(ACTION, PG.convertParcelable(oldNewActionInfo));
        dubFragment.setArguments(bundle);
        mFragmentTransaction.add(R.id.dub_container, dubFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = dubFragment;

        if(!isLostCoon()){
            UbtLog.d(TAG, "dub initHelper");
            handler.sendEmptyMessage(SYNC_ACTION);
        }

    }

    private void initHelper(){
        myActionsHelper =  MyActionsHelper.getInstance(this);
        myActionsHelper.RegisterHelper();
        myActionsHelper.registerListeners(this);
        if(myActionsHelper.getChargingState() && !SettingHelper.isPlayCharging(DubActivity.this)){
            Toast.makeText(DubActivity.this, DubActivity.this.getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
        }
        if(actionSyncState.equals(MyActionsHelper.Action_download_state.download_finish.toString())){
            UbtLog.d(TAG, "dub doSyncActionToRobot---");
            doSyncActionToRobot();

        }
    }

    public void doPlay(){
        if(!isLostCoon()){
            if(myActionsHelper.getChargingState() && !SettingHelper.isPlayCharging(DubActivity.this)){
                return;
            }
            if(myActionsHelper.isFirstPlayAction()){
                myActionsHelper.setIsFirstPlayAction();
                myActionsHelper.showFirstPlayTip();
                return;
            }

            if(type == TYPE_CREATE){
                myActionsHelper.doActionCommand(MyActionsHelper.Command_type.Do_play,oldNewActionInfo,action_type);
            }else{
                myActionsHelper.doActionCommand(MyActionsHelper.Command_type.Do_play,oldActionInfo,action_type);
            }

        }

    }

    public void doDefault() {
        if(!isLostCoon()){
            if(myActionsHelper.getChargingState() && !SettingHelper.isPlayCharging(DubActivity.this)){
                return;
            }
            myActionsHelper.doActionCommand(
                    MyActionsHelper.Command_type.Do_default, "", action_type);
        }

    }

    public void doSyncActionToRobot(){
        if(!isLostCoon()){
            if(myActionsHelper.getChargingState() && !SettingHelper.isPlayCharging(DubActivity.this)){
                return;
            }
            if(type == TYPE_CREATE){
                UbtLog.d(TAG, "dub sendActionFileToRobot");
                myActionsHelper.sendActionFileToRobot(oldNewActionInfo);
            }else{
                myActionsHelper.sendActionFileToRobot(oldActionInfo);
            }


        }
    }

    public void switchToDubPreviewFragment(){
        dubPreviewFragment = new DubPreviewFragment();
        Bundle data = new Bundle();
        data.putLong(ACTION_TIME, actionTime);
        dubPreviewFragment.setArguments(data);
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.dub_container, dubPreviewFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = dubPreviewFragment;
        llNext.setVisibility(View.VISIBLE);
        tvTitle.setText(getStringResources("ui_dub_preview"));
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(PrivateInfoActivity.class.getSimpleName());
        super.onResume();
//        if(!isLostCoon()){
//            UbtLog.d(TAG, "dub initHelper");
//            handler.sendEmptyMessage(SYNC_ACTION);
//        }
    }

    @Override
    public void onBackPressed() {
        if(mCurrentFragment instanceof DubPreviewFragment){
            doDefault();
            mFragmentTransaction = getFragmentManager().beginTransaction();
            dubFragment = new DubFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(ACTION_TIME, actionTime);
            bundle.putString(ACTION_HEAD_URL, actionHeadUrl);
            bundle.putString(ACTION_NAME, actionName);
            bundle.putLong(ACTION_ID, actionId);
            bundle.putInt(ACTION_TYPE, type);
            bundle.putBoolean(DUB_STATE, true);
            dubFragment.setArguments(bundle);
            mFragmentTransaction.replace(R.id.dub_container, dubFragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commitAllowingStateLoss();
            mCurrentFragment = dubFragment;
            llNext.setVisibility(View.INVISIBLE);
            tvTitle.setText(getStringResources("ui_dub"));

        }else{
            if(isShowTips){
                new AlertDialog(DubActivity.this).builder().setMsg(getStringResources("ui_dub_discard_message")).setCancelable(true).
                        setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doDefault();
                                isShowTips = false;
                                finish();
                            }
                        }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }else{
                finish();
            }

        }
    }

    @Override
    protected void initUI() {

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llNext = (LinearLayout) findViewById(R.id.ll_next);
        btnBack = (TextView) findViewById(R.id.tv_base_back);
        tvNext = (TextView) findViewById(R.id.tv_next);

        btnBack.setText(getStringResources("ui_common_back"));
        tvTitle.setText(getStringResources("ui_dub"));
        tvNext.setText(getStringResources("ui_perfect_next"));

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentFragment instanceof DubPreviewFragment){
                    doDefault();
                    Bundle bundle = new Bundle();
                    bundle.putLong(ACTION_TIME, actionTime);
                    bundle.putString(ACTION_HEAD_URL, actionHeadUrl);
                    bundle.putString(ACTION_NAME, actionName);
                    bundle.putLong(ACTION_ID, actionId);
                    bundle.putInt(ACTION_TYPE, type);
                    bundle.putBoolean(DUB_STATE, true);
                    dubFragment = new DubFragment();
                    dubFragment.setArguments(bundle);
                    mFragmentTransaction = getFragmentManager().beginTransaction();
                    mFragmentTransaction.replace(R.id.dub_container, dubFragment);
                    mFragmentTransaction.commit();
                    mCurrentFragment = dubFragment;
                    llNext.setVisibility(View.INVISIBLE);
                    tvTitle.setText(getStringResources("ui_dub"));

                }else{
                    if(isShowTips){
                        new AlertDialog(DubActivity.this).builder().setMsg(getStringResources("ui_dub_discard_message")).setCancelable(true).
                                setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        doDefault();
                                        isShowTips = false;
                                        finish();
                                    }
                                }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }else{
                        finish();
                    }

                }
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doDefault();
                Intent inte = new Intent();
                inte.setClass(DubActivity.this, ActionsEditSaveActivity.class);
                inte.putExtra(ActionsEditHelper.NewActionInfo, PG.convertParcelable(newNewActionInfo));
                inte.putExtra(ActionsEditSaveActivity.SCHEME_ID,mSchemeId);
                inte.putExtra(ActionsEditSaveActivity.SCHEME_NAME,mSchemeName);
                inte.putExtra(DUB_TAG, actionId);

                //发布之后的actionId会改变，与hts本来路径不一样
                if(type == TYPE_CREATE && oldNewActionInfo.actionStatus == 9){
                    File htsFile =  new File(FileTools.actions_new_cache + File.separator + actionId + File.separator + htsFileName+".hts");
                    if(!htsFile.exists()){
                        htsFile =  new File(FileTools.actions_new_cache + File.separator + oldNewActionInfo.actionOriginalId + File.separator + htsFileName+".hts");
                        if(htsFile.exists()){
                            inte.putExtra(DUB_TAG, oldNewActionInfo.actionOriginalId);
                        }
                    }
                }

                inte.putExtra(ACTION_TYPE, type);
//                startActivity(inte);
                startActivityForResult(inte, ActionsEditHelper.SaveActionReq);
                MobclickAgent.onEvent(DubActivity.this.getApplication(), AlphaStatics.ON_NEW_ACTION);//用户点击保存表示使用一次动作编辑

            }
        });


    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myActionsHelper != null) {
            myActionsHelper.unRegisterListeners(this);
        }
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    private boolean isSaveSuccess = false;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        isSaveSuccess =(Boolean) data.getExtras().get(
                ActionsEditHelper.SaveActionResult);
        if (requestCode == ActionsEditHelper.SaveActionReq
                && isSaveSuccess) {
            showToast("ui_save_action_success");
            finish();
        }
    }


    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionsFinish(List<String> names) {

    }

    @Override
    public void onNoteVol(int vol_pow) {

    }

    @Override
    public void onNoteVolState(boolean vol_state) {

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

    }

    @Override
    public void onSendFileStart() {
        UbtLog.d(TAG, "dub onSendFileStart");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog();
            }
        });
    }

    @Override
    public void onSendFileBusy() {
        UbtLog.d(TAG, "dub onSendFileBusy");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DubActivity.this, getStringResources("ui_remote_synchoronize_busy"), Toast.LENGTH_SHORT)
                        .show();
                dismissDialog();
            }
        });
    }

    @Override
    public void onSendFileError() {
        UbtLog.d(TAG, "dub onSendFileError");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DubActivity.this, getStringResources("ui_remote_select_robot_synchoronize_failed"), Toast.LENGTH_SHORT)
                        .show();
                dismissDialog();
            }
        });
    }

    @Override
    public void onSendFileFinish(int pos) {
        UbtLog.d(TAG, "dub onSendFileFinish  htsFileName::"+htsFileName);

        //update mCacheActionsNames
        if(type == TYPE_DOWNLOAD){
            if(!TextUtils.isEmpty(htsFileName)){
                MyActionsHelper.mCacheActionsNames.add((MyActionsHelper.localSize+MyActionsHelper.myDownloadSize),htsFileName.split("\\.")[0]);
                MyActionsHelper.myDownloadSize++;
            }

        }else if(type == TYPE_CREATE){
            if(!TextUtils.isEmpty(htsFileName)){
                MyActionsHelper.mCacheActionsNames.add(htsFileName);
                MyActionsHelper.myNewSize++;
                saveActionSyncState(MyActionsHelper.Action_download_state.send_finish.toString());
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DubActivity.this, getStringResources("ui_remote_select_robot_synchoronize_success"),
                        Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    @Override
    public void onSendFileCancel() {
        UbtLog.d(TAG, "dub onSendFileCancel");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dismissDialog();
            }
        });
    }

    @Override
    public void onSendFileUpdateProgress(String progress) {
        UbtLog.d(TAG, "dub onSendFileUpdateProgress");
    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void noteChangeFinish(boolean b, String string) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void syncServerDataEnd(List<Map<String, Object>> data) {

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "dub notePlayStart");
    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "dub notePlayPause");
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "dub notePlayContinue");
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG, "dub notePlayFinish");
    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG, "dub onPlaying");
    }

    @Override
    public void onPausePlay() {
        UbtLog.d(TAG, "dub onPausePlay");
    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "dub onFinishPlay");
    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {

    }

    @Override
    public void onDelRecordFinish() {

    }

    @Override
    public void onRecordFinish(long action_id) {

    }

    @Override
    public void onCollocateFinish(long action_id, boolean isSuccess, String error) {

    }

    @Override
    public void onCollocateRmoveFinish(boolean b) {

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClearCache() {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

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

    }


    public boolean isLostCoon() {
        if (((AlphaApplication) DubActivity.this.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    public void showDialog() {
        if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
        {
            mLoadingDialog.show();
        }
    }

    public void dismissDialog() {
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing())
        {
            mLoadingDialog.cancel();
        }
    }


}

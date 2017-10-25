package com.ubt.alpha1e.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.event.ActionEvent;
import com.ubt.alpha1e.ui.custom.CustomViewPager;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.dialog.SyncDownloadAlertDialog;
import com.ubt.alpha1e.ui.fragment.BaseMyActionsFragment;
import com.ubt.alpha1e.ui.fragment.MyActionsCircleFragment;
import com.ubt.alpha1e.ui.fragment.MyActionsCollectFragment;
import com.ubt.alpha1e.ui.fragment.MyActionsCreateFragment;
import com.ubt.alpha1e.ui.fragment.MyActionsDownloadFragment;
import com.ubt.alpha1e.ui.fragment.MyActionsLocalFragment;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyActionsActivity extends BaseActivity implements BaseDiaUI,View.OnClickListener,BaseMyActionsFragment.onRefreshActivityView,
        BaseMyActionsFragment.OnInteractWithActivity,MyActionsCircleFragment.OnFragmentInteractionListener,IActionsUI {

    private static final String TAG = "MyActionsActivity";

    private List<String> mTitles;
    private CustomViewPager mViewPager;
    private FragmentsAdapter mAdapter;
    private Button btn_start_cycle;

    public  BaseMyActionsFragment fragment;
    private LoadingDialog mLoadingDialog;

    public  List<Map<String,Object>> mInsideDatas = new ArrayList<>();
    private MyActionsCircleFragment myActionsCircleFragment;
    public  MyActionsHelper mHelper;
    public  MyActionsHelper.Action_type mCurrentActionType;
    private int mCurrentVol = 0;
    public static String REQUEST_TYPE = "REQUEST_TYPE";
    public static String PLAY_ACTION_ID = "PLAY_ACTION_ID";
    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    public static int requestPosition = -1;
    private boolean isSyncDownloadBack = false;
    private long mPlayActionId = -1;//传过来需要播放的动作ID

    //在我的下载里面，最新等待下载完成播放的动作
    private ActionInfo mCurrDownloadOnRobotWaitPlayAction = null;

    //定义同步下载弹出框
    private SyncDownloadAlertDialog mSyncAlertDialog = null;

    public static void launchActivity(Context mContext, int requestPosition)
    {
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext,MyActionsActivity.class);
        intent.putExtra(REQUEST_TYPE,requestPosition);
        mContext.startActivity(intent);

    }

    public static void launchActivity(Context mContext, int requestPosition, long mPlayActionId)
    {
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext,MyActionsActivity.class);
        intent.putExtra(REQUEST_TYPE,requestPosition);
        intent.putExtra(PLAY_ACTION_ID,mPlayActionId);
        mContext.startActivity(intent);

    }

    public static void launchActivity(Context mContext, int requestPosition,String schemeId,String schemeName)
    {
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext,MyActionsActivity.class);
        intent.putExtra(REQUEST_TYPE,requestPosition);
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        mContext.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_actions);
        if(getIntent().getExtras()!=null){
            requestPosition = (int)getIntent().getExtras().get(REQUEST_TYPE);
            mPlayActionId = getIntent().getExtras().getLong(PLAY_ACTION_ID,-1);
        }
        initUI();
        mTitles = new ArrayList<>();
        //初始化三种类型
        mTitles.add(getStringResources("ui_control_demo"));
        mTitles.add(getStringResources("ui_myaction_download"));
        mTitles.add(getStringResources("ui_mine_like"));
        mTitles.add(getStringResources("ui_myaction_creation"));
        initViewPager();
        initTabLayout();
        initControlListener();

    }

    @Override
    protected void initUI() {
        switch (requestPosition){
            case 0:
                initTitle(getStringResources("ui_show_actions"));
                break;
            case 1:
                initTitle(getStringResources("ui_myaction_download"));
                break;
            case 2:
                initTitle(getStringResources("ui_mine_like"));
                break;
            case 3:
                initTitle(getStringResources("ui_myaction_creation"));

                break;
        }

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);

        mSyncAlertDialog = new SyncDownloadAlertDialog(MyActionsActivity.this)
                .builder()
                .setMsg(getStringResources("ui_remote_select_robot_synchoronizing"))
                .setImageResoure(R.drawable.data_loading)
                .setCancelable(false);

        if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()
                && requestPosition == 1){ // 我的下载才会有后台同步
            mSyncAlertDialog.setPositiveButton(getStringResources("ui_background_synchorize"),backDownloadListener);
        }

        btn_start_cycle = (Button) findViewById(R.id.btn_start_cycle);
        btn_start_cycle.setOnClickListener(this);

    }

    /**
     * 后台下载监听器
     * 点击后台下载后，置空等待播放对象
     */
    private View.OnClickListener backDownloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0; i< fragment.mDatas.size();i++){
                Map<String,Object> map = fragment.mDatas.get(i);
                if(map != null && mCurrDownloadOnRobotWaitPlayAction.actionId == ((ActionInfo)map.get(MyActionsHelper.map_val_action)).actionId) {
                    fragment.mDatas.get(i).put(MyActionsHelper.map_val_action_download_state,MyActionsHelper.Action_download_state.downing);
                    fragment.sendMessage(i,fragment.UPDATE_ITEMS);
                    break;
                }
            }

            mCurrDownloadOnRobotWaitPlayAction = null;
        }
    };

    @Override
    public void showDialog() {
        if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
        {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissDialog() {

        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!this.isFinishing())
        {
            mLoadingDialog.cancel();
        }
    }

    /**
     * 显示同步进度框
     */
    public void showSyncDialog() {
        if(mSyncAlertDialog != null && !mSyncAlertDialog.isShowing())
        {
            mSyncAlertDialog.show();
        }
    }

    /**
     * 消失同步进度框
     */
    public void dismissSyncDialog() {

        if(mSyncAlertDialog != null && mSyncAlertDialog.isShowing() && !this.isFinishing())
        {
            mSyncAlertDialog.display();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper =  MyActionsHelper.getInstance(this);
        mHelper.setManagerListeners(this);
        mHelper.registerListeners(this);
        if(fragment!=null &&(fragment instanceof  MyActionsCreateFragment ||fragment instanceof  MyActionsCollectFragment || fragment instanceof  MyActionsDownloadFragment) )
        {
            UbtLog.d(TAG,"lihai----loadCreatedDatas--" + isSyncDownloadBack);
            if(isSyncDownloadBack){
                isSyncDownloadBack = false;
                return;
            }
            //deal Dub sync
            mInsideDatas.clear();
            fragment.firstLoadData();
        }

        if(requestPosition == 4){
//            fragment = new MyActionsCircleFragment();
            if(!AlphaApplication.isCycleActionFragment()){
                startCycleActionFragment();
            }

        }else if(requestPosition == 3){
            if(getIntent().getExtras() != null){
                mHelper.setSchemeInfo((String)getIntent().getExtras().get(SCHEME_ID),(String)getIntent().getExtras().get(SCHEME_NAME));
            }
        }
    }

    @Override
    protected void onPause() {
        UbtLog.d(TAG, "MyactionsActivity onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UbtLog.d(TAG, "MyactionsActivity onstop ");
        if(mHelper!= null){
            mHelper.unRegisterListeners(this);
        }

    }

    @Override
    protected void onDestroy() {
        UbtLog.d(TAG, "--wmma--MyActionsActivity onDestroy!");
        dismissDialog();
        dismissSyncDialog();

        mHelper.unRegisterListeners(this);
        super.onDestroy();
    }

    //fragment closed
    @Override
    public void onFragmentInteraction() {
        if(myActionsCircleFragment!=null&&!myActionsCircleFragment.isHidden())
            getSupportFragmentManager().beginTransaction().hide(myActionsCircleFragment).commit();
        if(btn_start_cycle!=null)
            btn_start_cycle.setVisibility(View.GONE);
        if(fragment!=null)
            fragment.userVisible(true);

    }

    @Override
    public void onShowLoopButton() {
        if(btn_start_cycle!=null){
            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_start_cycle.setVisibility(View.VISIBLE);
                }
            });

        }

    }

    @Override
    public void onHiddenLoopButton() {
        if(btn_start_cycle!=null)
            btn_start_cycle.setVisibility(View.GONE);
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(getAppCurrentLanguage().contains("zh"))
        {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);
    }

    protected void initViewPager() {
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager_list);
        mAdapter = new FragmentsAdapter(getSupportFragmentManager(),mPlayActionId);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setPagingEnabled(false);
        if(requestPosition!=-1)
        {
            mViewPager.setCurrentItem(requestPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_start_cycle:
                //检测是否在充电状态和边充边玩状态是否打开
                if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(MyActionsActivity.this)){
                    Toast.makeText(MyActionsActivity.this, getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(fragment!=null && fragment.equals(myActionsCircleFragment)){
                    mHelper = fragment.mHelper;
                }

                if (MyActionsHelper.mCurrentSeletedNameList.size() < 1) {
                    Toast.makeText(
                            this,
                            getStringResources("ui_action_cycle_empty"), Toast.LENGTH_SHORT)
                            .show();
                }else
                {
                    JSONArray action_cyc_list = new JSONArray(MyActionsHelper.mCurrentSeletedNameList
                    );
                    mHelper.doCycle(action_cyc_list);
                    Toast.makeText(
                            this,
                            getStringResources("ui_action_cycle_too_long"), Toast.LENGTH_SHORT).show();

                    List<Map<String,Object>> playActionMap = new ArrayList<>();
                    for(Map<String,Object> actionMap : mInsideDatas){
                        UbtLog.e(TAG, "mInsideDatas size=" + mInsideDatas.size());
                        if(MyActionsHelper.mCurrentSeletedActionInfoMap.get(actionMap.get(MyActionsHelper.map_val_action_name)) != null){
                            actionMap.put(MyActionsHelper.map_val_action_is_playing,true);
                            actionMap.put(MyActionsHelper.map_val_action_selected,true);
                            playActionMap.add(actionMap);
                        }
                    }
                    myActionsCircleFragment.setDatas(playActionMap);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    public void setmInsideDatas(List<Map<String,Object>> mDatas)
    {
        this.mInsideDatas = mDatas;
    }

    @Override
    public void updateUI(int type) {

    }

    @Override
    public void onLostBtCoon() {
        MyActionsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(fragment instanceof MyActionsDownloadFragment || fragment instanceof MyActionsCreateFragment){

                    for(int i=0; i< fragment.mDatas.size();i++){
                        if(fragment.mDatas.get(i) != null){
                            fragment.mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing,false);
                        }
                    }
                    fragment.sendMessage(1,fragment.UPDATE_VIEWS);

                    //同步中，蓝牙断开，消失同步框
                    dismissSyncDialog();
                }
            }
        });
        super.onLostBtCoon();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12306)//初始化加载数据
        {
            if(fragment!=null){
                ((AlphaApplication) this.getApplication()).setBaseActivity(this);
                fragment.firstLoadData();
            }
        }else if(requestCode == 10010&&resultCode==RESULT_OK)//历史记录漫游
        {
            if(fragment == null){
                return;
            }
            if(fragment instanceof MyActionsCreateFragment)
            {
                isSyncDownloadBack = true;
                dealSyncCreateActions(data);
            }else if(fragment instanceof  MyActionsDownloadFragment)
            {
                //isSyncDownloadBack = true;
                dealSyncDownloadActions(data);
            }
        }else if(requestCode == 10086)//
        {
//          if(fragment instanceof  MyActionsCreateFragment)
//          {
//              mHelper.doReadMyNewAction();
//          }

        }else if(requestCode == Constant.FROM_CREATE_ACTION_TO_DETAIL){
            //判断是否详情点击播放返回
            if(resultCode == Constant.FROM_DETAIL_PLAY_BACK_TO_CREATE){
                long actionId = data.getLongExtra("actionId",-1);
                if(actionId != -1 && fragment instanceof MyActionsCreateFragment){
                    ((MyActionsCreateFragment)fragment).playActionById(actionId);
                }
            }
        }
    }

    private void dealSyncCreateActions(Intent data)
    {
        MyActionsHelper actionsHelper = fragment.mHelper;
        List<String>  list = data.getStringArrayListExtra(MyActionsHelper.TRANSFOR_PARCEBLE);
        List<NewActionInfo> infos = new ArrayList<>();
        List<NewActionInfo> downloadInfos = new ArrayList<>();
        for(String s:list)
        {
            boolean isExist = false;
            NewActionInfo info =   new NewActionInfo().getThiz(s);
            info.actionImagePath = info.actionHeadUrl;
            downloadInfos.add(info);
            if(fragment.mDatas.size()==0)
            {
                infos.add(info);
            }else
            {
                for(Map<String,Object> original:fragment.mDatas)
                {
                    if(original==null){
                        continue;
                    }
                    if(info.actionOriginalId == ((NewActionInfo)original.get(MyActionsHelper.map_val_action)).actionOriginalId) {
                        isExist = true;
                        break;
                    }
                }
                if(!isExist)
                    infos.add(info);
            }
        }
        if(fragment.mDatas.size()!=0) {
            UbtLog.d(TAG,"lihai----loadCreatedDatas-2");
            List<Map<String,Object>> dataSets =actionsHelper.loadCreatedDatas(infos,mInsideDatas);
            fragment.mDatas.remove(fragment.mDatas.size()-1);
            fragment.mDatas.addAll(0,dataSets);
            fragment.mDatas.add(null);
            fragment.mAdapter.setData(fragment.mDatas);
            fragment.mAdapter.notifyDataSetChanged();
        }else
        {
            UbtLog.d(TAG,"lihai----loadCreatedDatas-3");
            fragment.updateViews(true,1);
            List<Map<String,Object>> dataSets =actionsHelper.loadCreatedDatas(infos,mInsideDatas);
            fragment.mDatas.addAll(dataSets);
            fragment.mDatas.add(null);
            fragment.mAdapter.setData(fragment.mDatas);
            fragment.mAdapter.notifyDataSetChanged();
        }
        actionsHelper.doWriteDownLoadFile(infos);
        for(NewActionInfo info:downloadInfos)
        {
            actionsHelper.doDownloadSyncFiles(info.actionUrl, FileTools.actions_new_cache,info.actionId+"");
        }
    }
    private void dealSyncDownloadActions(Intent data)
    {
        MyActionsHelper actionsHelper = fragment.mHelper;
        List<String>  list = data.getStringArrayListExtra(MyActionsHelper.TRANSFOR_PARCEBLE);
        List<ActionInfo> infos = new ArrayList<>();
        List<ActionInfo> downloadInfos = new ArrayList<>();

        for(String s:list)
        {
            boolean isExist = false;
            ActionInfo info = GsonImpl.get().toObject(s,ActionInfo.class);
            info.actionId = info.downloadObjectId;
            ActionRecordInfo recordInfo = new ActionRecordInfo(info,false);
            downloadInfos.add(info);
            if(fragment.mDatas.size()==0)
            {
                infos.add(info);

            }else
            {
                for(Map<String,Object> original:fragment.mDatas)
                {
                    if(original==null){
                        continue;
                    }
                    if(info.actionId== ((ActionInfo)original.get(MyActionsHelper.map_val_action)).actionId) {
                        isExist = true;
                        break;
                    }
                }
                if(!isExist)
                {
                    infos.add(info);
                }
            }
        }
        List<ActionRecordInfo> recordInfos = new ArrayList<>();
        List<Map<String,Object>> dataSets =actionsHelper.loadDatas(recordInfos,infos,null,mInsideDatas);
        if(fragment.mDatas.size()!=0)
        {
            fragment.mDatas.addAll(0,dataSets);
            fragment.mAdapter.setData(fragment.mDatas);
            fragment.mAdapter.notifyDataSetChanged();
        }
        else
        {
            fragment.mDatas.addAll(dataSets);
            fragment.mAdapter.setData(fragment.mDatas);
            fragment.sendMessage(1,BaseMyActionsFragment.UPDATE_VIEWS);
        }

        for(ActionInfo info:infos)
        {
            actionsHelper.doDownLoad(info,false);
        }
    }

    @Subscribe
    public void onEventAction(ActionEvent event) {
        if(event.getEvent() == ActionEvent.Event.ROBOT_ACTION_DOWNLOAD_START){
            mCurrDownloadOnRobotWaitPlayAction = event.getActionInfo();

            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showSyncDialog();
                }
            });

        }else if(event.getEvent() == ActionEvent.Event.ROBOT_ACTION_DOWNLOAD){

            UbtLog.d(TAG,"getActionInfo() = " + event.getActionInfo() + "     " + event.getDownloadProgressInfo().progress);
            final DownloadProgressInfo downloadProgressInfo = event.getDownloadProgressInfo();

            if(downloadProgressInfo.status != 1){
                //下载完成 成功/失败
                MyActionsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //必须是等待下载成功的动作，完成后，才消失
                        if(mSyncAlertDialog.isShowing()
                                && downloadProgressInfo.actionId == mCurrDownloadOnRobotWaitPlayAction.actionId){
                            mSyncAlertDialog.display();
                        }

                        UbtLog.d(TAG,"downloadProgressInfo = " + downloadProgressInfo + "   fragment = " + fragment);
                        //如果等于下载成功，则播放
                        if(downloadProgressInfo.status == 2
                                && fragment instanceof MyActionsDownloadFragment ){
                            int position = -1;
                            ActionInfo actionInfo = null;
                            for(int i = 0; i < fragment.mDatas.size(); i++){
                                actionInfo = ((ActionInfo)(fragment.mDatas.get(i).get("map_val_action")));
                                if(downloadProgressInfo.actionId == actionInfo.actionId){
                                    position = i;
                                    break;
                                }
                            }

                            UbtLog.d(TAG,"position = " + position );
                            //判断非-1，机器人下载完成，本地无此动作（存在情况：告诉机器人下载动作之后，本地立即删掉动作）
                            if(position != -1){
                                fragment.mDatas.get(position).put(MyActionsHelper.map_val_action_download_state,MyActionsHelper.Action_download_state.send_finish);
                                fragment.mAdapter.notifyItemChanged(position);

                                //下载完成，播放
                                if(mCurrDownloadOnRobotWaitPlayAction != null
                                        && mCurrDownloadOnRobotWaitPlayAction.actionId == downloadProgressInfo.actionId){
                                    UbtLog.d(TAG, "download finish to play" );
                                    mHelper.startPlayAction(fragment.mDatas.get(position),position, mCurrentActionType);
                                    mCurrDownloadOnRobotWaitPlayAction = null;
                                }
                            }
                        }else {
                            //下载失败，置空
                            if(mCurrDownloadOnRobotWaitPlayAction != null
                                    && mCurrDownloadOnRobotWaitPlayAction.actionId == downloadProgressInfo.actionId){
                                mCurrDownloadOnRobotWaitPlayAction = null;
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void noteWaitWebProcressShutDown() {

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
        if (vol_state) {
            if (mCurrentVol < 0) {
                mCurrentVol *= -1;
                mHelper.doChangeVol(mCurrentVol);
            }
        }

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

    }
    @Override
    public void onSendFileStart() {
        if((BaseActivity)AlphaApplication.getBaseActivity() instanceof  MyActionsActivity){
            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showSyncDialog();
                }
            });
        }
    }

    @Override
    public void onSendFileBusy() {

        // deal Dub sync actions
        if((BaseActivity)AlphaApplication.getBaseActivity() instanceof  MyActionsActivity){
            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyActionsActivity.this, getStringResources("ui_remote_synchoronize_busy"), Toast.LENGTH_SHORT)
                            .show();
                    dismissSyncDialog();

                }
            });
        }
    }

    @Override
    public void onSendFileError() {
        // deal Dub sync actions
        if((BaseActivity)AlphaApplication.getBaseActivity() instanceof  MyActionsActivity){
            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyActionsActivity.this, getStringResources("ui_remote_select_robot_synchoronize_failed"), Toast.LENGTH_SHORT)
                            .show();
                    dismissSyncDialog();
                }
            });
        }
    }

    @Override
    public void onSendFileFinish(int pos) {
        if(pos < 0 ){
            return;
        }

        // deal Dub sync actions
        if((BaseActivity)AlphaApplication.getBaseActivity() instanceof  MyActionsActivity) {
            final int position = pos;
            MyActionsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(MyActionsActivity.this, getStringResources("ui_remote_select_robot_synchoronize_success"),
                            Toast.LENGTH_SHORT).show();
                    dismissSyncDialog();

                    //update mCacheActionsNames
                    if(mCurrentActionType == MyActionsHelper.Action_type.My_download){
                        String sendFileName = ((ActionInfo)(fragment.mDatas.get(position).get("map_val_action"))).hts_file_name.split("\\.")[0];
                        MyActionsHelper.mCacheActionsNames.add((MyActionsHelper.localSize+MyActionsHelper.myDownloadSize),sendFileName);
                        MyActionsHelper.myDownloadSize++;
                    }else if(mCurrentActionType == MyActionsHelper.Action_type.My_new){
                        String sendFileName = ((ActionInfo)(fragment.mDatas.get(position).get("map_val_action"))).actionId+"";
                        if(sendFileName.length() != 13){
                            sendFileName = ((ActionInfo)(fragment.mDatas.get(position).get("map_val_action"))).actionOriginalId+"";
                        }
                        MyActionsHelper.mCacheActionsNames.add(sendFileName);
                        MyActionsHelper.myNewSize++;
                    }
                    fragment.mDatas.get(position).put(MyActionsHelper.map_val_action_download_state,MyActionsHelper.Action_download_state.send_finish);
                    fragment.mAdapter.notifyItemChanged(position);
                    UbtLog.d(TAG, "istop= start play" );
                    mHelper.startPlayAction(fragment.mDatas.get(position),position, mCurrentActionType);
                }
            });
        }

    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {
        UbtLog.d("wilson","onSendFileUpdateProgress:"+progress);

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
        if(!isFinishing() && !(fragment instanceof MyActionsCollectFragment)){
            dismissDialog();
            dismissSyncDialog();
            this.showToast("ui_remote_synchoronize_no_sd");
            this.finish();
        }
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

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "---wmma notePlayPause!");

        if (mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_download) {
            mHelper.doPauseMp3ForMyDownload();
        }
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        if (mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_download) {
            mHelper.doPauseMp3ForMyDownload();
        }
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        final ActionPlayer.Play_type  mType =  mCurrentPlayType;
        MyActionsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mType == ActionPlayer.Play_type.cycle_action) {
                    if(myActionsCircleFragment!=null){
                        myActionsCircleFragment.setDatas(mInsideDatas);
                    }
                }
            }
        });

    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG, "---onPlaying");
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    long actionId = ((MyActionsHelper) mHelper).getNewPlayerActionId();
                    UbtLog.d(TAG, "1018 actionId:" + actionId);
                    if(fragment instanceof MyActionsCreateFragment){
                        for(int i=0; i< fragment.mDatas.size();i++){
                            if(fragment.mDatas.get(i) != null){
                                UbtLog.d(TAG, "---onPlaying ---1");
                                NewActionInfo actionInfo = (NewActionInfo)fragment.mDatas.get(i).get(MyActionsHelper.map_val_action);
                                if(actionId == actionInfo.actionId){
                                    fragment.mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing,true);
                                    fragment.sendMessage(i,fragment.UPDATE_ITEMS);
                                    break;
                                }
                            }
                        }
                    }

                    mHelper.doPlayMp3ForMyCreate(actionId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPausePlay() {
        UbtLog.d(TAG, "onPausePlay");
        MyActionsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long actionId = ((MyActionsHelper) mHelper).getNewPlayerActionId();
                if(fragment instanceof MyActionsCreateFragment){
                    for(int i=0; i< fragment.mDatas.size();i++){
                        if(fragment.mDatas.get(i) != null){
                            NewActionInfo actionInfo = (NewActionInfo)fragment.mDatas.get(i).get(MyActionsHelper.map_val_action);
                            if(actionId == actionInfo.actionId){
                                fragment.mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing,false);
                                fragment.sendMessage(i,fragment.UPDATE_ITEMS);
                                break;
                            }
                        }
                    }
                }

                mHelper.doPauseMp3ForMyDownload();

            }
        });
    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay");
        final long actionId = mHelper.getNewPlayerActionId();
        mHelper.doStopMp3ForMyDownload();

        MyActionsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(fragment instanceof MyActionsCreateFragment){
                    for(int i=0; i< fragment.mDatas.size();i++){
                        if(fragment.mDatas.get(i) != null){
                            NewActionInfo actionInfo = (NewActionInfo)fragment.mDatas.get(i).get(MyActionsHelper.map_val_action);
                            if(actionId == actionInfo.actionId){
                                fragment.mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing,false);
                                fragment.sendMessage(i,fragment.UPDATE_ITEMS);
                                break;
                            }
                        }

                    }
                }

            }
        });
    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mHelper.doActionCommand(MyActionsHelper.Command_type.Do_Stop,
                        "", MyActionsHelper.Action_type.My_download);
            }
        });
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
    public void onClaerCache() {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {
        UbtLog.e(TAG, "--wmma--onReadNewActionsFinish=" + actions.size());
    }

    @Override
    public void onChangeNewActionsFinish() {

    }


    class FragmentsAdapter extends FragmentPagerAdapter {
        LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();
        long mPlayActionId = -1;
        public FragmentsAdapter(FragmentManager fm,long mActionId) {
            super(fm);
            mPlayActionId = mActionId;
        }

        private Fragment getNeedFragment(int pos)
        {
            switch (pos)
            {
                case 0:
                    return MyActionsLocalFragment.newInstance(pos);
                case 1:
                    return MyActionsDownloadFragment.newInstance(pos,mPlayActionId);
                case 2:
                    return MyActionsCollectFragment.newInstance(pos);
                case 3:
                    return MyActionsCreateFragment.newInstance(pos);
                default:
                    return MyActionsLocalFragment.newInstance(pos);
            }

        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = mFragmentCache.containsKey(position) ? mFragmentCache.get(position)
                    : getNeedFragment(position);
            if (!mFragmentCache.containsKey(position))
                mFragmentCache.put(position, f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {


        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            fragment = (BaseMyActionsFragment) object;
            if(fragment instanceof MyActionsLocalFragment)
            {
                mCurrentActionType = MyActionsHelper.Action_type.Base_type;
            }else if(fragment instanceof  MyActionsDownloadFragment)
            {
                mCurrentActionType = MyActionsHelper.Action_type.My_download;
            }else if(fragment instanceof  MyActionsCreateFragment)
            {
                mCurrentActionType = MyActionsHelper.Action_type.My_new;
            }
            AlphaApplication.setActionType(mCurrentActionType);

        }

    }


    /**
     *
     *@description add by wmma
     *<p>用于在浮动窗口中跳转到CycleActionFragment</p>
     *@params
     *@return
     *@throwsIOException
     *@throwsNullPointerException
     */
    public  void startCycleActionFragment(){
        if(myActionsCircleFragment==null){
            myActionsCircleFragment = new MyActionsCircleFragment();
            fragment = myActionsCircleFragment;
        }

        if(!myActionsCircleFragment.isAdded()){
                getSupportFragmentManager().beginTransaction().add(R.id.rl_fragment_content,myActionsCircleFragment).commit();
        }
        else if(myActionsCircleFragment.isHidden()){
            getSupportFragmentManager().beginTransaction().show(myActionsCircleFragment).commit();
        }
        myActionsCircleFragment.setDatas(mInsideDatas);
        btn_start_cycle.setVisibility(View.VISIBLE);
        if(fragment!=null){
            fragment.userVisible(false);
        }
    }

    private static long actionId = -1;

    public static synchronized long getActionDownLoadFailId() {
        return  actionId;
    }

}

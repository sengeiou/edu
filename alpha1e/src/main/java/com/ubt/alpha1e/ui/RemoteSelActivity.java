package com.ubt.alpha1e.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.MyGamegadRecyclerAdapter;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.business.ActionPlayer.Play_type;
import com.ubt.alpha1e.business.EndlessRecyclerOnScrollListener;
import com.ubt.alpha1e.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.RemoteItem;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IRemoteUI;
import com.ubt.alpha1e.ui.helper.RemoteHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

public class RemoteSelActivity extends BaseActivity implements IRemoteUI, BaseDiaUI {

    private static final String TAG = RemoteSelActivity.class.getSimpleName();

    public RecyclerView recyclerview_my_roles;
    public LinearLayoutManager mLayoutManager;
    public MyGamegadRecyclerAdapter mAdapter;

    private List<RemoteRoleInfo> mAllRemoteRoleDatas = null;

    private static final int TIEM_OUT = 0x0010;
    private static final int UPDATE_VIEW = 0x0011;
    private static final int READ_REMOTE_ROLE_FINISH = 0x0100;
    private static final int DELETE_REMOTE_ROLE = 0x0101;
    public static final int CLICK_REMOTE_ROLE = 0x0110;
    private static final int COLSE_HEAD_TITLE = 0x0111;

    private int mClickPosition = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RemoteRoleInfo roleInfo = null;
            switch (msg.what) {
                case READ_REMOTE_ROLE_FINISH:
                    mAllRemoteRoleDatas.clear();
                    List<RemoteRoleInfo> roleInfos = (List<RemoteRoleInfo>)msg.obj;
                    roleInfos = dealrRoleInfos(roleInfos);

                    if(roleInfos != null && roleInfos.size() > 0){
                        mAllRemoteRoleDatas.addAll(roleInfos);
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case DELETE_REMOTE_ROLE:
                    roleInfo = (RemoteRoleInfo)msg.obj;
                    Bundle bundle = msg.getData();
                    boolean isSuccess = bundle.getBoolean(RemoteHelper.IS_SUCCESS);
                    if(isSuccess){
                        for(int i= 0 ; i < mAllRemoteRoleDatas.size();i++){
                            if(roleInfo.roleid == mAllRemoteRoleDatas.get(i).roleid){
                                mAllRemoteRoleDatas.remove(i);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }else{
                        showToast("删除失败 ");
                    }
                    break;
                case UPDATE_VIEW:

                    break;
                case TIEM_OUT:
                    if (mCoonLoadingDia != null){
                        mCoonLoadingDia.cancel();
                    }
                    break;
                case CLICK_REMOTE_ROLE:
                    if(BaseHelper.hasSdcard){
                        mClickPosition = msg.arg1;
                        clickRemoteRole(msg.arg1,true);
                        /*if(mClickPosition == 1 || mClickPosition == 2){
                            List<String> mActionsNames = ((RemoteHelper) mHelper).getActionsNamesList();
                            UbtLog.d(TAG,"mActionsNames.size():" + mActionsNames.size());

                            if(!mActionsNames.isEmpty() && mActionsNames.size() >= 12 && (mActionsNames.size() == ((RemoteHelper) mHelper).getRobotActionsLength())){
                                clickRemoteRole(msg.arg1,true);
                            }else{
                                ((RemoteHelper) mHelper).doReadActions();
                                ((LoadingDialog) mCoonLoadingDia).showMessage(RemoteSelActivity.this.getStringResources("ui_remote_initializing"));
                                mHandler.sendEmptyMessageDelayed(TIEM_OUT, 15*1000);
                            }
                        }else{
                            clickRemoteRole(msg.arg1,true);
                        }*/
                    }else {
                        RemoteSelActivity.this.showToast("ui_remote_synchoronize_no_sd");
                    }
                    break;
                case COLSE_HEAD_TITLE:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AlphaApplication.isPad()){
            setContentView(R.layout.activity_remote_sel_pad);
        }else {
            setContentView(R.layout.activity_remote_sel);
        }
        mHelper = new RemoteHelper(this, this);
        mAllRemoteRoleDatas = new ArrayList<>();
        initUI();
        initControlListener();

        ((RemoteHelper) mHelper).doIntoRemote();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(RemoteSelActivity.class.getSimpleName());
        super.onResume();

        ((RemoteHelper) mHelper).dd();
        ((RemoteHelper) mHelper).doReadAllRemoteRole();

        mClickPosition = -1;
        mCoonLoadingDia = LoadingDialog.getInstance(this, this);

    }

    @Override
    protected void onDestroy() {
        if(isBulueToothConnected()){
            ((RemoteHelper) mHelper).doOutRemote();
        }
        if (mCoonLoadingDia != null) {
            if (mCoonLoadingDia.isShowing())
                mCoonLoadingDia.cancel();

            mCoonLoadingDia = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        //initTitle(getStringResources("ui_home_remote"));
        View backView = findViewById(R.id.ll_base_back);
        ImageView ivTitleRight = (ImageView)findViewById(R.id.iv_title_right);
        ImageView ivClosePublish = (ImageView)findViewById(R.id.iv_close_publish);
        TextView ivTitleName = (TextView) findViewById(R.id.tv_base_title_name);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        ivClosePublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.getInstance().put(Constant.SP_SHOW_REMOTE_PUBLISH,false);
                findViewById(R.id.rl_head_tip).setVisibility(View.GONE);
            }
        });

        ivTitleName.setText(getStringResources("ui_home_remote"));
        ivTitleName.setTextColor(getResources().getColor(R.color.white));
        ivTitleRight.setVisibility(View.INVISIBLE);
//        ivTitleRight.setBackgroundResource(R.drawable.icon_remote_add);
        if(SPUtils.getInstance().getBoolean(Constant.SP_SHOW_REMOTE_PUBLISH,true)){
            findViewById(R.id.rl_head_tip).setVisibility(View.VISIBLE);
        }

        initRecyclerViews();
    }

    @Override
    public void setTitleBack(View.OnClickListener listener) {

        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finishActivity();
            }
        };
        //super.setTitleBack(backListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {
            finishActivity();
        }
        return true;
    }

    private void finishActivity(){
        finish();
        /*List<Activity> mActivityList = ((AlphaApplication) RemoteSelActivity.this.getApplication()).getHistoryActivityList();
        for (int i = 0; i < mActivityList.size(); i++) {
            try {
                if(mActivityList.get(i) != null && mActivityList.get(i).toString().contains("RemoteSelActivity")){
                    mActivityList.get(i).finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    public void initRecyclerViews() {

        recyclerview_my_roles = (RecyclerView) findViewById(R.id.recyclerview_my_roles);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_my_roles.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = recyclerview_my_roles.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        recyclerview_my_roles.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.left = 30;
            }
        });
        mAdapter = new MyGamegadRecyclerAdapter(this,mAllRemoteRoleDatas,mHelper,mHandler);
        recyclerview_my_roles.setAdapter(mAdapter);
        recyclerview_my_roles.addOnScrollListener(mOnScrollListener);
    }

    /**
     * 出来足球员、格斗家 国际化显示
     * @param roles
     * @return
     */
    private List<RemoteRoleInfo> dealrRoleInfos(List<RemoteRoleInfo> roles){
        List<RemoteRoleInfo> newList = new ArrayList<RemoteRoleInfo>();

        RemoteRoleInfo roleInfo = null;
        for(int i = 0;i < roles.size();i++){
            roleInfo = roles.get(i);
            if(roleInfo.roleid == 1){
                roleInfo.roleName = getResources().getString(R.string.ui_remote_role_footballer);
                roleInfo.roleIntroduction = getResources().getString(R.string.ui_remote_role_footballer_introduction);
                roleInfo.roleIcon = R.drawable.gamepad_model_football+"";
            }else if(roleInfo.roleid == 2){
                roleInfo.roleName = getResources().getString(R.string.ui_remote_role_fighter);
                roleInfo.roleIntroduction = getResources().getString(R.string.ui_remote_role_fighter_introduction);
                roleInfo.roleIcon = R.drawable.gamepad_model_fighter+"";
            }else if(roleInfo.roleid == 3){
                roleInfo.roleName = getResources().getString(R.string.ui_remote_role_dancer);
                roleInfo.roleIntroduction = getResources().getString(R.string.ui_remote_role_dancer_introduction);
                roleInfo.roleIcon = R.drawable.gamepad_model_dancer+"";
            }
            newList.add(roleInfo);
        }
        return newList;
    }

    @Override
    public void onReadActionsFinish(List<String> mActionsNames) {
        try {
//            if(mClickPosition > 0){
//                UbtLog.d(TAG,"ccy onReadActionsFinish 2");
//                clickRemoteRole(mClickPosition,false);
//                return;
//            }
//            mCoonLoadingDia.cancel();
//            mHandler.removeMessages(TIEM_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void noteTFPulled() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()) {
            try {
                mCoonLoadingDia.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initControlListener() {

    }

    private void clickRemoteRole(int position,boolean showLoading){
        if(position == 0){// football
            ((RemoteHelper) mHelper).doStopPrePlay();

            RemoteHelper.mCurrentType = RemoteRecordOperater.ModelType.FOOTBALL_PLAYER;
            RemoteHelper.mCurrentInfo = RemoteRecordOperater.getInstance(this, FileTools.db_log_cache, FileTools.db_log_name)
                    .getRemoteInfoByModel(RemoteHelper.mCurrentType, false,mAllRemoteRoleDatas.get(position).roleid+"");
            UbtLog.d(TAG,"RemoteHelper.mCurrentInfo->"+RemoteHelper.mCurrentInfo);

            Intent intent = new Intent();
            intent.setClass(this, RemoteActivity.class);
            intent.putExtra(RemoteHelper.REMOTE_ROLE_INFO_PARAM,mAllRemoteRoleDatas.get(position));
            this.startActivity(intent);
            /*((RemoteHelper) mHelper).sendFiles(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER);
            if(showLoading){
                ((LoadingDialog) mCoonLoadingDia).showMessage(RemoteSelActivity.this.getStringResources("ui_remote_initializing"));
            }*/
        }else if(position == 1){// FIGHTER
            ((RemoteHelper) mHelper).doStopPrePlay();

            RemoteHelper.mCurrentType = RemoteRecordOperater.ModelType.FIGHTER;
            RemoteHelper.mCurrentInfo = RemoteRecordOperater.getInstance(this, FileTools.db_log_cache, FileTools.db_log_name)
                    .getRemoteInfoByModel(RemoteHelper.mCurrentType, false,mAllRemoteRoleDatas.get(position).roleid+"");
            UbtLog.d(TAG,"RemoteHelper.mCurrentInfo->"+RemoteHelper.mCurrentInfo);

            Intent intent = new Intent();
            intent.setClass(this, RemoteActivity.class);
            intent.putExtra(RemoteHelper.REMOTE_ROLE_INFO_PARAM,mAllRemoteRoleDatas.get(position));
            this.startActivity(intent);

            /*((RemoteHelper) mHelper).sendFiles(RemoteRecordOperater.ModelType.FIGHTER);
            if(showLoading){
                ((LoadingDialog) mCoonLoadingDia).showMessage(RemoteSelActivity.this.getStringResources("ui_remote_initializing"));
            }*/
        }else if(position == 2){//dancer
            //((RemoteHelper) mHelper).sendFiles(RemoteRecordOperater.ModelType.FIGHTER);
            //((LoadingDialog) mCoonLoadingDia).showMessage(RemoteSelActivity.this.getStringResources("ui_remote_initializing"));
        }else {
            ((RemoteHelper) mHelper).doStopPrePlay();
            RemoteHelper.mCurrentType = RemoteRecordOperater.ModelType.CUSTOM;
            RemoteHelper.mCurrentInfo = RemoteRecordOperater.getInstance(this, FileTools.db_log_cache, FileTools.db_log_name)
                    .getRemoteInfoByModel(RemoteHelper.mCurrentType, false,mAllRemoteRoleDatas.get(position).roleid+"");
            UbtLog.d(TAG,"RemoteHelper.mCurrentInfo->"+RemoteHelper.mCurrentInfo);
            Intent intent = new Intent();
            intent.setClass(this, RemoteActivity.class);
            intent.putExtra(RemoteHelper.REMOTE_ROLE_INFO_PARAM,mAllRemoteRoleDatas.get(position));
            this.startActivity(intent);
        }
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList,
                              ActionInfo action, Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList,
                              Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList,
                                 Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList,
                               Play_type mCurrentPlayType, String hashCode) {

    }

    @Override
    public void notePlayChargingError() {

    }


    @Override
    public void notePlayCycleNext(String action_name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendFileFinish(boolean isSuccess) {
        try {
            mCoonLoadingDia.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSuccess) {
            //跳转
            Intent inte = new Intent();
            inte.setClass(RemoteSelActivity.this, RemoteActivity.class);
            RemoteSelActivity.this.startActivity(inte);
        } else {
            //告知同步失败
            MyAlertDialog.getInstance(this, this.getStringResources("ui_remote_initialize_failed"), this.getStringResources("ui_common_cancel"), this.getStringResources("ui_remote_initialize_retry"), new IMessageListeter() {
                        @Override
                        public void onViewAction(boolean isOk) {
                            ((RemoteHelper) mHelper).sendFiles(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER);
                            ((LoadingDialog) mCoonLoadingDia).showMessage(RemoteSelActivity.this.getStringResources("ui_remote_initializing"));
                        }
                    }
            );
        }
    }

    @Override
    public void onPlayActionFileNotExist() {

    }

    @Override
    public void onSendFileStart() {

    }

    @Override
    public void onReadSettingItem(List<RemoteItem> items) {

    }

    @Override
    public void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles) {
        Message msg = new Message();
        msg.what = READ_REMOTE_ROLE_FINISH;
        msg.obj = mRemoteRoles;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onAddRemoteRole(boolean isSuccess,int roleId) {

    }

    @Override
    public void onUpdateRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onDelRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {
        UbtLog.d(TAG,"lihai-----isSuccess-->"+isSuccess+"---roleInfo->"+roleInfo.roleid);
        Message msg = new Message();
        msg.what = DELETE_REMOTE_ROLE;
        msg.obj = roleInfo;
        Bundle bundle = new Bundle();
        bundle.putBoolean(RemoteHelper.IS_SUCCESS,isSuccess);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onAddRemoteRoleActions(boolean isSuccess,int roleId) {

    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {
        mHandler.sendEmptyMessage(COLSE_HEAD_TITLE);
    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            if(mAllRemoteRoleDatas.size()!=0)
            {
                UbtLog.d(TAG,"lihai---------mOnScrollListener-->");
//                requestMoreData();
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    break;
            }

        }
    };

}

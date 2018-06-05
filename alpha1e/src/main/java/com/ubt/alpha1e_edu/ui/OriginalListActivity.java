package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.adapter.OriginalActionsAdapter;
import com.ubt.alpha1e_edu.business.ActionPlayer;
import com.ubt.alpha1e_edu.data.model.ActionColloInfo;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e_edu.data.model.BannerInfo;
import com.ubt.alpha1e_edu.data.model.CommentInfo;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e_edu.ui.custom.MyLinearLayoutManager;
import com.ubt.alpha1e_edu.ui.custom.TranslucentActionBar;
import com.ubt.alpha1e_edu.ui.custom.TranslucentScrollView;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.helper.ActionBarClickListener;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.IActionsLibUI;
import com.ubt.alpha1e_edu.ui.helper.LoginHelper;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 原创榜单页面
 */
public class OriginalListActivity extends BaseActivity implements IActionsLibUI,ActionBarClickListener, TranslucentScrollView.TranslucentChangedListener,BaseDiaUI {

    private static final String TAG = "OriginalListActivity";

    private List<ActionRecordInfo> mDownLoadHistory = null;
    private RecyclerView mRecyclerview;
    private MyLinearLayoutManager mLinearLayoutManager;
    private OriginalActionsAdapter mAdapter;
    private ImageView imgOriginalListLogo;
    private List<Map<String,Object>> mDatas = new ArrayList<>();
    private ActionsLibHelper mHelper;
    public static final int UPDATE_ITEMS = 0;
    public static final int UPDATE_ALL = 1;
    private LoadingDialog mLoadingDialog;

    private TranslucentScrollView translucentScrollView;
    private TranslucentActionBar actionBar;
    private View zoomView;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    mAdapter.notifyItemChanged((int) msg.obj);
                    break;
                case UPDATE_ALL:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }
        }
    };

    public static void launchActivity(Activity activity)
    {
        Intent intent = new Intent();
        intent.setClass(activity,OriginalListActivity.class);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_list);

        mHelper = new ActionsLibHelper(this,this);

        initUI();
        initRecyclerview();
        initTranslucent();
    }

    private void initRecyclerview(){

        mRecyclerview = (RecyclerView)findViewById(R.id.recyclerview_original_list);
        mLinearLayoutManager = new MyLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mAdapter = new OriginalActionsAdapter(mDatas,this,mHelper);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
        showDialog();

        mHelper.doReadDownLoadHistory();
    }

    public void sendMessage(Object object, int what) {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if (mHandler != null){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(OriginalListActivity.class.getSimpleName());
        mHelper.registerLisenters();
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mHelper!=null)
        {
            mHelper.removeListeners();
            mHelper.UnRegisterHelper();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
        mHelper.DistoryHelper();
    }

    private void initTranslucent() {
        actionBar = (TranslucentActionBar) findViewById(R.id.actionbar);
        //初始actionBar
        actionBar.setData(getStringResources("ui_square_title_create"), 1, null, 0, null, this);

        //开启渐变
        actionBar.setNeedTranslucent();
        //设置状态栏高度
        actionBar.setStatusBarHeight(getStatusBarHeight());

        translucentScrollView = (TranslucentScrollView) findViewById(R.id.pullzoom_scrollview);
        //设置透明度变化监听
        translucentScrollView.setTranslucentChangedListener(this);
        //关联需要渐变的视图
        translucentScrollView.setTransView(actionBar);

        zoomView = findViewById(R.id.lay_header);
        //关联伸缩的视图
        translucentScrollView.setPullZoomView(zoomView);

        //actionBar.tvTitle.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initUI() {
        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);
        imgOriginalListLogo = (ImageView)findViewById(R.id.img_original_list_logo);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onReadActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadActionCommentsFinish(List<CommentInfo> comments) {

    }

    @Override
    public void onActionCommentFinish(boolean is_success) {

    }

    @Override
    public void onActionPraisetFinish(boolean is_success) {

    }

    @Override
    public void onNoteNoUser() {
        Intent inte = new Intent();
        inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
        inte.setClass(OriginalListActivity.this, LoginActivity.class);
        OriginalListActivity.this.startActivity(inte);
    }

    @Override
    public void onGetShareUrl(String string) {

    }

    @Override
    public void onWeiXinShareFinish(Integer obj) {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionInfo(ActionInfo info) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {

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
    public void onStopDownloadFile(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {
        for (int i = 0; i < mDatas.size(); i++) {

            if (((ActionInfo) mDatas.get(i).get(
                    ActionsLibHelper.map_val_action)).actionId == action.actionId) {

                mDatas.get(i).put(
                        ActionsLibHelper.map_val_action_download_progress,
                        progess);
                mAdapter.setDatas(mDatas);
                sendMessage(i, UPDATE_ITEMS);
            }
        }

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (((ActionInfo) mDatas.get(i).get(
                    ActionsLibHelper.map_val_action)).actionId == action.actionId) {
                if (state == FileDownloadListener.State.success) {
                    mDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            ActionsLibHelper.Action_download_state.download_finish);
                    mAdapter.setDatas(mDatas);
                    sendMessage(i, UPDATE_ITEMS);
                } else {
                    mDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            ActionsLibHelper.Action_download_state.not_download);
                }
            }
        }

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {
        mDownLoadHistory = history;
        mHelper.doGetActionsOnLineOriginalListSimple(20);

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions) {

    }

    @Override
    public void onReadPopularActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadThemeRecommondFinish(boolean is_success, String error_msg, List<BannerInfo> themes) {

    }

    @Override
    public void onReadOriginalListActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {
        dismissDialog();
        mDatas = mHelper.loadDatas(actions,mDownLoadHistory);
        UbtLog.d(TAG,"mDatas = " + mDatas.size());
        mAdapter.setDatas(mDatas);
        mHandler.sendEmptyMessage(UPDATE_ALL);
    }

    @Override
    public void onLeftClick() {
        this.finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onTranslucentChanged(int transAlpha) {
        actionBar.tvTitle.setVisibility(transAlpha > 48 ? View.VISIBLE : View.GONE);
        //actionBar.tvTitle.setVisibility(View.VISIBLE);
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

        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!this.isFinishing())
        {
            mLoadingDialog.cancel();
        }
    }
}

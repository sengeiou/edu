package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.adapter.ActionsOnlineAdapter;
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
import com.ubt.alpha1e_edu.utils.SizeUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * banner详情页面
 */
public class BannerDetailActivity extends BaseActivity implements IActionsLibUI,ActionBarClickListener, TranslucentScrollView.TranslucentChangedListener,BaseDiaUI {

    private static final String TAG = "BannerDetailActivity";

    private BannerInfo mBannerInfo;
    private RecyclerView mRecyclerview;
    private LinearLayoutManager mLinearLayoutManager;
    private ActionsOnlineAdapter mActionsOnlineAdapter;
    private ImageView img_banner;
    private List<Map<String,Object>> mDatas = new ArrayList<>();
    private ActionsLibHelper mHelper;
    public static final int UPDATE_ITEMS = 0;
    public static final int UPDATE_ALL = 1;

    private TranslucentScrollView translucentScrollView;
    private TranslucentActionBar actionBar;
    private View zoomView;
    private LoadingDialog mLoadingDialog;

    private List<ActionRecordInfo> mDownLoadHistory = null;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    mActionsOnlineAdapter.notifyItemChanged((int) msg.obj);
                    break;
                case UPDATE_ALL:
                    mActionsOnlineAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity, Parcelable parcelable)
    {
        Intent intent = new Intent();
        intent.setClass(activity,BannerDetailActivity.class);
        intent.putExtra("BannerInfo",parcelable);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_forward);
        mBannerInfo = getIntent().getParcelableExtra("BannerInfo");
        mHelper = new ActionsLibHelper(this,this);

        initTranslucent();
        initUI();
        initRecyclerview();

    }
    public void sendMessage(Object object, int what) {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if (mHandler != null)
            mHandler.sendMessage(msg);
    }

    private void initRecyclerview(){
        if(mBannerInfo.recommendForwardType ==3)
        {
            mRecyclerview = (RecyclerView)findViewById(R.id.banner_recyclerview);
            mLinearLayoutManager = new MyLinearLayoutManager(this,MyLinearLayoutManager.VERTICAL,false);
            mActionsOnlineAdapter = new ActionsOnlineAdapter(mDatas,this,mHelper);
            mRecyclerview.setLayoutManager(mLinearLayoutManager);
            mRecyclerview.setAdapter(mActionsOnlineAdapter);
            mRecyclerview.setHasFixedSize(true);
            RecyclerView.ItemAnimator animator = mRecyclerview.getItemAnimator();
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            }
//            mRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
            mHelper.doReadDownLoadHistory();

        }
    }

    private void initTranslucent() {

        actionBar = (TranslucentActionBar) findViewById(R.id.actionbar);

        //初始actionBar
        actionBar.setData(mBannerInfo.actionTitle, 1, null, 0, null, this);
        //开启渐变
        actionBar.setNeedTranslucent();
        //设置状态栏高度
        actionBar.setStatusBarHeight(getStatusBarHeight());

        translucentScrollView = (TranslucentScrollView) findViewById(R.id.pullzoom_scrollview);
        //设置透明度变化监听
        translucentScrollView.setTranslucentChangedListener(this);
        //关联需要渐变的视图
        translucentScrollView.setTransView(actionBar);

        zoomView = findViewById(R.id.img_banner_detail);
        //关联伸缩的视图
        translucentScrollView.setPullZoomView(zoomView);

        //actionBar.tvTitle.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(BannerDetailActivity.class.getSimpleName());
        mHelper.registerLisenters();
        super.onResume();

        img_banner.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams bannerParams = (LinearLayout.LayoutParams) img_banner.getLayoutParams();
                int height = SizeUtils.getViewHeight(img_banner.getWidth(),160);
                bannerParams.height = height;
                img_banner.setLayoutParams(bannerParams);
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mHelper!=null)
        {
            mHelper.removeListeners();
            mHelper.UnRegisterHelper();
            mHelper.DistoryHelper();
        }
    }

    @Override
    protected void initUI() {
        img_banner = (ImageView)findViewById(R.id.img_banner_detail);
        Glide.with(this).load(mBannerInfo.recommendImage).fitCenter().
                placeholder(R.drawable.action_online_nointroduction_icon)
                .into(img_banner);

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);


    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onReadActionsFinish(boolean isSuccess, String errorInfo, List<ActionInfo> actions) {

        dismissDialog();

        UbtLog.d(TAG,"isSuccess = " + isSuccess + " actions = " + actions.size());
        if (!isSuccess) {
            Toast.makeText(this, getStringResources(errorInfo), Toast.LENGTH_SHORT).show();
        }else if(actions.size() > 0){
            mDatas = mHelper.loadDatas(actions,mDownLoadHistory);
            mActionsOnlineAdapter.setDatas(mDatas);
            mHandler.sendEmptyMessage(UPDATE_ALL);
        }
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
        inte.setClass(BannerDetailActivity.this, LoginActivity.class);
        BannerDetailActivity.this.startActivity(inte);
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
        UbtLog.d(TAG,"onReportProgress--");
        for (int i = 0; i < mDatas.size(); i++) {

            if (((ActionInfo) mDatas.get(i).get(
                    ActionsLibHelper.map_val_action)).actionId == action.actionId) {

                mDatas.get(i).put(
                        ActionsLibHelper.map_val_action_download_progress,
                        progess);
                mActionsOnlineAdapter.setDatas(mDatas);
                sendMessage(i, UPDATE_ITEMS);
            }
        }

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {
        UbtLog.d(TAG,"onDownLoadFileFinish--");
        for (int i = 0; i < mDatas.size(); i++) {
            if (((ActionInfo) mDatas.get(i).get(
                    ActionsLibHelper.map_val_action)).actionId == action.actionId) {
                if (state == FileDownloadListener.State.success) {
                    mDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            ActionsLibHelper.Action_download_state.download_finish);
                    ActionInfo actionInfo = (ActionInfo)mDatas.get(i).get(ActionsLibHelper.map_val_action);
                    actionInfo.actionDownloadTime++;
                } else {
                    mDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            ActionsLibHelper.Action_download_state.not_download);
                }
                mActionsOnlineAdapter.setDatas(mDatas);
                sendMessage(i, UPDATE_ITEMS);
            }
        }

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

        mDownLoadHistory = history;
        if(mActionsOnlineAdapter==null){
            return;
        }

        //有带动作过来，直接显示，兼容之前的逻辑，V3.0版本后，直接进来后，再查询显示
        if(mBannerInfo.clickForward != null && mBannerInfo.clickForward.size() > 0){
            List<ActionInfo> mActions = mBannerInfo.clickForward;
            mDatas = mHelper.loadDatas(mActions,history);
            mActionsOnlineAdapter.setDatas(mDatas);
            mHandler.sendEmptyMessage(UPDATE_ALL);
        }else {
            mHelper.doGetThemeRecommendDetail(mBannerInfo.recommendId);
        }
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
}

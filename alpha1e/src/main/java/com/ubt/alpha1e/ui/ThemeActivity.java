package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.ThemeAdapter;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.EndlessRecyclerOnScrollListener;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.BannerInfo;
import com.ubt.alpha1e.data.model.CommentInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.ui.custom.MyLinearLayoutManager;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.IActionsLibUI;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/***
 * 主题页面
 */
public class ThemeActivity extends BaseActivity implements IActionsLibUI{

    private static final String TAG = "ThemeActivity";

    private MyLinearLayoutManager mLayoutManager;
    private RecyclerView mThemeRecyclerview;
    private ThemeAdapter mThemeAdapter;
    private ActionsLibHelper mHelper;
    public static final int UPDATE_ITEMS = 0;
    public static final int UPDATE_ALL = 1;

    private List<BannerInfo> themeRecommendDatas = new ArrayList<>();

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ALL:
                    mThemeAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity)
    {
        Intent intent = new Intent();
        intent.setClass(activity,ThemeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_theme);
        mHelper = new ActionsLibHelper(this,this);

        initUI();
        initRecyclerViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentActivityLable(ThemeActivity.class.getSimpleName());
        mHelper.registerLisenters();
        mHelper.doGetThemeRecommendSimple();
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

    public void setActionsThemeRecommend(List<BannerInfo> themes) {

        themeRecommendDatas.clear();
        for (BannerInfo theme : themes){
            if(!TextUtils.isEmpty(theme.recommendUrl)){
                try {
                    JSONObject recommendUrlObj = new JSONObject(theme.recommendUrl);
                    JSONArray jsonArr = null;
                    String systemLanguage = getStandardLocale(getAppCurrentLanguage());
                    if (systemLanguage.equals("CN")) {
                        jsonArr = recommendUrlObj.getJSONArray("CN");
                    } else {
                        jsonArr = recommendUrlObj.getJSONArray("EN");
                    }
                    theme.actionTitle = jsonArr.getJSONObject(0).getString("actionTitle");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            themeRecommendDatas.add(theme);

            UbtLog.d(TAG,"bannerInfo recommendUrl == " + theme.recommendUrl + " recommendImage = " + theme.recommendImage + "     actionTitle = " + theme.actionTitle);
        }

        mThemeAdapter.setDatas(themeRecommendDatas);
        mThemeAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化原创榜单列表
     */
    private void initRecyclerViews() {
        mThemeRecyclerview = (RecyclerView) findViewById(R.id.recyclerview_theme);
        mLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mThemeAdapter = new ThemeAdapter(themeRecommendDatas, this);
        mThemeRecyclerview.setLayoutManager(mLayoutManager);
        mThemeRecyclerview.setAdapter(mThemeAdapter);
        mThemeRecyclerview.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mThemeRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mThemeRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
        mThemeRecyclerview.addOnScrollListener(mOnScrollListener);
    }

    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            if(themeRecommendDatas.size()!=0)
            {
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

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_square_topic_all"));
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
    public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions) {

    }

    @Override
    public void onReadPopularActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadThemeRecommondFinish(boolean is_success, String error_msg, List<BannerInfo> themes) {

        setActionsThemeRecommend(themes);
    }

    @Override
    public void onReadOriginalListActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

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

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {

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
    public void onNoteDataChaged(Bitmap img, long id) {

    }
}

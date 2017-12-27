package com.ubt.alpha1e.behaviorhabits.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.PlayContentRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PlayContentSelectFragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {

    private static final String TAG = PlayContentSelectFragment.class.getSimpleName();

    public static final int CLICK_SELECT = 1;

    Unbinder unbinder;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_play_content)
    RecyclerView rvPlayContent;

    public PlayContentRecyclerAdapter mAdapter;
    private List<PlayContentInfo> mPlayContentInfoDatas = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLICK_SELECT:
                    //切换开关
                    PlayContentInfo playContentInfo = mPlayContentInfoDatas.get(msg.arg1);
                    if("1".equals(playContentInfo.isSelect)){
                        playContentInfo.isSelect = "0";
                    }else {
                        playContentInfo.isSelect = "1";
                    }
                    mAdapter.notifyItemChanged(msg.arg1);
                    break;

            }
        }
    };

    public static PlayContentSelectFragment newInstance(){
        PlayContentSelectFragment selectFragment = new PlayContentSelectFragment();
        Bundle bundle = new Bundle();
        selectFragment.setArguments(bundle);
        return selectFragment;
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringRes("ui_habits_play_content_select"));
        ivTitleRight.setVisibility(View.VISIBLE);

        mPlayContentInfoDatas = new ArrayList<>();

        PlayContentInfo h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "小学一年级语文课";
        h.isSelect = "1";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "小学二年级语文课";
        h.isSelect = "1";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "小学三年级语文课";
        h.isSelect = "0";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "小学一年级语文课";
        h.isSelect = "0";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "宋词";
        h.isSelect = "1";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "儿歌三百首";
        h.isSelect = "1";
        mPlayContentInfoDatas.add(h);

        h = new PlayContentInfo();
        h.contentId = "1";
        h.contentName = "全唐诗";
        h.isSelect = "0";
        mPlayContentInfoDatas.add(h);

        initRecyclerViews();
    }

    public void initRecyclerViews() {
        UbtLog.d(TAG, "rvHabitsEvent => " + rvPlayContent);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);

        rvPlayContent.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemAnimator animator = rvPlayContent.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        rvPlayContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 36;
                outRect.left = 18;
                outRect.right = 18;
            }
        });

        mAdapter = new PlayContentRecyclerAdapter(getContext(), mPlayContentInfoDatas, mHandler);
        rvPlayContent.setAdapter(mAdapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_hibits_event_select;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onTest(boolean isSuccess) {
        UbtLog.d("Test1Fragment", "isSuccess = " + isSuccess);
    }

    @Override
    public void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

    }

    @Override
    public void showParentBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

    }

    @Override
    public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {

    }

    @Override
    public void showBehaviourPlayContent(boolean status, List<PlayContentInfo> playList, String errorMsg) {

    }

    @Override
    public void showNetworkRequestError() {

    }

    @Override
    public void onAlertSelectItem(int index, String language, int alertType) {

    }


    @OnClick({R.id.ll_base_back, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                pop();
                break;
            case R.id.iv_title_right:
                doConfirm();
                break;

        }
    }

    private void doConfirm(){
        ArrayList<Parcelable> selectList = new ArrayList<>();
        for(PlayContentInfo playContentInfo : mPlayContentInfoDatas){
            if("1".equals(playContentInfo.isSelect)){
                selectList.add(PG.convertParcelable(playContentInfo));
            }
        }
        UbtLog.d(TAG,"selectList = " + selectList.size());

        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList(Constant.PLAY_CONTENT_INFO_LIST_KEY, selectList);
        setFragmentResult(Constant.PLAY_CONTENT_SELECT_RESPONSE_CODE, resultBundle);
        pop();

    }
}

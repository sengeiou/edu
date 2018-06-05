package com.ubt.alpha1e_edu.behaviorhabits.fragment;


import android.app.Dialog;
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
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.ToastUtils;
import com.ubt.alpha1e_edu.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e_edu.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e_edu.behaviorhabits.adapter.PlayContentRecyclerAdapter;
import com.ubt.alpha1e_edu.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e_edu.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e_edu.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e_edu.behaviorhabits.model.UserScore;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e_edu.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

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

    private static final int UPDATE_UI_DATA = 2;

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
    private EventDetail<List<PlayContentInfo>> mEventDetail = null;
    protected Dialog mCoonLoadingDia;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_UI_DATA:
                    mCoonLoadingDia.cancel();
                    List<PlayContentInfo> playContentInfoList = (List<PlayContentInfo>)msg.obj;
                    List<String> hasSelectIdList = mEventDetail.contentIds;
                    for(PlayContentInfo playContentInfo1 :  playContentInfoList){
                        playContentInfo1.isSelect = "0";
                        for(String contentId :  hasSelectIdList){
                            if(playContentInfo1.contentId.equals(contentId)){
                                playContentInfo1.isSelect = "1";
                                break;
                            }
                        }
                    }
                    mPlayContentInfoDatas.clear();
                    mPlayContentInfoDatas.addAll(playContentInfoList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static PlayContentSelectFragment newInstance(EventDetail eventDetail){
        PlayContentSelectFragment selectFragment = new PlayContentSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.HIBITS_EVENT_DETAIL, PG.convertParcelable(eventDetail));
        selectFragment.setArguments(bundle);
        return selectFragment;
    }

    @Override
    protected void initUI() {
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());

        tvBaseTitleName.setText(getStringRes("ui_habits_play_content_select"));
        ivTitleRight.setVisibility(View.VISIBLE);

        mPlayContentInfoDatas = new ArrayList<>();

        initRecyclerViews();

        mCoonLoadingDia.show();
        mPresenter.getBehaviourPlayContent(mEventDetail.eventId);
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
        if (getArguments() != null) {
            mEventDetail = getArguments().getParcelable(Constant.HIBITS_EVENT_DETAIL);
        }

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
    public void onAlertSelectItem(int index, String language, int alertType) {

    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCoonLoadingDia.cancel();
                ToastUtils.showShort(getStringRes("ui_common_network_request_failed"));
            }
        });
    }

    @Override
    public void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg) {
        if(status){
            Message msg = new Message();
            msg.what = UPDATE_UI_DATA;
            msg.obj = playList;
            mHandler.sendMessage(msg);
        }else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCoonLoadingDia.cancel();
                    ToastUtils.showShort(getStringRes("ui_common_network_request_failed"));
                }
            });
        }
    }

    @Override
    public void onUserPassword(String password) {

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

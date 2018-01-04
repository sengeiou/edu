package com.ubt.alpha1e.behaviorhabits.fragment;


import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.HabitsEventRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.Md5;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.CircleBar;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.HibitsAlertDialog;
import com.ubt.alpha1e.ui.dialog.InputPasswordDialog;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.dialog.SetPasswordDialog;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManageActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HibitsEventFragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {

    private static final String TAG = HibitsEventFragment.class.getSimpleName();

    private static final int UPDATE_UI_DATA = 1;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Unbinder unbinder;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_habits_event)
    RecyclerView rvHabitsEvent;
    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.cb_score)
    CircleBar cbScore;
    @BindView(R.id.tv_ratio)
    TextView tvRatio;
    @BindView(R.id.tv_score)
    TextView tvScore;

    public HabitsEventRecyclerAdapter mAdapter;
    private List<HabitsEvent> mHabitsEventInfoDatas = null;
    private String mUserPassword = null;

    protected Dialog mCoonLoadingDia;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_UI_DATA:
                    UserScore<List<HabitsEvent>> userScore = (UserScore<List<HabitsEvent>>) msg.obj;
                    if(userScore != null){
                        tvRatio.setText(getStringRes("ui_habits_has_finish") + userScore.percent+"%");
                        tvScore.setText(userScore.totalScore);
                        cbScore.setSweepAngle((Float.parseFloat(userScore.percent)*250)/100);

                        List<HabitsEvent> habitsEventList = userScore.details;
                        mHabitsEventInfoDatas.clear();
                        mHabitsEventInfoDatas.addAll(habitsEventList);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    public static HibitsEventFragment newInstance() {
        HibitsEventFragment hibitsEventFragment = new HibitsEventFragment();
        return hibitsEventFragment;
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--");
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());
        tvBaseTitleName.setText(getStringRes("ui_habits_alert_event"));
        ivTitleRight.setBackgroundResource(R.drawable.icon_habits_parentscentral);
        ivTitleRight.setVisibility(View.VISIBLE);

        tvToday.setText(sdf.format(new Date()));
        tvRatio.setText(getStringRes("ui_habits_has_finish") + "0%");
        tvScore.setText("0");
        cbScore.setSweepAngle(0f);

        mHabitsEventInfoDatas = new ArrayList<>();
        initRecyclerViews();

        UserModel userModel = (UserModel) SPUtils.getInstance().readObject(com.ubt.alpha1e.base.Constant.SP_USER_INFO);
        UbtLog.d(TAG,"userModel = " + userModel.getSex() + "    " + userModel.getGrade());

        mPresenter.getUserPassword();
        mPresenter.getBehaviourList(userModel.getSex(), "1");
    }

    public void initRecyclerViews() {
        UbtLog.d(TAG, "rvHabitsEvent = " + rvHabitsEvent);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvHabitsEvent.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvHabitsEvent.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        rvHabitsEvent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 36;
            }
        });

        mAdapter = new HabitsEventRecyclerAdapter(getContext(), mHabitsEventInfoDatas, true, mHandler);
        rvHabitsEvent.setAdapter(mAdapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_hibits_event;
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
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        UbtLog.d(TAG, "requestCode = " + requestCode);
        if (requestCode == Constant.HIBITS_EVENT_EDIT_REQUEST_CODE && resultCode == Constant.HIBITS_EVENT_EDIT_RESPONSE_CODE) {
            UbtLog.d(TAG, "resultCode = " + resultCode + "   " + data.getParcelable(Constant.HABITS_EVENT_INFO_KEY));
        }

    }

    @Override
    public void onTest(boolean isSuccess) {
        UbtLog.d("Test1Fragment", "isSuccess = " + isSuccess);
    }

    @Override
    public void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {
        if(status){

            Message msg = new Message();
            msg.what = UPDATE_UI_DATA;
            msg.obj = userScore;
            mHandler.sendMessage(msg);
        }else {
            ToastUtils.showShort(errorMsg);
        }
    }

    @Override
    public void showParentBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

    }

    @Override
    public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {

    }

    @Override
    public void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg) {

    }

    @Override
    public void onUserPassword(String password) {
        mUserPassword = password;
        UbtLog.d(TAG,"mUserPassword = " + mUserPassword);
    }



    @Override
    public void onAlertSelectItem(int index, String language, int alertType) {

    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {
        if(requestType == mPresenter.SET_USER_PASSWORD){
            if(errorCode == mPresenter.NETWORK_SUCCESS){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCoonLoadingDia.cancel();
                        ToastUtils.showShort(getStringRes("ui_setting_password_modify_success"));
                    }
                });
            }else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserPassword = "";
                        mCoonLoadingDia.cancel();
                        ToastUtils.showShort(getStringRes("ui_setting_password_modify_fail"));
                    }
                });
            }
        }else {

        }
    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                getActivity().finish();
                break;
            case R.id.iv_title_right:
                enterParentManageCenter();
                break;
        }
    }

    /**
     * 进入家长管理中心
     */
    private void enterParentManageCenter() {
        if(TextUtils.isEmpty(mUserPassword)){
            new SetPasswordDialog(getContext()).builder()
                    .setCancelable(true)
                    .setCallbackListener(new SetPasswordDialog.ISetPasswordListener() {
                        @Override
                        public void onSetPassword(String password) {
                            mUserPassword = Md5.getMD5(password).toLowerCase();
                            mCoonLoadingDia.show();
                            mPresenter.doSetUserPassword(password);
                        }
                    })
                    .show();
            /*new HibitsAlertDialog(getContext()).builder()
                    .setCancelable(true)
                    .setEventId("182")
                    .setMsg("起床时间将在07:00开启")
                    .show();*/
        }else {
            new InputPasswordDialog(getContext()).builder()
                    .setMsg(getStringRes("ui_habits_password_input_tip"))
                    .setCancelable(true)
                    .setPassword(mUserPassword)
                    .setCallbackListener(new InputPasswordDialog.IInputPasswordListener() {
                        @Override
                        public void onCorrectPassword() {
                            startForResult(ParentManageCenterFragment.newInstance(), 0);
                        }

                        @Override
                        public void onFindPassword() {
                            PsdManageActivity.LaunchActivity(getContext(),true);
                        }
                    })
                    .show();

        }

    }

}

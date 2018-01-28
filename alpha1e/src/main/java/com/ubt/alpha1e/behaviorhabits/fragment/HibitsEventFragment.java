package com.ubt.alpha1e.behaviorhabits.fragment;


import android.app.Dialog;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.HabitsEventRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.helper.HabitsHelper;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.Md5;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.CircleBar;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.HibitsEventPlayDialog;
import com.ubt.alpha1e.ui.dialog.InputPasswordDialog;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.dialog.SetPasswordDialog;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManageActivity;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
    public static final int SHOW_PLAY_CONTROL = 2;
    public static final int DEAL_PLAY_STATUS = 3;
    public static final int REFRESH_REQUEST_DATA = 4;

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
    @BindView(R.id.rl_content_right)
    RelativeLayout rlContentRight;
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
    private HibitsEventPlayDialog mHibitsEventPlayDialog = null;
    private HabitsHelper mHelper = null;
    private boolean needRefreshPassword = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_UI_DATA:
                    mCoonLoadingDia.cancel();
                    rlContentRight.setVisibility(View.VISIBLE);
                    UserScore<List<HabitsEvent<List<PlayContentInfo>>>> userScore = (UserScore<List<HabitsEvent<List<PlayContentInfo>>>>) msg.obj;
                    if(userScore != null){
                        UbtLog.d(TAG,"userScore = " + userScore + "     = " + userScore.totalScore);
                        tvRatio.setText(getStringRes("ui_habits_has_finish") + userScore.percent+"%");
                        tvScore.setText(userScore.totalScore);
                        cbScore.setSweepAngle((Float.parseFloat(userScore.percent)*250)/100);

                        List<HabitsEvent<List<PlayContentInfo>>> habitsEventList = userScore.details;
                        mHabitsEventInfoDatas.clear();
                        mHabitsEventInfoDatas.addAll(habitsEventList);
                        mAdapter.notifyDataSetChanged();

                        if(isBulueToothConnected()){
                            mHelper.readPlayStatus();
                        }
                    }
                    break;
                case SHOW_PLAY_CONTROL:
                    HabitsEvent<List<PlayContentInfo>> habitsEvent = mHabitsEventInfoDatas.get(msg.arg1);
                    UbtLog.d(TAG,"habitsEvent = " + habitsEvent.eventName + "   contents = " + habitsEvent.contents);

                    /*if(habitsEvent.contents != null && habitsEvent.contents.size() > 0){
                        List<PlayContentInfo> playContentInfoList = habitsEvent.contents;
                    }*/

                    if(isBulueToothConnected()){
                        showPlayEventDialog(habitsEvent.contents,habitsEvent.eventId);
                    }else {
                        showBluetoothConnectDialog();
                    }

                    break;
                case DEAL_PLAY_STATUS:
                    EventPlayStatus eventPlayStatus = (EventPlayStatus) msg.obj;
                    UbtLog.d(TAG,"eventPlayStatus = " + eventPlayStatus);
                    if(eventPlayStatus != null && mHabitsEventInfoDatas != null){
                        if(StringUtils.isStringNumber(eventPlayStatus.playAudioSeq)){
                            int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
                            if(seqNo >= 0){
                                for(HabitsEvent<List<PlayContentInfo>> event : mHabitsEventInfoDatas){
                                    if(event.eventId.equals(eventPlayStatus.eventId)){

                                        showPlayEventDialog(event.contents, event.eventId);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case REFRESH_REQUEST_DATA:
                    UserModel userModel = (UserModel) SPUtils.getInstance().readObject(com.ubt.alpha1e.base.Constant.SP_USER_INFO);
                    UbtLog.d(TAG,"userModel = " + userModel.getSex() + "    " + userModel.getGrade() + "/" + userModel.getGradeByType());
                    mCoonLoadingDia.show();
                    mPresenter.getBehaviourList(userModel.getSex(), userModel.getGradeByType());
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

        mHandler.sendEmptyMessage(REFRESH_REQUEST_DATA);
    }

    private void readData(){

        if(needRefreshPassword){
            needRefreshPassword = false;
            mPresenter.getUserPassword();
        }
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

    /**
     * 选择播放事项
     */
    private void showPlayEventDialog(List<PlayContentInfo> playContentInfoList,String eventId){
        if(mHibitsEventPlayDialog == null){
            mHibitsEventPlayDialog = new HibitsEventPlayDialog(getActivity())
                    .builder()
                    .setCancelable(true)
                    .setPlayContent(playContentInfoList)
                    .setCurrentEventId(eventId)
                    .setCallbackListener(new HibitsEventPlayDialog.IHibitsEventPlayListener() {
                        @Override
                        public void onDismissCallback() {
                            mHibitsEventPlayDialog = null;
                        }
                    });
        }

        mHibitsEventPlayDialog.show();
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog(){
        new ConfirmDialog(getContext()).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙和Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        gotoConnectBluetooth();
                    }
                }).show();
    }

    //去连接蓝牙
    void gotoConnectBluetooth(){
        boolean isfirst = SPUtils.getInstance().getBoolean("firstBluetoothConnect", true);
        Intent bluetoothConnectIntent = new Intent();
        if (isfirst) {
            UbtLog.d(TAG, "第一次蓝牙连接");
            SPUtils.getInstance().put("firstBluetoothConnect", false);
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothguidestartrobotActivity.class);
        } else {
            UbtLog.d(TAG, "非第一次蓝牙连接 ");
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothandnetconnectstateActivity.class);
        }
        startActivityForResult(bluetoothConnectIntent, 100);
    }

    @Override
    public void onResume() {
        super.onResume();
        UbtLog.d(TAG,"-onResume->>");
        EventBus.getDefault().register(this);

        if(mHelper != null){
            mHelper.RegisterHelper();
        }

        readData();
    }

    @Override
    public void onPause() {
        super.onPause();
        UbtLog.d(TAG,"-onPause-");
        if(mHelper != null){
            mHelper.UnRegisterHelper();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventHibits(HibitsEvent event) {
        if(event.getEvent() == HibitsEvent.Event.READ_EVENT_PLAY_STATUS && mHibitsEventPlayDialog == null){
            UbtLog.d(TAG,"EventPlayStatus = " + event.getEventPlayStatus() + "/"+ getTopFragment() + "  = " +(getTopFragment() instanceof HibitsEventFragment));
            //当前Fragment才需要弹
            if(getTopFragment() != null && (getTopFragment() instanceof HibitsEventFragment)){
                EventPlayStatus eventPlayStatus = event.getEventPlayStatus();
                Message msg = new Message();
                msg.what = DEAL_PLAY_STATUS;
                msg.obj = eventPlayStatus;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mHelper != null){
            mHelper.DistoryHelper();
        }
        super.onDestroy();
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
        mHelper = new HabitsHelper(getContext());
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
        if (requestCode == Constant.HIBITS_PARENT_CENTER_REQUEST_CODE) {
            mHandler.sendEmptyMessage(REFRESH_REQUEST_DATA);
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
                        ToastUtils.showShort(getStringRes("ui_setting_password_setting_success"));
                        startForResult(ParentManageCenterFragment.newInstance(), Constant.HIBITS_PARENT_CENTER_REQUEST_CODE);
                    }
                });
            }else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserPassword = "";
                        mCoonLoadingDia.cancel();
                        ToastUtils.showShort(getStringRes("ui_setting_password_setting_fail"));
                    }
                });
            }
        }else if(requestType == mPresenter.GET_BEHAVIOURLIST_CMD){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCoonLoadingDia.cancel();
                    ToastUtils.showShort(getStringRes("ui_common_network_request_failed"));
                }
            });
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

            //startForResult(ParentManageCenterFragment.newInstance(), Constant.HIBITS_PARENT_CENTER_REQUEST_CODE);

            new InputPasswordDialog(getContext()).builder()
                    .setMsg(getStringRes("ui_habits_password_input_tip"))
                    .setCancelable(true)
                    .setPassword(mUserPassword)
                    .setCallbackListener(new InputPasswordDialog.IInputPasswordListener() {
                        @Override
                        public void onCorrectPassword() {
                            startForResult(ParentManageCenterFragment.newInstance(), Constant.HIBITS_PARENT_CENTER_REQUEST_CODE);
                        }

                        @Override
                        public void onFindPassword() {
                            PsdManageActivity.LaunchActivity(getActivity(),true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    needRefreshPassword = true;
                                }
                            }, 200);
                        }
                    })
                    .show();

        }

    }

}

package com.ubt.alpha1e.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.MessageRecordManager;
import com.ubt.alpha1e.business.MessageRecordManagerListener;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.MessageInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.LoginActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.MyDynamicActivity;
import com.ubt.alpha1e.ui.MyMainActivity;
import com.ubt.alpha1e.ui.PrivateInfoShowActivity;
import com.ubt.alpha1e.ui.SettingActivity;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 重写原有的ActionsLibMainFragment类
 */
public class MyFragment extends BaseFragment implements IActionsUI,MessageRecordManagerListener,BaseDiaUI {
    private static final String TAG = "MyFragment";
    private View mMainView;
    public static final int UPDATE_ITEMS = 0;

    public MyMainActivity activity;
    private RelativeLayout rlMyInfo,rlReceiveMsg, rlCreatedActions, rlDownloadActions, rlCollectedActions, rlSetting;
    private TextView tvName, tvCreatedNum, tvDownloadNum, tvCollectedNum,tvReceiveNum;
    private ImageView ivPhoto,ivSettingState;
    private ImageView ivPoint/*, ivTabPoint*/;
    private ImageView ivArrow;

    private MyActionsHelper myActionsHelper;
    private MainHelper mMainHelper;
    private MyMainActivity mActivity;

    private List<MessageInfo> noticeList = new ArrayList<>();
    private RelativeLayout rlMyDynamic;
    private int noReadMsgNum = 0;

    private static final int SHOW_NEW_MESSAGE = 1;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    break;
                case SHOW_NEW_MESSAGE:
                    if(noReadMsgNum > 0){
                        ivPoint.setVisibility(View.VISIBLE);
                        if(noReadMsgNum > 10){
                            tvReceiveNum.setText("10+");
                        }else {
                            tvReceiveNum.setText(noReadMsgNum+"");
                        }
                    }else {
                        ivPoint.setVisibility(View.GONE);
                        tvReceiveNum.setText("");
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public MyFragment() {

    }

    @SuppressLint("ValidFragment")
    public MyFragment(BaseActivity mBaseActivity,MainHelper mainHelper) {
        UbtLog.d(TAG, "new MyFragment");
        mActivity = (MyMainActivity) mBaseActivity;
        myActionsHelper = MyActionsHelper.getInstance(mBaseActivity);
        mMainHelper = mainHelper;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MyMainActivity) getActivity();
        mMainView = inflater.inflate(R.layout.fragment_my, null);

        initUI();
        initControlListener();
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(checkLoginState()){
            /*if(!mMainHelper.clickedMessage()){
                ivPoint.setVisibility(View.VISIBLE);
            }else{
                ivPoint.setVisibility(View.GONE);
            }*/

            ivPoint.setVisibility(View.GONE);
            tvReceiveNum.setText("");

            noReadMsgNum = 0;
            //读取收到的通知
            mMainHelper.doCheckIsAnyUnReadMsgByType(this, MessageRecordManager.TYPE_NOTICE,10);
        }else {
            ivPoint.setVisibility(View.GONE);
            tvReceiveNum.setText("");
        }

        if (mMainHelper != null && !mMainHelper.isFirstUseSetting()) {
            ivSettingState.setVisibility(View.INVISIBLE);
        }

        registerListeners();

        if(myActionsHelper != null){
            if(checkLoginState()){
                myActionsHelper.doReadMyNewAction();
                myActionsHelper.doReadMyDownLoadActions();
                myActionsHelper.doReadCollcationRecord();
            }else{
                tvCollectedNum.setText("");
                tvCreatedNum.setText("");
                tvDownloadNum.setText("");
                ivPhoto.setImageResource(R.drawable.default_head);
            }
        }

        if(mMainHelper != null){
            //读取机器人电量
            mMainHelper.RegisterHelper();
            mMainHelper.doReadState();
            mMainHelper.doReadUser();

            UbtLog.d(TAG, "UserInfo=" + mMainHelper.getCurrentUser());
            if(mMainHelper.getCurrentUser() != null){
                Glide.with(this).load(mMainHelper.getCurrentUser().userImage).into(ivPhoto);
                mMainHelper.readUserHead(ivPhoto.getHeight(),ivPhoto.getWidth());
                tvName.setText(mMainHelper.getCurrentUser().userName);
                tvName.setTextColor(getResources().getColor(R.color.text_user_name_color));
                ivArrow.setVisibility(View.VISIBLE);
            }else{
                tvName.setText(mActivity.getStringResources("ui_leftmenu_click_to_login"));
                tvName.setTextColor(Color.RED);
                ivArrow.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void registerListeners(){
        if(myActionsHelper != null){
            myActionsHelper.registerListeners(this);
            myActionsHelper.setManagerListeners(mActivity);
        }
    }

    public void unRegisterListeners(){
        if(myActionsHelper != null) {
            myActionsHelper.unRegisterListeners(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterListeners();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
        rlMyInfo = (RelativeLayout) mMainView.findViewById(R.id.rl_my_info);
        rlReceiveMsg = (RelativeLayout) mMainView.findViewById(R.id.rl_receive_msg);
        rlCreatedActions = (RelativeLayout) mMainView.findViewById(R.id.rl_created_action);
        rlDownloadActions = (RelativeLayout) mMainView.findViewById(R.id.rl_download_action);
        rlCollectedActions = (RelativeLayout) mMainView.findViewById(R.id.rl_collected_action);
        rlSetting = (RelativeLayout) mMainView.findViewById(R.id.rl_setting);
        rlMyDynamic = (RelativeLayout) mMainView.findViewById(R.id.rl_my_dynamic);

        tvName = (TextView) mMainView.findViewById(R.id.tv_name);
        tvCreatedNum = (TextView) mMainView.findViewById(R.id.tv_created_num);
        tvDownloadNum = (TextView) mMainView.findViewById(R.id.tv_download_num);
        tvCollectedNum = (TextView) mMainView.findViewById(R.id.tv_collected_num);
        tvReceiveNum = (TextView) mMainView.findViewById(R.id.tv_receive_num);
        ivPhoto = (ImageView) mMainView.findViewById(R.id.iv_photo);
        ivSettingState = (ImageView) mMainView.findViewById(R.id.iv_setting_state);
        ivPoint = (ImageView) mMainView.findViewById(R.id.iv_point);
        //ivTabPoint = (ImageView) mMainView.findViewById(R.id.iv_message_state);

        ivArrow = (ImageView) mMainView.findViewById(R.id.iv_right_arrow);
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
    protected void initControlListener() {
        // TODO Auto-generated method stub
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.rl_my_info:
                        if (mMainHelper.getCurrentUser() != null) {
                            startActivity(new Intent().setClass(
                                    mActivity, PrivateInfoShowActivity.class));
                        } else {
                            Intent inte = new Intent();
                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                            inte.setClass(mActivity, LoginActivity.class);
                            mActivity.startActivity(inte);
                        }

                        break;
                    case R.id.rl_receive_msg:
                        UbtLog.d(TAG," chengchangyin  enter buleteeth " );

                        Intent i = new Intent();
                        i.setClass(mActivity, BluetoothandnetconnectstateActivity.class);
                        mActivity.startActivity(i);
                        mActivity.overridePendingTransition(R.anim.activity_open_up_down,R.anim.activity_close_down_up);


//                        Intent i = new Intent();
//                        i.setClass(mActivity, BluetoothguidestartrobotActivity.class);
//                        mActivity.startActivity(i);
//                        if (mMainHelper.getCurrentUser() == null) {
//                            Intent inte = new Intent();
//                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
//                            inte.setClass(mActivity, LoginActivity.class);
//                            mActivity.startActivity(inte);
//                        } else {
//                            mMainHelper.doClickedMessage();
//
//                            Intent intent = new Intent();
//                            intent.setClass(mActivity, MessageActivity.class);
//                            startActivity(intent);
//                        }
                        break;
                    case R.id.rl_created_action:
//                        if (mMainHelper.getCurrentUser() == null) {
//                            Intent inte = new Intent();
//                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
//                            inte.setClass(mActivity, LoginActivity.class);
//                            mActivity.startActivity(inte);
//                        } else {
//                            if(isBulueToothConnected()){
//                                if(BaseHelper.hasSdcard){
//                                    MyActionsActivity.launchActivity(mActivity, 3);
//                                }else{
//                                    showToast("ui_remote_synchoronize_no_sd");
//                                }
//                            }else{
//                                MyActionsActivity.launchActivity(mActivity, 3);
//                            }
//                        }
                        Intent it = new Intent();
                        it.setClass(mActivity, BluetoothguidestartrobotActivity.class);
                        mActivity.startActivity(it);
                        break;
                    case R.id.rl_download_action:
                        if (mMainHelper.getCurrentUser() == null) {
                            Intent inte = new Intent();
                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                            inte.setClass(mActivity, LoginActivity.class);
                            mActivity.startActivity(inte);
                        } else {
                            if(isBulueToothConnected()){
                                if(BaseHelper.hasSdcard){
                                    MyActionsActivity.launchActivity(mActivity, 1);
                                }else{
                                    showToast("ui_remote_synchoronize_no_sd");
                                }
                            }else{
                                MyActionsActivity.launchActivity(mActivity, 1);
                            }
                        }
                        break;
                    case R.id.rl_collected_action:
                        if (mMainHelper.getCurrentUser() == null) {
                            Intent inte = new Intent();
                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                            inte.setClass(mActivity, LoginActivity.class);
                            mActivity.startActivity(inte);
                        } else {
                            MyActionsActivity.launchActivity(mActivity, 2);
                        }
                        break;
                    case R.id.rl_setting:
                        ivSettingState.setVisibility(View.INVISIBLE);
                        mMainHelper.setFirstUsedSetting();
                        Intent settingintent = new Intent();
                        settingintent.setClass(mActivity, SettingActivity.class);
                        startActivity(settingintent);
                        break;
                    case R.id.rl_my_dynamic:
                        if (mMainHelper.getCurrentUser() == null) {
                            Intent inte = new Intent();
                            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                            inte.setClass(mActivity, LoginActivity.class);
                            mActivity.startActivity(inte);
                        } else {
                            MyDynamicActivity.launchActivity(mActivity);
                        }
                        break;
                }
            }
        };

        rlMyInfo.setOnClickListener(onClickListener);
        rlReceiveMsg.setOnClickListener(onClickListener);
        rlCreatedActions.setOnClickListener(onClickListener);
        rlDownloadActions.setOnClickListener(onClickListener);
        rlCollectedActions.setOnClickListener(onClickListener);
        rlSetting.setOnClickListener(onClickListener);
        rlMyDynamic.setOnClickListener(onClickListener);

    }

    public void showToast(String str) {
        Toast.makeText(mActivity.getApplicationContext(), mActivity.getStringResources(str), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub
    }



    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub
    }

    public boolean checkLoginState()
    {
        UserInfo userInfo  = ((AlphaApplication)getActivity().getApplication()).getCurrentUserInfo();
        return userInfo == null?false:true;
    }


    @Override
    public void onAddNoReadMessage() {

    }

    @Override
    public void onReadUnReadRecords(List<Long> ids) {
        for(MessageInfo messageInfo : noticeList){
            if(ids == null || !ids.contains(messageInfo.messageId)){
                noReadMsgNum++ ;
            }
        }
    }

    @Override
    public void onGetNewMessages(boolean isSuccess, String errorInfo, List<MessageInfo> messages,int type) {
        UbtLog.d(TAG,"onGetNewMessages type = " + type);
        if(type == MessageRecordManager.TYPE_NOTICE){
            if(messages != null){
                noReadMsgNum = 0;
                noticeList.clear();
                noticeList.addAll(messages);
            }

            //读取收到的信息
            mMainHelper.doCheckIsAnyUnReadMsgByType(this, MessageRecordManager.TYPE_MESSAGE,11);
        }else {
            if(messages == null){
                mHandler.sendEmptyMessage(SHOW_NEW_MESSAGE);
                return;
            }

            try{
                String messageID = mMainHelper.getRecordMessageId();
                if(!messageID.equals("NO_VALUE")){
                    for(MessageInfo messageInfo : messages){
                        long id = Long.parseLong(messageID);
                        if(messageInfo.messageId > id){
                            noReadMsgNum++;
                        }
                    }
                }else{
                    noReadMsgNum = noReadMsgNum + messages.size();
                }

                UbtLog.d(TAG,"noReadMsgNum = " + noReadMsgNum);
                mHandler.sendEmptyMessage(SHOW_NEW_MESSAGE);

            }catch (Exception ex){
                ex.printStackTrace();
            }
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

    }

    @Override
    public void onSendFileBusy() {

    }

    @Override
    public void onSendFileError() {

    }

    @Override
    public void onSendFileFinish(int pos) {

    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {

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
    public void onPlaying() {

    }

    @Override
    public void onPausePlay() {

    }

    @Override
    public void onFinishPlay() {

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
        final int num = history.size();
        UbtLog.d(TAG, "onReadCollocationRecordFinish num=" + num);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(num>0){
                    UbtLog.d(TAG, "onReadCollocationRecordFinish setText num=" + num);
                    tvCollectedNum.setVisibility(View.VISIBLE);
                    tvCollectedNum.setText(""+ num);
                    UbtLog.d(TAG, "onReadCollocationRecordFinish setText num=" + tvCollectedNum.getText());

                }else{
                    tvCollectedNum.setText("");
                }

                tvCollectedNum.invalidate();

            }
        });
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
        final int num = history.size();
        UbtLog.d(TAG, "onReadHistoryFinish num=" + num);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(num>0){
                    tvDownloadNum.setText(""+num);
                }else{
                    tvDownloadNum.setText("");
                }
                tvDownloadNum.invalidate();


            }
        });
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
    public void onClearCache() {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {
        final int num = actions.size();
        UbtLog.d(TAG, "onReadNewActionsFinish num="+num);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(num>0){
                    UbtLog.d(TAG, "onReadNewActionsFinish setText num="+num);
                    tvCreatedNum.setText(""+ num);
                }else{
                    tvCreatedNum.setText("");
                }
                tvCollectedNum.invalidate();

            }
        });
    }

    @Override
    public void onChangeNewActionsFinish() {

    }
}

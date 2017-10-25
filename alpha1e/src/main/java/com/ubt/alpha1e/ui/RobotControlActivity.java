package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.blockly.BlocklyCourseActivity;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IMainUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class RobotControlActivity extends BaseActivity implements View.OnClickListener, IMainUI{

    private static final String TAG = "RobotControlActivity";

    private static final int BLUETOOTH_DISCONNECTED = 1;
    private static final int LOGIN_REQUEST_CODE = 1001;

    private ImageButton imgBtnConnect;
    private RelativeLayout rlActionShow, rlPlayGames, rlCreateAction;
    private TextView tvTitle;
    private LinearLayout backLayout;
    private TextView btnBack;
    private MainHelper mHelper;

    private RelativeLayout rlBlocklyProgram,rlBlocklyEdu;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BLUETOOTH_DISCONNECTED:
                    imgBtnConnect.setBackground(getDrawableRes("robot_disconnect_ft"));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robots_control);

        mHelper = new MainHelper(this);

        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper.initCourseAccessToken();

        if(isBulueToothConnected()){
            imgBtnConnect.setBackground(getDrawableRes("robot_connect_ft"));
        }else{
            imgBtnConnect.setBackground(getDrawableRes("robot_disconnect_ft"));
        }

        //读取机器人当前的电量
        if(mHelper != null){
            mHelper.RegisterHelper();
            mHelper.doRegisterListenerUI(this);
            mHelper.doReadState();
        }

    }

    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);

        if(event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS){
            if(mHelper!=null)
            {
                mHelper.RegisterHelper();
                mHelper.doReadState();
            }
        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECTED);
        }
    }

    @Override
    public void onLostBtCoon() {
        UbtLog.d(TAG,"---onLostBtCoon---");
        //这里有时候更新UI不成功
        mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECTED);
        super.onLostBtCoon();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mHelper != null){
            mHelper.UnRegisterHelper();
            mHelper.doUnRegisterListenerUI(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        backLayout = (LinearLayout) findViewById(R.id.layout_back);
        backLayout.setVisibility(View.VISIBLE);
        btnBack = (TextView) findViewById(R.id.tv_base_back);
        imgBtnConnect = (ImageButton) findViewById(R.id.imgbtn_robot_state);
        rlActionShow = (RelativeLayout) findViewById(R.id.rl_actions_show);
        rlPlayGames = (RelativeLayout) findViewById(R.id.rl_gamepad_play);
        rlCreateAction = (RelativeLayout) findViewById(R.id.rl_actions_create_connect);
        rlBlocklyProgram = (RelativeLayout) findViewById(R.id.rl_blockly_program);
        rlBlocklyEdu = (RelativeLayout) findViewById(R.id.rl_blockly_edu);

        tvTitle = (TextView) findViewById(R.id.txt_base_title_name);
        tvTitle.setText(getStringResources("app_name"));

        btnBack.setOnClickListener(this);
        imgBtnConnect.setOnClickListener(this);
        rlActionShow.setOnClickListener(this);
        rlPlayGames.setOnClickListener(this);
        rlCreateAction.setOnClickListener(this);
        rlBlocklyProgram.setOnClickListener(this);
        rlBlocklyEdu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String mobclickAgentType = "";
        switch (v.getId()){
            case R.id.tv_base_back:
                this.finish();
                break;
            case R.id.imgbtn_robot_state:
                if(isBulueToothConnected()){
                    RobotInfoActivity.launchActivity(RobotControlActivity.this, getRequestedOrientation());
                }else{
                    RobotConnectedActivity.launchActivity(RobotControlActivity.this,false,-1);
                }

                break;
            case R.id.rl_actions_show:
                if(!isBulueToothConnected()){
                    RobotConnectedActivity.launchActivity(RobotControlActivity.this,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    return;
                }

                if(BaseHelper.hasSdcard){
                    MobclickAgent.onEvent(this, mobclickAgentType);
                    MyActionsActivity.launchActivity(RobotControlActivity.this, 0);
                }else{
                    showToast("ui_remote_synchoronize_no_sd");
                }
                break;
            case R.id.rl_gamepad_play:
                if(!isBulueToothConnected()){
                    RobotConnectedActivity.launchActivity(RobotControlActivity.this,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    return;
                }
                intent.setClass(this,RemoteSelActivity.class);
                mobclickAgentType = AlphaStatics.GAME_PAD;
                startActivity(intent);
                MobclickAgent.onEvent(this, mobclickAgentType);
                break;
            case R.id.rl_actions_create_connect:
                if(!isBulueToothConnected()){
                    RobotConnectedActivity.launchActivity(RobotControlActivity.this,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    return;
                }
                if(checkLoginState()){
                    intent.putExtra(ActionsEditHelper.StartTypeStr,
                            ActionsEditHelper.StartType.new_type);
                    intent.setClass(this, ActionsNewEditActivity.class);
                    startActivity(intent);
                }else{
                    intent.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.rl_blockly_program:
                intent.setClass(this, BlocklyActivity.class);
                intent.putExtra(Constant.IS_FROM_COURSE,false);
                startActivity(intent);
                break;
            case R.id.rl_blockly_edu:

                UserInfo userInfo = ((AlphaApplication)getApplicationContext()).getCurrentUserInfo();
                UbtLog.d(TAG,"userInfo = " + userInfo );
                if(userInfo == null){
                    login();
                }else {
                    gotoBlocklyCourse();
                }
                break;
            default:
                break;

        }
    }

    private void gotoBlocklyCourse(){
        Intent intent = new Intent();
        intent.setClass(this, BlocklyCourseActivity.class);
        startActivity(intent);
    }

    private void login(){
        LoginActivity.launchActivity(this,true, LOGIN_REQUEST_CODE);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void noteCharging() {
        imgBtnConnect.setBackground(getDrawableRes("robot_connect_ft"));
    }

    @Override
    public void updateBattery(int power) {
        if(power < 10){
            imgBtnConnect.setBackground(getDrawableRes("robot_lowbattery_ft"));
        }else{
            imgBtnConnect.setBackground(getDrawableRes("robot_connect_ft"));
        }

    }

    @Override
    public void noteDiscoonected() {

    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void onNoteVol(int mCurrentVol) {

    }

    @Override
    public void onNoteVolState(boolean mCurrentVolState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_CODE){
            if(((AlphaApplication)getApplicationContext()).getCurrentUserInfo() != null){
                gotoBlocklyCourse();
            }
        }
    }
}

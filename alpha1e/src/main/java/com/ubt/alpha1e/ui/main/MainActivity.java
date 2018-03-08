package com.ubt.alpha1e.ui.main;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.BuildConfig;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.ActionTestActivity;
import com.ubt.alpha1e.animator.FrameAnimation;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loopHandler.HandlerCallback;
import com.ubt.alpha1e.base.loopHandler.LooperThread;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsActivity;
import com.ubt.alpha1e.behaviorhabits.model.behaviourHabitModel;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netsearchresult.NetSearchResultActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.ActionEvent;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.login.loginauth.LoginAuthActivity;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.services.SendClientIdService;
import com.ubt.alpha1e.ui.RemoteActivity;
import com.ubt.alpha1e.ui.RemoteSelActivity;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.ui.custom.CommonGuideView;
import com.ubt.alpha1e.ui.custom.MainActivityGuideView;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.RobotBindingDialog;
import com.ubt.alpha1e.ui.dialog.alertview.RobotBindDialog;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.ui.helper.BluetoothStateHelper;
import com.ubt.alpha1e.userinfo.mainuser.UserCenterActivity;
import com.ubt.alpha1e.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.notice.WebActivity;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Liliang
 * Main UI function
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View,HandlerCallback {

    @BindView(R.id.cartoon_body_touch_bg)
    ImageView cartoonBodyTouchBg;
    @BindView(R.id.charging)
    ImageView charging;
    @BindView(R.id.cartoon_action)
    ImageView cartoonAction;
    @BindView(R.id.right_icon)
    TextView rightIcon;
    @BindView(R.id.right_icon2)
    TextView rightIcon2;
    @BindView(R.id.right_icon3)
    TextView rightIcon3;
    @BindView(R.id.right_icon4)
    TextView rightIcon4;
    @BindView(R.id.top_icon)
    ImageView topIcon;
    @BindView(R.id.top_icon2)
    ImageView topIcon2;
    @BindView(R.id.top_icon2_disconnect)
    ImageView topIcon2Disconnect;
    @BindView(R.id.top_icon3)
    TextView topIcon3;

    @BindView(R.id.cartoon_head)
    ImageView cartoonHead;
    @BindView(R.id.cartoon_chest)
    ImageView cartoonChest;
    @BindView(R.id.cartoon_left_hand)
    ImageView cartoonLeftHand;
    @BindView(R.id.cartoon_right_hand)
    ImageView cartoonRightHand;
    @BindView(R.id.cartoon_left_leg)
    ImageView cartoonLeftLeg;
    @BindView(R.id.cartoon_right_leg)
    ImageView cartoonRightLeg;
    @BindView(R.id.mainui)
    RelativeLayout mainui;
    @BindView(R.id.buddle_text)
    TextView buddleText;
//    @BindView(R.id.charging_dot)
//    ImageView chargingDot;
    @BindView(R.id.touch_control)
    RelativeLayout touchControl;
    @BindView(R.id.indicator)
    ImageView actionIndicator;
    @BindView(R.id.indicator0)
    ImageView indicator;
    @BindView(R.id.tv_habits_event_time)
    TextView  habitsTime;
    @BindView(R.id.tv_hibits_event_name)
    TextView  habitsName;
    @BindView(R.id.top_icon4)
    ImageView voiceCmd;
    private String TAG = "MainActivity";
    int screen_width = 0;
    int screen_height = 0;
    int init_screen_width = 667;
    int init_screen_height = 375;
    RelativeLayout.LayoutParams params;
    private AnimationDrawable frameAnimation;
    FrameAnimation frameAnimationPro;
    int index = 0;
    private final int cartoon_action_swing_right_leg = 0;
    private final int cartoon_action_swing_left_leg = 1;
    private final int cartoon_action_swing_right_hand = 2;
    private final int cartoon_action_swing_left_hand = 3;
    private final int cartoon_action_hand_stand = 4;
    private final int cartoon_action_hand_stand_reverse = 5;
    private final int cartoon_action_squat = 6;
    private final int cartoon_action_enjoy = 7;
    private final int cartoon_action_fall = 8;
    private final int cartoon_action_greeting = 9;
    private final int cartoon_action_shiver = 10;
    private final int cartoon_action_sleep = 11;
    private final int cartoon_action_smile = 12;
    private final int cartoon_aciton_squat_reverse = 13;
    private int buddleTextTimeout = 5000;//5s
    private int charging_shrink_interval = 500;
    private int chargeShrinkTimeout = 1000;
    private Timer mBuddleTextTimer;
    private TimerTask mBuddleTextTimeOutTask;
    private LooperThread looperThread;
    Timer mChargetimer;
    private int tmp=-1;
    TimerTask mChargingTimeoutTask;
    private byte mChargeValue = 0;
    private int mPowerValue = 0;
    long mCurrentTouchTime = 0;
    //ONE MINUTE
    private long noOperationTimeout = 1 * 60 * 1000;
    private String STATUS_MACHINE = "status_machine";
    private final byte APP_LAUNCH_STATUS = 0x01;
    private final byte APP_BLUETOOTH_CONNECTED = 0x02;
    private final byte APP_BLUETOOTH_CLOSE = 0x03;
    private final byte ROBOT_LOW_POWER_LESS_TWENTY_STATUS = 0x04;
    private final byte ROBOT_LOW_POWER_LESS_FIVE_STATUS = 0x05;
    private final byte ROBOT_SLEEP_EVENT = 0x06;
    private final byte ROBOT_HIT_HEAD = 0x07;
    private final byte ROBOT_WAKEUP_ACTION = 0x08;
    private final byte ROBOT_POWEROFF = 0x09;
    private final byte ROBOT_hand_stand = 0x0a;
    private final byte ROBOT_fall = 0x0b;
    private final byte ROBOT_CHARGING = 0x0c;
    private final byte ROBOT_default_gesture = 0x0d;
    private final byte ROBOT_sleep_gesture = 0x0e;
    private final int LOW_BATTERY_TWENTY_THRESHOLD = 20;
    private final int LOW_BATTERY_FIVE_THRESHOLD = 5;
    private boolean ENTER_LOW_BATTERY_FIVE = false;
    private boolean ENTER_LOW_BATTERY_TWENTY = false;
    private int index_one_vol = 1; //volatage
    private int index_two_charging = 2; //charging or uncharging
    private int index_three_powerPercent = 3;//power value
    private BluetoothHelper mBtHelper;
    private int APP_CURRENT_STATUS = -1;
    private int DV_COMMON_COMMAND_POWEROFF_ZERO = 1;
    private int DV_COMMON_COMMAND_POWEROFF_ONE = 0;
    private int DV_COMMON_COMMAND_POWEROFF_TWO = 0;
    private int Cartoon_animation_last_execute = 0;
    // private boolean  app_bluetooth_conencted_executed=false;
    private boolean IS_CHARGING = false;
    private String current_mac_address = "";
    private String FilePath = "action/avatar/";
    private String waveRightArm = "wave the right arm.hts"; //摆右手臂
    private String waveRightLeg = "wave the right leg.hts";  //摆动右腿
    private String waveLeftArm = "wave the left arm.hts";   //摆左手臂
    private String waveLeftLeg = "wave the left leg.hts";  //摆动左腿
    private String upsideDown = "upside-down.hts"; //倒立
    private String squatTuch = "Squat Tuck.hts"; //蹲下抱膝
    private String squinting_satifying = "Squinting satisfying.hts"; //眯眼享受
    private String squintLaugh = "Squint laugh.hts"; // 眯眼笑
    private String trembling = "trembling.hts";  //  瑟瑟发抖
    private String fallDown = "fall down.hts"; // 摔倒
    private String sleepState = "sleep.hts"; //  睡觉
    private String sayHi = "say hi.hts";   //打招呼
    boolean isBtConnect = false;
    boolean isNetworkConnect = false;
    String WakeUpActionName = "初始化";
    private int ROBOT_HEAD_UP_STAND = 1;
    private int ROBOT_HEAD_DOWN = 2;
    private int ROBOT_LEFT_SHOULDER_SLEEP = 3;
    private int ROBOT_RIGHT_SHOULDER_SLEEP = 4;
    private int ROBOT_HEAD_UP_SLEEP = 5;
    private int ROBOT_HEAD_DOWN_SLEEP = 6;
    private int CARTOON_FRAME_INTERVAL = 4;
    boolean ANIMAITONSOLUTIONOOM = true;
    boolean animation_running = false;
    private int ROBOT_CHARGING_STATUS = 0x01;
    private int ROBOT_UNCHARGE_STATUS = 0x0;
    private int ROBOT_CHARGING_ENOUGH_STATUS = 0x03;
    private int CURRENT_ACTION_NAME = 0;
    private boolean cartoon_enable = false;
    private static final int ROBOT_GOTO_BIND = 20;
    AnimationDrawable  mActionIndicator=null;
    long mClickTime=0;
    int CLICK_THRESHOLD_DUPLICATE=800;
    Animation hyperspaceJump;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentTouchTime = System.currentTimeMillis();
        getScreenInch();
        initUI();

        mHelper = MainUiBtHelper.getInstance(getContext());
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver1, filter1);
        looperThread = new LooperThread(this);
        looperThread.start();
        SPUtils.getInstance().put(Constant.IS_TOAST_BINDED, false);
        // 启动发送clientId服务
        SendClientIdService.startService(MainActivity.this);

        mActionIndicator=(AnimationDrawable)actionIndicator.getBackground();
        mActionIndicator.setOneShot(false);
        mActionIndicator.setVisible(true,true);
        mPresenter.registerEventBus();
        mPresenter.getXGInfo();
        if(!MainActivityGuideView.hasShowGuide()) {
            MainActivityGuideView.getInstant(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UbtLog.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG, "onResume");
        initUI();
        showUserPicIcon();
        requireBehaviourNextEvent();
        if (!isBulueToothConnected()) {
            showDisconnectIcon();
            showGlobalButtonAnmiationEffect(false);
            looperThread.send(createMessage(Constant.APP_LAUNCH_STATUS));
            // looperThread.send(createMessage(ROBOT_LOW_POWER_LESS_FIVE_STATUS));
            m_Handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AutoScanConnectService.startService(MainActivity.this); //add by dicy.cheng 打开自动连接
                }
            }, 100);
        } else {
            MainUiBtHelper.getInstance(getContext()).readNetworkStatus();
            if (cartoonAction != null) {
                if (APP_CURRENT_STATUS == Constant.ROBOT_SLEEP_EVENT) {
                    looperThread.send(createMessage(Constant.ROBOT_SLEEP_EVENT));
                } else {
                    looperThread.send(createMessage(Constant.ROBOT_default_gesture));
                }
                if (MainActivity.this != null && ((AlphaApplication) MainActivity.this.getApplication()).getmCurrentNetworkInfo() != null) {
                    NetworkInfo networkInfo = ((AlphaApplication) MainActivity.this.getApplication()).getmCurrentNetworkInfo();
                    if (networkInfo.status) {
                        hiddenDisconnectIcon();
                    } else {
                        showDisconnectIcon();
                    }
                } else {
                    showDisconnectIcon();
                }
            }
            getCurrentPower();
        }
    }

    private void getCurrentPower() {
        int indexPower=mPresenter.getPowerCapacity(MainUiBtHelper.getInstance(getContext()).getPowerValue());
        showBatteryCapacity(MainUiBtHelper.getInstance(getContext()).getChargingState(),indexPower);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "mainactivity onDestroy ..... ");
        if (mBroadcastReceiver1 != null) {
            getContext().unregisterReceiver(mBroadcastReceiver1);
        }
        BluetoothStateHelper.getInstance(getContext()).doCancelCoon();
        stopBuddleTextAsynchronousTask();
        stopchargeAsynchronousTask();
        SendClientIdService.doStopSelf();
        AutoScanConnectService.doStopSelf();
        mPresenter.unregisterEventBus();
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        ((AlphaApplication) getContext().getApplicationContext())
                                .setCurrentBluetooth(null);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                }

            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                UbtLog.d(TAG, device.getName() + " ACTION_ACL_CONNECTED");
                getCurrentPower();
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                UbtLog.d(TAG, device.getName() + " ACTION_ACL_DISCONNECTED");
                //电池动画停止
                stopchargeAsynchronousTask();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        looperThread.send(createMessage(APP_BLUETOOTH_CLOSE));
                    }
                });

            }
        }
    };


    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.top_icon4,R.id.ll_remote, R.id.ll_action, R.id.ll_program,
            R.id.ll_community, R.id.cartoon_chest, R.id.cartoon_head, R.id.cartoon_left_hand,
            R.id.cartoon_right_hand, R.id.cartoon_left_leg, R.id.cartoon_right_leg, R.id.rl_course_center, R.id.rl_hibits_event})
    protected void switchActivity(View view) {
        UbtLog.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.top_icon:
                if(!removeDuplicateClickEvent()) {
                    Intent intent = new Intent();
                    UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
                    if (null == userModel) {
                        intent.setClass(this, LoginActivity.class);
                    } else {
                        if (TextUtils.isEmpty(userModel.getPhone())) {
                            intent.setClass(this, LoginAuthActivity.class);
                        } else if (TextUtils.isEmpty(userModel.getAge())) {
                            intent.setClass(this, UserEditActivity.class);
                        } else {
                            intent.setClass(this, UserCenterActivity.class);
                        }
                    }
                    startActivity(intent);
                    mClickTime=0;
                }
                break;
            case R.id.top_icon2:
                if(!removeDuplicateClickEvent()) {
                    gotoConnectBluetooth();
                }
                break;
            case R.id.top_icon3:
                if (isBulueToothConnected()) {
                    try {
                        CommonCtrlView mCommonCtrlView=CommonCtrlView.getInstace(getContext());
                        mCommonCtrlView.setPresenter(mPresenter);
                        UbtLog.d(TAG, "hasShowGuide = " + CommonGuideView.hasShowGuide());
                        if (!CommonGuideView.hasShowGuide()) {
                            new CommonGuideView(getContext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.top_icon4:
                if(!removeDuplicateClickEvent()) {
                    WebActivity.launchActivity(this,HttpEntity.VOICE_CMD,"语音指令");
                }
                break;
            case R.id.ll_remote:
                if (isBulueToothConnected()) {
                    if (!removeDuplicateClickEvent()) {
                        mPresenter.resetGlobalActionPlayer();
                        mLaunch.setClass(this, RemoteSelActivity.class);
                        //startActivity(new Intent(this, ActionTestActivity.class));
                        startActivity(mLaunch);


                    }
                } else {
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.ll_action:
                if (isBulueToothConnected()) {
                    if (!removeDuplicateClickEvent()) {
                        mPresenter.resetGlobalActionPlayer();
                        APP_CURRENT_STATUS = ROBOT_default_gesture;
                        startActivity(new Intent(this, ActionTestActivity.class));
                        this.overridePendingTransition(R.anim.activity_open_up_down, 0);

                    }
                } else {
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.ll_program:
                if(!removeDuplicateClickEvent()) {
                    startActivity(new Intent(this, BlocklyActivity.class));
                    this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                }
                break;
            case R.id.ll_community:
                //BehaviorHabitsActivity.LaunchActivity(this);
                ToastUtils.showShort("程序猿正在施工中！！！");
                break;
            case R.id.cartoon_head:
                UbtLog.d(TAG, "click head");
                sendCommandToRobot(FilePath + squinting_satifying);
                showCartoonAction(cartoon_action_enjoy);
                break;
            case R.id.cartoon_chest:
                UbtLog.d(TAG, "click chest");
                sendCommandToRobot(FilePath + squintLaugh);
                showCartoonAction(cartoon_action_smile);
                break;
            case R.id.cartoon_left_hand:
                UbtLog.d(TAG, "click left hand");
                sendCommandToRobot(FilePath + waveRightArm);
                showCartoonAction(cartoon_action_swing_left_hand);
                break;
            case R.id.cartoon_right_hand:
                UbtLog.d(TAG, "click right hand");
                sendCommandToRobot(FilePath + waveLeftArm);
                showCartoonAction(cartoon_action_swing_right_hand);
                break;
            case R.id.cartoon_left_leg:
                UbtLog.d(TAG, "click left leg");
                sendCommandToRobot(FilePath + waveRightLeg);
                showCartoonAction(cartoon_action_swing_left_leg);
                break;
            case R.id.cartoon_right_leg:
                UbtLog.d(TAG, "click right leg");
                sendCommandToRobot(FilePath + waveLeftLeg);
                showCartoonAction(cartoon_action_swing_right_leg);
                break;
            case R.id.rl_course_center:
                if (isBulueToothConnected()) {
                    if(!removeDuplicateClickEvent()) {
                        mPresenter.resetGlobalActionPlayer();
                        startActivity(new Intent(this, MainCourseActivity.class));
                        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                    }
                } else {
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.rl_hibits_event:
//                BehaviorHabitsActivity.LaunchActivity(this);
                mPresenter.checkMyRobotState();
                break;
            default:
                break;
        }
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog() {
        new ConfirmDialog(this).builder()
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
    void gotoConnectBluetooth() {
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
        isBtConnect = isBulueToothConnected();
        startActivityForResult(bluetoothConnectIntent, 100);

        if (AppManager.getInstance().currentActivity() != null
                && (AppManager.getInstance().currentActivity() instanceof PrincipleActivity
                || AppManager.getInstance().currentActivity() instanceof SplitActivity
                || AppManager.getInstance().currentActivity() instanceof MergeActivity
                || AppManager.getInstance().currentActivity() instanceof FeatureActivity)) {
            UbtLog.d(TAG, "有需要关闭的课程界面 ");
            AlphaApplication.setmNeedOpenActivity(AppManager.getInstance().currentActivity().getClass().getSimpleName());
            AppManager.getInstance().currentActivity().finish();
        }

        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
    }

    @Override
    public void onLostBtCoon() {
        super.onLostBtCoon();
        //
        UbtLog.d(TAG, "onLostBtConn");
    }

    ConfirmDialog dialog = null;

    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if (event.getEvent() == RobotEvent.Event.NETWORK_STATUS) {
            NetworkInfo networkInfo = (NetworkInfo) event.getNetworkInfo();
            UbtLog.d(TAG, "networkInfo == " + networkInfo.status);

            isNetworkConnect = networkInfo.status;
            if (isNetworkConnect) {
                if (MainActivity.this != null) {
                    ((AlphaApplication) MainActivity.this.getApplication()).setmCurrentNetworkInfo(networkInfo);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenDisconnectIcon();
                    }
                });
            } else {
                if (MainActivity.this != null) {
                    ((AlphaApplication) MainActivity.this.getApplication()).setmCurrentNetworkInfo(null);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDisconnectIcon();
                    }
                });
            }
        } else if (event.getEvent() == RobotEvent.Event.DISCONNECT) {//Bluetooth Disconect
            UbtLog.d(TAG, "DISCONNECTED ");
            if (MainActivity.this != null) {
                ((AlphaApplication) MainActivity.this.getApplicationContext()).doLostConnect();
            }else {
                return;
            }
            showGlobalButtonAnmiationEffect(false);
            stopchargeAsynchronousTask();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    looperThread.send(createMessage(APP_BLUETOOTH_CLOSE));
                    //隐藏全局控制按钮
                    mPresenter.exitGlocalControlCenter();

                    if (AppManager.getInstance().currentActivity() != null) {
                        UbtLog.d(TAG, "onLostBtCoon " + "  不为空" + AppManager.getInstance().currentActivity());
                        Activity mActivity = AppManager.getInstance().currentActivity();
                        if (!(mActivity instanceof RemoteActivity
                                || mActivity instanceof RemoteSelActivity
                                || mActivity instanceof MainCourseActivity
                                || mActivity instanceof PrincipleActivity
                                || mActivity instanceof SplitActivity
                                || mActivity instanceof MergeActivity
                                || mActivity instanceof FeatureActivity
                                /*|| mActivity instanceof MainCourseActivity
                                || mActivity instanceof PrincipleActivity
                                || mActivity instanceof SplitActivity
                                || mActivity instanceof MergeActivity
                                || mActivity instanceof FeatureActivity*/
                                || mActivity instanceof ActionTestActivity
                                || mActivity instanceof ActionCourseActivity
                                || mActivity instanceof NetconnectActivity
                                || mActivity instanceof NetSearchResultActivity
//                                || mActivity instanceof CourseLevelOneActivity
//                                || mActivity instanceof CourseLevelTwoActivity
//                                || mActivity instanceof CourseLevelThreeActivity
//                                || mActivity instanceof CourseLevelFourActivity
//                                || mActivity instanceof CourseLevelFiveActivity
//                                || mActivity instanceof CourseLevelSixActivity
//                                || mActivity instanceof CourseLevelSevenActivity
//                                || mActivity instanceof CourseLevelEightActivity
//                                || mActivity instanceof CourseLevelNineActivity
//                                || mActivity instanceof CourseLevelTenActivity
                        )) {
                            return;
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        if (AppManager.getInstance().currentActivity() instanceof NetconnectActivity || AppManager.getInstance().currentActivity() instanceof NetSearchResultActivity) {
                            AppManager.getInstance().finishActivity();
                            return;
                        }
                        showBluetoothDisconnect();
                        } else {
                            UbtLog.d(TAG, "onLostBtCoon " + "  为空");
                        }
                }
            });
        } else if (event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS) {
            UbtLog.d(TAG, "mainactivity CONNECT_SUCCESS 1");
            if(mHelper != null){
                mHelper.RegisterHelper();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCurrentPower();
                    }
                });
            }
            if (!MainUiBtHelper.getInstance(getContext()).isLostCoon()) {
                UbtLog.d(TAG, "mainactivity CONNECT_SUCCESS 2");
                MainUiBtHelper.getInstance(getContext()).readNetworkStatus();
                looperThread.send(createMessage(Constant.APP_BLUETOOTH_CONNECTED));
            }
        }else if(event.getEvent()==RobotEvent.Event.ENTER_CONNECT_DEVICE){
            gotoConnectBluetooth();
        }

    }

    @Subscribe
    public void onEventAction(ActionEvent event) {
        if(event.getEvent() == ActionEvent.Event.ACTION_PLAY_START){
            showGlobalButtonAnmiationEffect(true);
        }else if(event.getEvent() == ActionEvent.Event.ACTION_PLAY_PAUSE){
            if(event.getStatus() == 1){
                showGlobalButtonAnmiationEffect(true);
            }else {
                showGlobalButtonAnmiationEffect(false);
            }
        }else if(event.getEvent() == ActionEvent.Event.ACTION_PLAY_FINISH){
            showGlobalButtonAnmiationEffect(false);
        }
    }

    void showBluetoothDisconnect() {
        dialog = new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("提示")
                .setMsg("蓝牙连接断开，请重新连接")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        gotoConnectBluetooth();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                        AppManager.getInstance().finishUseBluetoothActivity();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (isBtConnect != isBulueToothConnected()) {
                if (isBulueToothConnected()) {
                    looperThread.send(createMessage(Constant.APP_BLUETOOTH_CONNECTED));
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        UbtLog.d(TAG, "X ,Y  " + (event.getX() + "," + event.getY()));
        mCurrentTouchTime = System.currentTimeMillis();
        if (System.currentTimeMillis() - mCurrentTouchTime < noOperationTimeout) {
            hiddenBuddleTextView();
        }
        return super.onTouchEvent(event);
    }

    private Handler m_Handler = new Handler();


    private void showCartoonAction_performance(final int value) {
        if (animation_running && CURRENT_ACTION_NAME == value) {
            UbtLog.d(TAG, "animation is execution");
            return;
        }
        if (value == cartoon_aciton_squat_reverse || value == cartoon_action_sleep) {
            hiddenCartoonTouchView();
        } else {
            showCartoonTouchView();
        }
        frameAnimationPro = new FrameAnimation(cartoonAction, mPresenter.requestCartoonAction(value), CARTOON_FRAME_INTERVAL, false);
        frameAnimationPro.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                UbtLog.d(TAG, "start");
                animation_running = true;
            }

            @Override
            public void onAnimationEnd() {
                UbtLog.d(TAG, "end");
                animation_running = false;
            }

            @Override
            public void onAnimationRepeat() {
                UbtLog.d(TAG, "repeat");
                frameAnimationPro.pauseAnimation();
            }
        });
        CURRENT_ACTION_NAME = value;
    }

    @Override
    public void dealMessage(byte cmd) {

        looperThread.send(createMessage(cmd));
        mPresenter.setRobotStatus(cmd);
    }


    @Override
    public void showCartoonAction(final int value) {
        if (ANIMAITONSOLUTIONOOM & cartoon_enable) {
            showCartoonAction_performance(value);
        } else {
            //Some time OOM BUG
            // showCartoonAction_original(value);
        }
    }


    public void buddleTextAsynchronousTask() {
        stopBuddleTextAsynchronousTask();
        mBuddleTextTimer = new Timer();
        mBuddleTextTimeOutTask = new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - mCurrentTouchTime > noOperationTimeout) {
                    if (looperThread != null) {
                        looperThread.post(new Runnable() {
                            public void run() {
                                try {
                                    // boolean isUiThread = Looper.getMainLooper().getThread() == Thread.currentThread();
                                    //UbtLog.d(TAG,"current random show ? "+APP_CURRENT_STATUS);
                                    if (APP_CURRENT_STATUS != Constant.APP_LAUNCH_STATUS && APP_CURRENT_STATUS != Constant.APP_BLUETOOTH_CLOSE) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showBuddleText(mPresenter.getBuddleText(Constant.BUDDLE_RANDOM_TEXT));
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showBuddleText(mPresenter.getBuddleText(Constant.BUDDLE_INIT_TEXT));
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        };
        mBuddleTextTimer.schedule(mBuddleTextTimeOutTask, 0, buddleTextTimeout); //execute in every 50000 ms
    }

    private void stopBuddleTextAsynchronousTask() {
        if (mBuddleTextTimeOutTask != null) {
            mBuddleTextTimeOutTask.cancel();
            mBuddleTextTimeOutTask = null;
            mBuddleTextTimer.purge();
            mBuddleTextTimer = null;
        }
    }

    /**
     * value current Power value
     * @param index
     */

    public void chargeAsynchronousTask(final int index ) {
        mChargetimer = new Timer();
        mChargingTimeoutTask = new TimerTask() {
            @Override
            public void run() {
                looperThread.post(new Runnable() {
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if (charging != null) {
                                            if(index!=(com.ubt.alpha1e.data.Constant.powerThreshold.length-1)) {
                                                charging.setBackground(getDrawableRes("power" + com.ubt.alpha1e.data.Constant.powerThreshold[index + 1]));
                                            }else {
                                                //BATTERY ENOUGH
                                                charging.setBackground(getDrawableRes("power" + com.ubt.alpha1e.data.Constant.powerThreshold[index]));
                                            }
                                        }
                                }
                            });
                            Thread.sleep(charging_shrink_interval);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if (charging != null) {
                                            charging.setBackground(getDrawableRes("power" + com.ubt.alpha1e.data.Constant.powerThreshold[index]));
                                        }
                                }
                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        mChargetimer.schedule(mChargingTimeoutTask, 0, chargeShrinkTimeout); //execute in every 50000 ms
    }

    private void stopchargeAsynchronousTask() {
        if (mChargingTimeoutTask != null) {
            mChargingTimeoutTask.cancel();
            mChargingTimeoutTask = null;
            mChargetimer.purge();
            mChargetimer = null;
        }

    }

    private void showBuddleText(String text) {
        if (buddleText != null) {
            buddleText.setText(text);
            showBuddleTextView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onGetRobotInfo(int result, MyRobotModel model) {
        UbtLog.d(TAG, "onGetRobotInfo == ");
        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(MainActivity.this);
        if (result == 0) {
            ToastUtils.showShort("获取机器人信息失败！");
        } else if (result == 1) {
            UbtLog.d(TAG, "账号已经绑定 ");
            if(!removeDuplicateClickEvent()) {
                BehaviorHabitsActivity.LaunchActivity(this);
            }
        } else if (result == 2) {
            UbtLog.d(TAG, "账户没有绑定 ");
            habitAdviceGotoBindDialog();
        }
    }

    @Override
    public void showGlobalButtonAnmiationEffect(final boolean status) {
            UbtLog.d(TAG,"ICON ANIMATION : "+status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status) {
                    indicator.setVisibility(View.INVISIBLE);
                    mActionIndicator.setOneShot(false);
                    mActionIndicator.start();
                    mActionIndicator.setVisible(true, true);
                } else {
                    mActionIndicator.stop();
                    mActionIndicator.setVisible(false, false);
                    indicator.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void hiddenBuddleText() {
        hiddenBuddleTextView();
    }

    @Override
    public Handler getHandler() {
        return m_Handler;
    }

    //若要使用此功能，需先绑定机器人！
    public void habitAdviceGotoBindDialog() {
        new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("提示")
                .setMsg("若要使用此功能，需先绑定机器人！")
                .setCancelable(false)
                .setPositiveButton("一键绑定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "一键绑定 ");
                        if (MainUiBtHelper.getInstance(getContext()).isLostCoon()) {
                            UbtLog.d(TAG, "没有连接蓝牙 ");
                            Intent intent = new Intent(MainActivity.this, BluetoothandnetconnectstateActivity.class);
                            startActivity(intent);
                        } else {
                            UbtLog.d(TAG, "连接了蓝牙 ");
                            gotoBind();
                        }
                    }
                })
                .setNegativeButton("暂不", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "暂不 ");
                    }
                }).show();
    }


    RobotBindingDialog robotBindingDialog = null;

    //一键绑定
    public void gotoBind() {
        if (AlphaApplication.currentRobotSN == null || AlphaApplication.currentRobotSN.equals("")) {
            ToastUtils.showShort("未读到机器人序列号");
            return;
        }
        if (robotBindingDialog == null) {
            robotBindingDialog = new RobotBindingDialog(AppManager.getInstance().currentActivity())
                    .builder()
                    .setCancelable(true);
        }
        robotBindingDialog.show();
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.ROBOT_BIND;
        doRequestBind(url, gotoBindRequest, ROBOT_GOTO_BIND);

    }

    /**
     * 网络请求
     */
    public void doRequestBind(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestCheckIsBind onError:" + e.getMessage());
                switch (id) {
                    case ROBOT_GOTO_BIND:
                        if (robotBindingDialog != null && robotBindingDialog.isShowing()) {
                            robotBindingDialog.display();
                            robotBindingDialog = null;
                        }
                        adviceBindFail("");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "doRequestCheckIsBind response = " + response);
//				BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());
                BaseResponseModel<String> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<String>>() {
                        }.getType());
                switch (id) {
                    case ROBOT_GOTO_BIND:
                        if (robotBindingDialog != null && robotBindingDialog.isShowing()) {
                            robotBindingDialog.display();
                            robotBindingDialog = null;
                        }
                        UbtLog.d(TAG, "status:" + baseResponseModel.status);
                        UbtLog.d(TAG, "info:" + baseResponseModel.info);
                        if (baseResponseModel.status) {
                            UbtLog.d(TAG, "绑定成功");
                            if (baseResponseModel.models == null || baseResponseModel.models.equals("")) {
                                adviceBindSuccess();
                            } else if (baseResponseModel.models != null && baseResponseModel.models.equals("1002")) {
                                adviceBindFail("机器人已被他人绑定！");
                            } else {
                                adviceBindFail("");
                            }
                        } else {
                            adviceBindFail("");
                            UbtLog.d(TAG, "绑定失败");
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    //绑定成功！
    public void adviceBindSuccess() {
        Drawable img_ok;
        Resources res1 = getResources();
        img_ok = res1.getDrawable(R.drawable.ic_bind_success);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定成功！")
                .setMsg("可到“个人中心-设置-我的机器人”查看状态。")
                .setCancelable(true)
                .setPositiveButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "我知道了 ");
                    }
                })
                .setTitlePicture(img_ok)
                .setNoTitleLayout()
                .show();
    }

    //绑定失败！
    public void adviceBindFail(String reason) {
        Drawable img_off;
        Resources res2 = getResources();
        img_off = res2.getDrawable(R.drawable.ic_bind_fail);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定失败！")
                .setMsg(reason)
                .setCancelable(true)
                .setPositiveButton("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "重试 ");
                        gotoBind();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                    }
                })
                .setTitlePicture(img_off)
                .setNoTitleLayout()
                .show();
    }

    @Override
    protected void initUI() {
        return;
        //course center icon
        /*int course_icon_margin_left = 22;
        int course_icon_margin_top = 260;
        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) bottomIcon.getLayoutParams();
        rlParams.leftMargin = getAdaptiveScreenX(course_icon_margin_left);
        rlParams.topMargin = getAdaptiveScreenY(course_icon_margin_top);
        bottomIcon.setLayoutParams(rlParams);
        bottomIcon.setVisibility(View.GONE);*/

    //Buddle Text
        /*int buddle_text_margin_left = 388;
        int buddle_text_margin_top = 116;//height*0.31
        RelativeLayout.LayoutParams buddleParams = (RelativeLayout.LayoutParams) buddleText.getLayoutParams();
        buddleParams.leftMargin = getAdaptiveScreenX(buddle_text_margin_left);
        buddleParams.topMargin = getAdaptiveScreenY(buddle_text_margin_top);
        buddleText.setLayoutParams(buddleParams);*/
   }
    private int getAdaptiveScreenX(int init_x) {
        return init_x * screen_width / init_screen_width;
    }
    private int getAdaptiveScreenY(int init_y) {
        return init_y * screen_height / init_screen_height;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        //return R.layout.activity_main;
        return R.layout.activity_main_new;
    }

    @Override
    protected void onStop() {
//        AutoScanConnectService.doEntryManalConnect(true);
//        AutoScanConnectService.doStopSelf();
        super.onStop();
    }

    private double getScreenInch() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scale = (int) this.getResources().getDisplayMetrics().density;

        int dpiClassification = dm.densityDpi;
        UbtLog.d(TAG, "SCREEN DENSITY DPI\n" +
                "0.75 - ldpi - 120 dpi\n" +
                "1.0 - mdpi - 160 dpi\n" +
                "1.5 - hdpi - 240 dpi\n" +
                "2.0 - xhdpi - 320 dpi\n" +
                "3.0 - xxhdpi - 480 dpi\n" +
                "4.0 - xxxhdpi - 640 dpi\n" +
                "current screen :" + dpiClassification);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        screen_width = width;
        screen_height = height;
        UbtLog.d(TAG, "width " + width + "height  " + height);
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        UbtLog.d(TAG, "SCREEN  " + Math.sqrt(x + y));
        return Math.sqrt(x + y);
    }


    public static class MessageEvent {
        /* Additional fields if needed */
        public final String message;

        public MessageEvent(String message) {
            this.message = message;
        }
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MessageEvent event) {
            mPresenter.dealMessage(event.message);
    }
   private Message createMessage(byte cmd) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByte(STATUS_MACHINE,cmd);
        message.setData(bundle);
        return message;
    }


	    @Override
    public void showBatteryCapacity(final boolean isCharging, final int value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBatteryUi();
                        if(isCharging) {
                               if(mChargetimer==null||value!=tmp) {
                                   stopchargeAsynchronousTask();
                                   chargeAsynchronousTask(value);
                                   tmp=value;
                               }
                        }else {
                            stopchargeAsynchronousTask();
                            charging.setBackground(getDrawableRes("power" + com.ubt.alpha1e.data.Constant.powerThreshold[value]));
                        }
                    }
                });
    }


    @Override
    public void handleMessage(Bundle bundle) {

      Byte status= bundle.getByte(STATUS_MACHINE);
      //  UbtLog.d(TAG,"STATE MACHINE IS "+status);
        switch (status){
           case Constant.APP_LAUNCH_STATUS:  //启动应用,虚拟形象睡觉姿势
             runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showBuddleText(getString(R.string.buddle_text_init_status));
                       buddleTextAsynchronousTask();
                       if(cartoonAction!=null) {
                           cartoonAction.setBackgroundResource(R.drawable.sleep21);
                           cartoonAction.setBackgroundResource(R.drawable.img_hoem_robot);
                       }
                       hiddenCartoonTouchView();
                       recoveryCartoonBodyUi();
                       hiddenBatteryUi();
                       //showCartoonAction(cartoon_action_sleep);
                   }
               });
               break;
           case Constant.APP_BLUETOOTH_CONNECTED: //蓝牙连接成功,虚拟形象站立姿势
               UbtLog.d(TAG,"APP_CONNECTED"+status);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if(isNetworkConnect){
                           hiddenDisconnectIcon();
                       }
                       if(!cartoon_enable){
                           cartoonAction.setBackgroundResource(R.drawable.main_robot);
                           cartoonAction.setBackgroundResource(R.drawable.img_hoem_robot);
                       }
                       showCartoonAction(cartoon_action_squat);
                       showBuddleText(getString(R.string.buddle_bluetoothConnection));
                      // showBattryUi();
                   }
               });
               break;
           case Constant.APP_BLUETOOTH_CLOSE: //手机端，主动关闭蓝牙，虚拟形象睡觉姿势
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showDisconnectIcon();
                       stopchargeAsynchronousTask();
                       stopBuddleTextAsynchronousTask();
                        showBuddleText(getString(R.string.buddle_text_init_status));
                       //showCartoonAction(cartoon_action_sleep);
                       cartoonAction.setBackgroundResource(R.drawable.sleep21);
                       cartoonAction.setBackgroundResource(R.drawable.img_hoem_robot);
                       recoveryCartoonBodyUi();
                       hiddenBatteryUi();
                   }
               });
               break;
           case Constant.ROBOT_LOW_POWER_LESS_TWENTY_STATUS: //低电量的时候，小于百分之20，弹框处理
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                      // new LowBatteryDialog(getContext()).setBatteryThresHold(LOW_BATTERY_TWENTY_THRESHOLD).builder().show();
                   }
               });
               break;
           case Constant.ROBOT_LOW_POWER_LESS_FIVE_STATUS:  //低电量的时候，小于百分之5，弹框，虚拟形象蹲下，气泡处理
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_aciton_squat_reverse);
                      // new LowBatteryDialog(getContext()).setBatteryThresHold(LOW_BATTERY_FIVE_THRESHOLD).builder().show();
                   }
               });
               for(int i=0;i<3;i++){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           showBuddleText(mPresenter.getBuddleText(Constant.BUDDLE_LOW_BATTERY_TEXT));
                       }
                   });
                   try {
                       Thread.sleep(3000);
                   }catch(InterruptedException e){
                       e.printStackTrace();
                   }
               }
               break;
           case Constant.ROBOT_SLEEP_EVENT: //休眠执行蹲下动作
               runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   showCartoonAction(cartoon_aciton_squat_reverse);
               }
           });
               break;
           case Constant.ROBOT_HIT_HEAD:  //该指令没有使用
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_action_enjoy);
                   }
               });
               break;
           case Constant.ROBOT_WAKEUP_ACTION: //休眠后，虚拟形象站立
               runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCartoonAction(cartoon_action_squat);
                }
            });
            break;
           case Constant.ROBOT_POWEROFF:  //该指令没有使用，通过蓝牙断开回调实现机器人关机的现象
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_action_sleep);
                       recoveryCartoonBodyUi();
                       showDisconnectIcon();
                       stopchargeAsynchronousTask();
                   }
               });
               break;
            case Constant.ROBOT_hand_stand: //机器人被翻转，虚拟形象翻转
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCartoonAction(cartoon_action_hand_stand);
                    }
                });
                break;
            case Constant.ROBOT_fall:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCartoonAction(cartoon_action_fall);
                    }
                });
                break;
            case Constant.ROBOT_default_gesture:
                runOnUiThread(new Runnable() {
                    @Override
                          public void run(){
                            cartoonAction.setBackgroundResource(R.drawable.main_robot);
                    }
                });
            case ROBOT_sleep_gesture:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        if(cartoon_enable) {
                            cartoonAction.setBackgroundResource(R.drawable.squat60);
                        }else {
                            cartoonAction.setBackgroundResource(R.drawable.main_robot);
                        }
                    }
                });
                break;
            case ROBOT_CHARGING:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        if(APP_CURRENT_STATUS!=ROBOT_SLEEP_EVENT) {
                        // showCartoonAction(cartoon_action_squat);
                        //站立
                        cartoonAction.setBackgroundResource(R.drawable.main_robot);
                    }else {
                        //蹲下
                         if(cartoon_enable) {
                             cartoonAction.setBackgroundResource(R.drawable.squat60);
                         }else {
                             cartoonAction.setBackgroundResource(R.drawable.main_robot);
                         }
                    }
                }
                });
                break;
           default:
               UbtLog.d(TAG,"NO SITUATION "+status);
               break;

       }
       APP_CURRENT_STATUS=status;
    }

    private void recoveryLowBatteryFlag(){
       ENTER_LOW_BATTERY_TWENTY=false;
       ENTER_LOW_BATTERY_FIVE=false;
   }

   private void hiddenCartoonTouchView(){
       if(cartoonChest!=null) {
           cartoonChest.setVisibility(View.INVISIBLE);
           cartoonHead.setVisibility(View.INVISIBLE);
           cartoonLeftHand.setVisibility(View.INVISIBLE);
           cartoonRightHand.setVisibility(View.INVISIBLE);
           cartoonLeftLeg.setVisibility(View.INVISIBLE);
           cartoonRightLeg.setVisibility(View.INVISIBLE);
       }
   }
    private void showCartoonTouchView(){
        if(cartoonChest!=null) {
            cartoonChest.setVisibility(View.VISIBLE);
            cartoonHead.setVisibility(View.VISIBLE);
            cartoonLeftHand.setVisibility(View.VISIBLE);
            cartoonRightHand.setVisibility(View.VISIBLE);
            cartoonLeftLeg.setVisibility(View.VISIBLE);
            cartoonRightLeg.setVisibility(View.VISIBLE);
        }
    }
    private void lowBatteryFunction(int currentValue){
        if(currentValue==(LOW_BATTERY_TWENTY_THRESHOLD)){//ROBOT END BATTERY CAPACITY <=5
            if(!ENTER_LOW_BATTERY_TWENTY) {
                looperThread.send(createMessage(ROBOT_LOW_POWER_LESS_TWENTY_STATUS));
                ENTER_LOW_BATTERY_TWENTY=true;
            }
        }else if(currentValue==(LOW_BATTERY_FIVE_THRESHOLD)){//ROBOT END BATTERY CAPACITY <=20
            if(!ENTER_LOW_BATTERY_FIVE) {
                looperThread.send(createMessage(ROBOT_LOW_POWER_LESS_FIVE_STATUS));
                ENTER_LOW_BATTERY_FIVE=true;
            }
        }
    }

  private void recoveryCartoonBodyUi(){
      if(charging!=null) {
          //charging.setBackground(getDrawableRes("charging_normal"));
          //chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
          cartoonBodyTouchBg.setBackground(getDrawableRes("main_robot_background"));
      }
  }
  private void hiddenBatteryUi(){
      if(charging!=null) {
          charging.setVisibility(View.INVISIBLE);
      }
  }
 private void showBatteryUi(){
     if(charging!=null) {
         charging.setVisibility(View.VISIBLE);
     }
 }

  private void showUserPicIcon(){
      UserModel mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
      if(mUserModel != null) {
          UbtLog.d(TAG, "user image picture" + mUserModel.getHeadPic());
          Glide.with(this).load(mUserModel.getHeadPic()).asBitmap().into(topIcon);
      }
  }
  private void hiddenDisconnectIcon(){
      if(topIcon2Disconnect!=null) {
          topIcon2Disconnect.setVisibility(View.INVISIBLE);
          topIcon2Disconnect.clearAnimation();
          topIcon2.clearAnimation();
      }
  }
    private void showDisconnectIcon(){
        if(topIcon2Disconnect!=null) {
            topIcon2Disconnect.setVisibility(View.VISIBLE);
            hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
            hyperspaceJump.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    topIcon2Disconnect.startAnimation(hyperspaceJump);
                    topIcon2.startAnimation(hyperspaceJump);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            topIcon2Disconnect.startAnimation(hyperspaceJump);
            topIcon2.startAnimation(hyperspaceJump);
        }

    }
  private void sendCommandToRobot(String absouteActionPath){
        if(cartoon_enable) {
            byte[] actions = BluetoothParamUtil.stringToBytes(absouteActionPath);
            mPresenter.commandRobotAction(ConstValue.DV_PLAYACTION, actions);
        }else {
            UbtLog.d(TAG,"sendCommandToRobot cartoon disable");
        }
  }
  private void hiddenBuddleTextView(){
      runOnUiThread(new Runnable() {
          @Override
          public void run(){
              if(buddleText != null) {
                  buddleText.setVisibility(View.INVISIBLE);
              }
          }
      });
  }
  private void showBuddleTextView(){
      runOnUiThread(new Runnable() {
          @Override
          public void run(){
              if(buddleText != null){
                  buddleText.setVisibility(View.VISIBLE);
              }
          }
      });
  }
  private void debugClickRegion(){
      if(cartoonHead!=null) {
          cartoonHead.setBackgroundColor(Color.YELLOW);
          cartoonChest.setBackgroundColor(Color.YELLOW);
          cartoonLeftHand.setBackgroundColor(Color.YELLOW);
          cartoonRightHand.setBackgroundColor(Color.YELLOW);
          cartoonLeftLeg.setBackgroundColor(Color.YELLOW);
          cartoonRightLeg.setBackgroundColor(Color.YELLOW);
      }
  }

  private Boolean removeDuplicateClickEvent(){
      UbtLog.d(TAG,"INTERVAL IS "+(System.currentTimeMillis()-mClickTime));
      if(System.currentTimeMillis()-mClickTime<CLICK_THRESHOLD_DUPLICATE){
          mClickTime=System.currentTimeMillis();
          return true;
      }else {
          mClickTime = System.currentTimeMillis();
          return false;
      }
  }

  private void setBehaviourHabitNextEvent(String eventName,String eventTime){
      habitsTime.setText(eventTime);
      habitsName.setText(eventName);
  }

    public void requireBehaviourNextEvent() {
        BaseRequest mBehaviourControlRequest = new BaseRequest();
        doRequestFromServer(BuildConfig.WebServiceUbx+HttpEntity.GET_BEHAVIOURHABIT_NEXTEVENT,mBehaviourControlRequest);
    }
    public void doRequestFromServer(String url, BaseRequest baseRequest) {
        synchronized (this) {
            OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, 999).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }
                @Override
                public void onResponse(String response, int id) {
                    UbtLog.d(TAG, "response = " + response);
                    if(id==999){
                        BaseResponseModel<behaviourHabitModel> baseResponseModel1 = GsonImpl.get().toObject(response, new TypeToken<BaseResponseModel<behaviourHabitModel>>() {
                        }.getType());
                        if(baseResponseModel1.models!=null) {
                            UbtLog.d(TAG, "GET RESOPNSE " + baseResponseModel1.models.eventName );
                            setBehaviourHabitNextEvent(baseResponseModel1.models.eventName,baseResponseModel1.models.eventTime);
                        }
                    }
                }
            });
        }
    }

}

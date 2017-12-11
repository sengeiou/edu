package com.ubt.alpha1e.ui.main;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.ActionTestActivity;
import com.ubt.alpha1e.animator.FrameAnimation;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loopHandler.HandlerCallback;
import com.ubt.alpha1e.base.loopHandler.LooperThread;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netsearchresult.NetSearchResultActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.login.loginauth.LoginAuthActivity;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseOneActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseTwoActivity;
import com.ubt.alpha1e.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.services.SendClientIdService;
import com.ubt.alpha1e.ui.RemoteActivity;
import com.ubt.alpha1e.ui.RemoteSelActivity;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.LowBatteryDialog;
import com.ubt.alpha1e.ui.helper.BluetoothHelper;
import com.ubt.alpha1e.userinfo.mainuser.UserCenterActivity;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;



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
    TextView topIcon;
    @BindView(R.id.top_icon2)
    TextView topIcon2;
    @BindView(R.id.top_icon2_disconnect)
    TextView topIcon2Disconnect;
    @BindView(R.id.top_icon3)
    TextView topIcon3;
    @BindView(R.id.habit_alert)
    EditText habitAlert;
    @BindView(R.id.bottom_icon)
    TextView bottomIcon;
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
    @BindView(R.id.charging_dot)
    ImageView chargingDot;
    @BindView(R.id.touch_control)
    RelativeLayout touchControl;
    private String TAG = "MainActivity";
    int screen_width = 0;
    int screen_height = 0;
    int init_screen_width = 667;
    int init_screen_height = 375;
    RelativeLayout.LayoutParams params;
    private AnimationDrawable frameAnimation;
     FrameAnimation frameAnimationPro;
    private int powerThreshold[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
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
    private int charging_shrink_interval=500;
    private int chargeShrinkTimeout=1000;
    private Timer mBuddleTextTimer;
    private TimerTask mBuddleTextTimeOutTask;
    private LooperThread looperThread;
    Timer mChargetimer;
    TimerTask mChargingTimeoutTask;
    private byte mChargeValue=0;
    private int mPowerValue=0;
    long mCurrentTouchTime=0;
    //ONE MINUTE
    private long noOperationTimeout=1*60*1000;
    private String STATUS_MACHINE="status_machine";
    private final byte APP_LAUNCH_STATUS=0x01;
    private final byte APP_BLUETOOTH_CONNECTED =0x02;
    private final byte APP_BLUETOOTH_CLOSE=0x03;
    private final byte ROBOT_LOW_POWER_LESS_TWENTY_STATUS =0x04;
    private final byte ROBOT_LOW_POWER_LESS_FIVE_STATUS =0x05;
    private final byte ROBOT_SLEEP_EVENT=0x06;
    private final byte ROBOT_HIT_HEAD=0x07;
    private final byte ROBOT_WAKEUP_ACTION =0x08;
    private final byte ROBOT_POWEROFF=0x09;
    private final byte ROBOT_hand_stand =0x0a;
    private final byte ROBOT_fall =0x0b;
    private final byte ROBOT_CHARGING=0x0c;
    private final byte ROBOT_default_gesture =0x0d;
    private final int LOW_BATTERY_TWENTY_THRESHOLD=20;
    private final int LOW_BATTERY_FIVE_THRESHOLD=5;
    private  boolean ENTER_LOW_BATTERY_FIVE=false;
    private  boolean ENTER_LOW_BATTERY_TWENTY=false;
    private int index_one_vol=1; //volatage
    private int index_two_charging=2; //charging or uncharging
    private int index_three_powerPercent=3;//power value
    private BluetoothHelper mBtHelper;
    private int APP_CURRENT_STATUS=-1;
    private int DV_COMMON_COMMAND_POWEROFF_ZERO=1;
    private int DV_COMMON_COMMAND_POWEROFF_ONE=0;
    private int DV_COMMON_COMMAND_POWEROFF_TWO=0;
    private int Cartoon_animation_last_execute=0;
   // private boolean  app_bluetooth_conencted_executed=false;
    private boolean IS_CHARGING=false;
    private String current_mac_address="";
    private String FilePath="action/avatar/";
    private String waveRightArm = "wave the right arm.hts"; //摆右手臂
    private String waveRightLeg = "wave the right leg.hts";  //摆动右腿
    private String waveLeftArm ="wave the left arm.hts";   //摆左手臂
    private String waveLeftLeg = "wave the left leg.hts";  //摆动左腿
    private String upsideDown = "upside-down.hts"; //倒立
    private String squatTuch = "Squat Tuck.hts"; //蹲下抱膝
    private String squinting_satifying = "Squinting satisfying.hts"; //眯眼享受
    private String squintLaugh = "Squint laugh.hts"; // 眯眼笑
    private String trembling = "trembling.hts";  //  瑟瑟发抖
    private String fallDown = "fall down.hts"; // 摔倒
    private String sleepState = "sleep.hts"; //  睡觉
    private String sayHi = "say hi.hts";   //打招呼
    boolean isBtConnect =false;
    boolean isNetworkConnect=false;
    String WakeUpActionName="初始化";
    private int ROBOT_HEAD_UP_STAND=1;
    private int ROBOT_HEAD_DOWN=2;
    private int ROBOT_LEFT_SHOULDER_SLEEP=3;
    private int ROBOT_RIGHT_SHOULDER_SLEEP=4;
    private int ROBOT_HEAD_UP_SLEEP=5;
    private int ROBOT_HEAD_DOWN_SLEEP=6;
    private int CARTOON_FRAME_INTERVAL=4;
    boolean ANIMAITONSOLUTIONOOM=true;
    boolean animation_running=false;
    private int ROBOT_CHARGING_STATUS=0x01;
    private int ROBOT_UNCHARGE_STATUS=0x0;
    private int ROBOT_CHARGING_ENOUGH_STATUS=0x03;
    private int CURRENT_ACTION_NAME=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG,"onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentTouchTime=System.currentTimeMillis();
        getScreenInch();
        initUI();
        mHelper=MainUiBtHelper.getInstance(getContext());
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);
        looperThread = new LooperThread(this);
        looperThread.start();

        // 启动发送clientId服务
        SendClientIdService.startService(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UbtLog.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG,"onResume");
        initUI();
        if(!isBulueToothConnected()){
             showDisconnectIcon();
            looperThread.send(createMessage(APP_LAUNCH_STATUS));
           // looperThread.send(createMessage(ROBOT_LOW_POWER_LESS_FIVE_STATUS));
              m_Handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AutoScanConnectService.startService(MainActivity.this); //add by dicy.cheng 打开自动连接
                }
             },100);
        }else {
            MainUiBtHelper.getInstance(getContext()).readNetworkStatus();
            if(cartoonAction != null) {
               looperThread.send(createMessage(ROBOT_default_gesture));
                if(MainActivity.this != null && ((AlphaApplication) MainActivity.this.getApplication()).getmCurrentNetworkInfo() != null){
                    NetworkInfo networkInfo = ((AlphaApplication) MainActivity.this.getApplication()).getmCurrentNetworkInfo();
                    if(networkInfo.status){
                        hiddenDisconnectIcon();
                    }else {
                        showDisconnectIcon();
                    }
                }else {
                    showDisconnectIcon();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBroadcastReceiver1!=null) {
            getContext().unregisterReceiver(mBroadcastReceiver1);
        }
        SendClientIdService.doStopSelf();
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        ((AlphaApplication) getContext().getApplicationContext())
                                .setCurrentBluetooth(null);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                }

            }
        }
    };


    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.right_icon, R.id.right_icon2, R.id.right_icon3,
            R.id.right_icon4, R.id.cartoon_chest, R.id.cartoon_head, R.id.cartoon_left_hand,
            R.id.cartoon_right_hand, R.id.cartoon_left_leg, R.id.cartoon_right_leg,R.id.bottom_icon})
    protected void switchActivity(View view) {
        UbtLog.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.top_icon:
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
                break;
            case R.id.top_icon2:
                gotoConnectBluetooth();
                break;
            case R.id.top_icon3:
                if(isBulueToothConnected()) {
                try {
                    CommonCtrlView.getInstace(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                    showBluetoothConnectDialog();
            }
                break;
            case R.id.right_icon:
                if(isBulueToothConnected()){
                    mLaunch.setClass(this, RemoteSelActivity.class);
                    //startActivity(new Intent(this, ActionTestActivity.class));
                    startActivity(mLaunch);
                }else {
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.right_icon2:
                if(isBulueToothConnected()){
                    startActivity(new Intent(this, ActionTestActivity.class));
                    this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                }else{
                    showBluetoothConnectDialog();
                }
                break;
            case R.id.right_icon3:
                startActivity(new Intent(this, BlocklyActivity.class));
                this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                break;
            case R.id.right_icon4:
                ToastUtils.showShort("程序猿正在施工中！！！");
                break;
            case R.id.cartoon_head:
                UbtLog.d(TAG, "click head");
                sendCommandToRobot(FilePath+squinting_satifying);
                showCartoonAction(cartoon_action_enjoy);
                break;
            case R.id.cartoon_chest:
                UbtLog.d(TAG, "click chest");
                sendCommandToRobot(FilePath+squintLaugh);
                showCartoonAction(cartoon_action_smile);
                break;
            case R.id.cartoon_left_hand:
                UbtLog.d(TAG, "click left hand");
                sendCommandToRobot(FilePath+waveRightArm);
                showCartoonAction(cartoon_action_swing_left_hand);
                break;
            case R.id.cartoon_right_hand:
                UbtLog.d(TAG, "click right hand");
                sendCommandToRobot(FilePath+waveLeftArm);
                showCartoonAction(cartoon_action_swing_right_hand);
                break;
            case R.id.cartoon_left_leg:
                UbtLog.d(TAG, "click left leg");
                sendCommandToRobot(FilePath+waveRightLeg);
                showCartoonAction(cartoon_action_swing_left_leg);
                break;
            case R.id.cartoon_right_leg:
                UbtLog.d(TAG, "click right leg");
                sendCommandToRobot(FilePath+waveLeftLeg);
                showCartoonAction(cartoon_action_swing_right_leg);
                break;
            case R.id.bottom_icon:
                if (isBulueToothConnected()) {
                    startActivity(new Intent(this, MainCourseActivity.class));
                    this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                } else {
                    showBluetoothConnectDialog();
                }
                break;
            default:
                break;
        }
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog(){
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
        isBtConnect = isBulueToothConnected();
        startActivityForResult(bluetoothConnectIntent, 100);

        if(AppManager.getInstance().currentActivity() != null
                && (AppManager.getInstance().currentActivity() instanceof PrincipleActivity
                || AppManager.getInstance().currentActivity() instanceof SplitActivity
                || AppManager.getInstance().currentActivity() instanceof MergeActivity
                || AppManager.getInstance().currentActivity() instanceof FeatureActivity)){
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
        UbtLog.d(TAG,"onLostBtConn");
    }
    ConfirmDialog dialog = null ;
    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.NETWORK_STATUS) {
            NetworkInfo networkInfo = (NetworkInfo)  event.getNetworkInfo();
            UbtLog.d(TAG,"networkInfo == " + networkInfo.status);
            if(MainActivity.this != null){
                ((AlphaApplication) MainActivity.this.getApplication()).setmCurrentNetworkInfo(networkInfo);
            }
            isNetworkConnect=networkInfo.status;
            if(isNetworkConnect){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenDisconnectIcon();
                    }
                });
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDisconnectIcon();
                    }
                });
            }
        }else if(event.getEvent()== RobotEvent.Event.DISCONNECT){//Bluetooth Disconect
            UbtLog.d(TAG,"DISCONNECTED ");
            if(MainUiBtHelper.getInstance(getContext()).isLostCoon()){
                UbtLog.d(TAG,"mainactivity isLostCoon");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        looperThread.send(createMessage(APP_BLUETOOTH_CLOSE));

                        if(AppManager.getInstance().currentActivity() != null){
                            UbtLog.d(TAG, "onLostBtCoon " + "  不为空"+ AppManager.getInstance().currentActivity());
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
                                    ||mActivity instanceof ActionTestActivity
                                    ||mActivity instanceof ActionCourseActivity
                                    ||mActivity instanceof CourseLevelActivity
                                    ||mActivity instanceof NetconnectActivity
                                    ||mActivity instanceof NetSearchResultActivity
                                    ||mActivity instanceof CourseTwoActivity
                                    ||mActivity instanceof CourseOneActivity
                            )) {
                                return;
                            }
                            if(dialog != null){
                                dialog.dismiss();
                                dialog = null;
                            }
                            if(AppManager.getInstance().currentActivity() instanceof NetconnectActivity || AppManager.getInstance().currentActivity()instanceof NetSearchResultActivity){
                                AppManager.getInstance().finishActivity();
                                return;
                            }

                            showBluetoothDisconnect();
                        }else {
                            UbtLog.d(TAG, "onLostBtCoon " + "  为空");
                        }
                    }
                });
            }
        }else if(event.getEvent()== RobotEvent.Event.CONNECT_SUCCESS){
            UbtLog.d(TAG,"mainactivity CONNECT_SUCCESS 1");
            if(!MainUiBtHelper.getInstance(getContext()).isLostCoon()){
                UbtLog.d(TAG,"mainactivity CONNECT_SUCCESS 2");
                MainUiBtHelper.getInstance(getContext()).readNetworkStatus();
                looperThread.send(createMessage(APP_BLUETOOTH_CONNECTED));

            }
        }

    }

    void showBluetoothDisconnect(){
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
        if(requestCode == 100 ){
            if(isBtConnect !=isBulueToothConnected()) {
                if (isBulueToothConnected()) {
                    looperThread.send(createMessage(APP_BLUETOOTH_CONNECTED));
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        UbtLog.d(TAG, "X ,Y  " + (event.getX() + "," + event.getY()));
        mCurrentTouchTime=System.currentTimeMillis();
        if(System.currentTimeMillis()-mCurrentTouchTime<noOperationTimeout) {
            hiddenBuddleTextView();
        }
        return super.onTouchEvent(event);
    }

    private Handler m_Handler = new Handler();


    public void showCartoonAction_original(final int value) {
        try {
            mCurrentTouchTime = System.currentTimeMillis();
            if (System.currentTimeMillis() - mCurrentTouchTime < noOperationTimeout) {
                hiddenBuddleTextView();
            }
            loadAnimationResources(value);
            // Type casting the Animation drawable
            frameAnimation = (AnimationDrawable) cartoonAction.getBackground();
            int time = frameAnimation.getNumberOfFrames() * frameAnimation.getDuration(0);
            UbtLog.d(TAG, "Animation time is " + time);
            //set true if you want to animate only once
            frameAnimation.setOneShot(true);
            if (!frameAnimation.isRunning()) {
                frameAnimation.start();
                 checkIfAnimationDone(frameAnimation);
            } else {
                UbtLog.d(TAG, "CURRENT ANIMAITON IS RUNNING, SO DONOT EXECUTE " + value);
            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }

    }
    private void showCartoonAction_performance(final int value ){
        if(animation_running&&CURRENT_ACTION_NAME==value){
            UbtLog.d(TAG,"animation is execution");
            return;
        }
        frameAnimationPro = new FrameAnimation(cartoonAction, getCartoonRes(value), CARTOON_FRAME_INTERVAL, false);
        frameAnimationPro.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                UbtLog.d(TAG, "start");
                animation_running=true;
            }
            @Override
            public void onAnimationEnd() {
                UbtLog.d(TAG, "end");
                animation_running=false;
            }
            @Override
            public void onAnimationRepeat() {
                UbtLog.d(TAG, "repeat");
                frameAnimationPro.pauseAnimation();
            }
        });
        CURRENT_ACTION_NAME=value;
    }
    @Override
    public void showCartoonAction(final int value ){
        if (ANIMAITONSOLUTIONOOM) {
            showCartoonAction_performance(value);
        } else {
            //Some time OOM BUG
            showCartoonAction_original(value);
        }
    }

    public int[] getCartoonRes(int value ){
        String actionName="";
        TypedArray typedArray=null;
        int[] resId={0};
        switch (value) {
            case cartoon_action_enjoy:
                typedArray = getResources().obtainTypedArray(R.array.enjoy);
                actionName="enjoy";
                break;
            case cartoon_action_fall:
                typedArray = getResources().obtainTypedArray(R.array.fall);
                actionName="fall";
                break;
            case cartoon_action_greeting:
                typedArray = getResources().obtainTypedArray(R.array.greetting);
                actionName="greetting";
                break;
            case cartoon_action_hand_stand:
                typedArray = getResources().obtainTypedArray(R.array.hand_stand);
                actionName="hand_stand";
                break;
            case cartoon_action_hand_stand_reverse:
                typedArray = getResources().obtainTypedArray(R.array.hand_stand_reverse);
                actionName="hand_stand_reverse";
                break;
            case cartoon_action_smile:
                typedArray = getResources().obtainTypedArray(R.array.smile);
                actionName="smile";
                break;
            case cartoon_action_squat:
                typedArray = getResources().obtainTypedArray(R.array.squat);
                actionName="squat";
                break;
            case cartoon_aciton_squat_reverse:
                typedArray = getResources().obtainTypedArray(R.array.squat_reverse);
                actionName="squat_reverse";
                break;
            case cartoon_action_shiver:
                typedArray = getResources().obtainTypedArray(R.array.shiver);
                actionName="shiver";
                break;
            case cartoon_action_swing_left_hand:
                typedArray = getResources().obtainTypedArray(R.array.swing_lefthand);
                actionName="left_hand";
                break;
            case cartoon_action_swing_left_leg:
                typedArray = getResources().obtainTypedArray(R.array.swing_leftleg);
                actionName="left_leg";
                break;
            case cartoon_action_swing_right_hand:
                typedArray = getResources().obtainTypedArray(R.array.swing_righthand);
                actionName="right_hand";
                break;
            case cartoon_action_swing_right_leg:
                typedArray = getResources().obtainTypedArray(R.array.swing_rightleg);
                actionName="right_leg";
                break;
            default:
                break;
        }
        UbtLog.d(TAG, "ACTIO NAME IS " + actionName);
        if(typedArray!=null) {
            Cartoon_animation_last_execute = value;
            if (value == cartoon_aciton_squat_reverse || value == cartoon_action_sleep) {
                hiddenCartoonTouchView();
            } else {
                showCartoonTouchView();
            }
            int len = typedArray.length();
           resId = new int[len];
            for (int i = 0; i < len; i++) {
                resId[i] = typedArray.getResourceId(i, -1);
            }
            typedArray.recycle();
            return resId;
        }else {
           return resId ;
        }

    }

    private void checkIfAnimationDone(AnimationDrawable anim){
        final AnimationDrawable mAnimation = anim;
        int timeBetweenChecks = mAnimation.getDuration(0);
        Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                if (mAnimation.getCurrent() != mAnimation.getFrame(mAnimation.getNumberOfFrames() - 1)){
                    checkIfAnimationDone(mAnimation);
                    //UbtLog.d(TAG, "stop the animation recover");
                } else{
                   // UbtLog.d(TAG, "stop the animation");
                    mAnimation.stop();
                }
            }
        }, timeBetweenChecks);
    };

    public void buddleTextAsynchronousTask() {
        stopBuddleTextAsynchronousTask();
        mBuddleTextTimer= new Timer();
        mBuddleTextTimeOutTask= new TimerTask() {
            @Override
            public void run() {
                if(System.currentTimeMillis()-mCurrentTouchTime>noOperationTimeout) {
                    if (looperThread != null) {
                        looperThread.post(new Runnable() {
                            public void run() {
                                try {
                                    // boolean isUiThread = Looper.getMainLooper().getThread() == Thread.currentThread();
                                    //UbtLog.d(TAG,"current random show ? "+APP_CURRENT_STATUS);
                                   if(APP_CURRENT_STATUS!=APP_LAUNCH_STATUS) {
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               randomBuddleText();
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
   private void stopBuddleTextAsynchronousTask(){
       if (mBuddleTextTimeOutTask != null){
           mBuddleTextTimeOutTask.cancel();
           mBuddleTextTimeOutTask = null;
           mBuddleTextTimer.purge();
           mBuddleTextTimer=null;
       }
   }
    public void chargeAsynchronousTask() {
       mChargetimer= new Timer();
        mChargingTimeoutTask= new TimerTask() {
            @Override
            public void run() {
                looperThread.post(new Runnable() {
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                   if(IS_CHARGING) {
                                       if(charging!=null) {
                                           charging.setBackground(getDrawableRes("charging"));
                                           chargingDot.setBackground(getDrawableRes("charging_dot"));
                                       }
                                   }
                                }
                            });
                            Thread.sleep(charging_shrink_interval);
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    if(IS_CHARGING) {
                                        if(charging!=null) {
                                            charging.setBackground(getDrawableRes("charging"));
                                            chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
                                        }
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
    private void stopchargeAsynchronousTask(){
        if (mChargingTimeoutTask != null){
            mChargingTimeoutTask.cancel();
            mChargingTimeoutTask= null;
            mChargetimer.purge();
            mChargetimer=null;
        }

    }

    private void showBuddleText(String text) {
        if(buddleText!=null) {
            buddleText.setText("\u3000" + text);
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
    public void showBluetoothStatus(String status) {
    }

    @Override
    public void showCartoonText(String text) {
    }

    @Override
    protected void initUI() {
        //course center icon
        int course_icon_margin_left = 22;
        int course_icon_margin_top = 260;
        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) bottomIcon.getLayoutParams();
        rlParams.leftMargin = getAdaptiveScreenX(course_icon_margin_left);
        rlParams.topMargin = getAdaptiveScreenY(course_icon_margin_top);
        bottomIcon.setLayoutParams(rlParams);
        bottomIcon.setVisibility(View.VISIBLE);
        //Buddle Text
        int buddle_text_margin_left = 388;
        int buddle_text_margin_top = 116;//height*0.31
        RelativeLayout.LayoutParams buddleParams = (RelativeLayout.LayoutParams) buddleText.getLayoutParams();
        buddleParams.leftMargin = getAdaptiveScreenX(buddle_text_margin_left);
        buddleParams.topMargin = getAdaptiveScreenY(buddle_text_margin_top);
        buddleText.setLayoutParams(buddleParams);

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
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
//        UbtLog.d(TAG, "RECEIVE THE MESSAGE IN MAIN THREAD" + event.message);
        //mPresenter.dealMessage(event.message);
        try {
            JSONObject mMessage = new JSONObject(event.message);
//            if(!mMessage.getString("mac").equals(current_mac_address)){
//                app_bluetooth_conencted_executed=false;
//            }
            String mParam=mMessage.getString("param");
            final byte[] mParams = Base64.decode(mParam, Base64.DEFAULT);
            current_mac_address=mMessage.getString("mac");
            int mCmd = Integer.parseInt(mMessage.getString("cmd"));
            if(mCmd<0){
                mCmd=255+mCmd;
            }
          //  UbtLog.d(TAG,"CMD IS  "+mCmd);
          if(mCmd==ConstValue.DV_TAP_HEAD) {
              //looperThread.send(createMessage(ROBOT_HIT_HEAD));
          }else if(mCmd==ConstValue.DV_6D_GESTURE){
               UbtLog.d(TAG,"DV_6D_GESTURE index[0]:"+mParams[0]);
              if(mParams[0]==ROBOT_HEAD_UP_STAND){
                 looperThread.send(createMessage(ROBOT_default_gesture));
              } else if(mParams[0]==ROBOT_HEAD_DOWN) {
                  looperThread.send(createMessage(ROBOT_hand_stand));
              }else if(mParams[0]==ROBOT_LEFT_SHOULDER_SLEEP||mParams[0]==ROBOT_RIGHT_SHOULDER_SLEEP||mParams[0]==ROBOT_HEAD_UP_SLEEP||mParams[0]==ROBOT_HEAD_DOWN_SLEEP){
                   looperThread.send(createMessage(ROBOT_fall));
              }
          }else if (mCmd == ConstValue.DV_SLEEP_EVENT) {
              UbtLog.d(TAG, "ROBOT SLEEP EVENT");
                looperThread.send(createMessage(ROBOT_SLEEP_EVENT));
            } else if (mCmd == ConstValue.DV_LOW_BATTERY) {
              UbtLog.d(TAG, "ROBOT LOW BATTERY");
              UbtLog.d(TAG,"LOW BATTERY +"+mParams[0]);
             // lowBatteryFunction(mParams[0]);
            } else if (mCmd == ConstValue.DV_READ_BATTERY) {
                batteryUiShow(mParam);
            } else if(mCmd==ConstValue.DV_COMMON_COMMAND) {
              UbtLog.d(TAG,"DV COMMON COMMAND [0]: +"+mParams[0] +"index [1]: "+mParams[1]+"index [2]: " +mParams[2]);
//              if(mParams[0]==DV_COMMON_COMMAND_POWEROFF_ZERO){
//                  if(mParams[1]==DV_COMMON_COMMAND_POWEROFF_ONE){
//                      if(mParams[2]==DV_COMMON_COMMAND_POWEROFF_TWO){
//                          looperThread.send(createMessage(ROBOT_POWEROFF));
//                      }
//                  }
//              }
            }else if(mCmd==ConstValue.DV_CURRENT_PLAY_NAME){
              try {
                String actionName=new String(mParams, 1, mParams.length-1, "utf-8");
                UbtLog.d(TAG,"ACTION NAME IS "+actionName);
                  if(APP_CURRENT_STATUS==ROBOT_SLEEP_EVENT) {
                      if (actionName.contains(WakeUpActionName)) {
                          looperThread.send(createMessage(ROBOT_WAKEUP_ACTION));
                      }
                  }
              }catch(UnsupportedEncodingException e){
                  e.printStackTrace();
              }

          } else {
            //  UbtLog.d(TAG, "ROBOT OTHER SITUATION" + mCmd);
          }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        AutoScanConnectService.doEntryManalConnect(true);
//        AutoScanConnectService.doStopSelf();
        super.onStop();
    }

    private int powerStatusUpdate(byte mParam) {
        //UbtLog.d(TAG, "POWER VALUE " + mParam);
        int power_index = 0;
        if (mParam < powerThreshold[powerThreshold.length / 2]) {
            for (int j = 0; j < powerThreshold.length / 2; j++) {
                if (mParam < powerThreshold[j + 1] && mParam > powerThreshold[j]) {
                        power_index = j;
                        break;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                    break;
                }
            }
        } else {
            for (int j = powerThreshold.length / 2; j < powerThreshold.length; j++) {
                if (powerThreshold[j] < mParam && mParam < powerThreshold[j+1]) {
                    power_index = j;
                    break;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                    break;
                }
            }
        }
       // UbtLog.d(TAG, "Current power is " + power_index);
        if(cartoonBodyTouchBg!=null) {
            if(mParam==LOW_BATTERY_TWENTY_THRESHOLD){
                cartoonBodyTouchBg.setBackground(getDrawableRes("power" + powerThreshold[power_index-1]));
            }else {
                cartoonBodyTouchBg.setBackground(getDrawableRes("power" + powerThreshold[power_index]));
            }
        }
        return power_index;
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


    private int getAdaptiveScreenX(int init_x) {
        return init_x * screen_width / init_screen_width;
    }

    private int getAdaptiveScreenY(int init_y) {
        return init_y * screen_height / init_screen_height;
    }

   private Message createMessage(byte cmd) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByte(STATUS_MACHINE,cmd);
        message.setData(bundle);
        return message;
    }


    @Override
    public void handleMessage(Bundle bundle) {

      Byte status= bundle.getByte(STATUS_MACHINE);
        UbtLog.d(TAG,"STATE MACHINE IS "+status);
        switch (status){
           case APP_LAUNCH_STATUS:  //启动应用,虚拟形象睡觉姿势
             runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       recoveryBatteryUi();
                       showBuddleText("开机来叫醒沉睡的alpha吧");
                       cartoonAction.setBackgroundResource(R.drawable.sleep21);
                       hiddenCartoonTouchView();
                       //showCartoonAction(cartoon_action_sleep);
                   }
               });
               break;
           case APP_BLUETOOTH_CONNECTED: //蓝牙连接成功,虚拟形象站立姿势
               UbtLog.d(TAG,"APP_CONNECTED"+status);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if(isNetworkConnect){
                           hiddenDisconnectIcon();
                       }
                       showCartoonAction(cartoon_action_squat);
                       showBuddleText("嗨，我是阿尔法");
                       buddleTextAsynchronousTask();


                   }
               });
               break;
           case APP_BLUETOOTH_CLOSE: //手机端，主动关闭蓝牙，虚拟形象睡觉姿势
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showDisconnectIcon();
                       stopchargeAsynchronousTask();
                       stopBuddleTextAsynchronousTask();
                       showBuddleText("开机来叫醒沉睡的alpha吧");
                       //showCartoonAction(cartoon_action_sleep);
                       cartoonAction.setBackgroundResource(R.drawable.sleep21);
                       recoveryBatteryUi();
                   }
               });
               break;
           case ROBOT_LOW_POWER_LESS_TWENTY_STATUS: //低电量的时候，小于百分之20，弹框处理
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       new LowBatteryDialog(getContext()).setBatteryThresHold(LOW_BATTERY_TWENTY_THRESHOLD).builder().show();
                   }
               });
               break;
           case ROBOT_LOW_POWER_LESS_FIVE_STATUS:  //低电量的时候，小于百分之5，弹框，虚拟形象蹲下，气泡处理
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_aciton_squat_reverse);
                       new LowBatteryDialog(getContext()).setBatteryThresHold(LOW_BATTERY_FIVE_THRESHOLD).builder().show();
                   }
               });
               for(int i=0;i<3;i++){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           lowBatteryBuddleText();
                       }
                   });
                   try {
                       Thread.sleep(3000);
                   }catch(InterruptedException e){
                       e.printStackTrace();
                   }
               }
               break;
           case ROBOT_SLEEP_EVENT: //休眠执行蹲下动作
               runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   showCartoonAction(cartoon_aciton_squat_reverse);
               }
           });
               break;
           case ROBOT_HIT_HEAD:  //该指令没有使用
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_action_enjoy);
                   }
               });
               break;
           case ROBOT_WAKEUP_ACTION: //休眠后，虚拟形象站立
               runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCartoonAction(cartoon_action_squat);
                }
            });
            break;
           case ROBOT_POWEROFF:  //该指令没有使用，通过蓝牙断开回调实现机器人关机的现象
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showCartoonAction(cartoon_action_sleep);
                       recoveryBatteryUi();
                       showDisconnectIcon();
                       stopchargeAsynchronousTask();
                   }
               });
               break;
            case ROBOT_hand_stand: //机器人被翻转，虚拟形象翻转
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCartoonAction(cartoon_action_hand_stand);
                    }
                });
                break;
            case ROBOT_fall:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCartoonAction(cartoon_action_fall);
                    }
                });
                break;
            case ROBOT_default_gesture:
                runOnUiThread(new Runnable() {
                    @Override
                          public void run(){
                            cartoonAction.setBackgroundResource(R.drawable.main_robot);
                    }
                });
                break;
            case ROBOT_CHARGING:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        showCartoonAction(cartoon_action_squat);
                    }
                });
                break;
           default:
               UbtLog.d(TAG,"NO SITUATION "+status);
               break;

       }
       APP_CURRENT_STATUS=status;
    }

  private void lowBatteryBuddleText(){
      Random random = new Random();
      UbtLog.i(TAG, "LOW_POWER_LESS_TWENTY:   ");
      String[] arrayText = getResources().getStringArray(R.array.mainUi_buddle_lowpower);
      int select = random.nextInt(arrayText.length);
      String text = arrayText[select];
      showBuddleText(text );
  }
  private void randomBuddleText(){
      Random random = new Random();
     // UbtLog.i(TAG, "randomBuddleText :   " + buddleTextTimeout + "main thread " );
      String[] arrayText = getResources().getStringArray(R.array.mainUi_buddle_text);
      int select = random.nextInt(arrayText.length);
      final String text = arrayText[select];
      showBuddleText(text );
  }
  private void batteryUiShow(String param){
      final byte[] mParams = Base64.decode(param, Base64.DEFAULT);
      for (int i = 0; i < mParams.length; i++) {
        // UbtLog.d(TAG, "index " + i + "value :" + mParams[i]);
          if (mParams[index_two_charging] == ROBOT_CHARGING_STATUS&&mParams[index_two_charging]!=mChargeValue) {
              IS_CHARGING=true;
              UbtLog.d(TAG, " IS CHARGING ");
              looperThread.send(createMessage(ROBOT_CHARGING));
              recoveryLowBatteryFlag();
              chargeAsynchronousTask();
          } else if(mParams[index_two_charging]==ROBOT_UNCHARGE_STATUS&&mParams[index_two_charging]!=mChargeValue) {
              IS_CHARGING=false;
              stopchargeAsynchronousTask();
              UbtLog.d(TAG,"NOT CHARGING");
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if(charging!=null) {
                          charging.setBackground(getDrawableRes("charging_normal"));
                          chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
                      }
                  }
              });
          }else if(mParams[index_two_charging]==ROBOT_CHARGING_ENOUGH_STATUS&&mParams[index_two_charging]!=mChargeValue){
              stopchargeAsynchronousTask();
              UbtLog.d(TAG,"BATTERY ENOUGH AND PLUG IN CHARGING");
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if(charging!=null) {
                          charging.setBackground(getDrawableRes("charging"));
                          chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
                      }
                  }
              });

          }
          runOnUiThread(new Runnable(){
              @Override
              public void run() {
                  powerStatusUpdate(mParams[index_three_powerPercent]);
                  if(mParams[index_two_charging]==0){
                      lowBatteryFunction(mParams[index_three_powerPercent]);
                  }
              }
          });
          mChargeValue=mParams[index_two_charging];
          mPowerValue=mParams[index_three_powerPercent];

      }
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

  private void recoveryBatteryUi(){
      if(charging!=null) {
          charging.setBackground(getDrawableRes("charging_normal"));
          chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
          cartoonBodyTouchBg.setBackground(getDrawableRes("main_robot_background"));
      }
  }

  private void showUserPicIcon(){
      UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
      UbtLog.d(TAG,"user image picture"+userModel.getHeadPic());
//      if(topIcon!=null){
//      }
  }
  private void hiddenDisconnectIcon(){
      if(topIcon2Disconnect!=null)
      topIcon2Disconnect.setVisibility(View.INVISIBLE);
  }
    private void showDisconnectIcon(){
        if(topIcon2Disconnect!=null)
        topIcon2Disconnect.setVisibility(View.VISIBLE);
    }
  private void sendCommandToRobot(String absouteActionPath){
      byte[] actions = BluetoothParamUtil.stringToBytes(absouteActionPath);
      mPresenter.commandRobotAction(ConstValue.DV_PLAYACTION,actions);
  }
  private void hiddenBuddleTextView(){
      if(buddleText == null){
          return;
      }
      buddleText.setVisibility(View.INVISIBLE);
  }
  private void showBuddleTextView(){
      if(buddleText == null){
          return;
      }
      buddleText.setVisibility(View.VISIBLE);
  }

  private String loadAnimationResources(int value ){
      String actionName="";

          if (value == cartoon_action_swing_left_hand) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_left_hand);
              }
              actionName = "swing_left_hand";
          } else if (value == cartoon_action_swing_right_hand) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.swigrighthand);
              }
              actionName = "swing_right_hand";
          } else if (value == cartoon_action_swing_left_leg) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_swing_leftleg);
              }
              actionName = "swing_left_leg";
          } else if (value == cartoon_action_swing_right_leg) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_right_leg);
              }
              actionName = "swing_right_leg";
          } else if (value == cartoon_action_hand_stand) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_hand_stand);
              }
              actionName = "hand_stand";
          } else if (value == cartoon_action_hand_stand_reverse) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_hand_stand_reverse);
              }
              actionName = "hand_stand_reverse";
          } else if (value == cartoon_action_squat) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_squat);
              }
              actionName = "squat";
          } else if (value == cartoon_action_enjoy) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_enjoy);
              }
              actionName = "enjoy";
          } else if (value == cartoon_action_fall) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_fall);
              }
              actionName = "fall";
          } else if (value == cartoon_action_greeting) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_greeting);
              }
              actionName = "greeting";
          } else if (value == cartoon_action_shiver) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_shiver);
              }
              actionName = "shiver";
          } else if (value == cartoon_action_sleep) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_sleep);
              }
              actionName = "sleep";
          } else if (value == cartoon_action_smile) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_smile);
              }
              actionName = "smile enjoy";
          } else if (value == cartoon_aciton_squat_reverse) {
              if(cartoonAction!=null) {
                  cartoonAction.setBackgroundResource(R.drawable.cartoon_squat_reverse);
              }
              actionName = "squat_reverse";
          }
          Cartoon_animation_last_execute = value;
          if (value == cartoon_aciton_squat_reverse || value == cartoon_action_sleep) {
              hiddenCartoonTouchView();
          } else {
              showCartoonTouchView();
          }
          UbtLog.d(TAG, "ACTIO NAME IS " + actionName);
      return actionName;
  }
}

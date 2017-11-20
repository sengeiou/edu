package com.ubt.alpha1e.ui.main;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.loopHandler.HandlerCallback;
import com.ubt.alpha1e.base.loopHandler.LooperThread;
import com.ubt.alpha1e.blockly.LedColorEnum;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.login.loginauth.LoginAuthActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.userinfo.mainuser.UserCenterActivity;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;
import com.zyyoona7.lib.EasyPopup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
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
    private EasyPopup mCirclePop;
    private int powerThreshold[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
    int index = 0;
    private int cartoon_action_swing_right_leg = 0;
    private int cartoon_action_swing_left_leg = 1;
    private int cartoon_action_swing_right_hand = 2;
    private int cartoon_action_swing_left_hand = 3;
    private int cartoon_action_hand_stand = 4;
    private int cartoon_action_hand_stand_reverse = 5;
    private int cartoon_aciton_squat = 6;
    private int cartoon_action_enjoy = 7;
    private int cartoon_action_fall = 8;
    private int cartoon_action_greeting = 9;
    private int cartoon_action_shiver = 10;
    private int cartoon_action_sleep = 11;
    private int cartoon_action_smile = 12;
    private int buddleTextTimeout = 5000;//5s
    private int charging_shrink_interval=1000;
    private int chargeShrinkTimeout=2000;
    private Timer mBuddleTextTimer;
    private TimerTask mBuddleTextTimeOutTask;
    private LooperThread looperThread;
    Timer mChargetimer;
    TimerTask mChargingTimeoutTask;
    private byte mChargeValue=0;
    long mCurrentTouchTime=0;
    private long noOperationTimeout=1*60*1000;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTouchTime=System.currentTimeMillis();
        getScreenInch();
        initUI();
        mHelper=MainUiBtHelper.getInstance(getContext());
        looperThread = new LooperThread(this);
        looperThread.start();
        buddleTextAsynchronousTask();


    }

    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.right_icon, R.id.right_icon2, R.id.right_icon3,
            R.id.right_icon4, R.id.cartoon_chest, R.id.cartoon_head, R.id.cartoon_left_hand,
            R.id.cartoon_right_hand, R.id.cartoon_left_leg, R.id.cartoon_right_leg})
    protected void switchActivity(View view) {
        Log.d(TAG, "VIEW +" + view.getTag());
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
                cartoonAction.setVisibility(View.VISIBLE);
                if (index >= 12) {
                    index = 0;
                }
                LedColorEnum colorEnum = LedColorEnum.WHITE;
                showCartoonAction(index++);
                byte [] mParams={0,0,0,0,0};
                mParams[0]=0x04;
                mParams[1] = ByteHexHelper.intToHexByte(colorEnum.getR());
                mParams[2] = ByteHexHelper.intToHexByte(colorEnum.getG());
                mParams[3] = ByteHexHelper.intToHexByte(colorEnum.getB());
                mParams[4] = ByteHexHelper.intToHexByte(Integer.valueOf(0xff));//time
                mPresenter.commandRobotAction((byte)0x61,mParams);
                byte[] papram = new byte[1];
                papram[0]=0x01;
                mPresenter.commandRobotAction(ConstValue.ENTER_COURSE_MODE,mParams);
                //TURN OFF LIGHT
                papram[0]=0x01;
                mPresenter.commandRobotAction(ConstValue.DV_LIGHT, papram);
                break;
            case R.id.top_icon3:
                try {
                    CommonCtrlView.getInstace(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.right_icon:
                mLaunch.setClass(this, ScanBluetoothActivity.class);
                startActivity(mLaunch);
                break;
            case R.id.right_icon2:
                break;
            case R.id.right_icon3:
                break;
            case R.id.right_icon4:
                break;
            case R.id.cartoon_head:
                Log.d(TAG, "click head");
                showCartoonAction(cartoon_action_enjoy);
                break;
            case R.id.cartoon_chest:
                Log.d(TAG, "click chest");
                showCartoonAction(cartoon_action_smile);
                break;
            case R.id.cartoon_left_hand:
                Log.d(TAG, "click left hand");
                showCartoonAction(cartoon_action_swing_left_hand);
                break;
            case R.id.cartoon_right_hand:
                Log.d(TAG, "click right hand");
                showCartoonAction(cartoon_action_swing_right_hand);
                break;
            case R.id.cartoon_left_leg:
                Log.d(TAG, "click left leg");
                showCartoonAction(cartoon_action_swing_left_leg);
                break;
            case R.id.cartoon_right_leg:
                Log.d(TAG, "click right leg");
                showCartoonAction(cartoon_action_swing_right_leg);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBulueToothConnected()) {
            topIcon2Disconnect.setVisibility(View.INVISIBLE);
        } else {
            topIcon2Disconnect.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "X ,Y  " + (event.getX() + "," + event.getY()));
        mCurrentTouchTime=System.currentTimeMillis();
        if(System.currentTimeMillis()-mCurrentTouchTime<noOperationTimeout) {
            buddleText.setVisibility(View.INVISIBLE);
        }
        return super.onTouchEvent(event);
    }

    private Handler m_Handler = new Handler();

    @Override
    public void showCartoonAction(int value) {
        mCurrentTouchTime=System.currentTimeMillis();
        if(System.currentTimeMillis()-mCurrentTouchTime<noOperationTimeout) {
            buddleText.setVisibility(View.INVISIBLE);
        }
        String actionName = "";
        if (m_animationTask == null) {
            m_animationTask = new AnimationTask();
        }
        if (value == cartoon_action_swing_left_hand) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_left_hand);
            actionName = "swing_left_hand";
        } else if (value == cartoon_action_swing_right_hand) {
            cartoonAction.setBackgroundResource(R.drawable.swigrighthand);
            actionName = "swing_right_hand";
        } else if (value == cartoon_action_swing_left_leg) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_swing_leftleg);
            actionName = "swing_left_leg";
        } else if (value == cartoon_action_swing_right_leg) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_right_leg);
            actionName = "swing_right_leg";
        } else if (value == cartoon_action_hand_stand) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_hand_stand);
            actionName = "hand_stand";
        } else if (value == cartoon_action_hand_stand_reverse) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_hand_stand_reverse);
            actionName = "hand_stand_reverse";
        } else if (value == cartoon_aciton_squat) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_squat);
            actionName = "squat";
        } else if (value == cartoon_action_enjoy) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_enjoy);
            actionName = "enjoy";
        } else if (value == cartoon_action_fall) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_fall);
            actionName = "fall";
        } else if (value == cartoon_action_greeting) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_greeting);
            actionName = "greeting";
        } else if (value == cartoon_action_shiver) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_shiver);
            actionName = "shiver";
        } else if (value == cartoon_action_sleep) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_sleep);
            actionName = "sleep";
        } else if (value == cartoon_action_smile) {
            cartoonAction.setBackgroundResource(R.drawable.cartoon_smile);
            actionName = "smile enjoy";
        }
        //showBuddleText(actionName);
        // Type casting the Animation drawable
        frameAnimation = (AnimationDrawable) cartoonAction.getBackground();
        int time = frameAnimation.getNumberOfFrames() * frameAnimation.getDuration(0);
        Log.d(TAG, "Animation time is " + time);
        //set true if you want to animate only once
        frameAnimation.setOneShot(true);
        boolean result = m_Handler.postDelayed(m_animationTask, time);
        frameAnimation.start();

    }

    private AnimationTask m_animationTask;

    @Override
    public void handleMessage(Bundle bundle) {
        runOnUiThread(new Runnable() {
            @Override public void run() {

            }
        });
    }

    private class AnimationTask implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "stop the animation");
            if (frameAnimation.isRunning()) {
                frameAnimation.stop();
            }
        }
    }

    public void buddleTextAsynchronousTask() {
        mBuddleTextTimer= new Timer();
        mBuddleTextTimeOutTask= new TimerTask() {
            @Override
            public void run() {
                looperThread.post(new Runnable() {
                    public void run() {
                       if(System.currentTimeMillis()-mCurrentTouchTime>noOperationTimeout) {
                           try {
                               boolean isUiThread = Looper.getMainLooper().getThread() == Thread.currentThread();
                               Random random = new Random();
                               Log.i(TAG, "Timer out IS :   " + buddleTextTimeout + "main thread " + isUiThread);
                               String[] arrayText = getResources().getStringArray(R.array.mainUi_buddle_text);
                               int select = random.nextInt(arrayText.length);
                               final String text = arrayText[select];
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       showBuddleText(text);
                                   }
                               });
                           } catch (Exception e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                           }
                       }else {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   buddleText.setVisibility(View.INVISIBLE);
                               }
                           });
                       }
                    }
                });
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
                                    charging.setBackground(getDrawableRes("charging"));
                                    chargingDot.setBackground(getDrawableRes("charging_dot"));
                                }
                            });
                            Thread.sleep(charging_shrink_interval);
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    charging.setBackground(getDrawableRes("charging"));
                                    chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
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
        buddleText.setText(text);
        buddleText.setVisibility(View.VISIBLE);
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
        //Bluetooth connection icon
        if (isBulueToothConnected()) {
            topIcon2Disconnect.setVisibility(View.INVISIBLE);
        } else {
            topIcon2Disconnect.setVisibility(View.VISIBLE);
        }

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
        Log.d(TAG, "RECEIVE THE MESSAGE IN MAIN THREAD" + event.message);
        mPresenter.dealMessage(event.message);
        try {
            JSONObject mMessage = new JSONObject(event.message);
            //  "mac","cmd" "param","len"
            int mCmd = Integer.parseInt(mMessage.getString("cmd"));
            UbtLog.d(TAG, "CMD IS   " + mCmd);
            if (mCmd == 0x72) {
                Log.d(TAG, "ROBOT SLEEP EVENT");
            } else if (mCmd == 0x73) {
                Log.d(TAG, "ROBOT LOW BATTERY");
            } else if (mCmd == 0x74) {
                Log.d(TAG, "ROBOT POWER OFF EVENT");
            } else if (mCmd == ConstValue.DV_READ_BATTERY) {
               final byte[] mParams = Base64.decode(mMessage.getString("param"), Base64.DEFAULT);
                for (int i = 0; i < mParams.length; i++) {
                    Log.d(TAG, "index " + i + "value :" + mParams[i]);
                            if (mParams[2] == 0x01&&mParams[2]!=mChargeValue) {
                                Log.d(TAG, " IS CHARGE ");
                                chargeAsynchronousTask();
                                mChargeValue=mParams[2];
                            } else if(mParams[2]==0x0&&mParams[2]!=mChargeValue) {
                                stopchargeAsynchronousTask();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        charging.setBackground(getDrawableRes("charging_normal"));
                                        chargingDot.setBackground(getDrawableRes("charging_normal_dot"));
                                    }
                                });
                                mChargeValue=mParams[2];
                            }
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                powerStatusUpdate(mParams[mParams.length - 1]);
                            }
                        });


                }
            } else {
                Log.d(TAG, "ROBOT OTHER SITUATION" + mCmd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private int powerStatusUpdate(byte mParam) {
        Log.d(TAG, "POWER VALUE " + mParam);
        int power_index = 0;
        if (mParam < powerThreshold[powerThreshold.length / 2]) {
            for (int j = 0; j < powerThreshold.length / 2; j++) {
                if (mParam < powerThreshold[j + 1] && mParam > powerThreshold[j]) {
                    power_index = j - 1;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                }
            }
        } else {
            for (int j = powerThreshold.length / 2; j < powerThreshold.length; j++) {
                if (powerThreshold[j - 1] < mParam && mParam < powerThreshold[j]) {
                    power_index = j - 1;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                }
            }
        }
        Log.d(TAG, "Current power is " + power_index);
        cartoonBodyTouchBg.setBackground(getDrawableRes("power" + powerThreshold[power_index]));
        return power_index;
    }

    private void buddleTextShow(String text) {

    }

    private double getScreenInch() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scale = (int) this.getResources().getDisplayMetrics().density;

        int dpiClassification = dm.densityDpi;
        Log.d(TAG, "SCREEN DENSITY DPI\n" +
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
        Log.d(TAG, "width " + width + "height  " + height);
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        Log.d(TAG, "SCREEN  " + Math.sqrt(x + y));
        return Math.sqrt(x + y);
    }

    private void initUi() {
        //Course icon
//        int course_icon_margin_left = 22;
//        int course_icon_margin_top = 260;
//        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) bottomIcon.getLayoutParams();
//        rlParams.leftMargin = getAdaptiveScreenX(course_icon_margin_left);
//        rlParams.topMargin = getAdaptiveScreenY(course_icon_margin_top);
//        bottomIcon.setLayoutParams(rlParams);
        //cartoon animation
//        int cartoon_view_margin_left = 170;
//        int cartoon_view_margin_top = 0;
//        RelativeLayout.LayoutParams rlParams2 = (RelativeLayout.LayoutParams) cartoonBodyTouch.getLayoutParams();
//        rlParams2.leftMargin = getAdaptiveScreenX(cartoon_view_margin_left);
//        rlParams2.topMargin = getAdaptiveScreenY(cartoon_view_margin_top);
//        cartoonBodyTouch.setLayoutParams(rlParams2);
        //cartoon action
//        int cartoon_action_margin_left = 170;
//        int cartoon_action_margin_top = 0;
//        RelativeLayout.LayoutParams rlParams3 = (RelativeLayout.LayoutParams) cartoonAction.getLayoutParams();
//        rlParams3.leftMargin = getAdaptiveScreenX(cartoon_action_margin_left);
//        rlParams3.topMargin = getAdaptiveScreenY(cartoon_action_margin_top);
//        cartoonAction.setLayoutParams(rlParams3);
        //habit alert

    }

    private int getAdaptiveScreenX(int init_x) {
        return init_x * screen_width / init_screen_width;
    }

    private int getAdaptiveScreenY(int init_y) {
        return init_y * screen_height / init_screen_height;
    }


}

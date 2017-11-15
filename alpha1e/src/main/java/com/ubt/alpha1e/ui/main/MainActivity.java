package com.ubt.alpha1e.ui.main;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.login.loginauth.LoginAuthActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.userinfo.mainuser.UserCenterActivity;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {

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
    private String TAG = "MainActivity";
    int screen_width = 0;
    int screen_height = 0;
    int init_screen_width = 667;
    int init_screen_height = 375;
    RelativeLayout.LayoutParams params;
    private AnimationDrawable frameAnimation;
    private EasyPopup mCirclePop;
    private int powerThreshold[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
    int index=0;
    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.right_icon, R.id.right_icon2, R.id.right_icon3,
            R.id.right_icon4,R.id.cartoon_chest, R.id.cartoon_head, R.id.cartoon_left_hand,
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
                showCartoonAction("TEX");
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
                mCirclePop = new EasyPopup(this)
                        .setContentView(R.layout.main_ui_buddletext)
                        //是否允许点击PopupWindow之外的地方消失
                        .setFocusAndOutsideEnable(true)
                        .createPopup();
                mCirclePop.showAtAnchorView(cartoonHead, VerticalGravity.ALIGN_TOP,  HorizontalGravity.RIGHT, 20 , 0);

                break;
            case R.id.right_icon3:
                mCirclePop = new EasyPopup(this)
                        .setContentView(R.layout.main_ui_buddletext)
                        //是否允许点击PopupWindow之外的地方消失
                        .setFocusAndOutsideEnable(true)
                        .createPopup();
                    mCirclePop.showAtAnchorView(cartoonHead,VerticalGravity.ALIGN_TOP,  HorizontalGravity.RIGHT, 20 , 0);
                    TextView mBuddleText=mCirclePop.getView(R.id.tv_delete);
                    if (index== 0) {
                    mBuddleText.setText("从机场到下榻宾馆沿途，欢迎的人群几乎就没有间断。他们簇拥在街道两侧，纷纷向习近平总书记、国家主席挥舞中老两国国旗，表达着老挝人民对中国的深厚感情和对习近平总书记、国家主席来访的由衷喜悦");
                } else if (index == 1) {
                    mBuddleText.setText("从机场到下榻宾馆沿途，欢迎的人群几乎就没有间断。他们簇拥在街道两侧，纷纷向习近平总书记");
                } else if (index== 2) {
                    mBuddleText.setText("从机场到下榻宾馆沿途，欢迎的人群几乎就没有间断");
                    index = 0;
                }
                index++;
                break;
            case R.id.right_icon4:
                break;
            case R.id.cartoon_head:
                Log.d(TAG, "click head");
                break;
            case R.id.cartoon_chest:
                Log.d(TAG, "click chest");
                break;
            case R.id.cartoon_left_hand:
                Log.d(TAG, "click left hand");
                break;
            case R.id.cartoon_right_hand:
                Log.d(TAG, "click right hand");
                break;
            case R.id.cartoon_left_leg:
                Log.d(TAG, "click left leg");
                break;
            case R.id.cartoon_right_leg:
                Log.d(TAG, "click right leg");
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenInch();
        initUI();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "X ,Y  " + (event.getX() + "," + event.getY()));
        return super.onTouchEvent(event);
    }


    @Override
    public void showCartoonAction(String json) {

//           try {
//               GifDrawable gifFromResource = new GifDrawable(getContext().getResources(), R.drawable.rightleg);
//               cartoonAction.setImageDrawable(gifFromResource);
//               Log.d(TAG, "After Animation CARTOON width*height :" + cartoonAction.getWidth() + "  Y: " + cartoonAction.getHeight());
//               int count = gifFromResource.getNumberOfFrames();
//               gifFromResource.setLoopCount(1);
//               Log.d(TAG, "FRAME COUNT " + count);
//           } catch (IOException e) {
//               e.printStackTrace();
//           }
        cartoonAction.setBackgroundResource(R.drawable.swigrighthand);
        // Type casting the Animation drawable
        frameAnimation = (AnimationDrawable) cartoonAction.getBackground();
        //set true if you want to animate only once
        frameAnimation.setOneShot(true);
        frameAnimation.start();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
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

                byte[] mParams = Base64.decode(mMessage.getString("param"), Base64.DEFAULT);
                for (int i = 0; i < mParams.length; i++) {
                    Log.d(TAG, "index " + i + "value :" + mParams[i]);

                    if (i == 3) {
                        if (i == 2) {
                            if (mParams[i] == 0x01) {
                                Log.d(TAG, " IS CHARGE ");
                                charging.setBackground(getDrawableRes("charging_flag"));
                            }

                        }
                        powerStatusUpdate(mParams[mParams.length - 1]);
                    }
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

    private void buddleTextShow(String text){

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

package com.ubt.alpha1e.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.MyMainActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.ui.FullScreenView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {

    @BindView(R.id.cartoon_action)
    GifImageView cartoonAction;
    @BindView(R.id.cartoon_body_touch)
    ImageView cartoonBodyTouch;
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
    @BindView(R.id.mainui)
    RelativeLayout mainui;
    private String TAG = "MainActivity";
    int screen_width = 0;
    int screen_height = 0;

    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.right_icon, R.id.right_icon2, R.id.right_icon3, R.id.right_icon4, R.id.cartoon_body_touch})
    protected void switchActivity(View view) {
        Log.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.top_icon:
                mLaunch.setClass(this, MyMainActivity.class);
                startActivity(mLaunch);
                break;
            case R.id.top_icon2:
                cartoonBodyTouch.setVisibility(View.VISIBLE);
                cartoonAction.setVisibility(View.INVISIBLE);
                break;
            case R.id.top_icon3:
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
            case R.id.cartoon_body_touch:
                cartoonAction.setVisibility(View.VISIBLE);
                cartoonBodyTouch.setVisibility(View.INVISIBLE);
                UbtLog.d(TAG, "CARTOON BODY TOUCH ");
                showCartoonAction("TEX");
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenInch();
        initUi();
    }

    @Override
    public void showCartoonAction(String json) {
        try {
            GifDrawable gifFromResource = new GifDrawable(getContext().getResources(), R.drawable.standup);
            cartoonAction.setImageDrawable(gifFromResource);
            int count = gifFromResource.getNumberOfFrames();
            gifFromResource.setLoopCount(1);
            Log.d(TAG, "FRAME COUNT " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showBluetoothStatus(String status) {

    }

    @Override
    public void showCartoonText(String text) {

    }

    @Override
    protected void initUI() {

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
    }

    private double getScreenInch() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
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
        int course_icon_width = 156;
        int course_icon_height = 86;
        int init_screen_width = 960;
        int init_screen_height = 540;
        int course_icon_margin_left = 29;
        int course_icon_margin_bottom = 100;
//        RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainui);
//        RelativeLayout.LayoutParams params;
//        params = new RelativeLayout.LayoutParams(screen_width,screen_height);
//        params.leftMargin = (course_icon_margin_left*screen_width)/init_screen_width;
//        params.bottomMargin = (course_icon_margin_bottom*screen_height)/init_screen_height;
//        rl.addView(bottomIcon, params);
        bottomIcon.setX((course_icon_margin_left*screen_width)/init_screen_width);
        bottomIcon.setY(screen_height-((course_icon_margin_bottom*screen_height)/init_screen_height)-86);
        Log.d(TAG,"COURSE POS  X:"+ (course_icon_margin_left*screen_width)/init_screen_width+"  Y: "+(screen_height-((course_icon_margin_bottom*screen_height)/init_screen_height)));




    }


}

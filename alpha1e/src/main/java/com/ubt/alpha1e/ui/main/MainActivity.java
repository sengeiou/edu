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
import com.ubt.alpha1e.userinfo.mainuser.UserCenterActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
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
    int init_screen_width = 960;
    int init_screen_height = 540;
    RelativeLayout.LayoutParams params;

    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3, R.id.right_icon, R.id.right_icon2, R.id.right_icon3, R.id.right_icon4, R.id.cartoon_body_touch})
    protected void switchActivity(View view) {
        Log.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.top_icon:

                Intent intent = new Intent();
                intent.setClass(this, UserCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.top_icon2:
                cartoonBodyTouch.setVisibility(View.VISIBLE);
                cartoonAction.setVisibility(View.INVISIBLE);
                break;
            case R.id.top_icon3:
                mLaunch.setClass(this, MyMainActivity.class);
                startActivity(mLaunch);
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
            Log.d(TAG,"After Animation CARTOON width*height :"+ cartoonAction.getWidth()+"  Y: "+cartoonAction.getHeight());
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
        int course_icon_margin_left =29;
        int course_icon_margin_top =375 ;
        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) bottomIcon.getLayoutParams();
        rlParams.leftMargin = getAdaptiveScreenX(course_icon_margin_left);
        rlParams.topMargin=getAdaptiveScreenY(course_icon_margin_top) ;
        bottomIcon.setLayoutParams(rlParams);
        //cartoon animation
        int cartoon_view_margin_left=224;
        int cartoon_view_margin_top=28;
        RelativeLayout.LayoutParams rlParams2 = (RelativeLayout.LayoutParams) cartoonBodyTouch.getLayoutParams();
        rlParams2.leftMargin = getAdaptiveScreenX(cartoon_view_margin_left);
        rlParams2.topMargin=getAdaptiveScreenY(cartoon_view_margin_top) ;
        cartoonBodyTouch.setLayoutParams(rlParams2);
        //cartoon action
        int cartoon_action_margin_left=224;
        int cartoon_action_margin_top=28;
        RelativeLayout.LayoutParams rlParams3 = (RelativeLayout.LayoutParams) cartoonAction.getLayoutParams();
        rlParams3.leftMargin = getAdaptiveScreenX(cartoon_action_margin_left);
        rlParams3.topMargin=getAdaptiveScreenY(cartoon_action_margin_top);
        cartoonAction.setLayoutParams(rlParams3);



    }

    private int getAdaptiveScreenX(int init_x) {
        return init_x * screen_width / init_screen_width;
    }

    private int getAdaptiveScreenY(int init_y) {
        return init_y * screen_height / init_screen_height;
    }


}

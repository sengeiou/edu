package com.ubt.alpha1e.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    private String TAG = "MainActivity";

    @OnClick({R.id.top_icon, R.id.top_icon2, R.id.top_icon3,R.id.right_icon,R.id.right_icon2, R.id.right_icon3, R.id.right_icon4,R.id.cartoon_body_touch})
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
                UbtLog.d(TAG,"CARTOON BODY TOUCH ");
                showCartoonAction("TEX");
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showCartoonAction(String json) {
        try {
            GifDrawable gifFromResource = new GifDrawable(getContext().getResources(), R.drawable.cartoon);
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


}

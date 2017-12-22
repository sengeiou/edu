package com.ubt.alpha1e.userinfo.myrobot;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/12/21
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class MyRobotActivity extends MVPBaseActivity<MyRobotContract.View, MyRobotPresenter> implements MyRobotContract.View {

    private String TAG = "MyRobotActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;


    @OnClick({R.id.ib_return})
    protected void switchActivity(View view) {
        UbtLog.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.ib_return:
                MyRobotActivity.this.finish();
                break;
            default:
                break;
        }
    }


    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, MyRobotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return R.layout.myrobot;
    }
}

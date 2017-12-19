package com.ubt.alpha1e.userinfo.myrobot;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.contactus.ContactUsActivity;
import com.ubt.alpha1e.userinfo.photoshow.PhotoShowActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MyRobotActivity extends MVPBaseActivity<MyRobotContract.View, MyRobotPresenter> implements MyRobotContract.View {

    private static final String TAG = MyRobotActivity.class.getSimpleName();

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.btn_connect_robot)
    Button btnConnectRobot;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, MyRobotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.btn_connect_robot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                MyRobotActivity.this.finish();
                break;
            case R.id.btn_connect_robot:
                UbtLog.d(TAG,"btn_connect_robot " );
                if (((AlphaApplication) MyRobotActivity.this.getApplicationContext())
                        .getCurrentBluetooth() == null) {
                    Intent intent = new Intent(this, BluetoothandnetconnectstateActivity.class);
                    startActivity(intent);
                    MyRobotActivity.this.finish();
                }else {

                }

                break;

        }
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_settings_message_myrobot"));
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_myrobot;
    }
}

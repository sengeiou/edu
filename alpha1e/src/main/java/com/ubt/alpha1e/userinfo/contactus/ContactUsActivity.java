package com.ubt.alpha1e.userinfo.contactus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ContactUsActivity extends MVPBaseActivity<ContactUsContract.View, ContactUsPresenter> implements ContactUsContract.View {

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.rl_cust_phone)
    RelativeLayout rlCustPhone;
    @BindView(R.id.rl_cust_email)
    RelativeLayout rlCustEmail;
    @BindView(R.id.rl_cust_website)
    RelativeLayout rlCustWebsite;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_contact_title"));
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_contact_us_mvp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.tv_base_title_name,R.id.rl_cust_phone, R.id.rl_cust_email, R.id.rl_cust_website})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                ContactUsActivity.this.finish();
                break;
            case R.id.rl_cust_phone:
                break;
            case R.id.rl_cust_email:
                break;
            case R.id.rl_cust_website:
                break;
        }
    }

}

package com.ubt.alpha1e.userinfo.helpfeedback.feelback;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.custom.ClearableEditText;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class FeelbackActivity extends MVPBaseActivity<FeelbackContract.View, FeelbackPresenter> implements FeelbackContract.View {

    private static final String TAG = FeelbackActivity.class.getSimpleName();

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.edt_feedback)
    ClearableEditText edtFeedback;
    @BindView(R.id.edt_email)
    ClearableEditText edtEmail;
    @BindView(R.id.edt_phone)
    ClearableEditText edtPhone;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, FeelbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_setting_idea_feedback"));
        ivTitleRight.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                FeelbackActivity.this.finish();
                break;
            case R.id.iv_title_right:
                doCommit();
                break;
        }
    }

    private void doCommit() {
        UbtLog.d(TAG, "doCommit");
    }

    @Override
    public void onFeedbackFinish(boolean isSuccess, String errorMsg) {

    }
}

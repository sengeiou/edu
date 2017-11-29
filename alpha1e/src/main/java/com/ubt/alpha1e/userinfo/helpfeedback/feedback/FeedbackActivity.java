package com.ubt.alpha1e.userinfo.helpfeedback.feedback;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.DataCheckTools;
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

public class FeedbackActivity extends MVPBaseActivity<FeedbackContract.View, FeedbackPresenter> implements FeedbackContract.View {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

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

    protected Dialog mCoonLoadingDia;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
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
                FeedbackActivity.this.finish();
                break;
            case R.id.iv_title_right:
                doCommit();
                break;
        }
    }

    private void doCommit() {
        UbtLog.d(TAG, "doCommit");
        String content = edtFeedback.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShort(getStringResources("ui_about_feedback_empty"));
            return;
        }

        if (TextUtils.isEmpty(email) ) {
            ToastUtils.showShort(getStringResources("ui_login_email_placeholder"));
            return;
        }

        if (!TextUtils.isEmpty(email) && !DataCheckTools.isEmail(email)) {
            ToastUtils.showShort(getStringResources("ui_login_prompt_email_wrong_format"));
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(getStringResources("ui_telephone_request"));
            return;
        }

        mPresenter.doFeedBack(content,email,phone);
    }

    @Override
    public void onFeedbackFinish(boolean isSuccess, String errorMsg) {
        if(!TextUtils.isEmpty(errorMsg)){
            ToastUtils.showShort(errorMsg);
        }

        if(isSuccess){
            FeedbackActivity.this.finish();
        }
    }
}

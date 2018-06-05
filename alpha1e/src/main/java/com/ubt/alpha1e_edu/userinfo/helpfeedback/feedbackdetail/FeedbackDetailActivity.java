package com.ubt.alpha1e_edu.userinfo.helpfeedback.feedbackdetail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.FeedbackInfo;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class FeedbackDetailActivity extends MVPBaseActivity<FeedbackDetailContract.View, FeedbackDetailPresenter> implements FeedbackDetailContract.View {

    private static final String TAG = FeedbackDetailActivity.class.getSimpleName();

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.tv_feedback_title)
    TextView tvFeedbackTitle;
    @BindView(R.id.tv_feedback_content)
    TextView tvFeedbackContent;

    private FeedbackInfo mFeedbackInfo = null;

    public static void LaunchActivity(Context context, FeedbackInfo feedbackInfo) {
        Intent intent = new Intent(context, FeedbackDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.FEEDBACK_INFO_KEY, PG.convertParcelable(feedbackInfo));
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_setting_feedback_detail"));

        mFeedbackInfo = getIntent().getExtras().getParcelable(Constant.FEEDBACK_INFO_KEY);
        UbtLog.d(TAG, "mFeedbackInfo = " + mFeedbackInfo);
        if(mFeedbackInfo != null){
            tvFeedbackTitle.setText(mFeedbackInfo.feedbackName);
            tvFeedbackContent.setText(mFeedbackInfo.feedbackIntroduction);
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
        return R.layout.activity_feedback_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                FeedbackDetailActivity.this.finish();
                break;
        }
    }
}

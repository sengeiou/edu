package com.ubt.alpha1e.edu.learningmode;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.login.LoginActivity;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LearningModeActivity extends MVPBaseActivity<LearningModeContract.View, LearningModePresenter> implements LearningModeContract.View {

    private static final String TAG = LearningModeActivity.class.getSimpleName();

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.tv_learning_model_tip)
    TextView tvLearningModelTip;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;

    private int type = 0;

    public static void LaunchActivity(Context context, int type) {
        Intent intent = new Intent(context, LearningModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        llBaseBack.setVisibility(View.GONE);


        ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setBackgroundResource(R.drawable.action_close);

        type = getIntent().getExtras().getInt("type", 0);

        if (type == 0) {
            tvBaseTitleName.setText(getStringResources("ui_common_tip"));
            tvLearningModelTip.setText(getStringResources("ui_learning_model_tip"));
            tvConfirm.setText(getStringResources("ui_common_known"));
        }else {
            tvBaseTitleName.setText(getStringResources("ui_learning_model_center"));
            tvLearningModelTip.setText(getStringResources("ui_learning_model_tip"));
            tvConfirm.setText(getStringResources("ui_learning_model_logout"));
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
        return R.layout.activity_learning_model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.tv_confirm, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
            case R.id.iv_title_right:
                LearningModeActivity.this.finish();
                break;
            case R.id.tv_confirm:
                if(type == 1){
                    LoginActivity.LaunchActivity(getContext());
                }
                LearningModeActivity.this.finish();
                break;
            default:
                break;

        }
    }
}

package com.ubt.alpha1e.userinfo.helpfeedback;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.helpfeedback.feedbacksearch.FeedbackSearchFragment;
import com.ubt.alpha1e.userinfo.helpfeedback.hotquestion.HotQuestionFragment;
import com.ubt.alpha1e.userinfo.usermanager.AndroidAdjustResizeBugFix;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HelpFeedbackActivity extends MVPBaseActivity<HelpFeedbackContract.View, HelpFeedbackPresenter> implements HelpFeedbackContract.View {

    private static final String TAG = HelpFeedbackActivity.class.getSimpleName();

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;

    @BindView(R.id.edt_base_title)
    EditText edtBaseTitle;
    @BindView(R.id.tv_base_right)
    TextView tvBaseRight;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.rl_base_search)
    RelativeLayout rlBaseSearch;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;

    public MVPBaseFragment mCurrentFragment;

    private String editString = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            if(mCurrentFragment instanceof FeedbackSearchFragment){
                ((FeedbackSearchFragment)mCurrentFragment).refreshSearchResult(editString);
            }
        }
    };

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, HelpFeedbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_setting_help_feedback"));
        tvBaseRight.setText(getStringResources("ui_common_cancel"));
        ivTitleRight.setBackgroundResource(R.drawable.icon_title_search);
        ivTitleRight.setVisibility(View.VISIBLE);

        switchMode(false);
        initControlListener();
    }

    @Override
    protected void initControlListener() {
        edtBaseTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(delayRun != null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    mHandler.removeCallbacks(delayRun);
                }
                editString = s.toString().trim();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                mHandler.postDelayed(delayRun, 500);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_help_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right, R.id.tv_base_right, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                HelpFeedbackActivity.this.finish();
                break;
            case R.id.iv_title_right:
                UbtLog.d(TAG, "iv_title_right");
                switchMode(true);
                break;
            case R.id.tv_base_right:
                switchMode(false);
                break;
            case R.id.iv_search:
                break;
        }
    }

    private void switchMode(boolean isSerch) {
        if (isSerch) {

            ivTitleRight.setVisibility(View.INVISIBLE);
            tvBaseTitleName.setVisibility(View.INVISIBLE);
            rlBaseSearch.setVisibility(View.VISIBLE);
            tvBaseRight.setVisibility(View.VISIBLE);
            tvBaseRight.setText(getStringResources("ui_common_cancel"));

            mCurrentFragment = findFragment(FeedbackSearchFragment.class);
            if(mCurrentFragment == null){
                mCurrentFragment = FeedbackSearchFragment.newInstance();
            }
            loadRootFragment(R.id.rl_content, mCurrentFragment,false,false);

        } else {
            edtBaseTitle.setText("");
            ivTitleRight.setVisibility(View.VISIBLE);
            tvBaseTitleName.setVisibility(View.VISIBLE);
            rlBaseSearch.setVisibility(View.INVISIBLE);
            tvBaseRight.setVisibility(View.INVISIBLE);

            mCurrentFragment = findFragment(HotQuestionFragment.class);
            if(mCurrentFragment == null){
                mCurrentFragment = HotQuestionFragment.newInstance();
            }
            loadRootFragment(R.id.rl_content, mCurrentFragment, false, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

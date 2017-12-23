package com.ubt.alpha1e.behaviorhabits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.fragment.HibitsEventFragment;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BehaviorHabitsActivity extends MVPBaseActivity<BehaviorHabitsContract.View, BehaviorHabitsPresenter>{

    private static final String TAG = BehaviorHabitsActivity.class.getSimpleName();

    @BindView(R.id.rl_content)
    RelativeLayout rlContent;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, BehaviorHabitsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {

        HibitsEventFragment fragment = findFragment(HibitsEventFragment.class);
        UbtLog.d(TAG,"HibitsEventFragment = " + fragment);
        if (fragment == null) {
            fragment = HibitsEventFragment.newInstance();
            loadRootFragment(R.id.rl_content, fragment);
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
        return R.layout.activity_behavior_habits;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }
}

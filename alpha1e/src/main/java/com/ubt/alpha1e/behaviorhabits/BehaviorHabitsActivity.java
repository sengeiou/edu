package com.ubt.alpha1e.behaviorhabits;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.fragment.Test1Fragment;
import com.ubt.alpha1e.behaviorhabits.fragment.Test2Fragment;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BehaviorHabitsActivity extends MVPBaseActivity<BehaviorHabitsContract.View, BehaviorHabitsPresenter> {

    private static final String TAG = BehaviorHabitsActivity.class.getSimpleName();

    private final static int FRAGMENT_1 = 1;
    private final static int FRAGMENT_2 = 2;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, BehaviorHabitsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        mFragmentManager = this.getFragmentManager();

        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        Test1Fragment test1Fragment = new Test1Fragment();
        mFragmentTransaction.add(R.id.rl_content, test1Fragment);
        mFragmentTransaction.commit();
        mCurrentFragment = test1Fragment;
        mFragmentCache.put(FRAGMENT_1, test1Fragment);

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

    @OnClick(R.id.ll_base_back)
    public void onViewClicked() {
    }

    public void switchMode(int fragment) {
        if (fragment == 1) {

            Fragment f = mFragmentCache.containsKey(FRAGMENT_1) ? mFragmentCache.get(FRAGMENT_1)
                    : new Test1Fragment();
            if (!mFragmentCache.containsKey(FRAGMENT_1)) {
                mFragmentCache.put(FRAGMENT_1, f);
            }
            loadFragment(f);

        } else {

            Fragment f = mFragmentCache.containsKey(FRAGMENT_2) ? mFragmentCache.get(FRAGMENT_2)
                    : new Test2Fragment();
            if (!mFragmentCache.containsKey(FRAGMENT_2)) {
                mFragmentCache.put(FRAGMENT_2, f);
            }
            loadFragment(f);
        }
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG, "targetFragment.isAdded()->>>" + (!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
            mCurrentFragment.onStop();

            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.rl_content, targetFragment)
                    .commit();
        } else {
            mCurrentFragment.onStop();
            targetFragment.onResume();

            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

}

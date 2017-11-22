package com.ubt.alpha1e.course;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.animator.FloatAnimator;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.course.feature.FeatureFragment;
import com.ubt.alpha1e.course.merge.MergeFragment;
import com.ubt.alpha1e.course.principle.PrincipleFragment;
import com.ubt.alpha1e.course.split.SplitFragment;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.psdmanage.psdmodify.PsdModifyFragment;
import com.ubt.alpha1e.userinfo.psdmanage.psdsetting.PsdSettingFragment;
import com.ubt.alpha1e.userinfo.psdmanage.psdverifycode.PsdVerifyCodeFragment;
import com.ubt.alpha1e.utils.SizeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseActivity extends MVPBaseActivity<CourseContract.View, CoursePresenter> implements CourseContract.View {

    private static final String TAG = CourseActivity.class.getSimpleName();

    public final static int FRAGMENT_PRINCIPLE = 1;
    public final static int FRAGMENT_SPLIT = 2;
    public final static int FRAGMENT_MERGE = 3;
    public final static int FRAGMENT_FEATURE = 4;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, CourseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {

        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        //PrincipleFragment mPrincipleFragment = new PrincipleFragment();
        FeatureFragment mFeatureFragment = new FeatureFragment();
        mFragmentTransaction.add(R.id.rl_content, mFeatureFragment);
        //mFragmentTransaction.add(R.id.rl_content, mPrincipleFragment);
        mFragmentTransaction.commit();
        //mCurrentFragment = mPrincipleFragment;
        //mFragmentCache.put(FRAGMENT_PRINCIPLE, mPrincipleFragment);

        mCurrentFragment = mFeatureFragment;
        mFragmentCache.put(FRAGMENT_FEATURE, mFeatureFragment);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_course_principle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        mFragmentManager = this.getFragmentManager();
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void switchFragment(int index){
        if(index == FRAGMENT_PRINCIPLE){

            Fragment f = mFragmentCache.containsKey(FRAGMENT_PRINCIPLE) ? mFragmentCache.get(FRAGMENT_PRINCIPLE) : new PrincipleFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_PRINCIPLE)) {
                mFragmentCache.put(FRAGMENT_PRINCIPLE, f);
            }
            loadFragment(f);
        }else if(index == FRAGMENT_SPLIT){
            Fragment f = mFragmentCache.containsKey(FRAGMENT_SPLIT) ? mFragmentCache.get(FRAGMENT_SPLIT) : new SplitFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_SPLIT)) {
                mFragmentCache.put(FRAGMENT_SPLIT, f);
            }
            loadFragment(f);
        }else if(index == FRAGMENT_MERGE){
            Fragment f = mFragmentCache.containsKey(FRAGMENT_MERGE) ? mFragmentCache.get(FRAGMENT_MERGE) : new MergeFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_MERGE)) {
                mFragmentCache.put(FRAGMENT_MERGE, f);
            }
            loadFragment(f);
        }else if(index == FRAGMENT_FEATURE){
            Fragment f = mFragmentCache.containsKey(FRAGMENT_FEATURE) ? mFragmentCache.get(FRAGMENT_FEATURE) : new FeatureFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_FEATURE)) {
                mFragmentCache.put(FRAGMENT_FEATURE, f);
            }
            loadFragment(f);
        }
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG,"targetFragment.isAdded()->>>"+(!targetFragment.isAdded()));
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

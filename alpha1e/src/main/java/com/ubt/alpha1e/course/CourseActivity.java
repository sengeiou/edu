package com.ubt.alpha1e.course;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.feature1.FeatureFragment;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.course.merge1.MergeFragment;
import com.ubt.alpha1e.course.principle1.PrincipleFragment;
import com.ubt.alpha1e.course.split1.SplitFragment;
import com.ubt.alpha1e.mvp.MVPBaseActivity;

import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;


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

    private int mEnterPropress = 0;

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

        int mCurrentProgress = mPresenter.doGetLocalProgress();
        mCurrentProgress = 0;
        mEnterPropress = mCurrentProgress;
        Fragment fragment = null;
        UbtLog.d(TAG,"mEnterPropress = " + mEnterPropress);
        if(mCurrentProgress == 1){
            fragment = new SplitFragment((PrincipleHelper) mHelper);
        }else if(mCurrentProgress == 2){
            fragment = new MergeFragment((PrincipleHelper) mHelper);
        }else if(mCurrentProgress == 3){
            fragment = new FeatureFragment((PrincipleHelper) mHelper);
        }else {
            fragment = new PrincipleFragment((PrincipleHelper) mHelper);
        }

        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_content, fragment);
        mFragmentTransaction.commit();
        mCurrentFragment = fragment;

    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG,"onResume = " + isBulueToothConnected());
        ((PrincipleHelper)mHelper).doEnterCourse((byte)1);
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        if(event.getEvent() == PrincipleEvent.Event.DISCONNECTED){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort(getStringResources("ui_robot_disconnect"));
                    CourseActivity.this.finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(mCurrentFragment instanceof PrincipleFragment){
            finish();
        }else if(mCurrentFragment instanceof SplitFragment){
            switchFragment(FRAGMENT_PRINCIPLE);
        }else if(mCurrentFragment instanceof MergeFragment){
            switchFragment(FRAGMENT_SPLIT);
        }else if(mCurrentFragment instanceof FeatureFragment){
            switchFragment(FRAGMENT_MERGE);
        }
        //super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
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
        mHelper = new PrincipleHelper(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        initUI();
    }

    @Override
    protected void onDestroy() {
        ((PrincipleHelper)mHelper).doInit();
        ((PrincipleHelper)mHelper).doEnterCourse((byte)0);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void switchFragment(int index){
        UbtLog.d(TAG,"switchFragment index = " + index);
        if(index == FRAGMENT_PRINCIPLE){
            Fragment f = new PrincipleFragment((PrincipleHelper) mHelper);
            loadFragment(f);
        }else if(index == FRAGMENT_SPLIT){
            Fragment f = new SplitFragment((PrincipleHelper) mHelper);
            loadFragment(f);
        }else if(index == FRAGMENT_MERGE){
            Fragment f = new MergeFragment((PrincipleHelper) mHelper);
            loadFragment(f);
        }else if(index == FRAGMENT_FEATURE){
            Fragment f = new FeatureFragment((PrincipleHelper) mHelper);
            loadFragment(f);
        }
    }

    public Fragment getCurrentFragment(){
        return mCurrentFragment;
    }

    public int getEnterPropress(){
        return mEnterPropress;
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG,"targetFragment.isAdded()->>>"+(!targetFragment.isAdded()));
        //transaction.setCustomAnimations(R.animator.fragment_open_up_down, 0);

        if (!targetFragment.isAdded()) {
            transaction
                    .remove(mCurrentFragment)
                    .add(R.id.rl_content, targetFragment)
                    .commit();
        } else {
            transaction
                    .remove(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

    /**
     * 获取课程进度
     * @param type
     */
    public void doGetCourseProgress(int type){
        mPresenter.doGetCourseProgress(type);
    }

    /**
     * 保存课程进度
     * @param type
     * @param courseOne
     * @param progressOne
     */
    public void doSaveCourseProgress(int type, int courseOne, int progressOne){
        mPresenter.doSaveCourseProgress(type,courseOne,progressOne);
    }

    @Override
    public void onSaveCourseProgress(boolean isSuccess, String msg) {

    }

    @Override
    public void onGetCourseProgress(boolean isSuccess, String msg, int progress) {

    }
}

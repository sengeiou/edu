package com.ubt.alpha1e.userinfo.psdmanage;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.psdmanage.psdmodify.PsdModifyFragment;
import com.ubt.alpha1e.userinfo.psdmanage.psdsetting.PsdSettingFragment;
import com.ubt.alpha1e.userinfo.psdmanage.psdverifycode.PsdVerifyCodeFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PsdManageActivity extends MVPBaseActivity<PsdManageContract.View, PsdManagePresenter> implements PsdManageContract.View {

    private static final String TAG = PsdManageActivity.class.getSimpleName();

    public final static int FRAGMENT_SETTING_PASSWORD = 1;
    public final static int FRAGMENT_SETTING_MODIFY = 2;
    public final static int FRAGMENT_FORGET_PASSWORD = 3;

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, PsdManageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {

        tvBaseTitleName.setText(getStringResources("ui_setting_password_massage"));

        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        PsdModifyFragment mPsdModifyFragment = new PsdModifyFragment();
        mFragmentTransaction.add(R.id.rl_content, mPsdModifyFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mPsdModifyFragment;
        mFragmentCache.put(FRAGMENT_SETTING_MODIFY, mPsdModifyFragment);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_password_manage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mFragmentManager = this.getFragmentManager();

        initUI();
    }

    @OnClick({R.id.ll_base_back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.ll_base_back:
                PsdManageActivity.this.finish();
                break;
        }
    }

    public void switchFragment(int index){
        if(index == FRAGMENT_FORGET_PASSWORD){
            tvBaseTitleName.setText(getStringResources("ui_setting_forget_password"));

            Fragment f = mFragmentCache.containsKey(FRAGMENT_FORGET_PASSWORD) ? mFragmentCache.get(FRAGMENT_FORGET_PASSWORD)
                    : new PsdVerifyCodeFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_FORGET_PASSWORD)) {
                mFragmentCache.put(FRAGMENT_FORGET_PASSWORD, f);
            }
            loadFragment(f);
        }else if(index == FRAGMENT_SETTING_MODIFY){
            tvBaseTitleName.setText(getStringResources("ui_setting_password_massage"));

            Fragment f = mFragmentCache.containsKey(FRAGMENT_SETTING_MODIFY) ? mFragmentCache.get(FRAGMENT_SETTING_MODIFY)
                    : new PsdSettingFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_SETTING_MODIFY)) {
                mFragmentCache.put(FRAGMENT_SETTING_MODIFY, f);
            }
            loadFragment(f);
        }else if(index == FRAGMENT_SETTING_PASSWORD){
            tvBaseTitleName.setText(getStringResources("ui_setting_password_massage"));

            Fragment f = mFragmentCache.containsKey(FRAGMENT_SETTING_PASSWORD) ? mFragmentCache.get(FRAGMENT_SETTING_PASSWORD)
                    : new PsdSettingFragment();
            if (!mFragmentCache.containsKey(FRAGMENT_SETTING_PASSWORD)) {
                mFragmentCache.put(FRAGMENT_SETTING_PASSWORD, f);
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

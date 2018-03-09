package com.ubt.alpha1e.userinfo.psdmanage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
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

    public final static int FRAGMENT_SETTING_MODIFY = 1;
    public final static int FRAGMENT_SETTING_PASSWORD = 2;
    public final static int FRAGMENT_FORGET_PASSWORD = 3;

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;

    public MVPBaseFragment mCurrentFragment;
    private boolean mFindPassword = false;

    public static void LaunchActivity(Activity activity, boolean isFindPassword) {
        Intent intent = new Intent(activity, PsdManageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isFindPassword", isFindPassword);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void initUI() {
        if(mFindPassword){
            switchFragment(FRAGMENT_FORGET_PASSWORD);
        }else {
            switchFragment(FRAGMENT_SETTING_MODIFY);
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
        return R.layout.activity_password_manage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        if(getIntent() != null){
            mFindPassword = getIntent().getBooleanExtra("isFindPassword", false);
        }

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
        if(index == FRAGMENT_SETTING_MODIFY){
            tvBaseTitleName.setText(getStringResources("ui_setting_password_massage"));
            mCurrentFragment = findFragment(PsdModifyFragment.class);
            if (mCurrentFragment == null) {
                mCurrentFragment = PsdModifyFragment.newInstance();
            }
            loadRootFragment(R.id.rl_content, mCurrentFragment);
        }else if(index == FRAGMENT_FORGET_PASSWORD){
            tvBaseTitleName.setText(getStringResources("ui_setting_forget_password"));
            mCurrentFragment = findFragment(PsdVerifyCodeFragment.class);
            if (mCurrentFragment == null) {
                mCurrentFragment = PsdVerifyCodeFragment.newInstance();
            }
            loadRootFragment(R.id.rl_content, mCurrentFragment, false, false);
        }else if(index == FRAGMENT_SETTING_PASSWORD){
            tvBaseTitleName.setText(getStringResources("ui_setting_password_massage"));
            mCurrentFragment = findFragment(PsdSettingFragment.class);
            if (mCurrentFragment == null) {
                mCurrentFragment = PsdSettingFragment.newInstance();
            }
            loadRootFragment(R.id.rl_content, mCurrentFragment, false, false);
        }
        UbtLog.d(TAG,"mCurrentFragment = " + mCurrentFragment);
    }
}

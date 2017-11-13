package com.ubt.alpha1e.userinfo.aboutus;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.IUpdateListener;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.dialog.UpdateDialog;
import com.ubt.alpha1e.update.ApkUpdateManager;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AboutUsActivity extends MVPBaseActivity<AboutUsContract.View, AboutUsPresenter> implements AboutUsContract.View {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.rl_app_update)
    RelativeLayout rlAppUpdate;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;

    private String version = "";

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {
        try {
            version = AboutUsActivity.this.getPackageManager().getPackageInfo(
                    AboutUsActivity.this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }

        tvVersion.setText(getStringResources("ui_about_version") + version);
        tvBaseTitleName.setText(getStringResources("ui_leftmenu_about"));
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_about_us_mvp;
    }

    @OnClick({R.id.ll_base_back,R.id.rl_app_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                AboutUsActivity.this.finish();
                break;
            case R.id.rl_app_update: {
                if (ApkUpdateManager.isUpdating()) {
                    Toast.makeText(getContext(), getStringResources("ui_about_update_doing"), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mPresenter.isOnlyWifiDownload(getContext())
                        && (!mPresenter.isWifiCoon(getContext()))) {
                    Toast.makeText(getContext(),
                            getStringResources("ui_about_update_wifi_need"), Toast.LENGTH_SHORT).show();
                } else {
                    mPresenter.doUpdateApk();
                    if (mCoonLoadingDia != null) {
                        mCoonLoadingDia.cancel();
                    }
                    mCoonLoadingDia = SLoadingDialog.getInstance(AboutUsActivity.this);
                    mCoonLoadingDia.show();
                }
            }
            break;
        }
    }

    @Override
    public void noteNewestVersion() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }
                mCoonLoadingDia = SLoadingDialog.getInstance(getContext());
                ((SLoadingDialog) mCoonLoadingDia).show(getStringResources("ui_about_update_already_latest"));
            }
        });
    }

    @Override
    public void noteApkUpsateFail(final String info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }
                Toast.makeText(AboutUsActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void noteApkUpdate(final String versionPath, final String versionNameSizeInfo) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }

                UpdateDialog.getInstance(AboutUsActivity.this,
                        versionNameSizeInfo.split("#"), new IUpdateListener() {
                            @Override
                            public void doUpdate() {
                                ApkUpdateManager.getInstance(AboutUsActivity.this, versionPath).Update();
                            }

                            @Override
                            public void doIgnore() {

                            }
                        }).show();
            }
        });
    }
}

package com.ubt.alpha1e.userinfo.cleancache;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.utils.SizeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CleanCacheActivity extends MVPBaseActivity<CleanCacheContract.View, CleanCachePresenter> implements CleanCacheContract.View {

    private static final String TAG = CleanCacheActivity.class.getSimpleName();

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.tv_cache_size)
    TextView tvCacheSize;
    @BindView(R.id.tv_use_cache)
    TextView tvUseCache;
    @BindView(R.id.tv_use_ratio)
    TextView tvUseRatio;
    @BindView(R.id.tv_clear_cache)
    TextView tvClearCache;
    @BindView(R.id.rl_content_main)
    RelativeLayout rlContentMain;
    @BindView(R.id.sv_main)
    ScrollView svMain;
    @BindView(R.id.rl_setting_storage)
    RelativeLayout rlSettingStorage;

    private DecimalFormat df = new DecimalFormat("0.0000");

    private String mCacheSize = "";
    private boolean hasInit = false;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, CleanCacheActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--" + getStringResources("ui_settings_clear_cache"));
        tvBaseTitleName.setText(getStringResources("ui_settings_clear_cache"));

        //UbtLog.d(TAG, "isPad = " + AlphaApplication.isPad() + "  " + SizeUtils.getScreenHeight(getContext())
        //        + "/" + SizeUtils.getScreenWidth(getContext()) + "/" + getContext().getResources().getDisplayMetrics().density);

        if (AlphaApplication.isPad()) {

            svMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    if (!hasInit && svMain.getHeight() > 0) {
                        hasInit = true;

                        float densityRatio = 3.0f / SizeUtils.getDensity(getContext());

                        UbtLog.d(TAG, "svMain = " + svMain.getWidth() + "/" + svMain.getHeight());

                        RelativeLayout.LayoutParams svMainParams = (RelativeLayout.LayoutParams) svMain.getLayoutParams();
                        svMainParams.topMargin = (int) (svMainParams.topMargin * densityRatio);
                        svMainParams.bottomMargin = (int) (svMainParams.bottomMargin * densityRatio);
                        svMainParams.width = (int) (svMain.getWidth() * densityRatio);
                        svMain.setLayoutParams(svMainParams);

                        RelativeLayout.LayoutParams rlSettingStorageParams = (RelativeLayout.LayoutParams) rlSettingStorage.getLayoutParams();
                        rlSettingStorageParams.width = (int) (rlSettingStorage.getWidth() * densityRatio);
                        rlSettingStorageParams.height = (int) (rlSettingStorage.getWidth() * densityRatio);
                        rlSettingStorage.setLayoutParams(rlSettingStorageParams);

                        tvUseCache.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvUseCache.getTextSize() * densityRatio);
                        tvCacheSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvCacheSize.getTextSize() * densityRatio);
                        tvClearCache.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvClearCache.getTextSize() * densityRatio);

                        RelativeLayout.LayoutParams tvClearCacheParams = (RelativeLayout.LayoutParams) tvClearCache.getLayoutParams();
                        tvClearCacheParams.topMargin = (int) (tvClearCacheParams.topMargin * densityRatio);
                        tvClearCacheParams.height = (int) (tvClearCache.getHeight() * densityRatio);
                        tvClearCache.setMinWidth((int) (tvClearCache.getMinWidth() * densityRatio));
                        tvClearCache.setLayoutParams(tvClearCacheParams);

                    }
                }
            });

        }

        mPresenter.doReadCacheSize();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_clear_cache;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        UbtLog.d(TAG, "initUI");
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.tv_clear_cache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                CleanCacheActivity.this.finish();
                break;
            case R.id.tv_clear_cache:
                mCacheSize = tvCacheSize.getText().toString();
                if ("0K".equalsIgnoreCase(mCacheSize)) {
                    ToastUtils.showShort(getStringResources("ui_setting_has_not_cache"));
                } else {
                    new ConfirmDialog(getContext()).builder().setMsg(getStringResources("ui_setting_clear_cache_warning")).setCancelable(true).
                            setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UbtLog.d(TAG, "mCacheSize");
                                    mPresenter.doClearCache();
                                }
                            }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }

                break;
            default:
                break;
        }

    }


    @Override
    public void onClearCache() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                ToastUtils.showShort(getStringResources("ui_setting_clear_cache_success").replace("#", mCacheSize));
                mCacheSize = "0K";
                tvCacheSize.setText(mCacheSize);
                tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + "0%");
            }
        });
    }

    @Override
    public void onReadCacheSize(final int useSize, final long totalSize) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                UbtLog.d(TAG, "--onReadCacheSize--" + tvCacheSize);
                if (tvCacheSize == null) {
                    return;
                }
                int kb = useSize / 1024;
                if (kb > 1024) {
                    int mb = kb / 1024;
                    tvCacheSize.setText(mb + "M");
                } else {
                    tvCacheSize.setText(kb + "K");
                }

                if (totalSize > 0) {
                    if (useSize > 0) {
                        String ratio = df.format((float) useSize * 100 / totalSize) + "%";
                        tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + ratio);
                    } else {
                        tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + "0%");
                    }
                }
            }
        });
    }
}

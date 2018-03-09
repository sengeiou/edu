package com.ubt.alpha1e.userinfo.cleancache;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
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

    private DecimalFormat df = new DecimalFormat("0.0000");

    private String mCacheSize = "";

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, CleanCacheActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--" + getStringResources("ui_settings_clear_cache"));
        tvBaseTitleName.setText(getStringResources("ui_settings_clear_cache"));
        UbtLog.d(TAG,"doReadCacheSize");
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
        UbtLog.d(TAG,"initUI");
        initUI();
    }

    @OnClick({R.id.ll_base_back , R.id.tv_clear_cache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                CleanCacheActivity.this.finish();
                break;
            case R.id.tv_clear_cache:
                mCacheSize = tvCacheSize.getText().toString();
                if("0K".equalsIgnoreCase(mCacheSize)){
                    ToastUtils.showShort(getStringResources("ui_setting_has_not_cache"));
                }else {
                    new ConfirmDialog(getContext()).builder().setMsg(getStringResources("ui_setting_clear_cache_warning")).setCancelable(true).
                            setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UbtLog.d(TAG,"mCacheSize");
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

                ToastUtils.showShort(getStringResources("ui_setting_clear_cache_success").replace("#",mCacheSize));
                mCacheSize = "0K";
                tvCacheSize.setText(mCacheSize);
                tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + "0%");
            }
        });
    }

    @Override
    public void onReadCacheSize(final int useSize,final long totalSize) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                UbtLog.d(TAG,"--onReadCacheSize--" + tvCacheSize);
                if(tvCacheSize == null){
                    return;
                }
                int kb = useSize / 1024;
                if (kb > 1024) {
                    int mb = kb / 1024;
                    tvCacheSize.setText(mb + "M");
                } else {
                    tvCacheSize.setText(kb + "K");
                }

                if(totalSize > 0){
                    if(useSize > 0){
                        String ratio =  df.format((float)useSize*100/totalSize)  + "%";
                        tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + ratio);
                    }else {
                        tvUseRatio.setText(getStringResources("ui_setting_cache_retio_title") + "0%");
                    }
                }
            }
        });
    }
}

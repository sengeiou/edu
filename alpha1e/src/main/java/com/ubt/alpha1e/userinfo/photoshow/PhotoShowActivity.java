package com.ubt.alpha1e.userinfo.photoshow;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.ui.dialog.alertview.SheetAlertView;
import com.ubt.alpha1e.userinfo.contactus.ContactUsActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PhotoShowActivity extends MVPBaseActivity<PhotoShowContract.View, PhotoShowPresenter> implements PhotoShowContract.View {

    private static final String TAG = PhotoShowActivity.class.getSimpleName();

    @BindView(R.id.iv_ubt_wechat)
    ImageView ivUbtWechat;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, PhotoShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {

    }

    private void showSaveAlert(SpannableString menuStr){
        new SheetAlertView(null, null, null,
                new SpannableString[]{menuStr,new SpannableString(getStringResources("ui_common_cancel"))},
                null,
                PhotoShowActivity.this, SheetAlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int position){
                UbtLog.d(TAG,"position = " + position);
                switch (position)
                {
                    case 0:
                        doSavePhoto();
                        break;
                    case 1:
                        finishActivity();
                        break;
                }
            }
        }).show();
    }

    private void doSavePhoto(){
        PermissionUtils.getInstance(PhotoShowActivity.this).request(new PermissionUtils.PermissionLocationCallback() {
            @Override
            public void onSuccessful() {
                mPresenter.doSavePhoto();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onRationSetting() {

            }

            @Override
            public void onCancelRationSetting() {
            }

        }, PermissionUtils.PermissionEnum.STORAGE,PhotoShowActivity.this);
    }

    private void finishActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PhotoShowActivity.this.finish();
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_photo_show;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.iv_back, R.id.iv_ubt_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                PhotoShowActivity.this.finish();
                break;
            case R.id.iv_ubt_wechat:
                {
                    showSaveAlert(new SpannableString(getStringResources("ui_setting_save_website")));
                }
                break;
        }
    }

    @Override
    public void onSavePhoto(boolean isSuccess,String msg) {
        ToastUtils.showShort(msg);
        if(isSuccess){
            finishActivity();
        }
    }
}

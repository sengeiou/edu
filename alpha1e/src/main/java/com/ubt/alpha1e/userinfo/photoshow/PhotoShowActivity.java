package com.ubt.alpha1e.userinfo.photoshow;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
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
        ivUbtWechat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                }, PermissionUtils.PermissionEnum.STORAGE,PhotoShowActivity.this);

                return false;
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

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                PhotoShowActivity.this.finish();
                break;
        }
    }

    @Override
    public void onSavePhoto(boolean isSuccess,String msg) {
        ToastUtils.showShort(msg);
    }
}

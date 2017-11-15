package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;


public class StartInitSkinActivity extends BaseActivity {

    private static final String TAG = "StartInitSkinActivity";
    private static final int REQUEST_CODE_PERMISSION_MULTI = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //测试版本提交，删除数据库
        if (BasicSharedPreferencesOperator
                .getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE)) {

        } else {
            File file = new File(FileTools.db_log_cache + "/" + FileTools.db_log_name);
            UbtLog.d(TAG, "IS_FIRST_USE_APP_KEY =>>" + file.exists());
            //if(file.exists()){
            //file.delete();
            //}
            UbtLog.d(TAG, "IS_FIRST_USE_APP_KEY == " + file.exists());
        }

        UbtLog.d(TAG, "--onCreate--");


    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //首次启动，要重新再设一次
        doCheckLanguage();
        boolean isFirst = SPUtils.getInstance().getBoolean(Constant.SP_FIRST_PERMISSION, false);
        if (isFirst) {
            startNextActivity();
        } else {
            // 申请多个权限。
            AndPermission.with(this)
                    .requestCode(REQUEST_CODE_PERMISSION_MULTI)
                    .permission(Permission.CAMERA, Permission.LOCATION, Permission.STORAGE)
                    .callback(permissionListener)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {

                        }
                    })
                    .start();

            SPUtils.getInstance().put(Constant.SP_FIRST_PERMISSION, true);
        }
    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {

                case REQUEST_CODE_PERMISSION_MULTI:
                    startNextActivity();
                    break;
                default:
                    break;

            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {

                case REQUEST_CODE_PERMISSION_MULTI:
                    startNextActivity();
                    break;
                default:
                    break;

            }

        }

    };

    private void startNextActivity() {
        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


}

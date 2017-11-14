package com.ubt.alpha1e.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/14 11:12
 * @modifier：ubt
 * @modify_date：2017/11/14 11:12
 * [A brief description]
 * version
 */

public class PermissionUtils {

    private Context mContext;
    private PermissionLocationCallback mCallback;
    private static volatile PermissionUtils instance;

    public enum PermissionEnum {
        LOACTION, CAMERA, STORAGE
    }

    private PermissionUtils(Context context) {
        this.mContext = context;
    }

    public static PermissionUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (PermissionUtils.class) {
                instance = new PermissionUtils(context);
            }
        }
        return instance;
    }

    /**
     * 请求授权
     *
     * @param callback 回调结果
     */
    public void request(PermissionLocationCallback callback, PermissionEnum permission) {
        this.mCallback = callback;
        String sp_key = "";
        String[] permiss = null;
        switch (permission) {
            case LOACTION:
                sp_key = Constant.SP_PERMISSION_LOCATION;
                permiss = Permission.LOCATION;
                break;
            case CAMERA:
                sp_key = Constant.SP_PERMISSION_CAMERA;
                permiss = Permission.CAMERA;
                break;
            case STORAGE:
                sp_key = Constant.SP_PERMISSION_STORAGE;
                permiss = Permission.STORAGE;
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(sp_key) || null == permiss) {
            return;
        }
        boolean isFirstLocation = SPUtils.getInstance().getBoolean(sp_key, false);
        if (isFirstLocation && AndPermission.hasAlwaysDeniedPermission(mContext, Arrays.asList(permiss))) {
            mCallback.onRationSetting();
        } else {
            AndPermission.with(mContext)
                    .requestCode(10000)
                    .permission(permiss)
                    .callback(permissionListener)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            rationale.resume();
                        }
                    })
                    .start();
            SPUtils.getInstance().put(sp_key, true);

        }

    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            mCallback.onSuccessful();

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            mCallback.onFailure();
        }
    };


    public boolean hasPermission(@NonNull String... permissions) {
        return AndPermission.hasPermission(mContext, permissions);
    }


    public interface PermissionLocationCallback {
        /**
         * 授权成功
         */
        void onSuccessful();

        /**
         * 授权失败
         */
        void onFailure();

        /**
         * 已经勾选拒绝过
         */
        void onRationSetting();
    }
}

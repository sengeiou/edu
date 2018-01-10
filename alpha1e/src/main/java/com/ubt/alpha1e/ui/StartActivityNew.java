package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.BuildConfig;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.IStartUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.StartHelper;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;


public class StartActivityNew extends BaseActivity implements IStartUI, BaseDiaUI {

    private static final String TAG = "StartActivity";
    @BindView(R.id.gif_start)
    ImageView gifStart;
    GifDrawable gifDrawable;

    private String alias = null;
    private LoginHelper loginHelper;
    private UserInfo mCurrentUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_new);
        ButterKnife.bind(this);
        mHelper = new StartHelper(this, this);
        UbtLog.d(TAG, "BUILD_TYPE = " + BuildConfig.BUILD_TYPE + "   DEBUG = " + BuildConfig.DEBUG);
        initUI();
    }

    @Override
    protected void initUI() {
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.gif_start);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted() {
                    updateLanguage();
                }
            });
            gifStart.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initControlListener() {
        gifDrawable.start();
    }

    private void clearAnimation() {
        gifDrawable.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //首次启动，要重新再设一次
        doCheckLanguage();
        clearAnimation();
        initControlListener();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadFinish(boolean is_success) {
        mCurrentUser = ((AlphaApplication) this.getApplicationContext())
                .getCurrentUserInfo();
        UbtLog.e(TAG, "start activity mCurrentUser=" + mCurrentUser);

        // 如果登录成功，则将userId作为别名发送给JPush服务器
        if (mCurrentUser != null) {
            alias = "JP" + mCurrentUser.userId;
            loginHelper.setAlias(alias);
            System.out.println("alias:" + alias);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void gotoNext() {

        PermissionUtils.getInstance(this)
                .request(new PermissionUtils.PermissionLocationCallback() {
                    @Override
                    public void onSuccessful() {
                        ((StartHelper) mHelper).UpgadeDB();
                        mHandler.sendEmptyMessageDelayed(0x111, 1000);
                    }

                    @Override
                    public void onFailure() {
                        finish();
                    }

                    @Override
                    public void onRationSetting() {

                    }

                    @Override
                    public void onCancelRationSetting() {
                        finish();
                    }
                }, PermissionUtils.PermissionEnum.STORAGE, this);


    }


    @Override
    public void noteWaitWebProcressShutDown() {

    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) {
                Intent inte = new Intent();
                MobclickAgent.onEvent(StartActivityNew.this.getApplicationContext(), AlphaStatics.ACTIONS_LIB);//动作库页面次数
                UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
                if (null == userModel) {
                    inte.setClass(StartActivityNew.this, com.ubt.alpha1e.login.LoginActivity.class);
                } else {
                    if (TextUtils.isEmpty(userModel.getPhone())) {
                        inte.setClass(StartActivityNew.this, LoginActivity.class);
                    } else {
                        if (TextUtils.isEmpty(userModel.getAge())) {
                            inte.setClass(StartActivityNew.this, UserEditActivity.class);
                        } else {
                            inte.setClass(StartActivityNew.this, MainActivity.class);
                        }
                    }
                }
                StartActivityNew.this.startActivity(inte);
                StartActivityNew.this.finish();
            }
        }
    };

    @Override
    public void themeCheckFinish() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                    if (!((StartHelper) mHelper).isNeedUpdateApk()) {
                        gotoNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateLanguage(){
        if (((StartHelper) mHelper).isNeedUpdateLanguage()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCoonLoadingDia.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mCoonLoadingDia = LoadingDialog.getInstance(StartActivityNew.this, StartActivityNew.this);
                    ((LoadingDialog) mCoonLoadingDia).setDoCancelable(false);
                    ((LoadingDialog) mCoonLoadingDia).showMessage(getStringResources("ui_settings_language_updating"));
                }
            });
            ((StartHelper) mHelper).doUpdateLanguage();

        } else {
            gotoNext();
        }
    }
}

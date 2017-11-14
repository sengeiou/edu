package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.UserInfo;
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


public class StartActivity extends BaseActivity implements IStartUI, BaseDiaUI {

    private static final String TAG = "StartActivity";
    private ImageView img_start_bg;
    private ImageView img_logo;
    private TextView txt_title;
    private Animation img_start_bg_anim;
    private Animation img_logo_anim;
    private Animation txt_title_alpha_anim;
    private Animation txt_title_anim;
    private String alias = null;
    private LoginHelper loginHelper;
    private UserInfo mCurrentUser = null;
    private int scale = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scale = (int) this.getResources().getDisplayMetrics().density;
        setContentView(R.layout.activity_start);
        mHelper = new StartHelper(this, this);
        loginHelper = new LoginHelper(this);
        //读取主题信息，如果在主题动画加载完成之前读取完毕则继续检查逻辑，否则不检查
//        ((StartHelper) mHelper).doCkeckThemeInfo();
//        ((StartHelper) mHelper).doCkeckLanguageInfo();
        ((StartHelper) mHelper).doReadUser();
        // google play不自主升级-------------------start
//        ((StartHelper) mHelper).doUpdateApk();
        // google play不自主升级-------------------end
        ((StartHelper) mHelper).doGetLocation();
        ((StartHelper) mHelper).doRunGetResServices();

        //add by lihai upgadeDB
       // ((StartHelper) mHelper).UpgadeDB();

        initUI();

    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub

        txt_title = (TextView) findViewById(R.id.txt_title);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_start_bg = (ImageView) findViewById(R.id.img_start_bg);
        //img_logo_line_anim = AnimationUtils.loadAnimation(this,R.anim.logo_line_anim);

        img_start_bg_anim = new AlphaAnimation(0.1f, 1);
        txt_title_alpha_anim = new AlphaAnimation(0.1f, 1);

        img_logo_anim = new AlphaAnimation(0.1f, 1);
        txt_title_anim = new TranslateAnimation(0, 0, 50 * scale, 0);
        img_logo_anim.setDuration(1000); //设置持续时间1.5秒
        txt_title_anim.setDuration(600); //设置持续时间1秒
        img_start_bg_anim.setDuration(1500); //设置持续时间1秒
        txt_title_alpha_anim.setDuration(1000); //设置持续时间1秒
        initControlListener();

    }

    @Override
    protected void initControlListener() {

        img_start_bg_anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                txt_title.setVisibility(View.VISIBLE);
                txt_title.startAnimation(txt_title_alpha_anim);
            }
        });

        txt_title_alpha_anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                txt_title.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txt_title.getLayoutParams();
                params.topMargin = ((int) 135 * scale);
                txt_title.setLayoutParams(params);
                txt_title.startAnimation(txt_title_anim);
            }
        });

        img_logo_anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (((StartHelper) mHelper).isNeedUpdateLanguage()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mCoonLoadingDia.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mCoonLoadingDia = LoadingDialog.getInstance(StartActivity.this, StartActivity.this);
                            ((LoadingDialog) mCoonLoadingDia).setDoCancelable(false);
                            ((LoadingDialog) mCoonLoadingDia).showMessage(getStringResources("ui_settings_language_updating"));
                        }
                    });
                    ((StartHelper) mHelper).doUpdateLanguage();
                } else if (((StartHelper) mHelper).isNeedUpdateApk()) {
                } else {
                    gotoNext();
                }
            }
        });

        txt_title_anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                img_logo.setVisibility(View.VISIBLE);
                img_logo.startAnimation(img_logo_anim);
            }
        });
        img_start_bg.startAnimation(img_start_bg_anim);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //首次启动，要重新再设一次
        doCheckLanguage();
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

        mHandler.sendEmptyMessage(0x111);

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) {
                Intent inte = new Intent();

        /*if (((StartHelper) mHelper).isNeedCompleteInfo()) {
            PrivateInfoHelper.EditType type = null;
            if (mCurrentUser != null) {
                if (mCurrentUser.userEmail == null)
                    type = PrivateInfoHelper.EditType.complete_info_type;
                else if (mCurrentUser.userName == null)
                    type = PrivateInfoHelper.EditType.local_register_type;
                else
                    type = PrivateInfoHelper.EditType.local_register_type;

            }
            inte.putExtra(PrivateInfoHelper.Edit_type,
                    type);
            inte.setClass(StartActivity.this, PrivateInfoActivity.class);
        } else {
            if (mCurrentUser == null) {
                inte.setClass(StartActivity.this, HomeActivity.class);
            } else {
                inte.setClass(StartActivity.this, MainHomeActivity.class);
            }
        }*/

//                if (BasicSharedPreferencesOperator
//                        .getInstance(StartActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
//                        .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY)
//                        .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE)
//                        || AlphaApplicationValues.getCurrentEdit() == AlphaApplicationValues.EdtionCode.for_factory_edit) {
//                    //this version has show introduction go to main page
////            inte.setClass(StartActivity.this,MyMainActivity.class);
//                    // inte.setClass(StartActivity.this,MainActivity.class);
//                    inte.setClass(StartActivity.this, MainActivity.class);
//                } else {
//                    inte.setClass(StartActivity.this, IntroductionActivity.class);
//                }
                MobclickAgent.onEvent(StartActivity.this.getApplicationContext(), AlphaStatics.ACTIONS_LIB);//动作库页面次数
                UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
                if (null == userModel) {
                    inte.setClass(StartActivity.this, com.ubt.alpha1e.login.LoginActivity.class);
                } else {
                    if (TextUtils.isEmpty(userModel.getPhone())) {
                        inte.setClass(StartActivity.this, com.ubt.alpha1e.login.LoginActivity.class);
                    } else {
                        if (TextUtils.isEmpty(userModel.getAge())) {
                            inte.setClass(StartActivity.this, UserEditActivity.class);
                        }else{
                            inte.setClass(StartActivity.this, MainActivity.class);
                        }
                    }
                }
                StartActivity.this.startActivity(inte);
                StartActivity.this.finish();
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
}

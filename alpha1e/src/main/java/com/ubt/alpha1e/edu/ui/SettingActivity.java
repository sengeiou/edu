package com.ubt.alpha1e.edu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.services.AutoScanConnectService;
import com.ubt.alpha1e.edu.ui.helper.ISettingUI;
import com.ubt.alpha1e.edu.ui.helper.SettingHelper;
import com.ubt.alpha1e.edu.update.ApkUpdateManager;

public class SettingActivity extends BaseActivity implements ISettingUI {

    private RelativeLayout lay_clear_cache;
    private ImageButton btn_wifi_download;
    private ImageButton btn_message_note;
    private ImageButton btn_play_chaging;
    private ImageButton btn_auto_connect;
    private TextView btn_back;
    private LinearLayout lay_back;
    private TextView txt_cache_size;
    private TextView txt_logout_button;
    private TextView txt_title_name;
    private RelativeLayout lay_language;
    private TextView txt_current_language;
    private RelativeLayout rlAbout;
    private ImageView ivAbout;
    private RelativeLayout rlContactUs;

    private ImageView img_use_language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mHelper = new SettingHelper(this, this);
        initUI();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(SettingActivity.class.getSimpleName());
        super.onResume();

        initControlListener();
        ((SettingHelper) mHelper).doReadCacheSize();

        UserInfo userInfo  = ((AlphaApplication) this
                .getApplicationContext()).getCurrentUserInfo();
        if(userInfo == null){
            txt_logout_button.setVisibility(View.INVISIBLE);
        }else{
            txt_logout_button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        txt_title_name.setText(getStringResources("ui_leftmenu_settings"));
        String language = ((SettingHelper) mHelper).getLanguageCurrentStr();
        txt_current_language.setText(language);
    }

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_leftmenu_settings"));
        btn_back = (TextView) findViewById(R.id.tv_base_back);
        lay_back = (LinearLayout) findViewById(R.id.lay_base_back);

        img_use_language
                = (ImageView) findViewById(R.id.img_use_language);
        if (!((SettingHelper) mHelper).isFirstUseLanguage()) {
            img_use_language.setVisibility(View.GONE);
        }

        lay_clear_cache = (RelativeLayout) findViewById(R.id.lay_clear_cache);
        btn_wifi_download = (ImageButton) findViewById(R.id.btn_wifi_download);
        btn_message_note = (ImageButton) findViewById(R.id.btn_message_note);
        btn_play_chaging = (ImageButton) findViewById(R.id.btn_play_chaging);
        btn_auto_connect = (ImageButton) findViewById(R.id.btn_auto_connect);

        lay_language = (RelativeLayout) findViewById(R.id.lay_language);
        txt_current_language = (TextView) findViewById(R.id.txt_current_language);
        txt_cache_size = (TextView) findViewById(R.id.txt_cache_size);
        txt_logout_button = (TextView) findViewById(R.id.txt_logout_button);
        txt_title_name = (TextView) findViewById(R.id.txt_base_title_name);
//        txt_title_name.setText(getStringResources("ui_leftmenu_settings"));
        if (SettingHelper.isOnlyWifiDownload(this)) {
            btn_wifi_download.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btn_wifi_download.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (SettingHelper.isMessageNote(this)) {
            btn_message_note.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btn_message_note.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (SettingHelper.isPlayCharging(this)) {
            btn_play_chaging.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btn_play_chaging.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (SettingHelper.isAutoConnect(this)) {
            btn_auto_connect.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btn_auto_connect.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        rlAbout = (RelativeLayout) findViewById(R.id.rl_about);
        ivAbout = (ImageView) findViewById(R.id.iv_about);
        if (ApkUpdateManager.isNewersion) {
            ivAbout.setVisibility(View.GONE);
        }

        rlContactUs = (RelativeLayout) findViewById(R.id.rl_contact_us);
    }

    @Override
    protected void initControlListener() {
        android.view.View.OnClickListener back_listener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SettingActivity.this.finish();
            }
        };

        btn_back.setOnClickListener(back_listener);
        lay_back.setOnClickListener(back_listener);

        btn_wifi_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SettingHelper.isOnlyWifiDownload(SettingActivity.this)) {
                    SettingHelper.doSetOnlyWifiDownload(SettingActivity.this,
                            false);
                    btn_wifi_download
                            .setBackgroundResource(R.drawable.menu_setting_unselect);
                } else {
                    SettingHelper.doSetOnlyWifiDownload(SettingActivity.this,
                            true);
                    btn_wifi_download
                            .setBackgroundResource(R.drawable.menu_setting_select);
                }
            }
        });
        btn_message_note.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SettingHelper.isMessageNote(SettingActivity.this)) {
                    btn_message_note
                            .setBackgroundResource(R.drawable.menu_setting_unselect);
                    ((SettingHelper) mHelper).doSetMessageNote(
                            SettingActivity.this, false);
                } else {
                    btn_message_note
                            .setBackgroundResource(R.drawable.menu_setting_select);
                    ((SettingHelper) mHelper).doSetMessageNote(
                            SettingActivity.this, true);
                }
            }
        });

        btn_play_chaging.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SettingHelper.isPlayCharging(SettingActivity.this)) {
                    ((SettingHelper) mHelper).doSetPalyCharging(false);
                    btn_play_chaging
                            .setBackgroundResource(R.drawable.menu_setting_unselect);
                } else {
                    ((SettingHelper) mHelper).doSetPalyCharging(true);
                    btn_play_chaging
                            .setBackgroundResource(R.drawable.menu_setting_select);
                }
            }
        });

        btn_auto_connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SettingHelper.isAutoConnect(SettingActivity.this)) {
                    ((SettingHelper) mHelper).doSetAutoConnect(false);
                    AutoScanConnectService.doRefreshAutoConnect(false);
                    btn_auto_connect.setBackgroundResource(R.drawable.menu_setting_unselect);
                } else {
                    ((SettingHelper) mHelper).doSetAutoConnect(true);
                    AutoScanConnectService.doRefreshAutoConnect(true);
                    btn_auto_connect.setBackgroundResource(R.drawable.menu_setting_select);
                }

            }
        });

        lay_language.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LanguageActivity.LaunchActivity(SettingActivity.this.getApplicationContext());
                img_use_language.setVisibility(View.GONE);
            }
        });

        lay_clear_cache.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((SettingHelper) mHelper).doClearCache();

            }
        });

        txt_logout_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (((SettingHelper) mHelper).getCurrentUser() == null) {
                    SettingActivity.this.showToast("ui_action_need_login_title");
                    return;
                }
                ((SettingHelper) mHelper).doExitLogin();
                finish();
            }
        });

        rlAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        rlContactUs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadCacheSize(final int size) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                int kb = size / 1024;
                if (kb > 1024) {
                    int mb = kb / 1024;
                    txt_cache_size.setText(mb + "M");
                } else {
                    txt_cache_size.setText(kb + "K");
                }
            }
        });
    }

    @Override
    public void onClaerCache() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        SettingActivity.this,getStringResources("ui_settings_clear_cache_success")
                        , Toast.LENGTH_SHORT).show();
                txt_cache_size.setText("0K");
            }
        });

    }

}

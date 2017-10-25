package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.ui.dialog.IUpdateListener;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.dialog.UpdateDialog;
import com.ubt.alpha1e.ui.helper.AboutUsHelper;
import com.ubt.alpha1e.ui.helper.IAboutUsUI;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.update.ApkUpdateManager;

public class AboutUsActivity extends BaseActivity implements IAboutUsUI {


    private TextView txt_version;
    private TextView txt_robot_web_sit;
    private RelativeLayout lay_app_update;
    private RelativeLayout lay_feedback;
    private RelativeLayout lay_version_info;
    private RelativeLayout lay_user_intrud;
    private RelativeLayout lay_help;
    private ImageView img_update;

    private String language;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);
        mHelper = new AboutUsHelper(this, this);
        language =  getStandardLocale(getAppSetLanguage());
        version = "";
        try {
            version = AboutUsActivity.this.getPackageManager().getPackageInfo(
                    AboutUsActivity.this.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            version = "";
        }
        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(AboutUsActivity.class.getSimpleName());

        if(AlphaApplicationValues.getChannelName() == AlphaApplicationValues.ChannelName.google){
            findViewById(R.id.view_app_spit).setVisibility(View.GONE);
            lay_app_update.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void initUI() {


        txt_version = (TextView) findViewById(R.id.txt_version);
        txt_robot_web_sit = (TextView) findViewById(R.id.txt_robot_web_sit);
        lay_app_update = (RelativeLayout) findViewById(R.id.lay_app_update);
        lay_feedback = (RelativeLayout) findViewById(R.id.lay_feedback);
        lay_version_info = (RelativeLayout) findViewById(R.id.lay_version_info);
        lay_user_intrud = (RelativeLayout) findViewById(R.id.lay_user_intrud);
        lay_help = (RelativeLayout) findViewById(R.id.lay_help);
        img_update = (ImageView) findViewById(R.id.img_update);
        if (ApkUpdateManager.isNewersion) {
            img_update.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub


        txt_robot_web_sit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String url = HttpAddress
                        .getRequestUrl(Request_type.ubt_web_sit);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        lay_app_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (ApkUpdateManager.isUpdating()) {
                    Toast.makeText(
                            AboutUsActivity.this,
                           getStringResources("ui_about_update_doing"), Toast.LENGTH_LONG).show();
                    return;
                }

                if (SettingHelper.isOnlyWifiDownload(AboutUsActivity.this)
                        && (!AboutUsHelper.isWifiCoon(AboutUsActivity.this))) {
                    Toast.makeText(
                            AboutUsActivity.this,
                            getStringResources("ui_about_update_wifi_need"), Toast.LENGTH_SHORT).show();
                } else {
                    ((AboutUsHelper) mHelper).doUpdateApk();
                    try {
                        mCoonLoadingDia.cancel();
                    } catch (Exception e) {
                    }
                    mCoonLoadingDia = SLoadingDialog
                            .getInstance(AboutUsActivity.this);
                    mCoonLoadingDia.show();
                }

            }
        });

        lay_feedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AboutUsActivity.this.startActivity(new Intent().setClass(
                        AboutUsActivity.this, FeedBackActivity.class));
            }
        });

        lay_version_info.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent inte = new Intent();
                inte.putExtra(
                        WebContentActivity.WEB_TITLE,
                        getStringResources("ui_about_version_introduction"));
                inte.putExtra(
                        WebContentActivity.WEB_URL,
                        HttpAddress
                                .getRequestUrl(Request_type.get_version_info)
                                + HttpAddress.getParamsForGet(new String[]{
                                        version, getStandardLocale(getAppSetLanguage())},
                                Request_type.get_version_info));
                inte.setClass(AboutUsActivity.this, WebContentActivity.class);
                AboutUsActivity.this.startActivity(inte);
            }
        });

        lay_user_intrud.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent inte = new Intent();
                inte.putExtra(
                        WebContentActivity.WEB_TITLE,
                        getStringResources("ui_about_user_manual"));
                inte.putExtra(
                        WebContentActivity.WEB_URL,
                        HttpAddress.getRequestUrl(Request_type.get_intro_doc)
                                + HttpAddress.getParamsForGet(
                                new String[]{getStandardLocale(getAppSetLanguage())},
                                Request_type.get_intro_doc));
                inte.setClass(AboutUsActivity.this, WebContentActivity.class);
                AboutUsActivity.this.startActivity(inte);
            }
        });

        lay_help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent inte = new Intent();
                inte.putExtra(
                        WebContentActivity.WEB_TITLE,
                       getStringResources("ui_about_help"));
                inte.putExtra(
                        WebContentActivity.WEB_URL,
                        HttpAddress.getRequestUrl(Request_type.get_help_doc)
                                + HttpAddress.getParamsForGet(
                                new String[]{getStandardLocale(getAppSetLanguage())},
                                Request_type.get_help_doc));
                inte.setClass(AboutUsActivity.this, WebContentActivity.class);
                AboutUsActivity.this.startActivity(inte);
            }
        });
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        initTitle(getStringResources("ui_leftmenu_about"));
        txt_version.setText(getStringResources("ui_about_version")
                + version);
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteNewestVersion() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mCoonLoadingDia = SLoadingDialog
                        .getInstance(AboutUsActivity.this);
                ((SLoadingDialog) mCoonLoadingDia).show(getStringResources("ui_about_update_already_latest"));
            }
        });
    }

    @Override
    public void noteApkUpsateFail(final String info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                }
                Toast.makeText(AboutUsActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void noteApkUpdate(final String versionPath,
                              final String versionNameSizeInfo) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                }
                UpdateDialog.getInstance(AboutUsActivity.this,
                        versionNameSizeInfo.split("#"), new IUpdateListener() {
                            @Override
                            public void doUpdate() {
                                ApkUpdateManager.getInstance(
                                        AboutUsActivity.this, versionPath)
                                        .Update();
                            }

                            @Override
                            public void doIgnore() {
                            }
                        }).show();
            }
        });
    }

}

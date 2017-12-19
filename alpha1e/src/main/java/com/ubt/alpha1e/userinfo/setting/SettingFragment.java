package com.ubt.alpha1e.userinfo.setting;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.userinfo.aboutus.AboutUsActivity;
import com.ubt.alpha1e.userinfo.cleancache.CleanCacheActivity;
import com.ubt.alpha1e.userinfo.contactus.ContactUsActivity;
import com.ubt.alpha1e.userinfo.helpfeedback.HelpFeedbackActivity;
import com.ubt.alpha1e.userinfo.myrobot.MyRobotActivity;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManageActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.changeskin.SkinManager;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SettingFragment extends MVPBaseFragment<SettingContract.View, SettingPresenter> implements SettingContract.View {

    private static final String TAG = SettingFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REFRESH_LANGUAGE = 100;
    private static final int REFRESH_LANGUAGE_FINISH = 101;

    @BindView(R.id.rl_clear_cache)
    RelativeLayout rlClearCache;
    @BindView(R.id.rl_language)
    RelativeLayout rlLanguage;
    @BindView(R.id.btn_wifi_download)
    ImageButton btnWifiDownload;
    @BindView(R.id.btn_message_note)
    ImageButton btnMessageNote;
    @BindView(R.id.btn_auto_upgrade)
    ImageButton btnAutoUpgrade;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.rl_contact_us)
    RelativeLayout rlContactUs;
    @BindView(R.id.rl_message_myrobot)
    RelativeLayout rlMessageMyrobot;


    Unbinder unbinder;
    @BindView(R.id.tv_current_language)
    TextView tvCurrentLanguage;
    @BindView(R.id.rl_logout)
    RelativeLayout rlLogout;

    private String mParam1;
    private String mParam2;

    protected LoadingDialog mCoonLoadingDia;

    private List<String> mLanguageUpList = null;
    private List<String> mLanguageTitleList = null;
    private int mCurrentLanguageIndex = -1;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_LANGUAGE: {
                    mCoonLoadingDia.cancel();
                    mCoonLoadingDia.setDoCancelable(false);
                    mCoonLoadingDia.show();

                    mPresenter.doChangeLanguage(getContext(), mLanguageUpList.get(mCurrentLanguageIndex));
                }
                break;
                case REFRESH_LANGUAGE_FINISH: {
                    mCoonLoadingDia.cancel();
                    SkinManager.getInstance().setLanguage(mLanguageUpList.get(mCurrentLanguageIndex));
                    SkinManager.getInstance().changeSkin(FileTools.theme_pkg_file, FileTools.package_name, null);
                }
                break;
                default:
                    break;
            }


        }
    };

    public SettingFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--");
        String[] languageUps = ((MVPBaseActivity) getActivity()).getStringArray("ui_lanuages_up");
        String[] languageTitles = ((MVPBaseActivity) getActivity()).getStringArray("ui_lanuages_title");
        mLanguageUpList = Arrays.asList(languageUps);
        mLanguageTitleList = Arrays.asList(languageTitles);

        mCoonLoadingDia = LoadingDialog.getInstance(getContext());

        if (mPresenter.isAutoUpgrade()) {
            btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (mPresenter.isOnlyWifiDownload(getContext())) {
            btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (mPresenter.isMessageNote(getContext())) {
            btnMessageNote.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btnMessageNote.setBackgroundResource(R.drawable.menu_setting_unselect);
        }
        mPresenter.doReadCurrentLanguage();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.rl_clear_cache, R.id.rl_password_massage, R.id.btn_wifi_download, R.id.btn_message_note,R.id.btn_auto_upgrade, R.id.rl_language, R.id.rl_help_feedback, R.id.rl_about, R.id.rl_contact_us, R.id.rl_logout ,R.id.rl_message_myrobot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_clear_cache:
                CleanCacheActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_password_massage:
                PsdManageActivity.LaunchActivity(getContext());
                break;
            case R.id.btn_wifi_download:
                if (mPresenter.isOnlyWifiDownload(getContext())) {
                    mPresenter.doSetOnlyWifiDownload(getContext(), false);
                    btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_unselect);
                } else {
                    mPresenter.doSetOnlyWifiDownload(getContext(), true);
                    btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_select);
                }
                break;
            case R.id.btn_message_note:
                if (mPresenter.isMessageNote(getContext())) {
                    btnMessageNote.setBackgroundResource(R.drawable.menu_setting_unselect);
                    mPresenter.doSetMessageNote(getContext(), false);
                } else {
                    btnMessageNote.setBackgroundResource(R.drawable.menu_setting_select);
                    mPresenter.doSetMessageNote(getContext(), true);
                }
                break;
            case R.id.btn_auto_upgrade:
                if (mPresenter.isAutoUpgrade()) {
                    btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
                    mPresenter.doSetAutoUpgrade(false);
                } else {
                    btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_select);
                    mPresenter.doSetAutoUpgrade(true);
                }
                break;

            case R.id.rl_language:
                mPresenter.showLanguageDialog(getContext(), mCurrentLanguageIndex, mLanguageTitleList);
                break;
            case R.id.rl_help_feedback:
                HelpFeedbackActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_about:
                AboutUsActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_contact_us:
                ContactUsActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_logout:

                new ConfirmDialog(getContext()).builder()
                        .setTitle(((MVPBaseActivity)getActivity()).getStringResources("ui_setting_logout"))
                        .setMsg(((MVPBaseActivity)getActivity()).getStringResources("ui_setting_logout_tip"))
                        .setCancelable(true)
                        .setPositiveButton(((MVPBaseActivity)getActivity()).getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPresenter.doLogout();
                                UbtLog.d(TAG, "--logout--success");
                                LoginActivity.LaunchActivity(getContext());
                                getActivity().finish();
                            }
                        }).setNegativeButton(((MVPBaseActivity)getActivity()).getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            case R.id.rl_message_myrobot:
                UbtLog.d(TAG, "--rl_message_myrobot");
//                MyRobotActivity.LaunchActivity(getContext());
                break;
        }
    }

    @Override
    public void onLanguageSelectItem(int index, String language) {
        UbtLog.d(TAG, "index = " + index + " mCurrentLanguageIndex = " + mCurrentLanguageIndex + "    language = " + language);
        if (mCurrentLanguageIndex != index) {
            mCurrentLanguageIndex = index;
            tvCurrentLanguage.setText(language);

            mHandler.sendEmptyMessage(REFRESH_LANGUAGE);
        }
    }

    @Override
    public void onReadCurrentLanguage(int index, String language) {
        UbtLog.d(TAG, "index == " + index + "    language == " + language);
        mCurrentLanguageIndex = index;
        tvCurrentLanguage.setText(language);

    }

    @Override
    public void onChangeLanguage() {

        mHandler.sendEmptyMessage(REFRESH_LANGUAGE_FINISH);
    }

}

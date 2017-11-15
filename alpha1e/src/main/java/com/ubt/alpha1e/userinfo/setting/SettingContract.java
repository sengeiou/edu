package com.ubt.alpha1e.userinfo.setting;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SettingContract {
    interface View extends BaseView {
        void onLanguageSelectItem(int index,String language);

        void onReadCurrentLanguage(int index,String language);

        void onChangeLanguage();

    }

    interface  Presenter extends BasePresenter<View> {

        boolean isOnlyWifiDownload(Context context);

        void doSetOnlyWifiDownload(Context context, boolean isOnly);

        boolean isMessageNote(Context context);

        void doSetMessageNote(Context context, boolean isNote);

        void showLanguageDialog(Context context, int currentPosition, List<String> languageList);

        void doReadCurrentLanguage();

        void doChangeLanguage(Context context,String language);

        void doLogout();
    }
}

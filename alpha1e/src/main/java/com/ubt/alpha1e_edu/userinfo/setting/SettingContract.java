package com.ubt.alpha1e_edu.userinfo.setting;

import android.content.Context;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;
import com.ubt.alpha1e_edu.userinfo.model.MyRobotModel;

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

        void onGetRobotInfo(int result, MyRobotModel model);

        void onGetMessageNote(boolean isSuccess,String msg);

        void onSetMessageNote(boolean isSuccess,String msg);
    }

    interface  Presenter extends BasePresenter<View> {

        boolean isOnlyWifiDownload(Context context);

        void doSetOnlyWifiDownload(Context context, boolean isOnly);

        void doGetMessageNote();

        boolean isAutoUpgrade();

        void doSetMessageNote(boolean isNote);

        void doSetAutoUpgrade(boolean isAutoUpgrade);

        void showLanguageDialog(Context context, int currentPosition, List<String> languageList);

        void doReadCurrentLanguage();

        void doChangeLanguage(Context context,String language);

        void doLogout();

        void checkMyRobotState();
    }
}

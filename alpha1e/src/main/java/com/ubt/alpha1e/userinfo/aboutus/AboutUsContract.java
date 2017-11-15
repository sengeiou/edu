package com.ubt.alpha1e.userinfo.aboutus;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class AboutUsContract {
    interface View extends BaseView {
        void noteNewestVersion();

        void noteApkUpsateFail(String info);

        void noteApkUpdate(String versionPath);
    }

    interface  Presenter extends BasePresenter<View> {
        boolean isOnlyWifiDownload(Context context);

        boolean isWifiCoon(Context context);

        void doUpdateApk(String version);
    }
}

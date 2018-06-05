package com.ubt.alpha1e.edu.userinfo.cleancache;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CleanCacheContract {
    interface View extends BaseView {
        void onClearCache();

        void onReadCacheSize(int useSize,long totalSize);
    }

    interface  Presenter extends BasePresenter<View> {
        void doClearCache();

        void doReadCacheSize();
    }
}

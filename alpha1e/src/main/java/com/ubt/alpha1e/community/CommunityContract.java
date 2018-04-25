package com.ubt.alpha1e.community;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CommunityContract {
    interface View extends BaseView {
        void onQiniuTokenFromServer(boolean status, String token);
        void onloadFileToQiNiu(boolean status, String url);
    }

    interface  Presenter extends BasePresenter<View> {
        void getQiniuTokenFromServer();

        void loadFileToQiNiu(String path);
    }
}

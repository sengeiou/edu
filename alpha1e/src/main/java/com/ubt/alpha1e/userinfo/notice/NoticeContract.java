package com.ubt.alpha1e.userinfo.notice;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.userinfo.model.NoticeModel;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class NoticeContract {
    interface View extends BaseView {
        void setNoticeData(List<NoticeModel> list);
    }

    interface  Presenter extends BasePresenter<View> {
        void getNoticeData(int type);
    }
}

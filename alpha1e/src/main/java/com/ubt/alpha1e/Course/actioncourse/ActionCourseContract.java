package com.ubt.alpha1e.Course.actioncourse;

import com.ubt.alpha1e.Course.model.ActionCourseModel;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ActionCourseContract {
    interface View extends BaseView {
        void setActionCourseData(List<ActionCourseModel> list);
    }

    interface  Presenter extends BasePresenter<View> {
        void getActionCourseData();
    }
}

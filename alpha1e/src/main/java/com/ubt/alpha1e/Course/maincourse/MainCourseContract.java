package com.ubt.alpha1e.Course.maincourse;

import com.ubt.alpha1e.Course.model.CourseModel;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainCourseContract {
    interface View extends BaseView {

        void getCourcesData(List<CourseModel> list);

    }

    interface Presenter extends BasePresenter<View> {
        void getCourcesData();
    }
}

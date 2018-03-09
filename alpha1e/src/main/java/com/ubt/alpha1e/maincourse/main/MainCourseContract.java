package com.ubt.alpha1e.maincourse.main;

import com.ubt.alpha1e.maincourse.model.CourseModel;
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

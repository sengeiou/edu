package com.ubt.alpha1e.maincourse.courseone;

import android.content.Context;

import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CourseOneContract {
    interface View extends BaseView {
        void getCourseOneData(List<ActionCourseOneContent> list);
    }

    interface  Presenter extends BasePresenter<View> {
        void getCourseOneData(Context context);
        void getCourseTwoData(Context context);
    }
}
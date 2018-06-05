package com.ubt.alpha1e.edu.maincourse.actioncourse;

import android.content.Context;

import com.ubt.alpha1e.edu.base.ResponseMode.CourseDetailScoreModule;
import com.ubt.alpha1e.edu.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCourseContract {
    interface View extends BaseView {
        void setActionCourseData(List<ActionCourseModel> list);

        void getLastProgressResult(boolean result);

        void getCourseScores(List<CourseDetailScoreModule> list);
    }

    interface Presenter extends BasePresenter<View> {
        void getActionCourseData(Context context);
    }
}

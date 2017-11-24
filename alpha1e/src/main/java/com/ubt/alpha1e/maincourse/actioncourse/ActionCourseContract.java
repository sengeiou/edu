package com.ubt.alpha1e.maincourse.actioncourse;

import android.content.Context;

import com.ubt.alpha1e.base.ResponseMode.CourseDetailScoreModule;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

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

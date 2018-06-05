package com.ubt.alpha1e_edu.blocklycourse.courselist;

import android.content.Context;

import com.ubt.alpha1e_edu.blocklycourse.model.CourseData;
import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CourseListContract {
    interface View extends BaseView {
        void setBlocklyCourseData(List<CourseData> list);
        void updateSuccess();
        void updateFail();
    }

    interface  Presenter extends BasePresenter<View> {
        void getBlocklyCourseList(Context context);
        void updateCurrentCourse(int cid);
    }
}

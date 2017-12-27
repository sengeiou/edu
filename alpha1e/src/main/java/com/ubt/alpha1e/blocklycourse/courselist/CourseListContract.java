package com.ubt.alpha1e.blocklycourse.courselist;

import android.content.Context;

import com.ubt.alpha1e.blocklycourse.model.CourseData;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CourseListContract {
    interface View extends BaseView {
        void setBlocklyCourseData(List<CourseData> list);
    }

    interface  Presenter extends BasePresenter<View> {
        void getBlocklyCourseList(Context context);
    }
}

package com.ubt.alpha1e.blocklycourse;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BlocklyCourseContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void getData();
        void updateCourseData();

    }
}

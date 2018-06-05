package com.ubt.alpha1e_edu.blocklycourse;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BlocklyCourseContract {
    interface View extends BaseView {
        void updateSuccess();
        void updateFail();
        
    }

    interface  Presenter extends BasePresenter<View> {
        void getData();
        void updateCourseData(int cid);

    }
}

package com.ubt.alpha1e.action.course;

import com.ubt.alpha1e.base.BasePresenter;
import com.ubt.alpha1e.base.BaseView;

/**
 * @author：liuhai
 * @date：2017/10/26 19:57
 * @modifier：ubt
 * @modify_date：2017/10/26 19:57
 * [A brief description]
 * version
 */

public interface CourseActionContact {
    interface CourseActionView extends BaseView {
        void showSuccessData(String str);

        void showFailedData(String str);
    }

    interface ActionPresenter extends BasePresenter<CourseActionView> {
        void getData();
    }
}

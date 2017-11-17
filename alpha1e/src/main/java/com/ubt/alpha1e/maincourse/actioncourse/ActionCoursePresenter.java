package com.ubt.alpha1e.maincourse.actioncourse;

import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCoursePresenter extends BasePresenterImpl<ActionCourseContract.View> implements ActionCourseContract.Presenter {

    @Override
    public void getActionCourseData() {
        List<ActionCourseModel> list = new ArrayList<>();
        ActionCourseModel courseModel1 = new ActionCourseModel();
        courseModel1.setActionCourcesName("原理");
        courseModel1.setActionLockType(1);
        courseModel1.setDrawableId(R.drawable.ic_action_level1);
        list.add(courseModel1);
        ActionCourseModel courseModel2 = new ActionCourseModel();
        courseModel2.setActionCourcesName("动作编辑");
        courseModel2.setDrawableId(R.drawable.ic_action_level2);
        courseModel2.setActionLockType(0);
        list.add(courseModel2);
        ActionCourseModel courseModel3 = new ActionCourseModel();
        courseModel3.setActionCourcesName("Blockly编程");
        courseModel3.setDrawableId(R.drawable.ic_action_level3);
        courseModel3.setActionLockType(0);
        list.add(courseModel3);
        ActionCourseModel courseModel4 = new ActionCourseModel();
        courseModel4.setActionCourcesName("传感器");
        courseModel4.setDrawableId(R.drawable.ic_action_level4);
        courseModel4.setActionLockType(0);
        list.add(courseModel4);
        ActionCourseModel courseModel5 = new ActionCourseModel();
        courseModel5.setActionCourcesName("传感器");
        courseModel5.setDrawableId(R.drawable.ic_action_level5);
        courseModel5.setActionLockType(0);
        list.add(courseModel4);
        if (isAttachView()) {
            mView.setActionCourseData(list);
        }
    }
}

package com.ubt.alpha1e.maincourse.main;

import com.ubt.alpha1e.maincourse.model.CourseModel;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainCoursePresenter extends BasePresenterImpl<MainCourseContract.View> implements MainCourseContract.Presenter {

    @Override
    public void getCourcesData() {
        List<CourseModel> list = new ArrayList<>();
        CourseModel courseModel0=new CourseModel();
        courseModel0.setMainCourcesName("在线资源库");
        courseModel0.setLockType(0);
        courseModel0.setDrawableId(R.drawable.ic_lesson_yuanli);
        list.add(courseModel0);
        CourseModel courseModel1 = new CourseModel();
        courseModel1.setMainCourcesName("原理");
        courseModel1.setLockType(0);
        courseModel1.setDrawableId(R.drawable.ic_lesson_yuanli);
        list.add(courseModel1);
        CourseModel courseModel2 = new CourseModel();
        courseModel2.setMainCourcesName("动作编辑");
        courseModel2.setDrawableId(R.drawable.ic_lesson_action);
        courseModel2.setLockType(0);
        list.add(courseModel2);
        CourseModel courseModel3 = new CourseModel();
        courseModel3.setMainCourcesName("图形化编程");
        courseModel3.setDrawableId(R.drawable.ic_lesson_blockly);
        courseModel3.setLockType(0);
        list.add(courseModel3);
        CourseModel courseModel4 = new CourseModel();
        courseModel4.setMainCourcesName("传感器");
        courseModel4.setDrawableId(R.drawable.ic_lesson_sensor);
        courseModel4.setLockType(1);
        list.add(courseModel4);
        if (isAttachView()) {
            mView.getCourcesData(list);
        }
    }
}

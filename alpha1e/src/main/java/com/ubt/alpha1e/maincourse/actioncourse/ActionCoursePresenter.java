package com.ubt.alpha1e.maincourse.actioncourse;

import com.ubt.alpha1e.maincourse.model.ActionCourseContent;
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
        courseModel1.setActionCourcesName("第一关");
        courseModel1.setActionLockType(0);
        courseModel1.setDrawableId(R.drawable.ic_action_level1);
        list.add(courseModel1);
        ActionCourseModel courseModel2 = new ActionCourseModel();
        courseModel2.setActionCourcesName("第二关");
        courseModel2.setDrawableId(R.drawable.ic_action_level2);
        courseModel2.setActionLockType(1);
        list.add(courseModel2);
        ActionCourseModel courseModel3 = new ActionCourseModel();
        courseModel3.setActionCourcesName("第三关");
        courseModel3.setDrawableId(R.drawable.ic_action_level3);
        courseModel3.setActionLockType(1);
        list.add(courseModel3);
        ActionCourseModel courseModel4 = new ActionCourseModel();
        courseModel4.setActionCourcesName("第四关");
        courseModel4.setDrawableId(R.drawable.ic_action_level4);
        courseModel4.setActionLockType(1);
        list.add(courseModel4);
        ActionCourseModel courseModel5 = new ActionCourseModel();
        courseModel5.setActionCourcesName("第五关");
        courseModel5.setDrawableId(R.drawable.ic_action_level5);
        courseModel5.setActionLockType(1);
        list.add(courseModel4);
        if (isAttachView()) {
            mView.setActionCourseData(list);
        }
    }

    private List<ActionCourseModel> initActionCourseData() {
        List<ActionCourseModel> list = new ArrayList<>();

        /**
         * 第一关课时内容
         */
        ActionCourseContent content11 = new ActionCourseContent();
        content11.setIndex(1);
        content11.setContent("认识时间轴");
        content11.save();
        ActionCourseContent content12 = new ActionCourseContent();
        content12.setIndex(2);
        content12.setContent("熟悉动作模板");
        content12.save();
        ActionCourseContent content13 = new ActionCourseContent();
        content13.setIndex(3);
        content13.setContent("了解音频");
        content13.save();

        ActionCourseModel courseModel1 = new ActionCourseModel();
        courseModel1.setActionCourcesName("第一关");
        courseModel1.setActionLockType(0);
        courseModel1.setDrawableId(R.drawable.ic_action_level1);
        courseModel1.getContents().add(content11);
        courseModel1.getContents().add(content12);
        courseModel1.getContents().add(content13);
        courseModel1.save();

        list.add(courseModel1);

        /**
         * 第一关课时内容
         */
         ActionCourseContent content21 = new ActionCourseContent();
        content21.setIndex(1);
        content21.setContent("了解动作模板");
        content21.save();
        ActionCourseContent content22 = new ActionCourseContent();
        content22.setIndex(2);
        content22.setContent("模板讲解");
        content22.save();
        ActionCourseContent content23 = new ActionCourseContent();
        content23.setIndex(3);
        content23.setContent("添加指定动作");
        content23.save();

        ActionCourseModel courseModel2 = new ActionCourseModel();
        courseModel2.setActionCourcesName("第二关");
        courseModel2.setDrawableId(R.drawable.ic_action_level2);
        courseModel2.setActionLockType(1);
        courseModel2.getContents().add(content21);
        courseModel2.getContents().add(content22);
        courseModel2.getContents().add(content23);
        courseModel2.save();
        list.add(courseModel2);
        ActionCourseModel courseModel3 = new ActionCourseModel();
        courseModel3.setActionCourcesName("第三关");
        courseModel3.setDrawableId(R.drawable.ic_action_level3);
        courseModel3.setActionLockType(1);
        list.add(courseModel3);
        ActionCourseModel courseModel4 = new ActionCourseModel();
        courseModel4.setActionCourcesName("第四关");
        courseModel4.setDrawableId(R.drawable.ic_action_level4);
        courseModel4.setActionLockType(1);
        list.add(courseModel4);
        ActionCourseModel courseModel5 = new ActionCourseModel();
        courseModel5.setActionCourcesName("第五关");
        courseModel5.setDrawableId(R.drawable.ic_action_level5);
        courseModel5.setActionLockType(1);
        list.add(courseModel4);
        return list;
    }
}

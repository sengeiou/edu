package com.ubt.alpha1e.maincourse.actioncourse;

import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.SaveCourseProQuest;
import com.ubt.alpha1e.base.RequstMode.SaveCourseStatuRequest;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCoursePresenter extends BasePresenterImpl<ActionCourseContract.View> implements ActionCourseContract.Presenter {

    @Override
    public void getActionCourseData(Context context) {
        List<ActionCourseModel> list = new ArrayList<>();
        ActionCourseModel courseModel1 = new ActionCourseModel();
        courseModel1.setActionCourcesName("第一关");
        courseModel1.setTitle("第一关 基本概念");
        courseModel1.setActionLockType(0);
        courseModel1.setDrawableId(R.drawable.ic_action_level1);
        List<String> card1 = new ArrayList<>();
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_1"));
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_2"));
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_3"));
        courseModel1.setList(card1);
        list.add(courseModel1);

        ActionCourseModel courseModel2 = new ActionCourseModel();
        courseModel2.setActionCourcesName("第二关");
        courseModel2.setTitle("第二关 创定指定动作");
        courseModel2.setDrawableId(R.drawable.ic_action_level2);
        courseModel2.setActionLockType(1);

        List<String> card2 = new ArrayList<>();
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_1"));
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_2"));
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_3"));
        courseModel2.setList(card2);
        list.add(courseModel2);
        ActionCourseModel courseModel3 = new ActionCourseModel();
        courseModel3.setActionCourcesName("第三关");
        courseModel3.setTitle("第三关 创定指定动作");
        courseModel3.setDrawableId(R.drawable.ic_action_level3);
        courseModel3.setActionLockType(1);
        list.add(courseModel3);
        ActionCourseModel courseModel4 = new ActionCourseModel();
        courseModel4.setActionCourcesName("第四关");
        courseModel4.setTitle("第四关 创定指定动作");
        courseModel4.setDrawableId(R.drawable.ic_action_level4);
        courseModel4.setActionLockType(1);
        list.add(courseModel4);
        ActionCourseModel courseModel5 = new ActionCourseModel();
        courseModel5.setActionCourcesName("第五关");
        courseModel5.setTitle("第五关 创定指定动作");
        courseModel5.setDrawableId(R.drawable.ic_action_level5);
        courseModel5.setActionLockType(1);
        list.add(courseModel4);
        if (isAttachView()) {
            mView.setActionCourseData(list);
        }
    }


    /**
     * 获取课程
     */
    public void getCourseProgress() {
        SaveCourseProQuest proQequest = new SaveCourseProQuest();
        proQequest.setType(2);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.COURSE_GET_PROGRESS, proQequest, 100)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("getCourseProgress", "e===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("getCourseProgress", "response===" + response);
                    }
                });

    }






    public void getLastProgress() {
        SaveCourseStatuRequest statuRequest = new SaveCourseStatuRequest();
        statuRequest.setType(2);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.COURSE_GET_STATU, statuRequest, 100)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("saveCourseProgress", "e===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("saveCourseProgress", "response===" + response);
                    }
                });

    }

}

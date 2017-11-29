package com.ubt.alpha1e.maincourse.courseone;

import android.content.ContentValues;
import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.SaveCourseProQuest;
import com.ubt.alpha1e.base.RequstMode.SaveCourseStatuRequest;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseOnePresenter extends BasePresenterImpl<CourseOneContract.View> implements CourseOneContract.Presenter {


    /**
     * 获取第一关卡课时列表
     *
     * @param context
     */
    @Override
    public void getCourseOneData(Context context) {
        List<ActionCourseOneContent> list = new ArrayList<>();
        /**
         * 第一课时
         */
        ActionCourseOneContent actionCourseOneContent1 = new ActionCourseOneContent();

        actionCourseOneContent1.setIndex(0);
        actionCourseOneContent1.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card1_1"));
        List<CourseOne1Content> one1ContentList = new ArrayList<>();
        CourseOne1Content one1Content1 = new CourseOne1Content();
        one1Content1.setIndex(0);
        one1Content1.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_1"));
        one1Content1.setId(R.id.ll_frame);
        one1Content1.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content1.setActionPath(Constant.COURSE_ACTION_PATH + "时间轴.hts");
        one1Content1.setDirection(0);
        one1Content1.setX(0);
        one1Content1.setY(-50);
        one1Content1.setVertGravity(VerticalGravity.CENTER);
        one1Content1.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content1);


        CourseOne1Content one1Content3 = new CourseOne1Content();
        one1Content3.setIndex(2);
        one1Content3.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_3"));
        one1Content3.setId(R.id.rl_musicz_zpne);
        one1Content3.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content3.setActionPath(Constant.COURSE_ACTION_PATH + "音乐轴.hts");
        one1Content3.setX(0);
        one1Content3.setY(-50);
        one1Content3.setVertGravity(VerticalGravity.CENTER);
        one1Content3.setHorizGravity(HorizontalGravity.RIGHT);
        one1Content3.setDirection(0);
        one1ContentList.add(one1Content3);

        CourseOne1Content one1Content4 = new CourseOne1Content();
        one1Content4.setIndex(3);
        one1Content4.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_4"));
        one1Content4.setId(R.id.iv_reset_index);
        one1Content4.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content4.setActionPath(Constant.COURSE_ACTION_PATH + "动作帧.hts");
        one1Content4.setDirection(0);
        one1Content4.setX(80);
        one1Content4.setY(30);
        one1Content4.setVertGravity(VerticalGravity.ALIGN_BOTTOM);
        one1Content4.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content4);

        CourseOne1Content one1Content5 = new CourseOne1Content();
        one1Content5.setIndex(4);
        one1Content5.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_5"));
        one1Content5.setId(R.id.iv_add_frame);
        one1Content5.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content5.setActionPath(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
        one1Content5.setDirection(1);
        one1Content5.setX(20);
        one1Content5.setY(0);
        one1Content5.setVertGravity(VerticalGravity.CENTER);
        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
        one1ContentList.add(one1Content5);

        CourseOne1Content one1Content6 = new CourseOne1Content();
        one1Content6.setIndex(5);
        one1Content6.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_6"));
        one1Content6.setId(R.id.iv_play_music);
        one1Content6.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content6.setActionPath(Constant.COURSE_ACTION_PATH + "播放按钮.hts");
        one1Content6.setDirection(0);
        one1Content6.setX(-20);
        one1Content6.setY(0);
        one1Content6.setVertGravity(VerticalGravity.CENTER);
        one1Content6.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content6);
        actionCourseOneContent1.setList(one1ContentList);
        list.add(actionCourseOneContent1);

        /**
         * 第二课时
         */
        ActionCourseOneContent actionCourseOneContent2 = new ActionCourseOneContent();
        actionCourseOneContent2.setIndex(1);
        actionCourseOneContent2.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card1_2"));
        List<CourseOne1Content> one1ContentList1 = new ArrayList<>();
        CourseOne1Content one1Content7 = new CourseOne1Content();
        one1Content7.setIndex(0);
        one1Content7.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_2_1"));
        one1Content7.setId(R.id.iv_hand_left);
        one1Content7.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content7.setDirection(0);
        one1Content7.setX(10);
        one1Content7.setY(-50);
        one1Content7.setVertGravity(VerticalGravity.BELOW);
        one1Content7.setHorizGravity(HorizontalGravity.LEFT);
        one1ContentList1.add(one1Content7);
        actionCourseOneContent2.setList(one1ContentList1);
        list.add(actionCourseOneContent2);

        /**
         * 第三课时
         */
        ActionCourseOneContent actionCourseOneContent3 = new ActionCourseOneContent();
        actionCourseOneContent3.setIndex(1);
        actionCourseOneContent3.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card1_3"));
        List<CourseOne1Content> one1ContentList2 = new ArrayList<>();
        CourseOne1Content one1Content8 = new CourseOne1Content();
        one1Content8.setIndex(0);
        one1Content8.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_3_1"));
        one1Content8.setId(R.id.iv_action_bgm);
        one1Content8.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content8.setActionPath(Constant.COURSE_ACTION_PATH + "音乐库.hts");
        one1Content8.setDirection(0);
        one1Content8.setX(0);
        one1Content8.setY(0);
        one1Content8.setVertGravity(VerticalGravity.CENTER);
        one1Content8.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList2.add(one1Content8);
        actionCourseOneContent3.setList(one1ContentList2);
        list.add(actionCourseOneContent3);

        if (isAttachView()) {
            mView.getCourseOneData(list);
        }
    }

    /**
     * 获取第二关卡课时列表
     *
     * @param context
     */
    @Override
    public void getCourseTwoData(Context context) {
        List<ActionCourseOneContent> list = new ArrayList<>();
        /**
         * 第一课时
         */
        ActionCourseOneContent actionCourseOneContent1 = new ActionCourseOneContent();
        actionCourseOneContent1.setIndex(0);
        actionCourseOneContent1.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card2_1"));
        List<CourseOne1Content> one1ContentList = new ArrayList<>();
        CourseOne1Content one1Content1 = new CourseOne1Content();
        one1Content1.setIndex(0);
        one1Content1.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card2_2_1"));
        one1Content1.setId(R.id.iv_action_lib);
        one1Content1.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content1.setActionPath(Constant.COURSE_ACTION_PATH + "基础模版.hts");
        one1Content1.setDirection(0);
        one1Content1.setX(0);
        one1Content1.setY(0);
        one1Content1.setVertGravity(VerticalGravity.CENTER);
        one1Content1.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content1);
        actionCourseOneContent1.setList(one1ContentList);
        list.add(actionCourseOneContent1);

        /**
         * 第二课时
         */
        ActionCourseOneContent actionCourseOneContent2 = new ActionCourseOneContent();
        actionCourseOneContent2.setIndex(1);
        actionCourseOneContent2.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card2_2"));
        List<CourseOne1Content> one1ContentList1 = new ArrayList<>();

        CourseOne1Content one1Content8 = new CourseOne1Content();
        one1Content8.setIndex(0);
        one1Content8.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card2_2_2"));
        one1Content8.setId(R.id.iv_action_lib_more);
        one1Content8.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content8.setActionPath(Constant.COURSE_ACTION_PATH + "高级模版.hts");
        one1Content8.setDirection(0);
        one1Content8.setX(0);
        one1Content8.setY(0);
        one1Content8.setVertGravity(VerticalGravity.CENTER);
        one1Content8.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList1.add(one1Content8);
        actionCourseOneContent2.setList(one1ContentList1);
        list.add(actionCourseOneContent2);

        /**
         * 第三课时
         */
        ActionCourseOneContent actionCourseOneContent3 = new ActionCourseOneContent();
        actionCourseOneContent3.setIndex(1);
        actionCourseOneContent3.setCourseName(ResourceManager.getInstance(context).getStringResources("action_course_card2_3"));
        List<CourseOne1Content> one1ContentList3 = new ArrayList<>();

        CourseOne1Content one1Content3 = new CourseOne1Content();
        one1Content3.setIndex(0);
        one1Content3.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card2_3_1_3"));
        one1Content3.setDirection(0);
        one1Content3.setX(0);
        one1Content3.setY(0);
        one1Content3.setVertGravity(VerticalGravity.CENTER);
        one1Content3.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList1.add(one1Content8);
        actionCourseOneContent3.setList(one1ContentList3);
        list.add(actionCourseOneContent3);

        if (isAttachView()) {
            mView.getCourseOneData(list);
        }
    }


    /**
     * 保存课程最新进度
     */
    public void saveLastProgress(String progressOne, String courseTwo) {
        SaveCourseProQuest proQequest = new SaveCourseProQuest();
        proQequest.setCourseOne("1");
        proQequest.setProgressOne(progressOne);
        proQequest.setCourseTwo(courseTwo);
        proQequest.setProgressTwo("1");
        proQequest.setType(2);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.COURSE_SAVE_PROGRESS, proQequest, 100)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("saveLastProgress", "e===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("saveLastProgress", "response===" + response);
                        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
                        if (null != record) {
                            ContentValues values = new ContentValues();
                            values.put("isUpload", true);
                            DataSupport.updateAll(LocalActionRecord.class, values);
                        }
                    }
                });

    }


    /**
     * 保存每个关卡的分数
     */
    public void saveCourseProgress(String course, String statu) {
        SaveCourseStatuRequest statuRequest = new SaveCourseStatuRequest();
        statuRequest.setType(2);
        statuRequest.setCourse(course);
        statuRequest.setStatus(statu);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.COURSE_SAVE_STATU, statuRequest, 100)
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

package com.ubt.alpha1e.maincourse.actioncourse;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.SaveCourseProQuest;
import com.ubt.alpha1e.base.RequstMode.SaveCourseStatuRequest;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.ResponseMode.CourseDetailScoreModule;
import com.ubt.alpha1e.base.ResponseMode.CourseLastProgressModule;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.GsonImpl;
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

public class ActionCoursePresenter extends BasePresenterImpl<ActionCourseContract.View> implements ActionCourseContract.Presenter {

    @Override
    public void getActionCourseData(Context context) {
        List<ActionCourseModel> list = new ArrayList<>();
        ActionCourseModel courseModel1 = new ActionCourseModel();
        courseModel1.setActionCourcesName("第1关");
        courseModel1.setTitle("第一关 了解动作编辑器");
        courseModel1.setActionLockType(1);
        courseModel1.setDrawableId(R.drawable.ic_action_level1);
        courseModel1.setSize(3);
        List<String> card1 = new ArrayList<>();
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_1"));
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_2"));
        card1.add(ResourceManager.getInstance(context).getStringResources("action_course_card1_3"));
        courseModel1.setList(card1);
        list.add(courseModel1);

        ActionCourseModel courseModel2 = new ActionCourseModel();
        courseModel2.setActionCourcesName("第2关");
        courseModel2.setTitle("第二关 学习动作库");
        courseModel2.setDrawableId(R.drawable.ic_action_level2);
        courseModel2.setActionLockType(0);
        courseModel2.setSize(3);
        List<String> card2 = new ArrayList<>();
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_1"));
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_2"));
        card2.add(ResourceManager.getInstance(context).getStringResources("action_course_card2_3"));
        courseModel2.setList(card2);
        list.add(courseModel2);

        ActionCourseModel courseModel3 = new ActionCourseModel();
        courseModel3.setActionCourcesName("第3关");
        courseModel3.setTitle("第三关 学习音乐库");
        courseModel3.setDrawableId(R.drawable.ic_action_level3);
        courseModel3.setActionLockType(0);
        courseModel3.setSize(2);
        list.add(courseModel3);

        ActionCourseModel courseModel4 = new ActionCourseModel();
        courseModel4.setActionCourcesName("第4关");
        courseModel4.setTitle("第四关 添加动作+音频");
        courseModel4.setDrawableId(R.drawable.ic_action_level4);
        courseModel4.setActionLockType(0);
        courseModel4.setSize(3);
        list.add(courseModel4);

        ActionCourseModel courseModel5 = new ActionCourseModel();
        courseModel5.setActionCourcesName("第5关");
        courseModel5.setTitle("第五关 创建动作");
        courseModel5.setDrawableId(R.drawable.ic_action_level5);
        courseModel5.setActionLockType(0);
        courseModel5.setSize(3);
        list.add(courseModel5);

        ActionCourseModel courseModel6 = new ActionCourseModel();
        courseModel6.setActionCourcesName("第6关");
        courseModel6.setTitle("第六关 创建音频");
        courseModel6.setDrawableId(R.drawable.ic_action_level6);
        courseModel6.setActionLockType(0);
        courseModel6.setSize(4);
        list.add(courseModel6);

        ActionCourseModel courseModel7 = new ActionCourseModel();
        courseModel7.setActionCourcesName("第7关");
        courseModel7.setTitle("第七关 修改动作");
        courseModel7.setDrawableId(R.drawable.ic_action_level7);
        courseModel7.setActionLockType(0);
        courseModel7.setSize(2);
        list.add(courseModel7);

        ActionCourseModel courseModel8 = new ActionCourseModel();
        courseModel8.setActionCourcesName("第8关");
        courseModel8.setTitle("第八关 连续动作");
        courseModel8.setDrawableId(R.drawable.ic_action_level8);
        courseModel8.setActionLockType(0);
        courseModel8.setSize(2);
        list.add(courseModel8);

        ActionCourseModel courseModel9 = new ActionCourseModel();
        courseModel9.setActionCourcesName("第9关");
        courseModel9.setTitle("第九关 快速创建连续动作");
        courseModel9.setDrawableId(R.drawable.ic_action_level9);
        courseModel9.setActionLockType(0);
        courseModel9.setSize(2);
        list.add(courseModel9);

        ActionCourseModel courseModel10 = new ActionCourseModel();
        courseModel10.setActionCourcesName("第10关");
        courseModel10.setTitle("第十关 自定义动作");
        courseModel10.setDrawableId(R.drawable.ic_action_level10);
        courseModel10.setActionLockType(0);
        courseModel10.setSize(1);
        list.add(courseModel10);
//
//        ActionCourseModel courseModel13 = new ActionCourseModel();
//        list.add(courseModel13);

        if (isAttachView()) {
            mView.setActionCourseData(list);
        }
    }


    /**
     * 获取最新进度
     */
    public void getLastCourseProgress() {
        SaveCourseProQuest proQequest = new SaveCourseProQuest();
        proQequest.setType(2);
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        //本地没有记录，说明之前没用过，则根据后台返回保存本地记录
        if (null == record) {
            LocalActionRecord record1 = new LocalActionRecord();
            record1.setUserId(SPUtils.getInstance().getString(Constant.SP_USER_ID));
            record1.setCourseLevel(1);
            record1.setPeriodLevel(0);
            record1.setUpload(true);
            UbtLog.d("getLastCourseProgress", "record1===" + record1.toString());
            record1.save();
        }
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.GET_COURSE_PROGRESS, proQequest, 100)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("getLastCourseProgress", "e===" + e.getMessage());
                        if (isAttachView()) {
                            mView.getLastProgressResult(false);
                        }
                    }

                    //   {"status":true,"info":"0000","models":{"userId":"748872","courseOne":"1","progressOne":"2","courseTwo":"3","progressTwo":"1","type":"2"}}
                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("ActionCourseActivity", " getLastCourseProgress response===" + response);
                        BaseResponseModel<CourseLastProgressModule> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<CourseLastProgressModule>>() {
                                }.getType());
                        if (baseResponseModel.status) {
                            CourseLastProgressModule courseLastProgressModule = baseResponseModel.models;
                            LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);

                            //本地有记录，证明需要更新本地数据，或者上传记录
                            int course = record.getCourseLevel();
                            int level = record.getPeriodLevel();
                            if (null != courseLastProgressModule) {
                                //如果从后台取得关卡大于本地则更新本地数据
                                if (Integer.parseInt(courseLastProgressModule.getProgressOne()) > course) {
                                    ContentValues values = new ContentValues();
                                    values.put("CourseLevel", Integer.parseInt(courseLastProgressModule.getProgressOne()));
                                    values.put("periodLevel", Integer.parseInt(courseLastProgressModule.getProgressTwo()));
                                    values.put("isUpload", true);
                                    DataSupport.updateAll(LocalActionRecord.class, values);
                                } else if (Integer.parseInt(courseLastProgressModule.getProgressOne()) == course) {
                                    //如果从后台取得的关卡跟本地一致则判断课程，如果后台的课程大于本地的课程则更新本地
                                    if (Integer.parseInt(courseLastProgressModule.getProgressTwo()) > level) {
                                        ContentValues values = new ContentValues();
                                        values.put("CourseLevel", Integer.parseInt(courseLastProgressModule.getProgressOne()));
                                        values.put("periodLevel", Integer.parseInt(courseLastProgressModule.getProgressTwo()));
                                        values.put("isUpload", true);
                                        DataSupport.updateAll(LocalActionRecord.class, values);
                                    }
                                }
                            }

                            if (isAttachView()) {
                                mView.getLastProgressResult(true);
                            }
                        } else {
                            if (isAttachView()) {
                                mView.getLastProgressResult(false);
                            }
                        }
                    }
                });

    }


    /**
     * 获取所有上传关卡的分数获取
     */
    public void getAllCourseScore() {
        SaveCourseStatuRequest statuRequest = new SaveCourseStatuRequest();
        statuRequest.setType(2);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.COURSE_GET_STATU, statuRequest, 100)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("ActionCourseActivity", " getAllCourseScore e===" + e.getMessage());
                        if (isAttachView()) {
                            mView.getCourseScores(null);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("ActionCourseActivity", " getAllCourseScoreresponse===" + response);

                        BaseResponseModel<List<CourseDetailScoreModule>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<List<CourseDetailScoreModule>>>() {
                                }.getType());
                        if (baseResponseModel.status) {
                            if (isAttachView()) {
                                mView.getCourseScores(baseResponseModel.models);
                            }
                        } else {
                            if (isAttachView()) {
                                mView.getCourseScores(null);
                            }
                        }
                    }
                });
    }

    /**
     * 保存课程最新进度
     */
    public void saveLastProgress(String progressOne, String courseTwo) {
        SaveCourseProQuest proQequest = new SaveCourseProQuest();
        proQequest.setCourseOne("1");
        proQequest.setProgressOne(progressOne);
        proQequest.setProgressTwo(courseTwo);
        proQequest.setCourseTwo(progressOne);
        proQequest.setType(2);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.SAVE_COURSE_PROGRESS, proQequest, 100)
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

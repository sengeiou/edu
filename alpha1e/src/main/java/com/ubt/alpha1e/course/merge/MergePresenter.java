package com.ubt.alpha1e.course.merge;

import android.content.Context;

import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GetCourseProgressRequest;
import com.ubt.alpha1e.base.RequstMode.SaveCourseProgressRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.course.split.SplitPresenter;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MergePresenter extends BasePresenterImpl<MergeContract.View> implements MergeContract.Presenter{
    private static final String TAG = MergePresenter.class.getSimpleName();
    private static final int SAVE_COURSE_PROGRESS = 1;
    private static final int GET_COURSE_PROGRESS  = 2;

    @Override
    public void doSaveCourseProgress(int type, int courseOne, int progressOne) {

        int mLocalProgress = doGetLocalProgress();
        if(progressOne > mLocalProgress ){
            doSaveLocal(progressOne);
        }else {
            progressOne = mLocalProgress;
        }

        SaveCourseProgressRequest saveCourseProgressRequest = new SaveCourseProgressRequest();
        saveCourseProgressRequest.setType(type);
        saveCourseProgressRequest.setCourseOne(courseOne);
        saveCourseProgressRequest.setProgressOne(progressOne);

        String url = HttpEntity.SAVE_COURSE_PROGRESS;
        doRequestFromWeb(url,saveCourseProgressRequest,SAVE_COURSE_PROGRESS);
    }

    @Override
    public void doGetCourseProgress(int type) {
        GetCourseProgressRequest getCourseProgressRequest = new GetCourseProgressRequest();
        getCourseProgressRequest.setType(type);

        String url = HttpEntity.GET_COURSE_PROGRESS;
        doRequestFromWeb(url,getCourseProgressRequest,GET_COURSE_PROGRESS);
    }

    private void doSaveLocal(int progressOne){
        SPUtils.getInstance().put(Constant.PRINCIPLE_PROGRESS + SPUtils.getInstance().getString(Constant.SP_USER_ID),progressOne);
    }

    @Override
    public int doGetLocalProgress(){
        String progressKey = Constant.PRINCIPLE_PROGRESS + SPUtils.getInstance().getString(Constant.SP_USER_ID);
        return SPUtils.getInstance().getInt(progressKey,0);
    }

    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage());
                switch (id){
                    case SAVE_COURSE_PROGRESS:
                        mView.onSaveCourseProgress(false,"");
                        break;
                }

            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                switch (id){
                    case SAVE_COURSE_PROGRESS:
                    {

                    }
                    break;
                    case GET_COURSE_PROGRESS:

                        break;
                }

            }
        });

    }
}

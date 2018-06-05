package com.ubt.alpha1e.edu.blocklycourse;

import com.ubt.alpha1e.edu.blocklycourse.model.UpdateCourseRequest;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BlocklyCoursePresenter extends BasePresenterImpl<BlocklyCourseContract.View> implements BlocklyCourseContract.Presenter{

    private static final String TAG = "BlocklyCoursePresenter";

    @Override
    public void getData() {
        //从后台获取课程数据
    }

    @Override
    public void updateCourseData(int cid) {
        UpdateCourseRequest courseRequest = new UpdateCourseRequest();
        courseRequest.setCurrGraphProgramId(cid);

        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_BLOCKLY_COURSE, courseRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.e(TAG, "updateCurrentCourse onError:" + e.getMessage());
                if(isAttachView()){
                    mView.updateFail();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "updateCurrentCourse onResponse:" + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if(status){
                        if(isAttachView()){
                            mView.updateSuccess();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}

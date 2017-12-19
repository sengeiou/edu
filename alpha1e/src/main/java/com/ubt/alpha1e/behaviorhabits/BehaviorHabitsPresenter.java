package com.ubt.alpha1e.behaviorhabits;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BehaviorHabitsPresenter extends BasePresenterImpl<BehaviorHabitsContract.View> implements BehaviorHabitsContract.Presenter{

    private static final String TAG = BehaviorHabitsPresenter.class.getSimpleName();

    private static final int GET_BEHAVIOURLIST_LIST = 1;
    private static final int GET_BEHAVIOURLIST_SCORE = 2;

    @Override
    public void doTest() {
        mView.onTest(true);
    }

    @Override
    public void getBehaviourList(int sex, int grade) {

        String url = "";
        BaseRequest baseRequest = null;
        doRequestFromWeb(url, baseRequest, GET_BEHAVIOURLIST_LIST);

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
                    case GET_BEHAVIOURLIST_LIST:
                        mView.showBehaviourList(null);
                        break;
                    case GET_BEHAVIOURLIST_SCORE:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

                switch (id){
                    case GET_BEHAVIOURLIST_LIST:
                    {
                        List<HabitsEvent> modelList = new ArrayList<>();
                        mView.showBehaviourList(modelList);
                    }
                    break;

                    case GET_BEHAVIOURLIST_SCORE:
                        break;
                }
            }
        });

    }
}

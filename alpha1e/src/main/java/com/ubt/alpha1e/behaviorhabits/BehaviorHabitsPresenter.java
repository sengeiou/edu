package com.ubt.alpha1e.behaviorhabits;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourControlRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourEventRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourListRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourSaveUpdateRequest;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
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

    private static final int GET_BEHAVIOURLIST_CMD = 1;
    private static final int GET_BEHAVIOUREVENT_CMD=2;
    private static final int GET_BEHAVIOURPLAYCONTENT_CMD=3;
    private static final int GET_BEHAVIOURCONTROL_CMD=4;
    private static final int GET_BEHAVIOURSAVEUPDATE_CMD=5;
    String url = "http://10.10.1.54:11808/mockjs/72/habit/item/getTotalScore?token=fdgadf&type=agdf";
    private String getTemplatePath="/behaviour/template/get";
    private String getEventPath="/behaviour/event/get";
    private String SaveModifyEventPath="/behaviour/event/update";
    private String ControlEventPath="/behaviour/template/update";
    private String getPlayContentPath="/behaviour/template/getEventContent";
    private boolean mock=true;

    @Override
    public void doTest() {
        mView.onTest(true);
    }

    @Override
    public void dealayAlertTime(int count) {

    }
    @Override
    public void getBehaviourList(String sex, String grade) {
        BehaviourListRequest mBehaviourListRequest = new BehaviourListRequest();
        mBehaviourListRequest.setSex(sex);
        mBehaviourListRequest.setGrade(grade);
        doRequestFromWeb(url+getTemplatePath, mBehaviourListRequest, GET_BEHAVIOURLIST_CMD);
    }

    @Override
    public void getBehaviourEvent(String eventId) {
        BehaviourEventRequest mBehaviourEventRequest=new BehaviourEventRequest();
        mBehaviourEventRequest.setEventId(eventId);
        doRequestFromWeb(url+getEventPath, mBehaviourEventRequest, GET_BEHAVIOUREVENT_CMD);
    }

    @Override
    public void getBehaviourPlayContent(String sex, String grade) {
        BehaviourListRequest mBehaviourListRequest = new BehaviourListRequest();
        mBehaviourListRequest.setSex(sex);
        mBehaviourListRequest.setGrade(grade);
        doRequestFromWeb(url+getPlayContentPath, mBehaviourListRequest, GET_BEHAVIOURPLAYCONTENT_CMD);
    }

    @Override
    public void setBehaviourEvent(String eventId, int status) {
        BehaviourControlRequest mBehaviourControlRequest=new BehaviourControlRequest();
        mBehaviourControlRequest.setEventId(eventId);
        mBehaviourControlRequest.setOperateType(new Integer(status).toString());
        doRequestFromWeb(url+ControlEventPath, mBehaviourControlRequest,GET_BEHAVIOURCONTROL_CMD);
    }

    @Override
    public void saveBehaviourEvent(HabitsEventDetail content) {
        BehaviourSaveUpdateRequest mBehaviourSaveUpdateRequest=new BehaviourSaveUpdateRequest();
        mBehaviourSaveUpdateRequest.setEventId(content.getEventId());
        mBehaviourSaveUpdateRequest.setEventTime(content.getEventTime());
        mBehaviourSaveUpdateRequest.setContentIds(content.getContentIds());
        doRequestFromWeb(url+SaveModifyEventPath, mBehaviourSaveUpdateRequest,GET_BEHAVIOURSAVEUPDATE_CMD);
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
                    case GET_BEHAVIOURLIST_CMD:
                        mView.showBehaviourList(false,null,mView.getContext().getResources().getString(R.string.network_request_error));
                        break;
                    case GET_BEHAVIOUREVENT_CMD:
                        break;
                    case GET_BEHAVIOURCONTROL_CMD:
                        break;
                    case GET_BEHAVIOURSAVEUPDATE_CMD:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

                switch (id){
                    case GET_BEHAVIOURLIST_CMD:
                    {
                        if(mock){
                            List<HabitsEvent> modelList = new ArrayList<>();
                            HabitsEvent mHabitsEvent=new HabitsEvent();
                            mHabitsEvent.setEventId("46");
                            mHabitsEvent.setEventName("早餐");
                            mHabitsEvent.setEventTime("07:30");
                            HabitsEvent mHabitsEvent1=new HabitsEvent();
                            mHabitsEvent1.setEventId("46");
                            mHabitsEvent1.setEventName("午餐");
                            mHabitsEvent1.setEventTime("07:30");
                            modelList.add(mHabitsEvent);
                            modelList.add(mHabitsEvent1);
                            mView.showBehaviourList(true,modelList,mView.getContext().getResources().getString(R.string.network_request_success));
                        }else {
                            List<HabitsEvent> modelList = new ArrayList<>();
                            mView.showBehaviourList(true,modelList,mView.getContext().getResources().getString(R.string.network_request_success));
                        }
                    }
                    break;
                    case GET_BEHAVIOUREVENT_CMD:
                        break;
                    case GET_BEHAVIOURCONTROL_CMD:
                        break;
                    case GET_BEHAVIOURSAVEUPDATE_CMD:
                        break;
                    default:
                        break;
                }
            }
        });

    }
}

package com.ubt.alpha1e.userinfo.notice;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.base.RequstMode.GetMessageListRequest;
import com.ubt.alpha1e.base.RequstMode.UpdateMessageRequest;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.model.NoticeModel;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticePresenter extends BasePresenterImpl<NoticeContract.View> implements NoticeContract.Presenter {


    @Override
    public void getNoticeData(final int type, int page, int offset) {
        GetMessageListRequest messageListRequest = new GetMessageListRequest();
        messageListRequest.setLimit(offset);
        messageListRequest.setOffset(page);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_GET_LIST, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("getLoopData", "onError:" + e.getMessage());
                if (mView != null) {
                    mView.setNoticeData(false, type, null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("getLoopData", "getUser__response==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d("getLoopData", "baseResponseModel.models==" + baseResponseModel.models);
                        mView.setNoticeData(true, type, baseResponseModel.models);
                    }
                } else {
                    if (mView != null) {
                        mView.setNoticeData(false, type, null);
                    }
                }
            }
        });
    }

    /**
     * 更新消息状态
     *
     * @param noticeId
     */
    @Override
    public void updateNoticeStatu(final int noticeId) {
        UpdateMessageRequest messageListRequest = new UpdateMessageRequest();
        messageListRequest.setMessageId(noticeId);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_UPDATE_STATU, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("getLoopData", "onError:" + e.getMessage());
                if (mView != null) {
                    mView.updateStatu(false, noticeId);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("getLoopData", "getUser__response==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d("getLoopData", "baseResponseModel.models==" + baseResponseModel.models);
                        mView.updateStatu(true, noticeId);
                    }
                } else {
                    if (mView != null) {
                        mView.updateStatu(false, noticeId);
                    }
                }
            }
        });
    }

    @Override
    public void deleteNotice(final int noticeId) {
        UpdateMessageRequest messageListRequest = new UpdateMessageRequest();
        messageListRequest.setMessageId(noticeId);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_DELETE, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("getLoopData", "onError:" + e.getMessage());
                if (mView != null) {
                    mView.deleteNotice(false, noticeId);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("getLoopData", "getUser__response==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d("getLoopData", "baseResponseModel.models==" + baseResponseModel.models);
                        mView.deleteNotice(true, noticeId);
                    }
                } else {
                    if (mView != null) {
                        mView.deleteNotice(false, noticeId);
                    }
                }
            }
        });

    }
}

package com.ubt.alpha1e_edu.userinfo.notice;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.base.RequstMode.GetMessageListRequest;
import com.ubt.alpha1e_edu.base.RequstMode.SendMessageRequest;
import com.ubt.alpha1e_edu.base.RequstMode.UpdateMessageRequest;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.login.HttpEntity;
import com.ubt.alpha1e_edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e_edu.userinfo.model.NoticeModel;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticePresenter extends BasePresenterImpl<NoticeContract.View> implements NoticeContract.Presenter {
    private static String TAG = NoticePresenter.class.getSimpleName();

    @Override
    public void getNoticeData(final int type, int page, int offset) {
        GetMessageListRequest messageListRequest = new GetMessageListRequest();
        messageListRequest.setOffset(page);
        messageListRequest.setLimit(offset);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_GET_LIST, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, " getNoticeData onError:" + e.getMessage());
                if (mView != null) {
                    mView.setNoticeData(false, type, null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getNoticeData==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d(TAG, "getNoticeData.models==" + baseResponseModel.models);
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
                UbtLog.d(TAG, "updateNoticeStatu onError:" + e.getMessage());
                if (mView != null) {
                    mView.updateStatu(false, noticeId);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "updateNoticeStatu==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d(TAG, "updateNoticeStatu.models==" + baseResponseModel.models);
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
        if (mView != null) {
            mView.showLoading();
        }
        UpdateMessageRequest messageListRequest = new UpdateMessageRequest();
        messageListRequest.setMessageId(noticeId);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_DELETE, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "onError:" + e.getMessage());
                if (mView != null) {
                    mView.deleteNotice(false, noticeId);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "deleteNotice==" + response);

                BaseResponseModel<List<NoticeModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<NoticeModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d(TAG, "deleteNotice.models==" + baseResponseModel.models);
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

    /**
     * 发送消息状态
     */

    public void sendMessage() {
        SendMessageRequest messageListRequest = new SendMessageRequest();
        messageListRequest.setType("1");
        messageListRequest.setContent("dddd");
        messageListRequest.setLinkUrl("http://www.baidu.com");
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.SEND_MESSAGE, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "updateNoticeStatu==" + response);

            }
        });
    }

}

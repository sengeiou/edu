package com.ubt.alpha1e_edu.community.actionselect;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.base.RequstMode.GetMessageListRequest;
import com.ubt.alpha1e_edu.base.ToastUtils;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.data.model.DownloadProgressInfo;
import com.ubt.alpha1e_edu.login.HttpEntity;
import com.ubt.alpha1e_edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e_edu.userinfo.dynamicaction.DownLoadActionManager;
import com.ubt.alpha1e_edu.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ActionSelectPresenter extends BasePresenterImpl<ActionSelectContract.View> implements ActionSelectContract.Presenter{

    private static final String TAG = ActionSelectPresenter.class.getSimpleName();

    private static final int GET_DYNAMIC_LIST = 0;

    /**
     * 获取原创列表
     *
     * @param type   上拉0 下拉1
     * @param page   页数
     * @param offset 数量
     */
    @Override
    public void getDynamicData(final int type, int page, int offset) {
        GetMessageListRequest messageListRequest = new GetMessageListRequest();
        messageListRequest.setOffset(page);
        messageListRequest.setLimit(offset);

        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.ACTION_DYNAMIC_LIST, messageListRequest, GET_DYNAMIC_LIST).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, " getNoticeData onError:" + e.getMessage());
                if (mView != null) {
                    mView.setDynamicData(false, type, null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getDynamicData==" + response);

                BaseResponseModel<List<DynamicActionModel>> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<List<DynamicActionModel>>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d(TAG, "getDynamicData.models==" + baseResponseModel.models);
                        mView.setDynamicData(true, type, baseResponseModel.models);
                    }
                } else {
                    if (mView != null) {
                        mView.setDynamicData(false, type, null);
                    }
                }
            }
        });
    }

    /**
     * 根据ID获取位置
     *
     * @param id
     * @param list
     * @return
     */
    public int getPositionById(long id, List<DynamicActionModel> list) {
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getActionId() == id) {
                n = i;
                break;
            }
        }
        return n;
    }


    /**
     * 从机器人列表数据处理，并结束刷新
     *
     * @param list
     */
    public void praseGetRobotData(Context context, List<String> list, List<DynamicActionModel> mDynamicActionModels) {

        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            for (String str : list) {
                if (mDynamicActionModels.get(i).getActionOriginalId().equals(str)) {
                    mDynamicActionModels.get(i).setDownload(true);
                    break;
                }
            }
        }

        //获取下载类中正在下载的列表数据
        List<DynamicActionModel> list1 = DownLoadActionManager.getInstance(context).getRobotDownList();
        if (null != list1 && list1.size() > 0) {
            for (int i = 0; i < mDynamicActionModels.size(); i++) {
                for (DynamicActionModel actionInfo : list1) {
                    if (mDynamicActionModels.get(i).getActionId() == actionInfo.getActionId()) {
                        mDynamicActionModels.get(i).setActionStatu(2);
                        break;
                    }
                }
            }
        }
        /**
         * 获取正在播放的PlayingInfo，下次进来的时候可以直接显示播放状态
         */
        DynamicActionModel actionInfo = DownLoadActionManager.getInstance(context).getPlayingInfo();
        if (null != actionInfo) {
            for (int i = 0; i < mDynamicActionModels.size(); i++) {
                if (mDynamicActionModels.get(i).getActionId() == actionInfo.getActionId()) {
                    mDynamicActionModels.get(i).setActionStatu(1);
                    break;
                }
            }
        }
    }


    /**
     * 处理下载进度
     *
     * @param downloadProgressInfo
     */
    public void praseDownloadData(Context context, DownloadProgressInfo downloadProgressInfo, List<DynamicActionModel> mDynamicActionModels) {
        long actionId = downloadProgressInfo.actionId;
        int position = getPositionById(actionId, mDynamicActionModels);//根据ID获取当前位置
        if (downloadProgressInfo.status == 1) {//正在下载
            String progress = downloadProgressInfo.progress;
            UbtLog.d(TAG, "progress=====" + progress);
            mDynamicActionModels.get(position).setActionStatu(2);
            mDynamicActionModels.get(position).setDownloadProgress(Double.parseDouble(progress));
        } else if (downloadProgressInfo.status == 2) {//下载成功后立即播放
            DynamicActionModel dynamicActionModel = mDynamicActionModels.get(position);
            dynamicActionModel.setDownload(true);
            dynamicActionModel.setActionStatu(0);
            for (int i = 0; i < mDynamicActionModels.size(); i++) {
                if (mDynamicActionModels.get(i).getActionStatu() == 1) {
                    UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                    mDynamicActionModels.get(i).setActionStatu(0);
                }
            }
            dynamicActionModel.setActionStatu(1);
            DownLoadActionManager.getInstance(context).playAction(false, dynamicActionModel);
        } else if (downloadProgressInfo.status == 3) {//机器人未联网
            ToastUtils.showShort("机器人未联网");
            mDynamicActionModels.get(position).setActionStatu(0);
        } else {//下载失败
            ToastUtils.showShort("下载失败");
            mDynamicActionModels.get(position).setActionStatu(0);
        }
    }


    /**
     * 播放动作文件
     *
     * @param context
     * @param position
     * @param mDynamicActionModels
     */
    public void playAction(Context context, int position, List<DynamicActionModel> mDynamicActionModels) {
        DynamicActionModel dynamicActionModel = mDynamicActionModels.get(position);
        int actionStatu = dynamicActionModel.getActionStatu();
        UbtLog.d(TAG, "actionName==" + dynamicActionModel.getActionName() + "  actionStatu===" + dynamicActionModel.getActionStatu());
        if (actionStatu == 0) {
            if (dynamicActionModel.isDownload()) {//已经下载
                for (int i = 0; i < mDynamicActionModels.size(); i++) {
                    if (mDynamicActionModels.get(i).getActionStatu() == 1) {
                        UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                        mDynamicActionModels.get(i).setActionStatu(0);
                    }
                }
                mDynamicActionModels.get(position).setActionStatu(1);
                DownLoadActionManager.getInstance(context).playAction(false, dynamicActionModel);
            } else {//没有下载，需要下载
                DownLoadActionManager.getInstance(context).readNetworkStatus();
                DownLoadActionManager.getInstance(context).downRobotAction(dynamicActionModel);
                mDynamicActionModels.get(position).setActionStatu(2);

            }
        } else if (actionStatu == 1) {//正在播放
            DownLoadActionManager.getInstance(context).stopAction(false);
            mDynamicActionModels.get(position).setActionStatu(0);
        } else if (actionStatu == 2) {//正在下载

        }
    }

}

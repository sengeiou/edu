package com.ubt.alpha1e.userinfo.dynamicaction;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.base.RequstMode.DeleteActionRequest;
import com.ubt.alpha1e.base.RequstMode.GetMessageListRequest;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.GsonImpl;
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

public class DynamicActionPresenter extends BasePresenterImpl<DynamicActionContract.View> implements DynamicActionContract.Presenter {

    private static final String TAG = DynamicActionPresenter.class.getSimpleName();

    @Override
    public void getDynamicData(int type) {
        if (type == 0) {
            mView.setDynamicData(true, type, getData());
        } else {
            List<DynamicActionModel> list = new ArrayList<>();
            mView.setDynamicData(true, type, list);
        }
    }

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
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.ACTION_DYNAMIC_LIST, messageListRequest, 0).execute(new StringCallback() {
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
     * 根根据ID删除动作
     *
     * @param actionId
     */
    @Override
    public void deleteActionById(int actionId) {
        DeleteActionRequest deleteActionRequest = new DeleteActionRequest();
        deleteActionRequest.setActionId(actionId);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.ACTION_DYNAMIC_DELETE, deleteActionRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, " getNoticeData onError:" + e.getMessage());
                if (mView != null) {
                    mView.deleteActionResult(false);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getDynamicData==" + response);

                BaseResponseModel baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        mView.deleteActionResult(true);
                    }
                } else {
                    if (mView != null) {
                        mView.deleteActionResult(false);
                    }
                }
            }
        });
    }

    private List<DynamicActionModel> getData() {
        List<DynamicActionModel> list = new ArrayList<>();

        DynamicActionModel dynamicActionModel1 = new DynamicActionModel();
        dynamicActionModel1.setActionId(14456);
        dynamicActionModel1.setActionName("%蚂蚁与鸽子");
        dynamicActionModel1.setActionUrl("https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip");
        dynamicActionModel1.setActionTime(4120);
        dynamicActionModel1.setActionDate("2013-11-11 18:35:35");
        dynamicActionModel1.setDownload(false);
        list.add(dynamicActionModel1);

        DynamicActionModel dynamicActionModel11 = new DynamicActionModel();
        dynamicActionModel11.setActionId(14451);
        dynamicActionModel11.setActionName("%蚂蚁与鸽子1");
        dynamicActionModel11.setActionUrl("https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip");
        dynamicActionModel11.setActionTime(542000);
        dynamicActionModel11.setActionDate("2013-11-11 18:35:35");
        dynamicActionModel11.setDownload(false);
        list.add(dynamicActionModel11);

        DynamicActionModel dynamicActionModel12 = new DynamicActionModel();
        dynamicActionModel12.setActionId(14452);
        dynamicActionModel12.setActionName("%蚂蚁与鸽子2");
        dynamicActionModel12.setActionUrl("https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip");
        dynamicActionModel12.setActionTime(234510);
        dynamicActionModel12.setActionDate("2013-11-11 18:35:35");
        dynamicActionModel12.setDownload(false);
        list.add(dynamicActionModel12);

        DynamicActionModel dynamicActionModel13 = new DynamicActionModel();
        dynamicActionModel13.setActionId(14453);
        dynamicActionModel13.setActionName("%蚂蚁与鸽子3");
        dynamicActionModel13.setActionUrl("https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip");
        dynamicActionModel13.setActionTime(500000);
        dynamicActionModel13.setActionDate("2017-12-26 14:35:35");
        dynamicActionModel13.setDownload(false);
        list.add(dynamicActionModel13);

        DynamicActionModel dynamicActionModel2 = new DynamicActionModel();
        dynamicActionModel2.setActionId(14457);
        dynamicActionModel2.setActionName("音乐轴");
        dynamicActionModel2.setDownload(true);
        dynamicActionModel2.setActionDate("2017-12-25 18:35:35");
        dynamicActionModel2.setActionTime(12220);
        list.add(dynamicActionModel2);

        DynamicActionModel dynamicActionModel3 = new DynamicActionModel();
        dynamicActionModel3.setActionId(14458);
        dynamicActionModel3.setActionName("动作帧");
        dynamicActionModel3.setDownload(true);
        dynamicActionModel3.setActionDate("2017-12-26 17:15:35");
        dynamicActionModel3.setActionTime(3000);
        list.add(dynamicActionModel3);

        return list;
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
            UbtLog.d("praseDownloadData", "progress=====" + progress);
            mDynamicActionModels.get(position).setActionStatu(2);
            mDynamicActionModels.get(position).setDownloadProgress(Double.parseDouble(progress));
        } else if (downloadProgressInfo.status == 2) {//下载成功后立即播放
            DynamicActionModel dynamicActionModel = mDynamicActionModels.get(position);
            dynamicActionModel.setDownload(true);
            dynamicActionModel.setActionStatu(0);
            for (int i = 0; i < mDynamicActionModels.size(); i++) {
                if (mDynamicActionModels.get(i).getActionStatu() == 1) {
                    UbtLog.d("praseDownloadData", "actionName==" + mDynamicActionModels.get(i));
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
        UbtLog.d("", "actionName==" + dynamicActionModel.getActionName() + "  actionStatu===" + dynamicActionModel.getActionStatu());
        if (actionStatu == 0) {
            if (dynamicActionModel.isDownload()) {//已经下载
                for (int i = 0; i < mDynamicActionModels.size(); i++) {
                    if (mDynamicActionModels.get(i).getActionStatu() == 1) {
                        UbtLog.d("DynamicActionFragment", "actionName==" + mDynamicActionModels.get(i));
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

package com.ubt.alpha1e_edu.community;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.ubt.alpha1e_edu.base.RequstMode.AddActionDownloadRecordRequest;
import com.ubt.alpha1e_edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e_edu.login.HttpEntity;
import com.ubt.alpha1e_edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e_edu.userinfo.dynamicaction.DownLoadActionManager;
import com.ubt.alpha1e_edu.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CommunityPresenter extends BasePresenterImpl<CommunityContract.View> implements CommunityContract.Presenter{

    private static final String TAG = CommunityPresenter.class.getSimpleName();

    private static final int GET_QINIU_TOKEN = 1;
    private static final int ADD_DOWNLOAD_ACTION = 2;

    private String mQiniuTokenUrl = "https://test79.ubtrobot.com/community/app/sys/getQiniuToken";

    private String mQiNiuPublicUrl = "https://video.ubtrobot.com/";

    private String mQiniuToken = "";
    private String mLoadFilePath = "";

    @Override
    public void getQiniuTokenFromServer() {
        BaseRequest mBaseRequest = new BaseRequest();
        doRequestFromWeb(mQiniuTokenUrl, mBaseRequest, GET_QINIU_TOKEN);
    }

    @Override
    public void loadFileToQiNiu(String path) {
        mLoadFilePath = path;
        if(TextUtils.isEmpty(mQiniuToken)){
            getQiniuTokenFromServer();
        }else {
            uploadVideoToQiNiuServer();
        }
    }

    /**
     * 请求网络操作
     */
    private void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {
        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage() + "  mView = " + mView + "  id = " + id);
                if(mView == null){
                    return;
                }
                switch (id) {
                    case GET_QINIU_TOKEN:
                        // mView.showBehaviourList(false,null,"network error");
                        mView.onQiniuTokenFromServer(false,"");
                        break;
                    case ADD_DOWNLOAD_ACTION:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "response = " + response + "  id = " + id);
                if(mView == null){
                    return;
                }

                switch (id) {
                    case GET_QINIU_TOKEN:

                        UbtLog.d(TAG, "mbaseResponseModel = " + response);
                        if(!TextUtils.isEmpty(response)){
                            mQiniuToken = response;
                            uploadVideoToQiNiuServer();
                            mView.onQiniuTokenFromServer(true, response);
                        }else {
                            mView.onQiniuTokenFromServer(false, "");
                        }
                        break;
                    case ADD_DOWNLOAD_ACTION:
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /***
     * 上传视频到七牛服务器
     */
    private void uploadVideoToQiNiuServer()
    {

        UploadManager uploadManager = new UploadManager();
        String key = System.currentTimeMillis()+".mp4";

        uploadManager.put(mLoadFilePath, key, mQiniuToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        UbtLog.d(TAG,"onResponse:" + key + "," + info.toString());
                        if(mView != null){
                            UbtLog.d(TAG,"onResponse:" + key + "," + info.isOK());
                            if(info != null && info.isOK()){
                                mView.onLoadFileToQiNiu(true, mQiNiuPublicUrl + key);
                            }else {
                                mView.onLoadFileToQiNiu(false,"");
                            }
                        }
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String s, double v) {
                        UbtLog.d(TAG,"onResponse:"+ s+"--progress:"+v);
                        int progress = (int)(v*100);

                    }
                },null));
    }

    /**
     * 播放动作文件
     *
     * @param context
     * @param dynamicActionModel
     */
    @Override
    public void playAction(Context context, DynamicActionModel dynamicActionModel) {
        int actionStatus = dynamicActionModel.getActionStatu();
        UbtLog.d(TAG, "actionName==" + dynamicActionModel.getActionName() + "  actionStatus = " + dynamicActionModel.getActionStatu());
        if (actionStatus == 0) {
            if (dynamicActionModel.isDownload()) {//已经下载

                DownLoadActionManager.getInstance(context).playAction(false, dynamicActionModel);
                mView.onActionStatus(dynamicActionModel.getActionId(), 1, 1, "0");
            } else {//没有下载，需要下载
                DownLoadActionManager.getInstance(context).readNetworkStatus();
                DownLoadActionManager.getInstance(context).downRobotAction(dynamicActionModel);
                mView.onActionStatus(dynamicActionModel.getActionId(), 2, 0, "0");

                addDownloadActionRecord(dynamicActionModel.getActionId(), dynamicActionModel.getPostId());
            }
        } else if (actionStatus == 1) {//正在播放
            DownLoadActionManager.getInstance(context).stopAction(false);
            mView.onActionStatus(dynamicActionModel.getActionId(), 1, 0, "0");
        } else if (actionStatus == 2) {//正在下载

        }
    }

    /**
     * 获取动作状态
     * @param context
     * @param dynamicActionModel
     */
    @Override
    public void getActionStatus(Context context, DynamicActionModel dynamicActionModel, List<String> mRobotDownActionList) {
        boolean hasDown = false;
        for(String actionName : mRobotDownActionList){
            if(actionName.equals(dynamicActionModel.getActionOriginalId())){
                hasDown = true;
                break;
            }
        }

        boolean isPlaying = false;
        if(hasDown){
            //获取正在播放的PlayingInfo，下次进来的时候可以直接显示播放状态
            DynamicActionModel actionInfo = DownLoadActionManager.getInstance(context).getPlayingInfo();
            if (actionInfo != null) {
                UbtLog.d(TAG,"actionInfo = " + actionInfo.getActionId() + "/" + actionInfo.getActionOriginalId() + "/" +  actionInfo.getActionName() );
                if(actionInfo.getActionId() == dynamicActionModel.getActionId()){
                    isPlaying = true;
                }
            }

            if(isPlaying){
                mView.onActionStatus(dynamicActionModel.getActionId(), 1, 1,"0");
            }else {
                mView.onActionStatus(dynamicActionModel.getActionId(), 1, 0,"0");
            }
        }else {
            boolean hasDowning = false;
            //获取下载类中正在下载的列表数据
            List<DynamicActionModel> downIngList = DownLoadActionManager.getInstance(context).getRobotDownList();
            if (downIngList != null && downIngList.size() > 0) {
                for(DynamicActionModel downingModel : downIngList){
                    if(dynamicActionModel.getActionId() == downingModel.getActionId()){
                        hasDowning = true;
                        break;
                    }
                }
            }

            if(hasDowning){
                mView.onActionStatus(dynamicActionModel.getActionId(), 2, 0,"0");
            }else {
                mView.onActionStatus(dynamicActionModel.getActionId(), 0, 0,"0");
            }
        }
    }

    /**
     * 添加下载记录
     * @param actionId 动作id
     * @param actionId 帖子id
     */
    private void addDownloadActionRecord(int actionId,int postId){
        AddActionDownloadRecordRequest addRecordRequest = new AddActionDownloadRecordRequest();
        addRecordRequest.setActionId(actionId);
        addRecordRequest.setPostId(postId);

        doRequestFromWeb(HttpEntity.ACTION_DOWNLOAD_ADD, addRecordRequest, ADD_DOWNLOAD_ACTION);
    }
}

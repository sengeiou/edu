package com.ubt.alpha1e.community;

import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CommunityPresenter extends BasePresenterImpl<CommunityContract.View> implements CommunityContract.Presenter{

    private static final String TAG = CommunityPresenter.class.getSimpleName();

    public static final int GET_QINIU_TOKEN = 1;

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

        getQiniuTokenFromServer();
    }

    /**
     * 请求网络操作
     */
    private void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {
        synchronized (this) {
            OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage() + "  mView = " + mView);
                    if(mView == null){
                        return;
                    }
                    switch (id) {
                        case GET_QINIU_TOKEN:
                            // mView.showBehaviourList(false,null,"network error");
                            mView.onQiniuTokenFromServer(false,"");
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    UbtLog.d(TAG, "response = " + response);
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

                        default:
                            break;
                    }
                }
            });
        }
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
                                mView.onloadFileToQiNiu(true, mQiNiuPublicUrl + key);
                            }else {
                                mView.onloadFileToQiNiu(false,"");
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
}

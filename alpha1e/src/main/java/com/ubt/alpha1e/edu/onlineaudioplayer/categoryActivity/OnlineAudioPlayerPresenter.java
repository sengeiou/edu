package com.ubt.alpha1e.edu.onlineaudioplayer.categoryActivity;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.OnlineAudioPlayerAlbumRequest;
import com.ubt.alpha1e.edu.base.RequstMode.OnlineAudioPlayerAudiosRequest;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.CategoryContentInfo;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerPresenter extends BasePresenterImpl<OnlineAudioPlayerContract.View> implements OnlineAudioPlayerContract.Presenter{
    private String TAG="OnlineAudioPlayerPresenter";
    private String[] courseList={"语文课堂","趣味课堂","英语名著","语文课堂"};
    private List<String> albumList=new ArrayList<>();

    private String getCourseContents="content/getCategory";
    private String getAlbumContents="content/getAlbum";
    private String getAudioContents="content/getContentDetail";
    private static final int GETCOURSElISTREQUIRE=1;
    private static final int GETALBUMREQUEST=2;
    private static final int GETAUDIOREQUEST=3;
    public static final int NETWORK_ERROR=1000;
    public static final int NETWORK_SUCCESS=2000;


    @Override
    public void getAlbumList(final String courseName) {
        OnlineAudioPlayerAlbumRequest mAlbumRequest=new OnlineAudioPlayerAlbumRequest();
        mAlbumRequest.setCategoryId(courseName);
        doRequestFromWeb(HttpEntity.BASIC_UBX_SYS+getAlbumContents,mAlbumRequest,GETALBUMREQUEST);

    }

    @Override
    public void getAudioList(String albumId) {
        OnlineAudioPlayerAudiosRequest mAudioRequest=new OnlineAudioPlayerAudiosRequest();
        mAudioRequest.setAlbumId(albumId);
        doRequestFromWeb(HttpEntity.BASIC_UBX_SYS+getAudioContents,mAudioRequest,GETAUDIOREQUEST);
    }
    
    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {
        synchronized (this) {
            OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage() + "  mView = " + mView);
                    if(mView == null){
                        return;
                    }
                    mView.onRequestStatus(id, NETWORK_ERROR);
                }
                @Override
                public void onResponse(String response, int id) {
                    UbtLog.d(TAG, "response = " + response);
                    if(mView == null){
                        return;
                    }
                    switch (id) {
                        case GETCOURSElISTREQUIRE:
                            BaseResponseModel<ArrayList<CategoryContentInfo>> mbaseResponseModel = GsonImpl.get().toObject(response,
                                    new TypeToken<BaseResponseModel<ArrayList<CategoryContentInfo>>>() {
                                    }.getType());//加上type转换，避免泛型擦除
                            UbtLog.d(TAG,"GET COURSE REQUIRE"+mbaseResponseModel.models.get(0));
                            mView.showCourseList(mbaseResponseModel.models);
                        case GETALBUMREQUEST:
                            BaseResponseModel<ArrayList<AlbumContentInfo>> mbaseResponseModel0 = GsonImpl.get().toObject(response,
                                    new TypeToken<BaseResponseModel<ArrayList<AlbumContentInfo>>>() {
                                    }.getType());//加上type转换，避免泛型擦除
                            if(mbaseResponseModel0.models!=null) {
                                mView.showAlbumList(true, mbaseResponseModel0.models, "success");
                            }else {
                                mView.showAlbumList(false, mbaseResponseModel0.models, "success");
                            }
                            break;
                        case GETAUDIOREQUEST:
                            BaseResponseModel<ArrayList<AudioContentInfo>> mbaseResponseModel1 = GsonImpl.get().toObject(response,
                                    new TypeToken<BaseResponseModel<ArrayList<AudioContentInfo>>>() {
                                    }.getType());//加上type转换，避免泛型擦除
                            mView.showAudioList(true,mbaseResponseModel1.models,"success");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

}

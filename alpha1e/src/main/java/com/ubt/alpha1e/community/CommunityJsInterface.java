package com.ubt.alpha1e.community;

import android.webkit.JavascriptInterface;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @className CommunityJsInterface
 *
 * @author wmma
 * @description android端和js交互接口
 * @date  2017/03/06
 * @update
 */


public class CommunityJsInterface {

    private static final String TAG = CommunityJsInterface.class.getSimpleName();

    private MVPBaseActivity mMVPBaseActivity;

    public CommunityJsInterface(MVPBaseActivity baseActivity) {
        mMVPBaseActivity = baseActivity;
    }

    /**
     * 从社区返回nativeApp
     */
    @JavascriptInterface
    public void goBackToNativeApp() {
        UbtLog.d(TAG,"goBackToNativeApp");
        mMVPBaseActivity.finish();
        mMVPBaseActivity.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    /**
     * 上传视频并把地址传给前端
     */
    @JavascriptInterface
    public void getVideoUrl() {
        UbtLog.d(TAG,"getVideoUrl");
        if(mMVPBaseActivity instanceof CommunityActivity){
            ((CommunityActivity)mMVPBaseActivity).loadVideoFileToQiNiu();
        }
    }

    /**
     * 从社区进入动作选择界面
     */
    @JavascriptInterface
    public void goActionSelect() {
        UbtLog.d(TAG,"-goActionSelect-" );

        if(mMVPBaseActivity instanceof CommunityActivity){
            ((CommunityActivity)mMVPBaseActivity).showActionSelect();
        }
    }

    /**
     * 发贴，主动获取发布的动作信息
     */
    @JavascriptInterface
    public void getReplyAction() {
        UbtLog.d(TAG,"-getReplyAction-" );

        if(mMVPBaseActivity instanceof CommunityActivity){
            ((CommunityActivity)mMVPBaseActivity).getReplyAction();
        }
    }

    /**
     * 从社区进入nativeApp的照片选择界面
     */
    @JavascriptInterface
    public void goImagePickVc(String params) {
        UbtLog.d(TAG,"goImagePickVc params = " + params);

        try {
            JSONObject jsonObject = new JSONObject(params);
            int type = jsonObject.getInt("type");
            int num = jsonObject.getInt("num");

            if(mMVPBaseActivity instanceof CommunityActivity){
                ((CommunityActivity)mMVPBaseActivity).showImagePicker(type, num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从社区进入nativeApp的照片选择界面
     */
    @JavascriptInterface
    public void shareToThirdApp(String params) {
        UbtLog.d(TAG,"shareToThirdApp params = " + params);

        try {
            JSONObject jsonObject = new JSONObject(params);
            String type = jsonObject.getString("type");
            String url = jsonObject.getString("url");
            String title = jsonObject.getString("title");
            String description = jsonObject.getString("description");

            if(mMVPBaseActivity instanceof CommunityActivity){
                ((CommunityActivity)mMVPBaseActivity).shareToThirdApp(type,url,title,description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 详情点击下载播放动作按钮
     * params: {"isDownload":0,"actionStatus":0,"actionId":0,"actionName":"","actionUrl":"","actionOriginalId":""}
     * isDownload : 0 未下载， 1 已下载，2 下载中
     * actionStatus : 0 未播放，1 播放中，2 暂停
     * actionId 动作ID
     * actionName 动作显示名称
     * actionUrl 动作下载url
     * actionOriginalId 动作文件名称
     */
    @JavascriptInterface
    public void playAction(String params) {
        UbtLog.d(TAG,"playAction params = " + params);

        try {
            JSONObject jsonObject = new JSONObject(params);
            String isDownload = jsonObject.getString("isDownload");
            int actionStatus = jsonObject.getInt("actionStatus");
            int actionId = jsonObject.getInt("actionId");
            String actionName = jsonObject.getString("actionName");
            String actionUrl = jsonObject.getString("actionUrl");
            String actionOriginalId = jsonObject.getString("actionOriginalId");
            int postId = jsonObject.getInt("postId");

            DynamicActionModel actionModel = new DynamicActionModel();
            actionModel.setActionId(actionId);
            actionModel.setActionName(actionName);
            actionModel.setActionUrl(actionUrl);
            actionModel.setDownload("1".equals(isDownload) ? true : false);
            actionModel.setActionStatu(actionStatus);
            actionModel.setActionOriginalId(actionOriginalId);
            actionModel.setPostId(postId);

            if(mMVPBaseActivity instanceof CommunityActivity){
                ((CommunityActivity)mMVPBaseActivity).playAction(actionModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取动作状态
     * params: {"actionId":0,"actionName":"" ,"actionOriginalId":""}
     * actionId 动作ID
     * actionName 动作名称
     */
    @JavascriptInterface
    public void getActionStatus(String params) {
        UbtLog.d(TAG,"getActionStatus params = " + params);

        try {
            JSONObject jsonObject = new JSONObject(params);

            int actionId = jsonObject.getInt("actionId");
            String actionName = jsonObject.getString("actionName");
            String actionOriginalId = jsonObject.getString("actionOriginalId");

            DynamicActionModel actionModel = new DynamicActionModel();
            actionModel.setActionId(actionId);
            actionModel.setActionName(actionName);
            actionModel.setActionOriginalId(actionOriginalId);

            if(mMVPBaseActivity instanceof CommunityActivity){
                ((CommunityActivity)mMVPBaseActivity).getActionStatus(actionModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

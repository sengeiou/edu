package com.ubt.alpha1e.community;

import android.webkit.JavascriptInterface;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
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
     * 从社区进入nativeApp的照片选择界面
     */
    @JavascriptInterface
    public void goImagePickVc(int num) {
        UbtLog.d(TAG,"goImagePickVc num = " + num);

        if(mMVPBaseActivity instanceof CommunityActivity){
            ((CommunityActivity)mMVPBaseActivity).showImagePicker(num);
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

}

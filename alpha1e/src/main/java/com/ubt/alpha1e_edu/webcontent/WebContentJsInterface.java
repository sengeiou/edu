package com.ubt.alpha1e_edu.webcontent;

import android.webkit.JavascriptInterface;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * @className CommunityJsInterface
 *
 * @author wmma
 * @description android端和js交互接口
 * @date  2017/03/06
 * @update
 */


public class WebContentJsInterface {

    private static final String TAG = WebContentJsInterface.class.getSimpleName();

    private MVPBaseActivity mMVPBaseActivity;

    public WebContentJsInterface(MVPBaseActivity baseActivity) {
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

}

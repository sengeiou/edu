package com.ubt.alpha1e.business.thrid_party;


import android.app.Activity;
import android.content.Intent;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.ubt.alpha1e.utils.log.UbtLog;

public class MyWeiBoNew {

    private static final String TAG = MyWeiBoNew.class.getSimpleName();

    public final static String APP_KEY = "122287094";
    public final static String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //private final static String SCOPE = "all";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    private static WbShareHandler shareHandler;

    public static void initMyWeiBoShare(Activity act) {

        /*WbShareHandler shareHandler = new WbShareHandler(act);
        shareHandler.registerApp();*/
        UbtLog.d(TAG,"initMyWeiBoShare");
        shareHandler = new WbShareHandler(act);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);

    }

    public static void doResultIntent(Intent intent, WbShareCallback callback){
        if(shareHandler != null){
            shareHandler.doResultIntent(intent, callback);
        }
    }

    public static void doShareWeiBo(Activity act,String msg, String url) {

        UbtLog.d(TAG,"doShareWeiBo" + shareHandler);
        if(shareHandler == null){
            initMyWeiBoShare(act);
        }

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(msg,url);
        shareHandler.shareMessage(weiboMessage, false);
    }

    /**
     * 创建文本消息对象。
     * @return 文本消息对象。
     */
    private static TextObject getTextObj(String msg, String url) {
        TextObject textObject = new TextObject();
        textObject.text = msg + url;
        textObject.title = "Alpha Ebot";
        textObject.actionUrl = url;
        return textObject;
    }




    /*public static void doShareWeiBo(Activity act, ActionInfo info, String url) {
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            ((BaseActivity) act).showToast("ui_remote_synchoronize_unknown_error");
            return;
        }
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = new WebpageObject();
        weiboMessage.mediaObject.identify = Utility.generateGUID();
        weiboMessage.mediaObject.title = info.actionName;
        weiboMessage.mediaObject.description = info.actionName;
        Bitmap bitmap = BitmapFactory.decodeResource(act.getResources(), R.drawable.ic_launcher);
        weiboMessage.mediaObject.setThumbImage(bitmap);
        weiboMessage.mediaObject.actionUrl = url + info.actionId;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(act, request);
        MyLog.writeLog("第三方分享", "mWeiboShareAPI.sendRequest");
    }*/


}

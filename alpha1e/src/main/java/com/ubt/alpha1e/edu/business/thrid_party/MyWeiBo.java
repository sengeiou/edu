package com.ubt.alpha1e.edu.business.thrid_party;

//import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
//import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
//import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
//import com.sina.weibo.sdk.api.share.WeiboShareSDK;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
//import com.sina.weibo.sdk.openapi.UsersAPI;


public class MyWeiBo {
//    private final static String APP_KEY = "4096435303";
//    private final static String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
//    private final static String SCOPE = "all";
//    private static SsoHandler mSsoHandler = null;
//    private static IWeiboShareAPI mWeiboShareAPI;
//
//    private static void initMyWeiBoLogin(Activity act) {
//        if (mSsoHandler == null) {
//            AuthInfo mAuthInfo = new AuthInfo(act, APP_KEY, REDIRECT_URL, SCOPE);
//            mSsoHandler = new SsoHandler(act, mAuthInfo);
//        }
//    }
//
//    public static void initMyWeiBoShare(Activity act) {
//
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(act, APP_KEY);
//        mWeiboShareAPI.registerApp();
//
//    }
//
//    public static void doLogin(Activity act, WeiboAuthListener listener) {
//        if (mSsoHandler == null)
//            initMyWeiBoLogin(act);
//        mSsoHandler.authorize(listener);
//    }
//
//    public static void doAuthorizeCallBack(int requestCode, int resultCode,
//                                           Intent data) {
//
//        MyLog.writeLog("微博登录", "MyWeiBo.doAuthorizeCallBack-->微博登录收到");
//
//        if (mSsoHandler == null)
//            return;
//        else {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }
//
//    public static void doGetUserInfo(Activity act, Oauth2AccessToken token,
//                                     RequestListener listener) {
//        if (mSsoHandler == null)
//            initMyWeiBoLogin(act);
//        UsersAPI mUsersAPI = new UsersAPI(act, APP_KEY, token);
//        long uid = Long.parseLong(token.getUid());
//        mUsersAPI.show(uid, listener);
//    }
//
//    public static void doShareWeiBo(Activity act, ActionInfo info, String url) {
//        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
//            ((BaseActivity) act).showToast("ui_remote_synchoronize_unknown_error");
//            return;
//        }
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        weiboMessage.mediaObject = new WebpageObject();
//        weiboMessage.mediaObject.identify = Utility.generateGUID();
//        weiboMessage.mediaObject.title = info.actionName;
//        weiboMessage.mediaObject.description = info.actionName;
//        Bitmap bitmap = BitmapFactory.decodeResource(act.getResources(), R.drawable.ic_launcher);
//        weiboMessage.mediaObject.setThumbImage(bitmap);
//        weiboMessage.mediaObject.actionUrl = url + info.actionId;
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        mWeiboShareAPI.sendRequest(act, request);
//        MyLog.writeLog("第三方分享", "mWeiboShareAPI.sendRequest");
//    }
//
//    public static void doHandleWeiboResponse(Intent intent, Response listener) {
//        mWeiboShareAPI.handleWeiboResponse(intent, listener);
//    }
}

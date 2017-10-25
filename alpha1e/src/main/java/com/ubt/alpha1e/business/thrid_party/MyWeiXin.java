package com.ubt.alpha1e.business.thrid_party;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.Date;

public class MyWeiXin {

	public final static String WEIXIN_APP_ID = "wxaccd90fe93e07aa6";
	public final static String WEIXIN_APP_SECRET = "7e8c318a8412261601056c30bf25f74b";
	private final static String SCOPE = "snsapi_userinfo";
	public final static String GRANTTYPE = "authorization_code";
	public static final String ACTION_WEIXIN_API_CALLBACK = "ACTION_WEIXIN_API_CALLBACK";
	// ---------------------------------------------------------
	private static long DO_LOGIN_REQUEST = -1;
	private static IWXAPI mIWXAPI = null;

	private static void initMyWeiXin(Context mContext) {
		if (mIWXAPI == null) {
			mIWXAPI = WXAPIFactory.createWXAPI(mContext, WEIXIN_APP_ID, false);
			mIWXAPI.registerApp(WEIXIN_APP_ID);
		}
	}

	public static void doLogin(Context mContext, IWeiXinListener mListener) {

		initMyWeiXin(mContext);
		if (!mIWXAPI.isWXAppInstalled()) {
			mListener.noteWeixinNotInstalled();
			return;
		}
		DO_LOGIN_REQUEST = new Date().getTime();
		SendAuth.Req req = new SendAuth.Req();
		req.scope = SCOPE;
		req.state = DO_LOGIN_REQUEST + "";
		MyLog.writeLog("微信登录",
				"com.ubtechic.alpha1blooth.thrid_party.MyWeiXin.doLogin："
						+ mIWXAPI.toString());
		mIWXAPI.sendReq(req);
	}

	public static SendAuth.Resp handleIntent(Intent inte, Context mContext) {
		initMyWeiXin(mContext);
		SendAuth.Resp resp = new SendAuth.Resp(inte.getExtras());
		MyLog.writeLog("微信登录", "收到回调MyWeiXin.handleIntent：" + resp.errCode);
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
			MyLog.writeLog("微信登录", "收到回调，用户同意登录");
			return resp;
		}
		return null;
	}

	// flag 0是朋友圈，1是好友
	public static void doShareToWeiXin(String url, ActionInfo info,
			Activity act, IWeiXinListener mListener, int flag) {
		UbtLog.d("Myweixin", "info=" + info);
		initMyWeiXin(act);
		if (!mIWXAPI.isWXAppInstalled()) {
			mListener.noteWeixinNotInstalled();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if(!TextUtils.isEmpty(info.actionName) && !info.actionName.equals("")){
			msg.title = info.actionName;
		}else{
			msg.title = "Alpha";
		}
		if(!TextUtils.isEmpty(info.actionDesciber) && !info.actionDesciber.equals("")){
			msg.description = info.actionDesciber;
		}

		Bitmap thumb = BitmapFactory.decodeResource(act.getResources(),
				R.drawable.ic_launcher);
		msg.setThumbImage(thumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag;
		mIWXAPI.sendReq(req);

	}

	public static void doShareToWeiXin(String url,String title, Activity act,
			IWeiXinListener mListener, int flag) {
		initMyWeiXin(act);
		if (!mIWXAPI.isWXAppInstalled()) {
			mListener.noteWeixinNotInstalled();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if(!TextUtils.isEmpty(title) || !title .equals("")){
			msg.title = title;
		}else{
			msg.title = "Alpha";
		}


		Bitmap thumb = BitmapFactory.decodeResource(act.getResources(),
				R.drawable.ic_launcher);
		msg.setThumbImage(thumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag;
		mIWXAPI.sendReq(req);

	}
}

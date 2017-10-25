package com.ubt.alpha1e.business.thrid_party;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.QQLoginInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;

public class MyTencent {

	private final static String APP_ID = "1104799391";
	private final static String RIGHT = "all";
	public static boolean isNeedOnResualt = false;

	private static Tencent mTencent = null;

	private static void initTencent(Context mContext) {
		if (mTencent == null)
			mTencent = Tencent.createInstance(APP_ID, mContext);
	}

	public static void doLogin(Activity act, IUiListener listener) {
		MyLog.writeLog("第三方登录",
				"MyTencent.doLogin-->com.tencent.tauth.Tencent.login");
		initTencent(act);
		mTencent.login(act, RIGHT, listener);
	}

	public static void getUserInfo(Activity act, IUiListener listener) {
		initTencent(act);
		QQLoginInfo info = (QQLoginInfo) ((AlphaApplication) act
				.getApplicationContext()).getCurrentThridLoginInfo();
		mTencent.setOpenId(info.openid);
		mTencent.setAccessToken(info.access_token, info.expires_in + "");
		UserInfo userInfo = new UserInfo(act, mTencent.getQQToken());
		MyLog.writeLog("第三方登录",
				"MyTencent.getUserInfo--> com.tencent.connect.UserInfo.getUserInfo");
		userInfo.getUserInfo(listener);
	}

	public static void doShareQQ(Activity act, ActionInfo info, String url,
			IUiListener listener) {
		if (mTencent == null) {
			initTencent(act);
		}
		UbtLog.d("MyTencent","url = " + url);
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		if(!TextUtils.isEmpty(info.actionName) && !info.actionName.equals("")){
			params.putString(QQShare.SHARE_TO_QQ_TITLE, info.actionName);
		}else{
			params.putString(QQShare.SHARE_TO_QQ_TITLE, "Alpha");
		}

		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
		if(!TextUtils.isEmpty(info.actionDesciber) && !info.actionDesciber.equals("")){
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, info.actionDesciber);
		}
		if(!TextUtils.isEmpty(info.actionImagePath) && !info.actionImagePath.equals("")){
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, info.actionImagePath);
		}

		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Alpha");
		mTencent.shareToQQ(act, params, listener);

	}

	public static void doShareQQ(Activity act, String url, IUiListener listener) {
		if (mTencent == null) {
			initTencent(act);
		}
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, "Alpha");
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "");
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "");
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Alpha");
		mTencent.shareToQQ(act, params, listener);

	}

	public static void doShareQQKongjian(Activity act, ActionInfo info,
			String url, IUiListener listener) {
		MyLog.writeLog("第三方分享", "doShareQQKongjian");
		if (mTencent == null) {
			initTencent(act);
		}
		final Bundle params = new Bundle();

		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		if(!TextUtils.isEmpty(info.actionName) && !info.actionName.equals("")){
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, info.actionName);
		}else{
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "Alpha");
		}
		if(!TextUtils.isEmpty(info.actionDesciber) && !info.actionDesciber.equals("")){
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, info.actionDesciber);
		}

		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
		ArrayList<String> imageUrls = new ArrayList<String>();
		if(!TextUtils.isEmpty(info.actionImagePath) && !info.actionImagePath.equals("")){
			imageUrls.add(info.actionImagePath);

		}else{
			if(!TextUtils.isEmpty(info.actionHeadUrl) && !info.actionHeadUrl.equals("")){
				imageUrls.add(info.actionHeadUrl);
			}else{
				imageUrls.add(HttpAddress.WebDefaultAppLauncherAddress);
			}

		}
		/*else{
			imageUrls.add("http://services.ubtrobot.com/userImage/ic_launcher.png");
		}*/
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

		// params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Alpha");

		mTencent.shareToQzone(act, params, listener);
		MyLog.writeLog("第三方分享", "shareToQzone被调用");
	}

	public static void doShareQQKongjian(Activity act,String url,String title, IUiListener listener) {
		MyLog.writeLog("第三方分享", "doShareQQKongjian");
		if (mTencent == null) {
			initTencent(act);
		}
		final Bundle params = new Bundle();

		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, TextUtils.isEmpty(title) ? "Alpha" : title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "");
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(HttpAddress.WebDefaultAppLauncherAddress);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

		//params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Alpha");

		mTencent.shareToQzone(act, params, listener);
		MyLog.writeLog("第三方分享", "shareToQzone被调用");
	}
}

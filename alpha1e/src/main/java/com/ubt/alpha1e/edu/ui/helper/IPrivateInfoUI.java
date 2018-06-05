package com.ubt.alpha1e.edu.ui.helper;

import org.json.JSONObject;

import android.graphics.Bitmap;

import com.facebook.Profile;
//import com.sina.weibo.sdk.openapi.models.User;
import com.ubt.alpha1e.edu.data.model.QQUserInfo;
import com.ubt.alpha1e.edu.data.model.WeiXinUserInfo;

public interface IPrivateInfoUI {

	public Bitmap onGetHead(Bitmap img);

	public void onNodeNickNameEmpty();

	public void onNodeEmialEmpty();

	public void onNodeHeadEmpty();

	public void onEditFinish(boolean is_success, String error_info, JSONObject info);

	public void onQQPrivateInfo(QQUserInfo info);

	public void onWeiXinPrivateInfo(WeiXinUserInfo info);

	//public void onWeiBoPrivateInfo(User info);
	
	public void onFaceBookProfileInfo(Profile profile,String url);

	public void onTwitterProfileInfo(twitter4j.User user);
	
	public void onPreEditFinish(boolean b, Object object);

}

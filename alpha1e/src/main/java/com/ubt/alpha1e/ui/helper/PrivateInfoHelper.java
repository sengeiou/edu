package com.ubt.alpha1e.ui.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.business.thrid_party.MyWeiBo;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.IPrivateFileListener;
import com.ubt.alpha1e.data.ISharedPreferensListenet;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.QQUserInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.data.model.WeiXinLoginInfo;
import com.ubt.alpha1e.data.model.WeiXinUserInfo;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONObject;

public class PrivateInfoHelper extends BaseHelper implements
		IPrivateFileListener, IJsonListener, ISharedPreferensListenet,
		IUiListener, IImageListener, RequestListener {

	private static final String TAG = "PrivateInfoHelper";

	public static final String Edit_type = "Edit_type";

	public static enum EditType {
		local_register_type, thired_register_type, complete_info_type
	};

	public static enum Gender {
		M, F
	}

	public static final int GetUserHeadRequestCodeByShoot = 1001;
	public static final int GetUserHeadRequestCodeByFile = 1002;
	// -----------------------------------------
	private static final int MSG_DO_GET_IMG = 1001;
	private static final int MSG_DO_EDIT_PRIVATE_INFO = 1002;
	private static final int MSG_DO_PRE_EDIT_PRIVATE_INFO = 1003;
	private static final int MSG_DO_READ_THRID_USER_INFO = 1004;
	private static final int MSG_DO_GET_IMG_FIRST = 1005;
	public static final String HeadImgFileName = "HeadImgFileName.ubt";
	// -----------------------------------------
	private long do_edit_private_info_request = 11001;
	private long do_edit_pre_private_info_request = 11002;
	private long do_read_user_head_img = 11003;
	private long do_get_thrid_user_info_request = 11004;
	// -----------------------------------------
	private IPrivateInfoUI mUI;
	private Bitmap mCurrentHead;

	public void doRecordUser(JSONObject model_obj) {

		if (model_obj == null) {
			PrivateInfoHelper.this.mUI.onEditFinish(false, mBaseActivity
					.getResources().getString(R.string.ui_common_operate_fail),
					null);

		} else {

			String model_str = model_obj.toString();
			UserInfo info = new UserInfo().getThiz(model_str);
			UbtLog.d("PrivateInfoHelper", "currentUser=" + info);

			// 在内存中记录用户状态
			((AlphaApplication) mBaseActivity.getApplicationContext())
					.setCurrentUserInfo(info);
			// 在外存中记录用户状态
			String info_str = UserInfo.getModelStr(info);
			UbtLog.d("PrivateInfoHelper", "currentUser=info_str=" + info_str);
			BasicSharedPreferencesOperator.getInstance(mBaseActivity,
					DataType.USER_USE_RECORD).doWrite(
					BasicSharedPreferencesOperator.LOGIN_USER_INFO, info_str,
					PrivateInfoHelper.this, -1);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (MSG_DO_EDIT_PRIVATE_INFO == msg.what) {

				if (msg.obj == null) {
					PrivateInfoHelper.this.mUI.onEditFinish(
							false,
							mBaseActivity.getResources().getString(
									R.string.ui_common_network_request_failed), null);
					return;
				}
				String json = (String) msg.obj;

				if (!JsonTools.getJsonStatus(json)) {
					UbtLog.d("PrivateInfoHelper", "currentUser=!JsonTools.getJsonStatus(json)");
					if (JsonTools.getJsonInfo(json) == 9999) {
						PrivateInfoHelper.this.mUI.onEditFinish(
								false,
								mBaseActivity.getResources().getString(
										R.string.ui_login_prompt_info_wrong_format), null);
					}

					else if (JsonTools.getJsonInfo(json) == 1008) {
						PrivateInfoHelper.this.mUI.onEditFinish(
								false,
								mBaseActivity.getResources().getString(
										R.string.ui_register_prompt_phone_email_exist), null);
					}

					else {

						PrivateInfoHelper.this.mUI.onEditFinish(
								false,
								mBaseActivity.getResources().getString(
										R.string.ui_common_operate_fail), null);
					}
					return;
				}

				PrivateInfoHelper.this.mUI.onEditFinish(true, "",
						JsonTools.getJsonModel(json));

			}

			else if (msg.what == MSG_DO_PRE_EDIT_PRIVATE_INFO) {
				if (msg.obj == null) {
					PrivateInfoHelper.this.mUI.onPreEditFinish(false, null);
					return;
				}
				String json = (String) msg.obj;
				if (!JsonTools.getJsonStatus(json)) {
					PrivateInfoHelper.this.mUI.onPreEditFinish(false, null);
					return;
				}
				PrivateInfoHelper.this.mUI.onPreEditFinish(true,
						JsonTools.getJsonModel(json));
				doRecordUser(JsonTools.getJsonModel(json));

			}

			else if (msg.what == MSG_DO_GET_IMG) {

				PrivateInfoHelper.this.opreaterUserHead((Bitmap) msg.obj);

			} else if (msg.what == MSG_DO_GET_IMG_FIRST) {
				mUI.onGetHead((Bitmap) msg.obj);
			} else if (msg.what == MSG_DO_READ_THRID_USER_INFO) {
				String json = (String) msg.obj;
				MyLog.writeLog("微信登录", "收到微信用户个人信息-->" + json);
				WeiXinUserInfo info = new WeiXinUserInfo().getThiz(json);
				mUI.onWeiXinPrivateInfo(info);
			}
		}
	};

	public PrivateInfoHelper(IPrivateInfoUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
	}


	public void setmCurrentHead(Bitmap bitmap)
	{
		if(mCurrentHead != null){
			mCurrentHead.recycle();
		}
		mCurrentHead = bitmap;
	}
	public void doEditPrivateInfo(UserInfo mCurrentInfo) {
		UbtLog.d(TAG,"用户编辑测试 : " + UserInfo.getModelStr(mCurrentInfo));
		JSONObject jobj = new JSONObject();
		try {
			//jobj.put("userImage",mCurrentInfo.userImage);
			//UbtLog.d(TAG,"mCurrentHead == " + mCurrentInfo.userImage);
			UbtLog.d(TAG,"mCurrentHead = " + mCurrentHead);
			if (mCurrentHead != null) {
				jobj.put("userImage", ImageTools.getImgStr(mCurrentHead));
			}
			jobj.put("token", mCurrentInfo.token);
			jobj.put("userId", mCurrentInfo.userId);
			jobj.put("userName", mCurrentInfo.userName);
			jobj.put("userEmail", mCurrentInfo.userEmail);
			jobj.put("userPhone", mCurrentInfo.userPhone);
			jobj.put("userGender", mCurrentInfo.userGender);
			jobj.put("countryCode", mCurrentInfo.countryCode);
		} catch (Exception e) {
			mUI.onEditFinish(
					false,
					mBaseActivity.getResources().getString(
							R.string.ui_common_operate_fail), null);
			MyLog.writeLog("用户编辑测试", "第三方登录模型更新失败：" + e.getMessage());
		}
		GetDataFromWeb.getJsonByPost(do_edit_private_info_request,
				HttpAddress.getRequestUrl(Request_type.edit_private_info),
				HttpAddress.getParamsForPost(jobj.toString(), mBaseActivity),
				this);
	}

	public void opreaterUserHead(Bitmap img) {
		try {
			if(mCurrentHead != null){
				mCurrentHead.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mCurrentHead = mUI.onGetHead(img);
	}

	@Override
	public void onPrivateBitMapOpreaterFinish(boolean isSuccess,
			long request_code, Bitmap value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		// TODO Auto-generated method stub
		if (do_edit_private_info_request == request_code) {
			UbtLog.d(TAG, "currentUser= onGetJson" + json);
			if (isSuccess) {
				Message msg = new Message();
				msg.obj = json;
				msg.what = MSG_DO_EDIT_PRIVATE_INFO;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = null;
				msg.what = MSG_DO_EDIT_PRIVATE_INFO;
				mHandler.sendMessage(msg);
			}

		}

		if (do_edit_pre_private_info_request == request_code) {

			if (isSuccess) {
				Message msg = new Message();
				msg.obj = json;
				msg.what = MSG_DO_PRE_EDIT_PRIVATE_INFO;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = null;
				msg.what = MSG_DO_PRE_EDIT_PRIVATE_INFO;
				mHandler.sendMessage(msg);
			}

		}

		if (do_get_thrid_user_info_request == request_code) {

			Message msg = new Message();
			msg.obj = json;
			msg.what = MSG_DO_READ_THRID_USER_INFO;
			mHandler.sendMessage(msg);

		}
	}

	@Override
	public void onSharedPreferenOpreaterFinish(boolean isSuccess,
			long request_code, String value) {
		// TODO Auto-generated method stub

	}

	public void doReadThridInfo(Thrid_login_type type) {
		// TODO Auto-generated method stub
		if (type == Thrid_login_type.WECHAT) {
			WeiXinLoginInfo info = (WeiXinLoginInfo) ((AlphaApplication) mBaseActivity
					.getApplicationContext()).getCurrentThridLoginInfo();
			do_get_thrid_user_info_request = GetDataFromWeb.getJsonByGet(
					HttpAddress
							.getRequestUrl(Request_type.get_weixin_user_info),
					HttpAddress.getParamsForGet(new String[] {
							info.access_token, info.openid },
							Request_type.get_weixin_user_info), this);
		} else if (type == Thrid_login_type.QQ) {
			MyLog.writeLog("第三方登录", "PrivateInfoHelper.doReadThridInfo");
			MyTencent.getUserInfo((Activity) mUI, this);
		} else if (type == Thrid_login_type.SINABLOG) {
			MyLog.writeLog("微博登录", "PrivateInfoHelper.doReadThridInfo");
			Oauth2AccessToken info = (Oauth2AccessToken) ((AlphaApplication) mBaseActivity
					.getApplicationContext()).getCurrentThridLoginInfo();
			MyWeiBo.doGetUserInfo((Activity) mUI, info, this);
		} else if (type == Thrid_login_type.FACEBOOK) {
			MyLog.writeLog("FaceBook登录", "PrivateInfoHelper.doReadThridInfo");
			MyFaceBook.onGetUserProfile(mUI);
		} else if (type == Thrid_login_type.TWITTER) {
			MyLog.writeLog("MyTwitter登录", "PrivateInfoHelper.doReadThridInfo");
			MyTwitter.doGetUserProfile(mUI);
		}
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		// MyLog.writeLog("onCancel");
	}


	@Override
	public void onComplete(Object arg0) {
		String result = ((JSONObject) arg0).toString();
		MyLog.writeLog("第三方登录", "收到QQ用户个人信息-->" + result);
		QQUserInfo info = new QQUserInfo().getThiz(result);
		mUI.onQQPrivateInfo(info);
	}

	@Override
	public void onError(UiError arg0) {
		// TODO Auto-generated method stub
		MyLog.writeLog("第三方登录", "收到QQ errorMessage:" + arg0.errorMessage);
		mUI.onEditFinish(
				false,
				mBaseActivity.getResources().getString(
						R.string.ui_common_operate_fail), null);
	}

	public void getUserHead(String img_path, float h, float w) {

		getUserHead(img_path, h, w, true);
	}

	public void getUserHead(String img_path, float h, float w, boolean isConvert) {

		if (img_path == null)
			img_path = "";
		if (img_path.equals(""))
			return;

		GetDataFromWeb.getImageFromHttp(img_path, do_read_user_head_img, this,
				h, w, -1, isConvert, -1);

	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

		if (do_read_user_head_img == request_code) {
			Message msg = new Message();
			msg.what = MSG_DO_GET_IMG;
			msg.obj = bitmap;
			mHandler.sendMessage(msg);
		} else if (readUserHeadImgRequest == request_code) {
			Message msg = new Message();
			msg.what = MSG_DO_GET_IMG_FIRST;
			msg.obj = bitmap;
			mHandler.sendMessage(msg);

		}
	}

	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			User user = User.parse(response);
			mUI.onWeiBoPrivateInfo(user);
		}

	}

	@Override
	public void onWeiboException(WeiboException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendData(String mac, byte[] datas, int nLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectState(boolean bsucceed, String mac) {
		// TODO Auto-generated method stub

	}

	@Override
	public void DistoryHelper() {
		// TODO Auto-generated method stub

	}

	public String getCurrentUserCountryCode() {
		if (this.getCurrentUser() != null
				&& this.getCurrentUser().countryCode != null
				&& !this.getCurrentUser().countryCode.equals("")) {
			return this.getCurrentUser().countryCode;
		} else {
			return null;
		}
	}
}

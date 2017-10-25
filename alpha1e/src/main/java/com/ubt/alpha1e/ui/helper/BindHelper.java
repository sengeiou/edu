package com.ubt.alpha1e.ui.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.BaseActivity;

public class BindHelper extends BaseHelper implements IImageListener,
		IJsonListener {
	private IBindUI mUI;
	// -------------------------------
	private long do_read_user_head_img_request = 11002;
	private long do_bind_request = 11001;
	// -------------------------------
	private static final int MSG_DO_READ_USER_HEAD_IMG = 1001;
	private static final int MSG_BIND_FINISH = 1002;
	// -------------------------------
	public static final int bind_success = 0000;
	public static final int bind_fail = -1;
	public static final int bind_fail_no_deivce = 2001;
	public static final int bind_fail_has_binded = 2002;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_DO_READ_USER_HEAD_IMG) {
				if (msg.obj != null) {
					mUI.onReadHeadImgFinish(true, (Bitmap) msg.obj);
				} else {
					mUI.onReadHeadImgFinish(false, null);
				}
			}
			if (msg.what == MSG_BIND_FINISH) {

				int bind_info = bind_fail;
				try {
					bind_info = (Integer) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mUI.onBindFinish(new Integer(bind_info));
			}
		}
	};

	public BindHelper(IBindUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
	}

	public void doReadUserHead(String img_url, int h, int w) {

		GetDataFromWeb.getImageFromHttp(img_url, do_read_user_head_img_request,
				this, -1, -1, -1, -1);

	}

	public UserInfo getCurrentUserInfo() {
		return ((AlphaApplication) mBaseActivity.getApplicationContext())
				.getCurrentUserInfo();
	}

	public void doBindAlpha(String uid, String num) {
		if (num.length() < 9) {
			mUI.onNoteNumNotFull();
			return;
		}
		GetDataFromWeb.getJsonByPost(do_bind_request, HttpAddress
				.getRequestUrl(Request_type.bind), HttpAddress
				.getParamsForPost(new String[] { uid, num }, Request_type.bind,
						((AlphaApplication) mBaseActivity
								.getApplicationContext()).getCurrentUserInfo(),
						mBaseActivity), this);
	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		if (do_read_user_head_img_request == request_code) {
			if (isSuccess) {
				Message msg = new Message();
				msg.obj = bitmap;
				msg.what = MSG_DO_READ_USER_HEAD_IMG;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = null;
				msg.what = MSG_DO_READ_USER_HEAD_IMG;
				mHandler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		// TODO Auto-generated method stub
		if (request_code == do_bind_request) {
			Message msg = new Message();
			msg.what = MSG_BIND_FINISH;
			msg.obj = JsonTools.getJsonInfo(json);
			MyLog.writeLog("绑定功能", "收到的绑定信息：" + msg.obj);
			mHandler.sendMessage(msg);
		}
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

}

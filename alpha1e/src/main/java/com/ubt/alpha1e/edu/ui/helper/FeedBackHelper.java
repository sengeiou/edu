package com.ubt.alpha1e.edu.ui.helper;

import org.json.JSONObject;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;

public class FeedBackHelper extends BaseHelper implements IJsonListener {

	private IFeedBackUI mUI;

	private long do_feedback = 11001;

	public FeedBackHelper(IFeedBackUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;

	}

	public void doFeedBack(String _account, String _content) {

		String verLocal = "";
		try {
			verLocal = mBaseActivity.getPackageManager().getPackageInfo(
					mBaseActivity.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		String robot_hard_version = ((AlphaApplication) mBaseActivity
				.getApplication()).getRobotHardVersion();
		String robot_soft_version = ((AlphaApplication) mBaseActivity
				.getApplication()).getRobotSoftVersion();

		String account = _account;
		if (getCurrentUser() != null) {
			account = getCurrentUser().userEmail.equals("") ? getCurrentUser().userPhone
					: getCurrentUser().userEmail;
		}

		JSONObject jobj = new JSONObject();
		try {
			jobj.put("appVersion", verLocal);
			jobj.put("robotHardVersion", robot_hard_version);
			jobj.put("robotSoftVersion", robot_soft_version);
			jobj.put("feedbackUser", account);
			jobj.put("systemType", "android");
			jobj.put("feedbackInfo", _content);

		} catch (Exception e) {
			mUI.onFeedBackFinish(
					false,
					mBaseActivity.getResources().getString(
							R.string.ui_about_feedback_fail));
		}

		GetDataFromWeb.getJsonByPost(do_feedback,
				HttpAddress.getRequestUrl(Request_type.do_feed_back),
				HttpAddress.getParamsForPost(jobj.toString(), mBaseActivity),
				this);
	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {

		if (do_feedback == request_code) {
			if (JsonTools.getJsonStatus(json)) {
				mUI.onFeedBackFinish(true, mBaseActivity.getResources()
						.getString(R.string.ui_about_feedback_success));

			} else {
				mUI.onFeedBackFinish(false, mBaseActivity.getResources()
						.getString(R.string.ui_about_feedback_fail_net));
			}
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

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

}

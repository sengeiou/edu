package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

public class RegisterHelper extends BaseHelper implements IJsonListener,
		ISharedPreferensListenet {

	private static final String TAG = "RegisterHelper";

	public  enum Register_type {
		Emial_type, Phone_type
	}

	private IRegisterUI mUI;

	private String mCurrentPhoneAccount;
	private String mCurrentPhonePwd;
	private String mCurrentCountryCode;
	// --------------------------------
	private long do_register_request = 11001;
	private long do_get_verification_code = 11002;
	private long do_check_verification_code = 11003;
	// --------------------------------
	private static final int MSG_DO_REGISTER = 1001;
	private static final int MSG_CHECK_VERIFICATION = 1002;
	public static final int RegisterCompleteCode = 10086;
	public static final String REGISTER_SUCCESS = "register_success";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_DO_REGISTER) {

				if (msg.obj == null) {
					mUI.onRegisterFinish(false, null, mBaseActivity
							.getResources().getString(R.string.ui_common_network_request_failed));
					return;
				}
				String json = (String) msg.obj;

				if (!JsonTools.getJsonStatus(json)) {

					if (JsonTools.getJsonInfo(json) == 1003) {
						mUI.onRegisterFinish(
								false,
								null,
								mBaseActivity.getResources().getString(
										R.string.ui_register_prompt_account_exists));
						return;

					} else {
						mUI.onRegisterFinish(
								false,
								null,
								mBaseActivity.getResources().getString(
										R.string.ui_register_prompt_information_error));
						return;
					}
				}

				String model_str = JsonTools.getJsonModel(json).toString();
				UbtLog.d(TAG,"model_str = " + model_str);
				UserInfo info = new UserInfo().getThiz(model_str);
				info.countryCode = mCurrentCountryCode;
				// ���ڴ��м�¼�û�״̬
				((AlphaApplication) mBaseActivity.getApplicationContext())
						.setCurrentUserInfo(info);
				// ������м�¼�û�״̬
				String info_str = UserInfo.getModelStr(info);
				BasicSharedPreferencesOperator.getInstance(mBaseActivity,
						DataType.USER_USE_RECORD).doWrite(
						BasicSharedPreferencesOperator.LOGIN_USER_INFO,
						info_str, RegisterHelper.this, -1);

				mUI.onRegisterFinish(true, JsonTools.getJsonModel(json), "");

			} else if (msg.what == MSG_CHECK_VERIFICATION) {
				if ((Boolean) msg.obj) {

					/*String url = HttpAddress.getRequestUrl(Request_type.new_phone_login);
					String param = HttpAddress.getParamsForPost(new String[] {
									mCurrentPhoneAccount, mCurrentPhonePwd,
									getType(Register_type.Phone_type) + "",
									mCurrentCountryCode },
							Request_type.new_phone_register, mBaseActivity);
					GetDataFromWeb.getJsonByPost(do_register_request, url, param, RegisterHelper.this);*/

					GetDataFromWeb.getJsonByPost(do_register_request,
							HttpAddress.getRequestUrl(Request_type.register),
							HttpAddress.getParamsForPost(new String[] {
									mCurrentPhoneAccount, mCurrentPhonePwd,
									getType(Register_type.Phone_type) + "",
									mCurrentCountryCode },
									Request_type.register, mBaseActivity),
							RegisterHelper.this);
				} else {
					mUI.onNoteVCodeInvalid();
				}
			}
		}
	};

	public int getType(Register_type type) {
		return (type == Register_type.Emial_type) ? 1 : 2;
	}

	public RegisterHelper(IRegisterUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
	}

	public void doReigster(String countryCode, String account, String passwd,
			String v_code, Register_type type) {

		if (type == Register_type.Phone_type) {

			mCurrentCountryCode = countryCode;
			mCurrentPhoneAccount = account;
			mCurrentPhonePwd = passwd;

			GetDataFromWeb.getJsonByPost(do_check_verification_code,
					HttpAddress.getRequestUrl(Request_type.check_verification),
					HttpAddress.getParamsForPost(
							new String[] { account, v_code },
							Request_type.check_verification, mBaseActivity),
					this);
		} else {
			mCurrentCountryCode = countryCode;

			GetDataFromWeb.getJsonByPost(do_register_request, HttpAddress
					.getRequestUrl(Request_type.register), HttpAddress
					.getParamsForPost(new String[] { account, passwd,
							getType(Register_type.Emial_type) + "", "" },
							Request_type.register, mBaseActivity), this);
		}

	}

	public void doGetVerificationCode(String pho_num) {
		GetDataFromWeb.getJsonByPost(do_get_verification_code, HttpAddress
				.getRequestUrl(Request_type.get_verification), HttpAddress
				.getParamsForPost(new String[] { pho_num },
						Request_type.get_verification, mBaseActivity), this

		);
	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		// TODO Auto-generated method stub
		if ("debug".equals("debug")) {
			if (do_register_request == request_code) {
				UbtLog.d(TAG,"do_register_request json = " + json);
				if (isSuccess) {
					Message msg = new Message();
					msg.obj = json;
					msg.what = MSG_DO_REGISTER;
					mHandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.obj = null;
					msg.what = MSG_DO_REGISTER;
					mHandler.sendMessage(msg);
				}
			} else if (do_check_verification_code == request_code) {
				Message msg = new Message();
				msg.obj = JsonTools.getJsonStatus(json);
				msg.what = MSG_CHECK_VERIFICATION;
				mHandler.sendMessage(msg);
			}
		} else {
			Message msg = new Message();
			msg.obj = "";
			msg.what = MSG_DO_REGISTER;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onSharedPreferenOpreaterFinish(boolean isSuccess,
			long request_code, String value) {
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

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

	public void testSuccess()
	{
		mUI.onRegisterFinish(true,null,"");
	}
}

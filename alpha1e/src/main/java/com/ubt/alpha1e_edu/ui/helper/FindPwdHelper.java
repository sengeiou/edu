package com.ubt.alpha1e_edu.ui.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e_edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e_edu.ui.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FindPwdHelper extends BaseHelper implements IJsonListener {

	public static enum Find_Type {
		by_phone, by_email
	}

	public static enum Find_Fail_type {
		no_account, other_reason
	}

	private List<IFindPwdUI> mUIs;
	public Find_Type mCurrentType;
	public String mCurrentAccount;
	public String mCurrentVCode;
	// -------------------------------
	private long do_start_forget_pwd_request = 11001;
	private long do_get_verification_code_request = 11002;
	private long do_check_verification_code_request = 11003;
	private long do_reset_pwd_request = 11004;
	// -------------------------------
	private static final int MSG_START_FORGET_PWD = 1001;
	private static final int MSG_CHECK_VERIFICATION = 1002;
	private static final int MSG_RESET_PWD = 1003;
	private static final int MSG_GET_VCODE = 1004;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == MSG_START_FORGET_PWD) {
				String response_str = (String) msg.obj;

				if (JsonTools.getJsonStatus(response_str)) {

					for (int i = 0; i < mUIs.size(); i++) {
						try {
							mUIs.get(i).doReSetPwd(mCurrentAccount,
									mCurrentType);
						} catch (Exception e) {
						}
					}
				} else {
					if (JsonTools.getJsonInfo(response_str) == 1006) {
						for (int i = 0; i < mUIs.size(); i++) {
							try {
								mUIs.get(i).doReSetPwdFail(
										Find_Fail_type.no_account);
							} catch (Exception e) {
								mUIs.get(i).doReSetPwdFail(
										Find_Fail_type.other_reason);
							}
						}

					} else {
						for (int i = 0; i < mUIs.size(); i++) {
							try {
								mUIs.get(i).doReSetPwdFail(
										Find_Fail_type.other_reason);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}

			}

			else if (msg.what == MSG_GET_VCODE) {

				String response_str = (String) msg.obj;

				if (JsonTools.getJsonStatus(response_str)) {

					for (int i = 0; i < mUIs.size(); i++) {
						try {
							mUIs.get(i)
									.onStartFindPwd(true, mCurrentType, null);
						} catch (Exception e) {
						}
					}

				} else {

					try {
						if (JsonTools.getJsonInfo(response_str) == 1006) {
							for (int i = 0; i < mUIs.size(); i++) {
								try {
									mUIs.get(i).doReSetPwdFail(
											Find_Fail_type.no_account);
								} catch (Exception e) {
									mUIs.get(i).doReSetPwdFail(
											Find_Fail_type.other_reason);
								}
							}

						} else {
							for (int i = 0; i < mUIs.size(); i++) {
								try {
									mUIs.get(i).doReSetPwdFail(
											Find_Fail_type.other_reason);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
					} catch (Exception e1) {
						for (int i = 0; i < mUIs.size(); i++) {
							try {
								mUIs.get(i).doReSetPwdFail(
										Find_Fail_type.other_reason);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				}

			}

			else if (msg.what == MSG_RESET_PWD) {
				for (int i = 0; i < mUIs.size(); i++) {
					try {

						mUIs.get(i).onNoteResetPwd((Boolean) msg.obj);
					} catch (Exception e) {
					}
				}
			}

			else if (msg.what == MSG_CHECK_VERIFICATION) {
				if ((Boolean) msg.obj) {

					for (int i = 0; i < mUIs.size(); i++) {
						try {
							mUIs.get(i).doReSetPwd(mCurrentAccount,
									mCurrentType);
						} catch (Exception e) {
						}
					}

				} else {

					for (int i = 0; i < mUIs.size(); i++) {
						try {

							mUIs.get(i).onNoteVCodeInvalid();
						} catch (Exception e) {
						}
					}

				}
			}

		}
	};

	public FindPwdHelper(BaseActivity _baseActivity) {
		super(_baseActivity);
		mUIs = new ArrayList<IFindPwdUI>();
	}

	public void doGetVerificationCode(String pho_num) {
		GetDataFromWeb.getJsonByPost(do_get_verification_code_request,
				HttpAddress.getRequestUrl(Request_type.get_verification),
				HttpAddress.getParamsForPost(new String[] { pho_num },
						Request_type.get_verification, mBaseActivity), this

		);
	}

	public void addToListenerList(IFindPwdUI ui) {
		mUIs.add(ui);
	}

	public void doStartFind(String account) {

		mCurrentAccount = account;
		if (mCurrentAccount.contains("@")) {
			// 如果通过邮箱找回
			mCurrentType = Find_Type.by_email;
			GetDataFromWeb
					.getJsonByPost(
							do_start_forget_pwd_request,
							HttpAddress
									.getRequestUrl(HttpAddress.Request_type.for_get_pwd),
							HttpAddress.getParamsForPost(
									new String[] { account },
									HttpAddress.Request_type.for_get_pwd,
									mBaseActivity), this);
		} else {
			// 如果用手机号码找回
			mCurrentType = Find_Type.by_phone;
			doGetVerificationCode(mCurrentAccount);
		}

	}

	public void doCheckVCode(String account, String v_code) {
		mCurrentAccount = account;
		GetDataFromWeb.getJsonByPost(do_check_verification_code_request,
				HttpAddress.getRequestUrl(Request_type.check_verification),
				HttpAddress.getParamsForPost(new String[] { account, v_code },
						Request_type.check_verification, mBaseActivity), this);

	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		// TODO Auto-generated method stub
		if (do_start_forget_pwd_request == request_code) {
			if (isSuccess) {
				Message msg = new Message();
				msg.obj = json;
				msg.what = MSG_START_FORGET_PWD;
				mHandler.sendMessage(msg);
			}
		} else if (do_check_verification_code_request == request_code) {
			Message msg = new Message();
			msg.obj = JsonTools.getJsonStatus(json);
			if (JsonTools.getJsonStatus(json)) {
				try {
					mCurrentVCode = new JSONObject(json).getString("models");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			msg.what = MSG_CHECK_VERIFICATION;
			mHandler.sendMessage(msg);
		} else if (do_reset_pwd_request == request_code) {
			Message msg = new Message();
			msg.obj = JsonTools.getJsonStatus(json);
			msg.what = MSG_RESET_PWD;
			mHandler.sendMessage(msg);
		} else if (do_get_verification_code_request == request_code) {
			Message msg = new Message();
			msg.obj = json;
			msg.what = MSG_GET_VCODE;
			mHandler.sendMessage(msg);
		}
	}

	public void doResetPwd(String pwd) {
		GetDataFromWeb.getJsonByPost(do_reset_pwd_request, HttpAddress
				.getRequestUrl(Request_type.reset_pwd),
				HttpAddress.getParamsForPost(new String[] { mCurrentAccount,
						pwd, mCurrentVCode }, Request_type.reset_pwd,
						mBaseActivity), this);
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

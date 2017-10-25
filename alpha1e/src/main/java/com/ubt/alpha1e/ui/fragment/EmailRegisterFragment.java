package com.ubt.alpha1e.ui.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.data.model.RegisterInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.PrivateInfoActivity;
import com.ubt.alpha1e.ui.RegisterActivity;
import com.ubt.alpha1e.ui.RegisterActivity.EmailFragmentListener;
import com.ubt.alpha1e.ui.custom.EditTextCheck;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.IRegisterUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.ui.helper.RegisterHelper;
import com.ubt.alpha1e.utils.NavigateUtil;

import org.json.JSONObject;

public class EmailRegisterFragment extends BaseFragment implements IRegisterUI,
		BaseDiaUI, EmailFragmentListener {

	private View mMainView;
	private RegisterHelper mHelper;
	private EditText edt_email_address;
	private EditText edt_passwd;
	private RegisterActivity activity;
	private LoadingDialog mCoonLoadingDia;
	private String emailRegisterAccount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.fragment_register_email, null);
		initUI();
		initControlListener();
		mHelper = new RegisterHelper(this, (BaseActivity) getActivity());
		return mMainView;
	}

	public static EmailRegisterFragment newInstance(String account) {
		EmailRegisterFragment fragment = new EmailRegisterFragment();
		Bundle args = new Bundle();
		args.putString(LoginHelper.ACCOUNT,account);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			emailRegisterAccount = getArguments().getString(LoginHelper.ACCOUNT);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity = (RegisterActivity) this.getActivity();
		setEditTextHint(edt_email_address,activity.getStringResources("ui_login_email_placeholder"));
		setEditTextHint(edt_passwd,activity.getStringResources("ui_login_password_placeholder"));
	}

	private void setEditTextHint(EditText edt,String hint)
	{
		SpannableString ss = new SpannableString(hint);
		// 新建一个属性对象,设置文字的大小
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
		// 附加属性到文本
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置hint
		edt.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
	}

	@Override
	public void onRegisterFinish(boolean is_success, JSONObject info,
			String error) {
		// TODO Auto-generated method stub
		if (mCoonLoadingDia != null)
			mCoonLoadingDia.cancel();

		if (is_success) {
			Intent inte = new Intent();
			inte.putExtra(PrivateInfoHelper.Edit_type,
					PrivateInfoHelper.EditType.local_register_type);
			inte.setClass(getActivity(), PrivateInfoActivity.class);
			getActivity().startActivity(inte);
			getActivity().finish();
		} else {
			Toast.makeText(getActivity(), error, 500).show();
		}
	}

	@Override
	protected void initUI() {
		edt_email_address = (EditText) mMainView
				.findViewById(R.id.edt_name);
		edt_passwd = (EditText) mMainView.findViewById(R.id.edt_passwd);
		edt_passwd.setTypeface(Typeface.DEFAULT);
		edt_passwd.setTransformationMethod(new PasswordTransformationMethod());
		EditTextCheck.addCheckForEmail(edt_email_address, this.getActivity());
		EditTextCheck.addCheckForPasswd(edt_passwd, this.getActivity());
		edt_email_address.setText(emailRegisterAccount);
	}

	@Override
	protected void initControlListener() {

		edt_email_address.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.length() > 0) {
//					if (activity != null)
//						activity.changeButtonText(true, true);
				} else {
//					if (activity != null)
//						activity.changeButtonText(false, true);
				}
				activity.clearWrongMessage();

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		edt_email_address.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (!arg1) {
				} else {
				}
			}
		});

		edt_passwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

				if (arg0.length() > 0)
				{
					edt_passwd.setTypeface(Typeface.MONOSPACE);
					activity.setLoginButtonEnable(arg0.length()>0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});


	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoteVCodeInvalid() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteWaitWebProcressShutDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkRegisterInput() {

		String account = edt_email_address.getText().toString();
		String passwd = edt_passwd.getText().toString();

		// 1、用户名不可为空
		if (account == null || account.trim().equals("")) {
			activity.toastForWrongMeassage(getActivity().getResources()
					.getString(R.string.ui_login_prompt_empty_email));
			return;
		}
		// 2、密码和重输密码不可为空
		if (passwd == null || passwd.trim().equals("")) {
			activity.toastForWrongMeassage(getActivity().getResources()
					.getString(R.string.ui_login_prompt_empty_password));
			return;
		}
		// 3、邮箱要合法
		if (!DataCheckTools.isEmail(account)) {
			activity.toastForWrongMeassage(getActivity().getResources()
					.getString(R.string.ui_login_prompt_email_wrong_format));
			return;
		}
		// 5、密码不能太短或太长
		if (passwd.length() < 6) {
			activity.toastForWrongMeassage(getActivity().getResources()
					.getString(R.string.ui_login_prompt_password_too_short));
			return;
		}

		if (passwd.length() > 16) {
			activity.toastForWrongMeassage(getActivity().getResources()
					.getString(R.string.ui_login_prompt_passwprd_too_long));
			return;
		}


		// 7、密码格式要合法
		if (!DataCheckTools.isCorrectPswFormat(edt_passwd.getText().toString())
				) {
			activity.toastForWrongMeassage(getActivity().getString(
					R.string.ui_login_prompt_passwprd_error));
			return;
		}

//		if (mCoonLoadingDia == null)
//			mCoonLoadingDia = LoadingDialog.getInstance(
//					EmailRegisterFragment.this.getActivity(),
//					EmailRegisterFragment.this);
//		mCoonLoadingDia.show();
//		mHelper.doReigster("", account, passwd, "", Register_type.Emial_type);
		RegisterInfo info = new RegisterInfo();
		info.account =  account;
		info.password = passwd;
		NavigateUtil.INSTANCE.navigateToRegisterNextStepForResult(activity,info,RegisterHelper.RegisterCompleteCode);



	}

}

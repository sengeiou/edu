package com.ubt.alpha1e.login.loginauth;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.GetCodeRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import okhttp3.Call;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginAuthActivity extends MVPBaseActivity<LoginAuthContract.View, LoginAuthPresenter> implements LoginAuthContract.View {

    private static final String TAG = "LoginAuthActivity";

    @BindView(R.id.edt_tel)
    EditText edtTel;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.edt_verify_code)
    EditText edtVerifyCode;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    RequestCountDown requestCountDown;
    private static final long REQUEST_TIME = 61 * 1000;

    private String token;
    private String userId;
    private String nickName;
    private String userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControlListener();
        requestCountDown = new RequestCountDown(REQUEST_TIME, 1000);
        token = SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN);
        userId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
        nickName = SPUtils.getInstance().getString(Constant.SP_USER_NICKNAME);
        userImage = SPUtils.getInstance().getString(Constant.SP_USER_IMAGE);
        UbtLog.d(TAG, "token:" + token + "--userId:" + userId + "--nickName:" + nickName + "--userImage:" + userImage);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

        edtTel.addTextChangedListener(new TextWatcher() {


            String phoneNum = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                phoneNum = charSequence.toString();


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (CheckPhoneNumberUtil.isChinaPhoneLegal(phoneNum)) {
                    setGetCodeTextEnable(true);
                } else {
                    setGetCodeTextEnable(false);
                }

            }
        });

        edtVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnConfirm.setEnabled(true);
                } else {
                    btnConfirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCountDown.start();
                setGetCodeTextEnable(false);
//                String params = "{"
//                        + "\"token\":" + "\"" + token + "\""
//                        + ",\n\"userId\":" + "\"" + userId + "\""
//                        + ",\n\"phone\":" + "\"" + edtTel.getText().toString() + "\""
//                        + "}";
                GetCodeRequest getCodeRequest = new GetCodeRequest();
                getCodeRequest.setPhone(edtTel.getText().toString());
                 OkHttpClientUtils.getJsonByPostRequest(HttpEntity.REQUEST_SMS_CODE, getCodeRequest, 0).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.e(TAG, "REQUEST_SMS_CODE Exception:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.e(TAG, "REQUEST_SMS_CODE response:" + response);
                    }
                });

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String params = "{"
                        + "\"token\":" + "\"" + token + "\""
                        + ",\n\"userId\":" + "\"" + userId + "\""
                        + ",\n\"phone\":" + "\"" + edtTel.getText().toString() + "\""
                        + ",\n\"nickName\":" + "\"" + nickName + "\""
                        + ",\n\"headPic\":" + "\"" + userImage + "\""
                        + ",\n\"code\":" + "\"" + edtVerifyCode.getText().toString() + "\""
                        + "}";

                OkHttpClientUtils.getJsonByPostRequest(HttpEntity.BIND_ACCOUNT, params, 0).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.e(TAG, "Exception:" + e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d(TAG, "response:" + response);
                        BaseResponseModel baseResponseModel = GsonImpl.get().toObject(response, BaseResponseModel.class);
                        if (baseResponseModel.status) {
                            Intent intent = new Intent();
                            intent.setClass(LoginAuthActivity.this, UserEditActivity.class);
                            startActivity(intent);
                        }

                    }
                });


            }
        });

    }

    private void GoUserInfo() {
        Intent intent = new Intent();
        intent.setClass(LoginAuthActivity.this, UserEditActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_login_auth;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;//拦截事件传递,从而屏蔽back键。
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setGetCodeTextEnable(boolean enable) {

        tvGetCode.setEnabled(enable);
        if (enable == true) {
            tvGetCode.setTextColor(getResources().getColor(R.color.tv_blue_color));
        } else {
            tvGetCode.setTextColor(getResources().getColor(R.color.login_line_color));
        }


    }


    /**
     * 类名
     *
     * @author作者<br/> 实现的主要功能。
     * created at
     * 修改者，修改日期，修改内容。
     */


    class RequestCountDown extends CountDownTimer {

        public RequestCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

            tvCountdown.setText("0 s");
            setGetCodeTextEnable(true);


        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCountdown.setText("" + (millisUntilFinished / 1000) + " s");
        }
    }


}

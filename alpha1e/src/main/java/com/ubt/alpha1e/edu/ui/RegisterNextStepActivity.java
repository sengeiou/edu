package com.ubt.alpha1e.edu.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
//import com.sina.weibo.sdk.openapi.models.User;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.AlphaApplicationValues;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.QQUserInfo;
import com.ubt.alpha1e.edu.data.model.RegisterInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.data.model.WeiXinUserInfo;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.fragment.BaseRegisterFragment;
import com.ubt.alpha1e.edu.ui.fragment.RegisterCompleteInfoFragment;
import com.ubt.alpha1e.edu.ui.fragment.RegisterSelectCountryFragment;
import com.ubt.alpha1e.edu.ui.fragment.RegisterVeriCodeFragment;
import com.ubt.alpha1e.edu.ui.helper.IPrivateInfoUI;
import com.ubt.alpha1e.edu.ui.helper.IRegisterUI;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.edu.ui.helper.RegisterHelper;
import com.ubt.alpha1e.edu.utils.NavigateUtil;

import org.json.JSONObject;

import java.util.Stack;

public class RegisterNextStepActivity extends BaseActivity implements IRegisterUI,BaseDiaUI,IPrivateInfoUI {


    private RegisterInfo registInfo;
    private Fragment mCurrentFragment;
    private Button btn_next;
    private LinearLayout btn_back;
    public  PrivateInfoHelper mPrivateInfoHelper;
    public boolean isPhoneRegister = true;
    private AlphaApplicationValues.Thrid_login_type mThirdLoginType;
    private Stack<Fragment> fragmentStack = new Stack<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_verify_code);
        initUI();
        initControlListener();
        registInfo = getIntent().getParcelableExtra(NavigateUtil.INSTANCE.REGISTER);
        if(!TextUtils.isEmpty(registInfo.countryCode))//手机注册
        {
            RegisterVeriCodeFragment registerVeriCodeFragment = RegisterVeriCodeFragment.newInstance(registInfo);
            loadFragment(registerVeriCodeFragment);
            isPhoneRegister = true;
        }else//邮箱注册
        {
            RegisterSelectCountryFragment registerSelectCountryFragment = RegisterSelectCountryFragment.newInstance(registInfo);
            loadFragment(registerSelectCountryFragment);
            setNextButtonEnable(true);
            isPhoneRegister = false;
        }
    }

    @Override
    protected void initUI() {
        btn_next = (Button)findViewById(R.id.btn_base_save);
        btn_back = (LinearLayout)findViewById(R.id.lay_base_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
                setNextButtonEnable(true);
            }
        });
        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDailog();
                ((BaseRegisterFragment)fragmentStack.peek()).gotoNextStep();
            }
        },getStringResources("ui_perfect_next"));
        setNextButtonEnable(false);
        mHelper = new RegisterHelper(this,this);
        mPrivateInfoHelper = new PrivateInfoHelper(this,this);
        mCoonLoadingDia = LoadingDialog.getInstance(this,this);
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        ((TextView) this.findViewById(R.id.txt_base_title_name))
                .setText(getStringResources("ui_register"));
    }

    public void setNextButtonEnable(boolean isEnable) {

        if (isEnable)
        {
            btn_next.setTextColor(getResources().getColor(R.color.T5));
        }else
        {
            btn_next.setTextColor(getResources().getColor(R.color.T7));
        }
        btn_next.setEnabled(isEnable);

    }

    private void showLoadingDailog()
    {
        if(mCoonLoadingDia!=null&&!mCoonLoadingDia.isShowing()&&!isFinishing()){
            mCoonLoadingDia.show();
        }
    }

    private void dismissLoadingDailog()
    {
       if(mCoonLoadingDia!=null&&mCoonLoadingDia.isShowing()&&!isFinishing()){
           mCoonLoadingDia.cancel();
       }
    }

    public void doCloseKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }


    public void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if(mCurrentFragment == null)
                transaction.add(R.id.lay_content,targetFragment).addToBackStack(null).commit();
            else
            {
                transaction
                        .hide(mCurrentFragment)
                        .add(R.id.lay_content, targetFragment)
                        .addToBackStack(null)
                        .commit();

            }
        } else {
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
        fragmentStack.add(mCurrentFragment);

    }

    public void popBackStack()
    {
        if(getFragmentManager().getBackStackEntryCount()>1)
        {
            getFragmentManager().popBackStack();
            fragmentStack.pop();
        }else{
            //取消注册，清空当前用户
            ((AlphaApplication) getApplicationContext()).clearCurrentUserInfo();
            finish();
        }
    }

    @Override
    public void onNoteVCodeInvalid() {

        dismissLoadingDailog();
        showToast("ui_register_prompt_vertify_code_failed");
    }

    @Override
    public void onRegisterFinish(boolean is_success, JSONObject info, String error_info) {

        dismissLoadingDailog();
        if (is_success) {
                setNextButtonEnable(false);
                RegisterCompleteInfoFragment registerCompleteInfoFragment = new RegisterCompleteInfoFragment();
                loadFragment(registerCompleteInfoFragment);
        } else {
            Toast.makeText(this, error_info, Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 返回上一个fragment
            popBackStack();
            setNextButtonEnable(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public Bitmap onGetHead(Bitmap img) {
        return null;
    }

    @Override
    public void onNodeNickNameEmpty() {

    }

    @Override
    public void onNodeEmialEmpty() {

    }

    @Override
    public void onNodeHeadEmpty() {

    }

    @Override
    public void onEditFinish(boolean is_success, String error_info, JSONObject info) {

        //完善信息完成
        dismissLoadingDailog();
        if(is_success)
        {
                mPrivateInfoHelper.doRecordUser(info);
                Intent intent = new Intent();
                intent.putExtra(RegisterHelper.REGISTER_SUCCESS,is_success);
                setResult(RegisterHelper.RegisterCompleteCode, intent);
                finish();
        }else
        {
            //注册失败，清空当前用户
            ((AlphaApplication) getApplicationContext()).clearCurrentUserInfo();
            Toast.makeText(this, error_info, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onQQPrivateInfo(QQUserInfo info) {
        UserInfo mCurUserInfo = ((RegisterSelectCountryFragment)mCurrentFragment).getMcurrentUserInfo();
        mCurUserInfo.userName = info.nickname;
        mCurUserInfo.userImage = info.figureurl_qq_2;
        if("男".equals(info.gender)){
            mCurUserInfo.userGender = "1";
        }else if("女".equals(info.gender)){
            mCurUserInfo.userGender = "2";
        }
        //mCurUserInfo.userGender = info.gender;
        ((RegisterSelectCountryFragment)mCurrentFragment).setUserInfo(mCurUserInfo);
    }

    @Override
    public void onWeiXinPrivateInfo(WeiXinUserInfo info) {
        UserInfo mCurUserInfo = ((RegisterSelectCountryFragment)mCurrentFragment).getMcurrentUserInfo();
        mCurUserInfo.userName = info.nickname;
        mCurUserInfo.userImage = info.headimgurl;
        mCurUserInfo.userGender = info.sex;
        ((RegisterSelectCountryFragment)mCurrentFragment).setUserInfo(mCurUserInfo);
    }

//    @Override
//    public void onWeiBoPrivateInfo(User info) {
//
//    }

    @Override
    public void onFaceBookProfileInfo(Profile profile, String url) {
        UserInfo mCurUserInfo = ((RegisterSelectCountryFragment)mCurrentFragment).getMcurrentUserInfo();
        mCurUserInfo.userName = profile.getName();
        mCurUserInfo.userImage = profile.getProfilePictureUri(120,120).toString();
        ((RegisterSelectCountryFragment)mCurrentFragment).setUserInfo(mCurUserInfo);
    }

    @Override
    public void onTwitterProfileInfo(twitter4j.User user) {

        UserInfo mCurUserInfo = ((RegisterSelectCountryFragment)mCurrentFragment).getMcurrentUserInfo();
        mCurUserInfo.userName = user.getName();
        mCurUserInfo.userImage = user.getBiggerProfileImageURLHttps();
        ((RegisterSelectCountryFragment)mCurrentFragment).setUserInfo(mCurUserInfo);
    }

    @Override
    public void onPreEditFinish(boolean b, Object object) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NavigateUtil.INSTANCE.NAV_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String mCountryName = bundle.getString("countryName");
                String mCountryNumber = bundle.getString("countryNumber");
                if (mCurrentFragment != null&&mCurrentFragment instanceof RegisterSelectCountryFragment) {
                    try {
                        ((RegisterSelectCountryFragment)mCurrentFragment).setCountryInfo(mCountryName,
                                mCountryNumber);
                        setNextButtonEnable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

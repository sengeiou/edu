package com.ubt.alpha1e.edu.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.RegisterInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.ui.RegisterNextStepActivity;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.edu.ui.helper.RegisterHelper;
import com.ubt.alpha1e.edu.utils.NavigateUtil;

/**
   * User: wilson
   * Description: 邮箱注册选择国家码
   * Time: 2016/7/18 10:19 
   */  
 
public class RegisterSelectCountryFragment extends BaseRegisterFragment {


    private RegisterInfo registerInfo;

    private UserInfo mCurrentUserInfo;
    private PrivateInfoHelper privateInfoHelper;
    private TextView txt_country_select;
    private LinearLayout lay_contory;
    private TextView txt_country_des;
    public RegisterSelectCountryFragment() {
        // Required empty public constructor
    }

    public static RegisterSelectCountryFragment newInstance(RegisterInfo info) {
        RegisterSelectCountryFragment fragment = new RegisterSelectCountryFragment();
        Bundle args = new Bundle();
        args.putParcelable(NavigateUtil.INSTANCE.REGISTER, PG.convertParcelable(info));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        privateInfoHelper = ((RegisterNextStepActivity)getActivity()).mPrivateInfoHelper;
    }

    @Override
     protected int getLayoutResourceId() {
         return R.layout.fragment_register_select_country;
     }

     @Override
     public void gotoNextStep() {
         if(registerInfo.thirdLoginType!=null)//从第三方登录跳转过来
         {
             privateInfoHelper.doReadThridInfo(registerInfo.thirdLoginType);

         }else
         {
             //邮箱注册跳转过来
             if(mHelper!=null)
                 ( (RegisterHelper)mHelper).doReigster(registerInfo.countryCode,registerInfo.account,registerInfo.password,
                         "", RegisterHelper.Register_type.Emial_type);
         }

     }

     @Override
     protected void initViews() {
         lay_contory = (LinearLayout) mView.findViewById(R.id.layout_country_choose);
         txt_country_select = (TextView) mView
                 .findViewById(R.id.txt_country_code);
         txt_country_des = (TextView) mView
                 .findViewById(R.id.txt_country_num);
         mCurrentUserInfo = UserInfo.doClone(((AlphaApplication) mActivity.getApplicationContext())
                 .getCurrentUserInfo());
     }

     @Override
     protected void initListeners() {

         lay_contory.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View arg0) {
                   NavigateUtil.INSTANCE.navigateToCountryActivityForResult(mActivity,NavigateUtil.INSTANCE.NAV_REQUEST_CODE);
             }
         });

     }

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            registerInfo = getArguments().getParcelable(NavigateUtil.INSTANCE.REGISTER);
            registerInfo.countryCode = "86";//默认为中国

        }
    }

    public void setCountryInfo(String _mCountryName, String _mCountryNumber) {

        txt_country_des.setText(_mCountryName);
        txt_country_select.setText(_mCountryNumber);
        registerInfo.countryCode = _mCountryNumber.substring(1,_mCountryNumber.length());
    }

    public UserInfo getMcurrentUserInfo()
    {
        return mCurrentUserInfo;
    }

    public void setUserInfo(UserInfo info)
    {
        this.mCurrentUserInfo = info;
        this.mCurrentUserInfo.countryCode = registerInfo.countryCode;
        privateInfoHelper.doEditPrivateInfo(mCurrentUserInfo);
    }
}

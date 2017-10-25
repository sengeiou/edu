package com.ubt.alpha1e.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.data.model.RegisterInfo;
import com.ubt.alpha1e.ui.FindPassWdActivity;
import com.ubt.alpha1e.utils.NavigateUtil;

/**
   * User: wilson chen
   * Description: 手机找回密码填写手机号页面
   * Time: 2016/7/19 16:46 
   */  
 
public class FindPswPhoneNumFragment extends BaseRegisterFragment {


     private TextView txt_country_name;
     private TextView txt_country_number;
     private LinearLayout lay_contory;
    private EditText edtPhoneNum;
    private RegisterInfo registerInfo = new RegisterInfo("86");


    public FindPswPhoneNumFragment() {
        // Required empty public constructor
    }

    public static FindPswPhoneNumFragment newInstance() {
        return new FindPswPhoneNumFragment();
    }

     @Override
     protected int getLayoutResourceId() {
         return R.layout.fragment_find_psw_phone_number;
     }

     @Override
     public void gotoNextStep() {

         registerInfo.account = registerInfo.countryCode+edtPhoneNum.getText().toString();
         registerInfo.isFindPws = true;
         RegisterVeriCodeFragment registerVeriCodeFragment = RegisterVeriCodeFragment.newInstance(registerInfo);
         ((FindPassWdActivity)mActivity).loadFragment(registerVeriCodeFragment);
     }

     @Override
     protected void initViews() {
         lay_contory = (LinearLayout) mView.findViewById(R.id.layout_country_choose);
         txt_country_name = (TextView) mView
                 .findViewById(R.id.txt_country_code);
         txt_country_number = (TextView) mView
                 .findViewById(R.id.txt_country_num);
         edtPhoneNum = (EditText)mView.findViewById(R.id.edt_phone_num);
     }

     @Override
     protected void initListeners() {
         lay_contory.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View arg0) {
                 NavigateUtil.INSTANCE.navigateToCountryActivityForResult(mActivity,NavigateUtil.INSTANCE.NAV_REQUEST_CODE);
             }
         });
         edtPhoneNum.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 ((FindPassWdActivity)mActivity).setNextButtonEnable(s.length()>0&& DataCheckTools.isPhoneNum(s.toString()));

             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
     }

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public void setCountryInfo(String _mCountryName, String _mCountryNumber) {

        registerInfo.countryCode = _mCountryNumber;
        txt_country_name.setText(_mCountryName);
        txt_country_number.setText(_mCountryNumber);
    }
}

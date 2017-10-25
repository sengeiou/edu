package com.ubt.alpha1e.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ant.country.CountryActivity;
import com.baoyz.pg.PG;
import com.ubt.alpha1e.data.model.RegisterInfo;
import com.ubt.alpha1e.ui.LoginActivity;
import com.ubt.alpha1e.ui.RegisterActivity;
import com.ubt.alpha1e.ui.RegisterNextStepActivity;
import com.ubt.alpha1e.ui.helper.LoginHelper;

/**
 * User: wilson <br>
 * Description: activity navigate class <br>
 * Time: 2016/7/11 13:43 <br>
 */

public enum NavigateUtil {

    INSTANCE;

    public final int LOGIN_REQUEST = 1001;

    public final int  NAV_REQUEST_CODE = 1002;

    public final String ACCOUNT = "account";

    public final String PASSWORD = "password";

    public final String REGISTER = "register";

    public final String THIRD_REGISTER = "third_register";

    public void navigateToActivity(Context context) {


    }

    /**
     * loginActivity
     */
    public void navigateToLoginActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * registerActivity
     */
    public void navigateToRgsActivityForResult(Activity context, boolean isPhone,int requestCode)
    {
        navigateToRgsActivityForResult(context,"",isPhone,requestCode);
    }

    public void navigateToRgsActivityForResult(Activity context,String account, boolean isPhone, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, RegisterActivity.class);
        intent.putExtra(LoginHelper.ACCOUNT, account);
        intent.putExtra(LoginHelper.PHONE_REGISTER, isPhone);
        context.startActivityForResult(intent, requestCode);
    }

    public void navigateToRegisterNextStepForResult(Activity context, RegisterInfo info, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, RegisterNextStepActivity.class);
        intent.putExtra(REGISTER, PG.convertParcelable(info));
        context.startActivityForResult(intent,requestCode);
    }

    public void navigateToCountryActivityForResult(Activity context, int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(context, CountryActivity.class);
        context.startActivityForResult(intent,requestCode);

    }

}

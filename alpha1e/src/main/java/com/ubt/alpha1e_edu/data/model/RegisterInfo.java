package com.ubt.alpha1e_edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.AlphaApplicationValues;

/**
 * Created by Administrator on 2016/7/14.
 */
@Parcelable
public class RegisterInfo {


    public RegisterInfo(){}

    public RegisterInfo(String countryCode)
    {
        this.countryCode = countryCode;
    }
    public String account;
    public String password;
    public String countryCode ="";
    public boolean isFindPws = false;
    public AlphaApplicationValues.Thrid_login_type thirdLoginType = null/*AlphaApplicationValues.Thrid_login_type.FACEBOOK*/;

}

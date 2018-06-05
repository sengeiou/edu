package com.ubt.alpha1e_edu.utils;

import android.content.Context;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.data.model.UserInfo;

/**
   * User: wilson <br>
   * Description: account management<br>
   * Time: 2016/7/11 14:14 <br>
   */

public class UserAccountManager {

     private Context mContext;
     private static UserAccountManager sInstance = null;
     private UserAccountManager(Context context){
         this.mContext = context;
     }

     public static UserAccountManager get(Context context)
     {
         if(sInstance == null)
             sInstance = new UserAccountManager(context);
         return sInstance;
     }

     public  boolean checkLoginState()
     {
         UserInfo userInfo  = ((AlphaApplication)mContext.getApplicationContext()).getCurrentUserInfo();
         return userInfo == null?false:true;
     }
}

package com.ubt.alpha1e.edu.utils;

import android.app.Dialog;
import android.content.Context;

import com.ubt.alpha1e.edu.ui.dialog.SLoadingDialog;

/**
 * Created by wilson on 2016/5/25.
 */
public class ProgressUtils {
    public static Dialog mDialog;

    public static void showLoadingProgressBar(Context context)
    {
        if(mDialog==null)
           mDialog =  SLoadingDialog.getInstance(context);
        if(!mDialog.isShowing())
            mDialog.show();
    }

    public static void dimissLoadingProgressBar()
    {
        if(mDialog!=null)
        {
            if(mDialog.isShowing())
                mDialog.dismiss();
            mDialog = null;
        }
    }
}

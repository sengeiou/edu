package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2016/3/23.
 */
public class BaseDialog extends Dialog {
    public BaseDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        Context context = this.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = this.findViewById(divierId);
        if(divider!=null)
        divider.setBackgroundColor(0x00000000);
    }
}

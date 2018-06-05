package com.ubt.alpha1e_edu.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.DataTools;

public class SLoadingDialog extends BaseDialog {
    private Context mContext;
    private ImageView img_dia_logo;
    private TextView txt_msg;
    private static SLoadingDialog mDia;

    @Override
    public void show() {

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 设置窗口宽度
        lp.width = (int) DataTools.dip2px(mContext, 100);
        dialogWindow.setAttributes(lp);

        try {
            super.show();
            Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
            img_dia_logo.startAnimation(operatingAnim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String msg) {
        try {
            img_dia_logo.setVisibility(View.GONE);
            txt_msg.setText(msg);
            txt_msg.setVisibility(View.VISIBLE);
            //super.show();

            Window dialogWindow = this.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            // 设置窗口宽度
            lp.width = (int) DataTools.dip2px(mContext, 170);
            dialogWindow.setAttributes(lp);
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private SLoadingDialog(Context context) {
        super(context);
    }

    public static SLoadingDialog getInstance(Context _context) {
       //if(mDia==null)
       //{
           mDia = new SLoadingDialog(_context);
           mDia.mContext = _context;
           mDia.initDia();
       //}
         return  mDia;
    }

    private void initDia() {
        View root = View.inflate(mContext, R.layout.dialog_sloading, null);
        img_dia_logo = (ImageView) root.findViewById(R.id.img_dia_logo);
        txt_msg = (TextView) root.findViewById(R.id.txt_msg);
        this.setContentView(root);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    @Override
    public void cancel() {
        super.cancel();

        mDia = null;
        img_dia_logo = null;
        txt_msg = null;
    }
}

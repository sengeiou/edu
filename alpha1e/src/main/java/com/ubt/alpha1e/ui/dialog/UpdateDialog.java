package com.ubt.alpha1e.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.ui.helper.AboutUsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class UpdateDialog extends BaseDialog {
    public static UpdateDialog mDia;
    private Context mContext;
    private View mRootView;
    private String[] mApkInfo;
    private IUpdateListener mListener;
    // ------------------------------------
    private TextView txt_edtion_info;
    private TextView txt_apk_info_all;
    private TextView txt_wifi_note;

    private Button btn_update_cancel;
    private Button btn_update_ok;

    private UpdateDialog(Context context) {
        super(context);
    }

    public static UpdateDialog getInstance(Context _context, String[] apk_info,
                                           IUpdateListener listener) {
        if (mDia != null && mDia.isShowing())
            mDia.cancel();
        mDia = new UpdateDialog(_context);
        mDia.mContext = _context;
        mDia.mApkInfo = apk_info;
        mDia.mListener = listener;
        mDia.initDia();
        mDia.initData();
        mDia.initControlListener();
        return mDia;
    }

    private void initDia() {
        mRootView = View.inflate(mContext, R.layout.dialog_update_apk, null);
        txt_edtion_info = (TextView) mRootView
                .findViewById(R.id.txt_edtion_info);
        txt_apk_info_all = (TextView) mRootView
                .findViewById(R.id.txt_apk_info_all);
        txt_apk_info_all.setMovementMethod(ScrollingMovementMethod.getInstance());

        txt_wifi_note = (TextView) mRootView.findViewById(R.id.txt_wifi_note);
        btn_update_cancel = (Button) mRootView
                .findViewById(R.id.btn_update_cancel);
        btn_update_ok = (Button) mRootView.findViewById(R.id.btn_update_ok);
        // ------------------------------------------------
        this.setContentView(mRootView);
        // 得到该对话框的的窗口对象
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 设置窗口宽度
        lp.width = (int) DataTools.dip2px(mContext, 280);
        lp.height = (int) DataTools.dip2px(mContext, 530);
        dialogWindow.setAttributes(lp);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(false);
    }

    private void initData() {

        String apk_version = "";
        if (mApkInfo != null && mApkInfo.length > 0) {
            apk_version = mApkInfo[0];
        }

        String apk_size = "";
        if (mApkInfo != null && mApkInfo.length > 1) {
            apk_size = mApkInfo[1];
        }

        txt_edtion_info.setText(mContext.getResources().getString(
                R.string.ui_about_version)
                + apk_version
                + "   "
                + mContext.getResources()
                .getString(R.string.ui_about_update_size)
                + apk_size
                + "M");
        String language = Locale.getDefault().getCountry().toUpperCase();
        JSONArray j_list = null;
        try {
            JSONObject j_obj = new JSONObject(mApkInfo[2]);
            j_list = j_obj.getJSONArray(language);
        } catch (JSONException e) {
            j_list = null;
        }
        if (j_list == null) {
            try {
                JSONObject j_obj = new JSONObject(mApkInfo[2]);
                j_list = j_obj.getJSONArray("US");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            String info = "";
            for (int i = 0; i < j_list.length(); i++) {
                info += j_list.getJSONObject(i).getString("name") + "\n\n";
            }
            txt_apk_info_all.setText(info);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (AboutUsHelper.isWifiCoon(mContext)) {
            txt_wifi_note.setVisibility(View.GONE);
        } else {
            txt_wifi_note.setVisibility(View.VISIBLE);
        }

    }

    private void initControlListener() {
        btn_update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mListener.doIgnore();
                mDia.cancel();
            }
        });
        btn_update_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mListener.doUpdate();
                mDia.cancel();
            }
        });
    }

}

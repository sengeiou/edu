package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * DownloadSuccessDubView
 *
 * @author wmma
 * @description 主要实现下载成功配音提示
 * @date 2016/9/18
 * @update 
 */


public class DownloadSuccessDubView {

    private String TAG = "DownloadSuccessDubView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private TextView txtDownloadSuccess;
    private Button btnTry;
    private ImageView imgDubClose;

    private RelativeLayout rlGuideLayout;

    private boolean created = false;
    private String mActionName = "";

    public DownloadSuccessDubView(Context context,String actionName) {
        mContext = context;
        mActionName = actionName;
        createGuideView();
    }

    private void createGuideView() {


        if(created){
            UbtLog.d(TAG, "app guide view already created!");
            return;
        }
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN ;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(120, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.view_dub_alertdialog, null);
        if (Build.VERSION.SDK_INT >= 16) {
            rlGuideLayout.setBackground(colorDrawable);
        }else{
            rlGuideLayout.setBackgroundDrawable(colorDrawable);
        }
        initGuideView(rlGuideLayout);
        setOnclick();

        mWindowManager.addView(rlGuideLayout, wmParams);
        created = true;
        recordDownloadSuccessDubState();
    }

    private void initGuideView(View view) {
        UbtLog.d(TAG,"----initGuideView----");
        txtDownloadSuccess = (TextView)view.findViewById(R.id.txt_download_success);
        btnTry = (Button)view.findViewById(R.id.btn_try);
        imgDubClose = (ImageView) view.findViewById(R.id.img_dub_close);

        txtDownloadSuccess.setText(mActionName + " " + AlphaApplication.getBaseActivity().getStringResources("ui_download_success"));
    }

    private void setOnclick(){
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDownloadSuccessDubView();
                if(AlphaApplication.getBaseActivity() instanceof MyActionsActivity){
                    // need not goto
                }else{
                    MyActionsActivity.launchActivity(mContext, 1);
                }
            }
        });

        imgDubClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDownloadSuccessDubView();
            }
        });


    }

    public void closeDownloadSuccessDubView() {
        if(mWindowManager != null){
            mWindowManager.removeView(rlGuideLayout);
            mWindowManager = null;
        }
    }

    public void recordDownloadSuccessDubState() {
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_DOWNLOAD_SUCCESS_DUB_STATE,
                "true", null, -1);
    }

    public static boolean isShowDownloadSuccessDubView(BaseActivity mActivity) {
        String dubState = BasicSharedPreferencesOperator.getInstance(mActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_DOWNLOAD_SUCCESS_DUB_STATE);
        if("true".equals(dubState)){
            return true;
        }else {
            return false;
        }
    }
}

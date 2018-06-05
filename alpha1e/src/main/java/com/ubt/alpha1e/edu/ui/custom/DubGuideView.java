package com.ubt.alpha1e.edu.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * DubGuideView
 *
 * @author wmma
 * @description 主要实现配音导航
 * @date 2016/9/18
 * @update 
 */


public class DubGuideView {

    private String TAG = "DubGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;

    private boolean created = false;

    public DubGuideView(Context context) {
        mContext = context;
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
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |WindowManager.LayoutParams.FLAG_FULLSCREEN;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(180, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_dub_guid, null);
        if (Build.VERSION.SDK_INT >= 16) {
            rlGuideLayout.setBackground(colorDrawable);
        }else{
            rlGuideLayout.setBackgroundDrawable(colorDrawable);
        }
        initGuideView(rlGuideLayout);
        setOnclick(rlGuideLayout);

        mWindowManager.addView(rlGuideLayout, wmParams);
        created = true;

    }

    private void initGuideView(View view) {
        UbtLog.d(TAG,"lihai-------initGuideView----");
    }

    private void setOnclick(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDubGuideView();
                recordDubGuideState();
            }
        });
    }

    public void closeDubGuideView() {
        if(mWindowManager != null){
            mWindowManager.removeView(rlGuideLayout);
            mWindowManager = null;
        }
    }

    public void recordDubGuideState() {
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_DUB_GUIDE_SHOW_STATE,
                "true", null, -1);
    }

    public static boolean isShowDubGuide(BaseActivity mActivity) {
        String dubState = BasicSharedPreferencesOperator.getInstance(mActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_DUB_GUIDE_SHOW_STATE);
        if("true".equals(dubState)){
            return true;
        }else {
            return false;
        }
    }



}

package com.ubt.alpha1e_edu.ui.custom;

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

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * RemoteGuideView
 *
 * @author wmma
 * @description 主要实现配音导航
 * @date 2016/9/18
 * @update 
 */


public class RemoteGuideView {

    private String TAG = "DubGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;

    private boolean created = false;
    private RelativeLayout rlLeftLayout;
    private RelativeLayout rlRightLayout;

    public RemoteGuideView(Context context) {
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
        if(AlphaApplication.isPad()){
            rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_remote_guid_pad, null);
        }else {
            rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_remote_guid, null);
        }


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
        rlLeftLayout = (RelativeLayout) view.findViewById(R.id.rl_remote_guide_left);
        rlRightLayout = (RelativeLayout) view.findViewById(R.id.rl_remote_guide_right);
    }

    private void setOnclick(View view){

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlLeftLayout.getVisibility() == View.VISIBLE){
                    rlLeftLayout.setVisibility(View.GONE);
                    rlRightLayout.setVisibility(View.VISIBLE);
                }else if(rlRightLayout.getVisibility() == View.VISIBLE){
                    rlRightLayout.setVisibility(View.GONE);
                    closeGuideView();
                    recordGuideState();
                }
            }
        });
    }

    public void closeGuideView() {
        if(mWindowManager != null){
            mWindowManager.removeView(rlGuideLayout);
            mWindowManager = null;
        }
    }

    public void recordGuideState() {
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_REMOTE_GUIDE_SHOW_STATE,
                "true", null, -1);
    }

    public static boolean isShowGuide(BaseActivity mActivity) {
        String dubState = BasicSharedPreferencesOperator.getInstance(mActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_REMOTE_GUIDE_SHOW_STATE);
        if("true".equals(dubState)){
            return true;
        }else {
            return false;
        }
    }



}

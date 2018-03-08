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
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * CommonGuideView
 *
 * @author wmma
 * @description 全局控制按钮导航
 * @date 2016/9/18
 * @update 
 */


public class CommonGuideView {

    private String TAG = "CommonGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;

    private boolean created = false;
    private RelativeLayout rlGuideReset;
    private RelativeLayout rlGuideVolume;
    private RelativeLayout rlGuideLight;
    private RelativeLayout rlGreeting;
    private RelativeLayout rlGuideProtection;

    public CommonGuideView(Context context) {
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
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_common_guid, null);


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
        rlGuideReset = (RelativeLayout) view.findViewById(R.id.rl_guide_reset);
        rlGuideVolume = (RelativeLayout) view.findViewById(R.id.rl_guide_volume);
        rlGuideLight = (RelativeLayout) view.findViewById(R.id.rl_guide_light);
        rlGreeting =  (RelativeLayout) view.findViewById(R.id.rl_guide_greet);
        rlGuideProtection = (RelativeLayout) view.findViewById(R.id.rl_guide_protection);
    }

    private void setOnclick(View view){

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlGuideReset.getVisibility() == View.VISIBLE){
                    rlGuideReset.setVisibility(View.GONE);
                    rlGuideVolume.setVisibility(View.VISIBLE);
                }else if(rlGuideVolume.getVisibility() == View.VISIBLE){
                    rlGuideVolume.setVisibility(View.GONE);
                    rlGuideLight.setVisibility(View.VISIBLE);
                }else if(rlGuideLight.getVisibility() == View.VISIBLE){
                    rlGuideLight.setVisibility(View.GONE);
                    rlGreeting.setVisibility(View.VISIBLE);
                }else if(rlGreeting.getVisibility() == View.VISIBLE){
                    rlGreeting.setVisibility(View.GONE);
                    rlGuideProtection.setVisibility(View.VISIBLE);
                }else if(rlGuideProtection.getVisibility() == View.VISIBLE){
                    rlGuideProtection.setVisibility(View.GONE);
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
        SPUtils.getInstance().put(Constant.SP_SHOW_COMMON_GUIDE, true);
    }

    public static boolean hasShowGuide() {
        return SPUtils.getInstance().getBoolean(Constant.SP_SHOW_COMMON_GUIDE,false);
    }
}

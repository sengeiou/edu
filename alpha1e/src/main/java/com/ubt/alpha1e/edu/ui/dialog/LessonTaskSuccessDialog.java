package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.LessonInfo;
import com.ubt.alpha1e.edu.data.model.LessonTaskResultInfo;
import com.ubt.alpha1e.edu.ui.custom.IOnClickListener;
import com.ubt.alpha1e.edu.utils.SizeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by liuqiang on 10/23/15.
 */
public class LessonTaskSuccessDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView tvTaskShowOff = null;
    private TextView tvTaskChallenge = null;
    private LinearLayout llTaskResult = null;
    private LinearLayout llTaskDifficulty = null;
    private LinearLayout llTaskQuality = null;
    private LinearLayout llTaskEfficiency = null;
    private TextView tvTaskName = null;
    private ImageView gitTaskSuccess = null;

    private Display display;

    private LessonInfo mLessonInfo = null;

    private int mDifficultyStarCount = 0;
    private int mEfficiencyStarCount = 0;
    private int mQualityStarCount = 0;
    private int mTaskCount = 0;

    public LessonTaskSuccessDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LessonTaskSuccessDialog builder() {
        boolean isPad = AlphaApplication.isPad();
        int layoutId = R.layout.view_lesson_task_success_dialog;
        if(isPad){
            layoutId = R.layout.view_lesson_task_success_dialog_pad;
        }

        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(layoutId, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);

        llTaskResult = (LinearLayout) view.findViewById(R.id.ll_task_result);
        llTaskDifficulty = (LinearLayout) view.findViewById(R.id.ll_task_difficulty);
        llTaskQuality = (LinearLayout) view.findViewById(R.id.ll_task_quality);
        llTaskEfficiency = (LinearLayout) view.findViewById(R.id.ll_task_efficiency);

        gitTaskSuccess = (ImageView) view.findViewById(R.id.gif_task_success);

        tvTaskShowOff = (TextView) view.findViewById(R.id.tv_task_show_off);
        tvTaskChallenge = (TextView) view.findViewById(R.id.tv_task_challenge);
        tvTaskName = (TextView) view.findViewById(R.id.tv_task_name);


        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyleFullScreen);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth()), (int) (display.getHeight())));

        if(!isPad){
            RelativeLayout.LayoutParams gifParams = (RelativeLayout.LayoutParams) gitTaskSuccess.getLayoutParams();
            RelativeLayout.LayoutParams taskNameParams = (RelativeLayout.LayoutParams) tvTaskName.getLayoutParams();
            RelativeLayout.LayoutParams taskResultParams = (RelativeLayout.LayoutParams) llTaskResult.getLayoutParams();

            gifParams.width = SizeUtils.dip2px(context,400);
            gifParams.height = SizeUtils.dip2px(context,200);
            gitTaskSuccess.setLayoutParams(gifParams);

            taskNameParams.topMargin = SizeUtils.dip2px(context,125);
            tvTaskName.setLayoutParams(taskNameParams);
            tvTaskName.setPadding(SizeUtils.dip2px(context,50),0,0,0);

            taskResultParams.topMargin = SizeUtils.dip2px(context,173);
            llTaskResult.setLayoutParams(taskResultParams);

        }else {

        }

        return this;
    }


    public LessonTaskSuccessDialog setData(LessonInfo lessonInfo ,List<LessonTaskResultInfo> resultInfoList) {
        mLessonInfo = lessonInfo;
        mDifficultyStarCount = mLessonInfo.lessonDifficulty;
        mEfficiencyStarCount = 0;
        mQualityStarCount = 0;
        //mTaskCount = resultInfoList.size();
        mTaskCount = lessonInfo.taskDown;

        for(LessonTaskResultInfo resultInfo : resultInfoList){
            if(resultInfo.efficiencyStar == 1){
                mEfficiencyStarCount++;
            }

            if(resultInfo.qualityStar == 1){
                mQualityStarCount++;
            }
        }

        return this;
    }

    public LessonTaskSuccessDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public LessonTaskSuccessDialog setShowOffButton(final IOnClickListener listener) {

        tvTaskShowOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String screenShotPath = Screenshot();

                listener.onClick(screenShotPath);
                //dialog.dismiss();
            }
        });
        return this;
    }

    public LessonTaskSuccessDialog setContinueChallengeButton(final View.OnClickListener listener) {

        tvTaskChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private String Screenshot(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String screenShotPath = FileTools.course_screenshot_cache + File.separator + sdf.format(new Date()) + ".png";

        tvTaskShowOff.setVisibility(View.INVISIBLE);
        tvTaskChallenge.setVisibility(View.INVISIBLE);

        View view = lLayout_bg.getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();

        tvTaskShowOff.setVisibility(View.VISIBLE);
        tvTaskChallenge.setVisibility(View.VISIBLE);
        if(bitmap != null){
            FileTools.writeImage(bitmap,screenShotPath,true);
            UbtLog.d("Screenshot","screenShotPath = " + screenShotPath);
            return screenShotPath;
        }
        return null;
    }


    private void setLayout() {
        int scale = (int) context.getResources().getDisplayMetrics().density;

        for(int i=0; i<mDifficultyStarCount; i++){
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.icon_stars_sel);
            llTaskDifficulty.addView(imageView);

            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            llParams.leftMargin = 10 * scale;
            imageView.setLayoutParams(llParams);
        }

        for(int i=0; i<mTaskCount; i++){
            int imageId = R.drawable.icon_stars;
            if(i < mEfficiencyStarCount){
                imageId = R.drawable.icon_stars_sel;
            }

            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageId);
            llTaskEfficiency.addView(imageView);

            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            llParams.leftMargin = 10 * scale;
            imageView.setLayoutParams(llParams);
        }

        for(int i=0; i < mTaskCount; i++){

            int imageId = R.drawable.icon_stars;
            if(i < mQualityStarCount){
                imageId = R.drawable.icon_stars_sel;
            }

            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageId);
            llTaskQuality.addView(imageView);

            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            llParams.leftMargin = 10 * scale;
            imageView.setLayoutParams(llParams);
        }

        if(mLessonInfo != null){
            tvTaskName.setText(mLessonInfo.lessonName);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void display(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }
}

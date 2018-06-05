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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.ui.helper.CourseHelper;

import java.io.File;

/**
 * Created by lihai on 5/16/17.
 */
public class LessonDetailDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;

    private ImageView ivLessonPosition;
    private ImageView ivLessonDetail;
    private TextView tvLessonDetail;
    private Display display;

    public LessonDetailDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LessonDetailDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_lesson_detail_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);

        ivLessonDetail = (ImageView) view.findViewById(R.id.iv_lesson_detail);
        ivLessonPosition = (ImageView) view.findViewById(R.id.iv_lesson_position);
        tvLessonDetail = (TextView) view.findViewById(R.id.tv_lesson_detail);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public LessonDetailDialog setMsg(String msg) {
        tvLessonDetail.setText(msg);
        return this;
    }

    public LessonDetailDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public LessonDetailDialog setPositiveButton(final View.OnClickListener listener) {
        ivLessonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public LessonDetailDialog setImage(String url) {
        File cacheFile = CourseHelper.getLocalFile(url);
        if(cacheFile.exists()){
            Bitmap bitmap = ImageTools.compressImage(cacheFile, 0, 0, false);
            ivLessonDetail.setImageBitmap(bitmap);
        }else {
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .placeholder(R.drawable.icon_lessson_detail_default)
                    .into(ivLessonDetail);
        }
        return this;
    }

    private void setLayout() {

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

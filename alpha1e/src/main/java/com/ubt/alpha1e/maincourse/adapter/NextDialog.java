package com.ubt.alpha1e.maincourse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.maincourse.courselayout.ActionCourseDataManager;

import static com.ubt.alpha1e.AlphaApplication.mContext;

/**
 * Created by liuqiang on 10/23/15.
 */
public class NextDialog {
    private Context context;
    private Dialog dialog;
    private RelativeLayout lLayout_bg;

    private Button btn_neg;
    private Button btn_pos;
    private Display display;


    public NextDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public NextDialog builder() {
        // 获取Dialog布局
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_action_course_content, null);
        lLayout_bg = (RelativeLayout) contentView.findViewById(R.id.lLayout_bg);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText("第六关 创建音频");
        btn_neg = contentView.findViewById(R.id.btn_pos);
        btn_neg.setText("下一节");
        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(6, 2));
        mrecyle.setAdapter(itemAdapter);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.NewAlertDialogStyle);
        dialog.setContentView(contentView);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.6), RelativeLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public NextDialog setPositiveButton(
                                           final View.OnClickListener listener) {

        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}

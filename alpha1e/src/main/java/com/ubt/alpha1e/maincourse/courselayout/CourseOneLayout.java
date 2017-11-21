package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import java.util.ArrayList;
import java.util.List;



/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseOneLayout extends BaseActionEditLayout {
    private String TAG = CourseOneLayout.class.getSimpleName();
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;
    private ActionOneGuideView actionGuideView;

    private RelativeLayout rlFrame;
    /**
     * 第一关卡所有课时列表
     */
    private List<ActionCourseOneContent> mContents = new ArrayList<>();


    /**
     * 当前课时
     */
    private int currentCourse = 1;


    private int currentIndex = 0;

    public CourseOneLayout(Context context) {
        super(context);
    }

    public CourseOneLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseOneLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.action_course_one;
    }

    /**
     * 设置数据
     */
    public void setData(List<ActionCourseOneContent> list, int currentCourse) {
        this.mContents = list;
        this.currentCourse = currentCourse;
        setLayoutByCurrentCourse();
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;
        UbtLog.d("", "density:" + density);

        if (actionGuideView == null) {
            //    actionGuideView = new ActionOneGuideView(mContext, null, density);
        }
    }

    /**
     * 根据当前课时显示界面
     */
    public void setLayoutByCurrentCourse() {
        if (currentCourse == 1) {
            showPop(currentIndex);
        } else if (currentCourse == 2) {

        } else if (currentCourse == 3) {

        }
    }

    @Override
    public void init(Context context) {
        super.init(context);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvCourseContent = (TextView) findViewById(R.id.tv_course_index);
        rlFrame = findViewById(R.id.ll_frame);
    }


    EasyPopup mCirclePop = null;

    /**
     * 课时显示
     * @param index
     */
    private void showPop(int index) {
        if (index >= mContents.get(0).getList().size()) {
            return;
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(0).getList().toString());
        CourseOne1Content oneContent = mContents.get(0).getList().get(index);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());

        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currentIndex++;
                        showPop(currentIndex);
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        //mCirclePop.showAtAnchorView(recyclerViewFrames, VerticalGravity.ABOVE, HorizontalGravity.ALIGN_RIGHT);
    }

    private void showPop1() {
        mCirclePop = new EasyPopup(mContext)
                .setContentView(R.layout.layout_pop_course_one)
                // .setWidth(420)
                // .setHeight(200)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                });

        mCirclePop.showAtAnchorView(ivPlay, VerticalGravity.CENTER, HorizontalGravity.RIGHT, 0, 0);
    }


}
package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;

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

    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;

    /**
     * 第一关卡所有课时列表
     */
    private List<ActionCourseOneContent> mContents = new ArrayList<>();


    /**
     * 当前课时
     */
    private int currentCourse = 1;


    private int currentIndex;

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

    }

    /**
     * 根据当前课时显示界面
     */
    public void setLayoutByCurrentCourse() {
        if (currentCourse == 1) {

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
    }

}
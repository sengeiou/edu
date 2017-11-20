package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;

/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseOneLayout extends BaseActionEditLayout {

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

    @Override
    public void init(Context context) {
        super.init(context);
    }

}
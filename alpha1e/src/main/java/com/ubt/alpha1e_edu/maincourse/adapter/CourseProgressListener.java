package com.ubt.alpha1e_edu.maincourse.adapter;

/**
 * @author：liuhai
 * @date：2017/12/27 11:01
 * @modifier：ubt
 * @modify_date：2017/12/27 11:01
 * [A brief description]
 * version
 */

public interface CourseProgressListener {
    void completeCurrentCourse(int current);

    void finishActivity();

    void completeSuccess(boolean isSuccess);
}

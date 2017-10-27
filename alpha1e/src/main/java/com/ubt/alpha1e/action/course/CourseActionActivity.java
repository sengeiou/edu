package com.ubt.alpha1e.action.course;

import android.os.Bundle;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.BaseMvpActivity;

public class CourseActionActivity extends BaseMvpActivity<CourseActionContact.CourseActionView, CourseActionPresenter> implements CourseActionContact.CourseActionView {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_action);
    }

    @Override
    protected CourseActionPresenter createPresenter() {
        mPresenter = new CourseActionPresenter();
        return mPresenter;
    }


    @Override
    public void showSuccessData(String str) {

    }

    @Override
    public void showFailedData(String str) {

    }


    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }
}

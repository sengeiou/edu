package com.ubt.alpha1e.maincourse.main;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.adapter.MainCoursedapter;
import com.ubt.alpha1e.maincourse.model.CourseModel;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.services.SyncDataService;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainCourseActivity extends MVPBaseActivity<MainCourseContract.View, MainCoursePresenter> implements MainCourseContract.View, BaseQuickAdapter.OnItemClickListener {


    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.recyleview_content)
    RecyclerView mRecyleviewContent;
    private List<CourseModel> mCourseModels;
    private MainCoursedapter mMainCoursedapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mPresenter.getCourcesData();
    }

    @OnClick(R.id.iv_main_back)
    public void onClick(View view) {
        finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }


    @Override
    protected void initUI() {
        mCourseModels = new ArrayList<>();
        mMainCoursedapter = new MainCoursedapter(R.layout.layout_main_cources, mCourseModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyleviewContent.setLayoutManager(linearLayoutManager);
        mRecyleviewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.left = 30;
            }
        });
        mRecyleviewContent.setAdapter(mMainCoursedapter);
        mMainCoursedapter.setOnItemClickListener(this);

        doSyncData();
    }

    private void doSyncData() {
        Intent mIntent = new Intent(this, SyncDataService.class);
        startService(mIntent);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_course_layout;
    }

    @Override
    public void getCourcesData(List<CourseModel> list) {
        mCourseModels.clear();
        mCourseModels.addAll(list);
        mMainCoursedapter.notifyDataSetChanged();
        UbtLog.d("MainCourse", "list==" + list.toString());
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position == 0) {

            if (isBulueToothConnected()) {
                CourseActivity.launchActivity(this);
            } else {
                ToastUtils.showShort(getStringResources("ui_action_connect_robot"));
            }

        } else if (position == 1) {
            startActivity(new Intent(this, ActionCourseActivity.class));

        } else if (position == 2) {
            startActivity(new Intent(this, BlocklyActivity.class));
        }
        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
    }
}

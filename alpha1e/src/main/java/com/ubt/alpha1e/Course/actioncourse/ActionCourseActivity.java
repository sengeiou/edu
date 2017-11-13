package com.ubt.alpha1e.Course.actioncourse;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ubt.alpha1e.Course.adapter.ActionCoursedapter;
import com.ubt.alpha1e.Course.model.ActionCourseModel;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCourseActivity extends MVPBaseActivity<ActionCourseContract.View, ActionCoursePresenter> implements ActionCourseContract.View {

    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.recyleview_content)
    RecyclerView mRecyleviewContent;
    private List<ActionCourseModel> mActionCourseModels;
    private ActionCoursedapter mMainCoursedapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mPresenter.getActionCourseData();
    }

    @Override
    protected void initUI() {
        mActionCourseModels = new ArrayList<>();
        mMainCoursedapter = new ActionCoursedapter(R.layout.layout_action_cources, mActionCourseModels);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
        mRecyleviewContent.setLayoutManager(linearLayoutManager);
        mRecyleviewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 20;
                outRect.left = 20;
                if (parent.getChildAdapterPosition(view) == 4) {
                    outRect.top = 30;
                }
            }
        });
        mRecyleviewContent.setAdapter(mMainCoursedapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_action_course_layout;
    }

    @Override
    public void setActionCourseData(List<ActionCourseModel> list) {
        mActionCourseModels.clear();
        mActionCourseModels.addAll(list);
        mMainCoursedapter.notifyDataSetChanged();
        UbtLog.d("MainCourse", "list==" + list.toString());
    }
}

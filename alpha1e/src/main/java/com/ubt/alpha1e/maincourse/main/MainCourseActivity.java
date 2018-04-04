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
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.blocklycourse.courselist.CourseListActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.adapter.MainCoursedapter;
import com.ubt.alpha1e.maincourse.model.CourseModel;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerActivity;
import com.ubt.alpha1e.services.SyncDataService;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
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

    private static MainCourseActivity mainCourseInstance = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainCourseInstance = this;
        initUI();
        mPresenter.getCourcesData();
    }

    @OnClick(R.id.iv_main_back)
    public void onClick(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    @Override
    protected void onDestroy() {
        mainCourseInstance = null;
        super.onDestroy();
    }

    public static void finishByMySelf() {
        if (mainCourseInstance != null && !mainCourseInstance.isFinishing()) {
            mainCourseInstance.finish();
        }
    }

    public static void showHabitsStartDialog() {
        if (mainCourseInstance != null) {
            new ConfirmDialog(mainCourseInstance)
                    .builder()
                    .setMsg(mainCourseInstance.getStringResources("ui_habits_process_start"))
                    .setCancelable(false)
                    .setPositiveButton(mainCourseInstance.getStringResources("ui_common_ok"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
        }
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

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        UbtLog.d("dddd", "lastclickTime===" + lastClickTime + "         ++++flag==" + flag);
        return flag;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (isFastClick()) {
            if(position==0){
                startActivity(new Intent(this, OnlineAudioPlayerActivity.class));
            }
            else if (position == 1) {

                if (isBulueToothConnected()) {
                    String progressKey = Constant.PRINCIPLE_PROGRESS + SPUtils.getInstance().getString(Constant.SP_USER_ID);
                    int progress = SPUtils.getInstance().getInt(progressKey, 0);
                    UbtLog.d("progress", "progress = " + progress);
                    if (progress == 1) {
                        SPUtils.getInstance().put(Constant.PRINCIPLE_ENTER_PROGRESS, progress);
                        SplitActivity.launchActivity(this, false);
                    } else if (progress == 2) {
                        SPUtils.getInstance().put(Constant.PRINCIPLE_ENTER_PROGRESS, progress);
                        MergeActivity.launchActivity(this, false);
                    } else if (progress == 3) {
                        SPUtils.getInstance().put(Constant.PRINCIPLE_ENTER_PROGRESS, progress);
                        FeatureActivity.launchActivity(this, false);
                    } else {
                        SPUtils.getInstance().put(Constant.PRINCIPLE_ENTER_PROGRESS, 0);
                        PrincipleActivity.launchActivity(this, false);
                    }

                } else {
                    ToastUtils.showShort(getStringResources("ui_action_connect_robot"));
                }

            } else if (position == 2) {
                startActivity(new Intent(this, ActionCourseActivity.class));

            } else if (position == 3) {
                startActivity(new Intent(this, CourseListActivity.class));
            }
            this.overridePendingTransition(R.anim.activity_open_up_down, 0);
        }
    }
}

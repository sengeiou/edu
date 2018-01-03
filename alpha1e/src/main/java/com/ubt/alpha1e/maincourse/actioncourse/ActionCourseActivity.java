package com.ubt.alpha1e.maincourse.actioncourse;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResponseMode.CourseDetailScoreModule;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.maincourse.adapter.ActionCoursedapter;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.courselayout.ActionCourseDataManager;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelFiveActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelFourActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelOneActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelSevenActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelSixActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelThreeActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelTwoActivity;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCourseActivity extends MVPBaseActivity<ActionCourseContract.View, ActionCoursePresenter> implements ActionCourseContract.View, BaseQuickAdapter.OnItemClickListener {

    private static final String TAG = ActionCourseActivity.class.getSimpleName();
    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.recyleview_content)
    RecyclerView mRecyleviewContent;
    private List<ActionCourseModel> mActionCourseModels;
    private ActionCoursedapter mMainCoursedapter;

    private static final int REQUESTCODE = 10000;
    private static ActionCourseActivity mainCourseInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainCourseInstance = this;
        initUI();
        mPresenter.getActionCourseData(this);
        LoadingDialog.show(this);
    }

    @OnClick(R.id.iv_main_back)
    public void onClick(View view) {
        finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    @Override
    protected void initUI() {
        mActionCourseModels = new ArrayList<>();
        mMainCoursedapter = new ActionCoursedapter(R.layout.layout_action_cources, mActionCourseModels);
        mMainCoursedapter.setOnItemClickListener(this);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
        mRecyleviewContent.setLayoutManager(linearLayoutManager);
        mRecyleviewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.left = 30;
                outRect.top = 50;
            }
        });

        mRecyleviewContent.setAdapter(mMainCoursedapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainCourseInstance = null;
        UbtLog.d(TAG, "------------------onDestroy-----------------");

    }

    public static void finishByMySelf() {
        if (mainCourseInstance != null && !mainCourseInstance.isFinishing()) {
            mainCourseInstance.finish();
        }
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
    protected void onPause() {
        super.onPause();
        UbtLog.d(TAG, "------------------onPause-----------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG, "------------------onResume-----------------");
    }


    /**
     * 获取关卡列表
     *
     * @param list
     */
    @Override
    public void setActionCourseData(List<ActionCourseModel> list) {
        mActionCourseModels.clear();
        mActionCourseModels.addAll(list);
        mMainCoursedapter.notifyDataSetChanged();
        mPresenter.getLastCourseProgress();

    }

    /**
     * 获取最新进度
     *
     * @param result 最新进度返回结果
     */
    @Override
    public void getLastProgressResult(boolean result) {
        mPresenter.getAllCourseScore();
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            UbtLog.d(TAG, "record===" + record.toString() + "  record.size===" + DataSupport.findAll(LocalActionRecord.class).size());
        }
    }

    /**
     * 获取关卡分数
     *
     * @param list 返回每个关卡列表
     */
    @Override
    public void getCourseScores(List<CourseDetailScoreModule> list) {
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            int course = record.getCourseLevel();
            int level = record.getPeriodLevel();//课时
            UbtLog.d(TAG, "getCourseScores==" + "course==" + course + "   leavel==" + level);

            for (int i = 0; i < course; i++) {
                mActionCourseModels.get(i).setActionLockType(1);
                mActionCourseModels.get(i).setActionCourcesScore(1);
                if (i == (course - 1)) {
                    int totalLeavel = mActionCourseModels.get(i).getSize();//总的课时数
                    if (level < totalLeavel) {
                        mActionCourseModels.get(i).setActionCourcesScore(0);
                    } else if (level == totalLeavel) {
                        mActionCourseModels.get(i + 1).setActionLockType(1);
                    }
                }
            }
        }

        mMainCoursedapter.notifyDataSetChanged();
        LoadingDialog.dismiss(this);
        if (!record.isUpload()) {
            mPresenter.saveLastProgress(String.valueOf(record.getCourseLevel()), String.valueOf(record.getPeriodLevel()));
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

        if (mActionCourseModels.get(position).getActionLockType() == 0) {
            return;
        }

        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_action_course_content, null);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText(mActionCourseModels.get(position).getTitle());
        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(this));
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseDataList(position, mActionCourseModels.get(position).getSize()));
        mrecyle.setAdapter(itemAdapter);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度

        DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentBackgroundResource(R.drawable.action_dialog_filter_rect)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_pos) {
                            int n = position + 1;
                            if (position == 0) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelOneActivity.class), REQUESTCODE);
                            } else if (position == 1) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelTwoActivity.class), REQUESTCODE);
                            } else if (position == 2) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelThreeActivity.class), REQUESTCODE);
                            }else if (position == 3) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelFourActivity.class), REQUESTCODE);
                            }else if (position == 4) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelFiveActivity.class), REQUESTCODE);
                            }else if (position ==5) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelSixActivity.class), REQUESTCODE);
                            }else if (position ==6) {
                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseLevelSevenActivity.class), REQUESTCODE);
                            }
//                            if (position == 0) {
//                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseOneActivity.class), REQUESTCODE);
//                            } else if (position == 1) {
//                                startActivityForResult(new Intent(ActionCourseActivity.this, CourseTwoActivity.class), REQUESTCODE);
//                            }
                            ActionCourseActivity.this.overridePendingTransition(R.anim.activity_open_up_down, 0);
                            dialog.dismiss();
                        }
                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    /**
     * 播放动作
     *
     * @param actionName
     */
    public void playAction(String actionName) {

        byte[] actions = BluetoothParamUtil.stringToBytes(actionName);
        ((AlphaApplication) this
                .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) this.getApplicationContext())
                .getCurrentBluetooth().getAddress(), ConstValue.DV_PLAYACTION, actions, actions.length, false);
    }

    /**
     * 播放动作
     */
    public void exitCourse() {
        UbtLog.d(TAG, "退出课程:" + 0);
        byte[] params = new byte[1];
        params[0] = 0;
        ((AlphaApplication) this
                .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) this.getApplicationContext())
                .getCurrentBluetooth().getAddress(), ConstValue.DV_ENTER_COURSE, params, params.length, false);
    }

    // 为了获取结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == 1) {
            if (requestCode == REQUESTCODE) {
                //设置结果显示框的显示数值
                int course = data.getIntExtra("course", 1);
                int leavel = data.getIntExtra("leavel", 1);
                boolean isComplete = data.getBooleanExtra("isComplete", false);
                int score = data.getIntExtra("score", 0);
                showResultDialog(course, isComplete);
                UbtLog.d(TAG, "course==" + course + "   leavel==" + leavel + "  isComplete==" + isComplete + "  socre===" + score);
                mPresenter.saveCourseProgress(String.valueOf(course), isComplete ? "1" : "0");
                playAction(Constant.COURSE_ACTION_PATH + "AE_victory editor.hts");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitCourse();
                    }
                }, 4000);
            }
        }
    }


    /**
     * 显示完成结果
     *
     * @param course
     * @param result
     */
    public void showResultDialog(final int course, boolean result) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_action_course_result, null);
        TextView tvResult = contentView.findViewById(R.id.tv_result);
        tvResult.setText(result ? "闯关成功" : "闯关失败");
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText(mActionCourseModels.get(course - 1).getTitle());
        ((ImageView) contentView.findViewById(R.id.iv_result)).setImageResource(result ? R.drawable.img_level_success : R.drawable.img_level_fail);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度

        DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentBackgroundResource(android.R.color.transparent)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_retry) {//点击确定以后刷新列表并解锁下一关
                            mActionCourseModels.get(course).setActionLockType(1);
                            mActionCourseModels.get(course - 1).setActionCourcesScore(1);
                            mMainCoursedapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                    }
                })
                .setCancelable(false)
                .create().show();
    }

}

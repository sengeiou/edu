package com.ubt.alpha1e.edu.maincourse.main;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.blocklycourse.courselist.CourseListActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.edu.course.feature.FeatureActivity;
import com.ubt.alpha1e.edu.course.merge.MergeActivity;
import com.ubt.alpha1e.edu.course.principle.PrincipleActivity;
import com.ubt.alpha1e.edu.course.split.SplitActivity;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.edu.maincourse.adapter.MainCoursedapter;
import com.ubt.alpha1e.edu.maincourse.helper.MainCourseHelper;
import com.ubt.alpha1e.edu.maincourse.model.CourseModel;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.onlineaudioplayer.categoryActivity.OnlineAudioPlayerActivity;
import com.ubt.alpha1e.edu.services.SyncDataService;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.helper.BaseHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

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

    public static boolean ADD_CONFICT_SOLUTION = false;
    private static MainCourseActivity mainCourseInstance = null;

    public int startIndex = 0;//开始索引

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainCourseInstance = this;
        initUI();
        mHelper = new MainCourseHelper(MainCourseActivity.this);
        mPresenter.getCourcesData();
        if (ADD_CONFICT_SOLUTION) {
            ((MainCourseHelper) mHelper).stopActionPlayRes();
        }

        if(SPUtils.getInstance().getBoolean(Constant.SP_EDU_MODULE, false)){
            startIndex = 1;
        }
    }

    @OnClick(R.id.iv_main_back)
    public void onClick(View view) {
        finish();
    }

    @Override
    public void finish() {
        UbtLog.d("MainCourseActivity","-----finish---");
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

        if(!SPUtils.getInstance().getBoolean(Constant.SP_EDU_MODULE, false)){
            doSyncData();
        }
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
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isNotFastClick() {
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

        if (!isNotFastClick()) {
            return;
        }

        if (!isBulueToothConnected() && position != (3 - startIndex)) {
            showBluetoothConnectDialog();
            return;
        }

        if (BaseHelper.isLowBatteryNotExecuteAction && position != (3 - startIndex)) {
            new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                    .setTitle("提示")
                    .setMsg("机器人电量低动作不能执行，请充电！")
                    .setCancelable(true)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //调到主界面
                            UbtLog.d("MainCourseActivty", "确定 ");
                        }
                    }).show();
            return;
        }

        if (position == (0 - startIndex)) {
            mHelper.checkMyRobotState();
            // startActivity(new Intent(this, OnlineAudioPlayerActivity.class));
        } else if (position == (1 - startIndex)) {
            if (isBulueToothConnected()) {
                ((MainCourseHelper) mHelper).stopOnlineRes();
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

        } else if (position == (2 - startIndex)) {
            ((MainCourseHelper) mHelper).stopOnlineRes();
            startActivity(new Intent(this, ActionCourseActivity.class));
        } else if (position == (3 - startIndex)) {
            /*((MainCourseHelper) mHelper).stopOnlineRes();
            startActivity(new Intent(this, CourseListActivity.class));*/
        }
        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
    }


    @Subscribe
    public void onEventRobot(RobotEvent event) {
        if (event.getEvent() == RobotEvent.Event.ROBOT_BIND_SUCCESS) {
            UbtLog.d("MainCourseActivity", "--BIND_SUCCESS--");
            startActivity(new Intent(this, OnlineAudioPlayerActivity.class));
        }
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙和Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d("MainCourseActivity", "去连接蓝牙 ");
                        gotoConnectBluetooth();
                    }
                }).show();
    }

    //去连接蓝牙
    void gotoConnectBluetooth() {
        boolean isfirst = SPUtils.getInstance().getBoolean("firstBluetoothConnect", true);
        Intent bluetoothConnectIntent = new Intent();
        if (isfirst) {
            UbtLog.d("MainCourse", "第一次蓝牙连接");
            SPUtils.getInstance().put("firstBluetoothConnect", false);
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothguidestartrobotActivity.class);
        } else {
            UbtLog.d("MainCourse", "非第一次蓝牙连接 ");
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothandnetconnectstateActivity.class);
        }

        startActivityForResult(bluetoothConnectIntent, 100);
        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
    }
}

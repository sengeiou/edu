package com.ubt.alpha1e_edu.maincourse.courseone;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.ResourceManager;
import com.ubt.alpha1e_edu.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.event.RobotEvent;
import com.ubt.alpha1e_edu.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e_edu.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e_edu.maincourse.courselayout.CourseLevelTwoLayout;
import com.ubt.alpha1e_edu.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e_edu.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e_edu.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e_edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e_edu.ui.helper.IEditActionUI;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseLevelTwoActivity extends MVPBaseActivity<CourseOneContract.View, CourseOnePresenter> implements CourseOneContract.View, IEditActionUI, CourseProgressListener, ActionsEditHelper.PlayCompleteListener {

    private static final String TAG = CourseLevelTwoActivity.class.getSimpleName();
     CourseLevelTwoLayout mActionEdit;
    RelativeLayout mRlInstruction;
    private TextView mTextView;


    /**
     * 当前课时
     */
    private int currentCourse;


    /**
     * 是否从总介绍播放
     */
    private boolean isAllIntroduc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new ActionsEditHelper(CourseLevelTwoActivity.this, this);
        mHelper.RegisterHelper();
        ((ActionsEditHelper) mHelper).setListener(this);
        initUI();
        if (mHelper.isStartHibitsProcess()) {
            mHelper.showStartHibitsProcess(new IDismissCallbackListener() {
                @Override
                public void onDismissCallback(Object obj) {
                    UbtLog.d("onDismissCallback", "obj = " + obj);
                    if ((boolean) obj) {
                        //行为习惯流程未结束，退出当前流程
                        finish();
                    } else {
                        //行为习惯流程结束，该干啥干啥
                        ((ActionsEditHelper) mHelper).doEnterCourse((byte) 1);
                        mActionEdit.setData(CourseLevelTwoActivity.this);

                    }
                }
            });
        } else {
            //行为习惯流程未开始，该干啥干啥
            ((ActionsEditHelper) mHelper).doEnterCourse((byte) 1);
            mActionEdit.setData(this);

        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                isAllIntroduc = true;
                mRlInstruction.setVisibility(View.GONE);
                mActionEdit.setData(CourseLevelTwoActivity.this);
            } else if (msg.what == 1112) {
                mActionEdit.playComplete();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();


    }
    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        UbtLog.d(TAG,"onEventRobot==========="+event.isHibitsProcessStatus());
        if (event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS) {
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            if (event.isHibitsProcessStatus()) {
                ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
                 Intent intent = new Intent();
                intent.putExtra("resulttype", 1);//结束类型

                setResult(1, intent);
                finish();
            }
        }else if (event.getEvent() == RobotEvent.Event.LOW_BATTERY_LESS_FIVE_PERCENT) {
            ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
            Intent intent = new Intent();
            intent.putExtra("resulttype", 2);//结束类型
            setResult(1, intent);
            finish();
        }
    }

    @Override
    protected void initUI() {
        mActionEdit = (CourseLevelTwoLayout) findViewById(R.id.action_edit);
        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText(ResourceManager.getInstance(this).getStringResources("action_course_card2_1_all"));
        mActionEdit.setUp(mHelper);

    }

    /**
     * 获取到课时列表后设置数据
     *
     * @param list
     */
    @Override
    public void getCourseOneData(List<ActionCourseOneContent> list) {

    }

    /**
     * 当前完成进度
     *
     * @param current
     */
    @Override
    public void completeCurrentCourse(int current) {
        currentCourse = current;
        mPresenter.savaCourseDataToDB(2, current);

    }


    /**
     * 响应所有R.id.iv_known的控件的点击事件
     * <p>
     * 移除高亮布局
     * </p>
     *
     * @param view
     */
    public void clickKnown(View view) {
        mActionEdit.clickKnown();
    }

    @Override
    public void finishActivity() {
        showExitDialog();
    }

    @Override
    public void completeSuccess(boolean isSuccess) {
        returnCardActivity();
        mPresenter.savaCourseDataToDB(3, 1);
    }

    /**
     * 返回关卡页面
     */
    public void returnCardActivity() {
        Intent intent = new Intent();
        intent.putExtra("course", 2);//第几关
        intent.putExtra("leavel", currentCourse);//第几个课时
        intent.putExtra("isComplete", true);
        intent.putExtra("score", 1);
        setResult(1, intent);
        finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UbtLog.d(TAG, "------------onPause___________");

    }


    //监听手机屏幕上的按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UbtLog.d("CourseOneActivity", "返回键");
            showExitDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "------------onDestroy------------");
        //((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
        mActionEdit.onPause();
        isAllIntroduc = false;
        if (mHelper!=null){
            mHelper.unRegister();
        }
    }

    private void showExitDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_comfirmdialog, null);
        TextView title = contentView.findViewById(R.id.txt_msg);
        title.setText("成功就在眼前，放弃闯关吗？");
        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("放弃闯关");
        Button button1 = contentView.findViewById(R.id.btn_neg);
        button1.setText("继续闯关");
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度
        DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                //.setContentBackgroundResource(R.drawable.action_dialog_filter_rect)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_pos) {
                            ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
                            finish();
                            //关闭窗体动画显示
                            CourseLevelTwoActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                        } else if (view.getId() == R.id.btn_pos) {

                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_action_course_level_two;
    }

    @Override
    public void onPlaying() {
        mActionEdit.onPlaying();
    }

    @Override
    public void onPausePlay() {
        mActionEdit.onPausePlay();
    }

    @Override
    public void onFinishPlay() {
        mActionEdit.onFinishPlay();
    }

    @Override
    public void onFrameDo(int index) {
        mActionEdit.onFrameDo(index);
    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClearCache() {

    }

    @Override
    public void onReadEng(byte[] eng_angle) {
        mActionEdit.onReadEng(eng_angle);
    }

    @Override
    public void onChangeActionFinish() {

    }


    @Override
    public void playComplete() {
        UbtLog.d("EditHelper", "播放完成");
        mHandler.sendEmptyMessage(1112);
//        if (isAllIntroduc) {
//            mHandler.sendEmptyMessageDelayed(1112, 2000);
//        } else {
//            mHandler.sendEmptyMessageDelayed(1111, 2000);
//        }
    }

    @Override
    public void onDisconnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UbtLog.d("onLostBtCoon", "蓝牙掉线");
                if (!isFinishing()) {
                    CourseLevelTwoActivity.this.finish();
                    //关闭窗体动画显示
                    CourseLevelTwoActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                }
            }
        });
    }

    @Override
    public void tapHead() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isHowHeadDialog) {
                    showTapHeadDialog();
                }
            }
        });
    }

    private boolean isHowHeadDialog;

    private void showTapHeadDialog() {
        isHowHeadDialog = true;
        new ConfirmDialog(this).builder()
                .setMsg(getStringResources("ui_course_principle_exit_tip"))
                .setCancelable(false)
                .setPositiveButton(getStringResources("ui_common_yes"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
                        ActionCourseActivity.finishByMySelf();
                        CourseLevelTwoActivity.this.finish();
                        CourseLevelTwoActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                        isHowHeadDialog = false;
                    }
                }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHowHeadDialog = false;
            }
        }).show();
    }

    @Override
    public void onLostBtCoon() {
        super.onLostBtCoon();
//        ToastUtils.showShort("蓝牙掉线！！");
//        finish();

    }

    private boolean isShowBleDialog;
    private void showLoasBleDiaog() {
        isShowBleDialog = true;
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接机器人蓝牙")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        Intent intent = new Intent();
                        intent.setClass(CourseLevelTwoActivity.this, BluetoothconnectActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainCourseActivity.finishByMySelf();
                ActionCourseActivity.finishByMySelf();
                CourseLevelTwoActivity.this.finish();
                //关闭窗体动画显示
                CourseLevelTwoActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
            }
        }).show();
    }
}

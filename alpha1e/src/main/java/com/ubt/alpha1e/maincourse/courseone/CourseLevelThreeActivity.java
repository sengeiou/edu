package com.ubt.alpha1e.maincourse.courseone;


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
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e.maincourse.courselayout.CourseLevelThreeLayout;
import com.ubt.alpha1e.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseLevelThreeActivity extends MVPBaseActivity<CourseOneContract.View, CourseOnePresenter> implements CourseOneContract.View, IEditActionUI, CourseProgressListener, ActionsEditHelper.PlayCompleteListener {

    private static final String TAG = CourseLevelThreeActivity.class.getSimpleName();
    BaseHelper mHelper;
    CourseLevelThreeLayout mActionEdit;

    /**
     * 当前课时
     */
    private int currentCourse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new ActionsEditHelper(CourseLevelThreeActivity.this, this);
        mHelper.RegisterHelper();
        ((ActionsEditHelper) mHelper).setListener(this);
        initUI();
        ((ActionsEditHelper) mHelper).doEnterCourse((byte) 1);
        mActionEdit.setData(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                mActionEdit.playComplete();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG, "------------onResume------");

    }

    @Override
    protected void initUI() {
        mActionEdit = (CourseLevelThreeLayout) findViewById(R.id.action_edit);
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
        mPresenter.savaCourseDataToDB(3, current);

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
        mPresenter.savaCourseDataToDB(4, 1);
    }

    /**
     * 返回关卡页面
     */
    public void returnCardActivity() {
        Intent intent = new Intent();
        intent.putExtra("course", 3);//第几关
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
        mActionEdit.onPause();
    }


    //监听手机屏幕上的按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UbtLog.d("CourseOneActivity", "返回键");
            showExitDialog();
        }
        return false;
        //return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "------------onDestroy------------");
        // ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);

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
                // .setContentBackgroundResource(R.drawable.action_dialog_filter_rect)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_pos) {
                            ((ActionsEditHelper) mHelper).doEnterCourse((byte) 0);
                            finish();
                            //关闭窗体动画显示
                            CourseLevelThreeActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
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
        return R.layout.activity_action_course_level_three;
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

        mHandler.sendEmptyMessage(1111);

    }

    @Override
    public void onDisconnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UbtLog.d("onLostBtCoon", "蓝牙掉线");
                if (!isFinishing()) {
                    CourseLevelThreeActivity.this.finish();
                    //关闭窗体动画显示
                    CourseLevelThreeActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                }
            }
        });
    }

    @Override
    public void tapHead() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()&&!isHowHeadDialog) {
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
                        CourseLevelThreeActivity.this.finish();
                        CourseLevelThreeActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                        isHowHeadDialog=false;
                    }
                }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHowHeadDialog=false;
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
                        intent.setClass(CourseLevelThreeActivity.this, BluetoothconnectActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainCourseActivity.finishByMySelf();
                ActionCourseActivity.finishByMySelf();
                CourseLevelThreeActivity.this.finish();
                //关闭窗体动画显示
                CourseLevelThreeActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
            }
        }).show();
    }
}

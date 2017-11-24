package com.ubt.alpha1e.maincourse.courseone;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.maincourse.courselayout.CourseOneLayout;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseOneActivity extends MVPBaseActivity<CourseOneContract.View, CourseOnePresenter> implements CourseOneContract.View, IEditActionUI, CourseOneLayout.CourseProgressListener, ActionsEditHelper.PlayCompleteListener {

    private static final String TAG = CourseOneActivity.class.getSimpleName();
    BaseHelper mHelper;
    CourseOneLayout mActionEdit;
    RelativeLayout mRlInstruction;
    private TextView mTextView;
    /**
     * 当前课时
     */
    private int currentCourse;


    /**
     * 当前第几个
     */
    private int currentIndex;

    List<ActionCourseOneContent> list;

    /**
     * 是否从总介绍播放
     */
    private boolean isAllIntroduc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new ActionsEditHelper(CourseOneActivity.this, this);
        mHelper.RegisterHelper();
        ((ActionsEditHelper) mHelper).setListener(this);
        initUI();
        sendStartStudy(true);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                isAllIntroduc = true;
                mRlInstruction.setVisibility(View.GONE);
                mPresenter.getCourseOneData(CourseOneActivity.this);
            } else if (msg.what == 1112) {
                mActionEdit.playComplete();
            }
        }
    };


    private boolean isFocus;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isFocus) {
            isFocus = true;
            UbtLog.d(TAG, "onWindowFocusChanged");
            boolean flag = SPUtils.getInstance().getBoolean(Constant.SP_ACTION_COURSE_CARD_ONE);
            flag = false;
            if (!flag) {
                mRlInstruction.setVisibility(View.VISIBLE);
                //SPUtils.getInstance().put(Constant.SP_ACTION_COURSE_CARD_ONE, false);
                ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "动作编辑1总介.hts");
            } else {
                isAllIntroduc = true;
                mPresenter.getCourseOneData(CourseOneActivity.this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initUI() {
        mActionEdit = (CourseOneLayout) findViewById(R.id.action_edit);
        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText(ResourceManager.getInstance(this).getStringResources("action_course_card1_1_all"));
        mActionEdit.setUp(mHelper);

    }

    /**
     * 获取到课时列表后设置数据
     *
     * @param list
     */
    @Override
    public void getCourseOneData(List<ActionCourseOneContent> list) {
        this.list = list;
        int level = 1;
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            int course = record.getCourseLevel();
            level = record.getPeriodLevel();
            if (course == 1 && level == 3) {
                level = 1;
            } else {
                level = 1;
            }
        }
        mActionEdit.setData(list, level, this);
    }

    /**
     * 当前完成进度
     *
     * @param current
     */
    @Override
    public void completeCurrentCourse(int current) {
        currentCourse = current;
        saveLastProgress(current);
        if (current == 3) {
            sendStartStudy(false);
            returnCardActivity();
        }
    }

    public void sendStartStudy(boolean isEnter) {
        //Enter the course enter
        byte[] papram = new byte[1];
        if (isEnter) {
            papram[0] = 0x01;
            mHelper.doSendComm(ConstValue.DV_ENTER_COURSE, papram);
        }//Exit the course enter
        else {
            papram[0] = 0x00;
            mHelper.doSendComm(ConstValue.DV_ENTER_COURSE, papram);
        }

    }

    /**
     * 保存进度到数据库
     *
     * @param current
     */
    private void saveLastProgress(int current) {
        UbtLog.d(TAG, "保存进度到数据库1" + current);
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            UbtLog.d(TAG, "保存进度到数据库2" + record.toString());
            int course = record.getCourseLevel();
            int level = record.getPeriodLevel();
            if (course == 1 && level < current) {
                UbtLog.d(TAG, "保存进度到数据库3" + "保存成功");
                ContentValues values = new ContentValues();
                values.put("CourseLevel", 1);
                values.put("periodLevel", current);
                values.put("isUpload", false);
                DataSupport.updateAll(LocalActionRecord.class, values);
            }
        }
    }

    @Override
    public void finishActivity() {
        finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    /**
     * 返回关卡页面
     */
    public void returnCardActivity() {
        Intent intent = new Intent();
        intent.putExtra("course", 1);//第几关
        intent.putExtra("leavel", currentCourse);//第几个课时
        intent.putExtra("isComplete", currentCourse == list.size() ? true : false);
        intent.putExtra("score", currentCourse == list.size() ? 1 : 0);
        setResult(1, intent);
        finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }


    //监听手机屏幕上的按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UbtLog.d("CourseOneActivity", "返回键");
            //如果点击的是后退键  首先判断webView是否能够后退
            //如果点击的是后退键  首先判断webView是否能够后退   返回值是boolean类型的
            finish();
            //关闭窗体动画显示
            this.overridePendingTransition(0, R.anim.activity_close_down_up);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActionEdit.dismiss();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_action_course_one;
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
        if (isAllIntroduc) {
            mHandler.sendEmptyMessageDelayed(1112,2000);
        } else {
            mHandler.sendEmptyMessageDelayed(1111, 2000);
        }
    }

    @Override
    public void onDisconnect() {
        finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    @Override
    public void onLostBtCoon() {
        super.onLostBtCoon();
        ToastUtils.showShort("蓝牙掉线！！");
        finish();

    }
}

package com.ubt.alpha1e.maincourse.courseone;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.maincourse.courselayout.CourseOneLayout;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseOneActivity extends MVPBaseActivity<CourseOneContract.View, CourseOnePresenter> implements CourseOneContract.View, IEditActionUI, CourseOneLayout.CourseProgressListener {

    BaseHelper mHelper;
    CourseOneLayout mActionEdit;
    RelativeLayout mRlInstruction;


    /**
     * 当前课时
     */
    private int currentCourse;

    /**
     * 当前第几个
     */
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new ActionsEditHelper(CourseOneActivity.this, this);
        mHelper.RegisterHelper();
        initUI();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                mRlInstruction.setVisibility(View.GONE);
                mPresenter.getCourseOneData(CourseOneActivity.this);
            }
        }
    };


    @Override
    protected void initUI() {
        mActionEdit = (CourseOneLayout) findViewById(R.id.action_edit);
        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mActionEdit.setUp(mHelper);
        boolean flag = SPUtils.getInstance().getBoolean(Constant.SP_ACTION_COURSE_CARD_ONE);
        if (!flag) {
            mRlInstruction.setVisibility(View.VISIBLE);
            SPUtils.getInstance().put(Constant.SP_ACTION_COURSE_CARD_ONE, false);
            mHandler.sendEmptyMessageDelayed(1111, 3000);
        }
    }

    /**
     * 获取到课时列表后设置数据
     *
     * @param list
     */
    @Override
    public void getCourseOneData(List<ActionCourseOneContent> list) {
        mActionEdit.setData(list, 1, this);
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


    /**
     * 当前完成进度
     *
     * @param current
     */
    @Override
    public void completeCurrentCourse(int current) {
        mPresenter.saveLastProgress("1", String.valueOf(current));
        if (current == 3) {
            mPresenter.saveCourseProgress("1", "1");
        }
    }
}

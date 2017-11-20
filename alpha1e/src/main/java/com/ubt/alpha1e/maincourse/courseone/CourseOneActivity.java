package com.ubt.alpha1e.maincourse.courseone;


import android.graphics.Bitmap;
import android.os.Bundle;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.maincourse.courselayout.CourseOneLayout;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CourseOneActivity extends BaseActivity implements IEditActionUI {

    CourseOneLayout mActionEdit;

    private BaseHelper mHelper;

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_course_one);
        mHelper = new ActionsEditHelper(this, this);
        mHelper.RegisterHelper();
        mActionEdit = (CourseOneLayout) findViewById(R.id.action_edit);
        mActionEdit.setUp(mHelper);
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable("ActionTestActivity");
        super.onResume();

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
}

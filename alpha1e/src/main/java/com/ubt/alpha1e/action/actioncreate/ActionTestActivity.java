package com.ubt.alpha1e.action.actioncreate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;


public class ActionTestActivity extends BaseActivity implements IEditActionUI, BaseActionEditLayout.OnSaveSucessListener {

    ActionEditsStandard mActionEdit;

    private BaseHelper mHelper;

    private boolean isSaveSuccess;

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
        setContentView(R.layout.activity_action_test);
        mHelper = new ActionsEditHelper(this, this);
        mHelper.RegisterHelper();
        mActionEdit = (ActionEditsStandard) findViewById(R.id.action_edit);
        mActionEdit.setUp(mHelper);
        mActionEdit.setOnSaveSucessListener(this);
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


    @Override
    public void startSave(Intent intent) {
        startActivityForResult(intent,  ActionsEditHelper.SaveActionReq);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == ActionsEditHelper.SaveActionReq) {

            if(data == null){
                return;
            }
            isSaveSuccess =(Boolean) data.getExtras().get(ActionsEditHelper.SaveActionResult);
            if(isSaveSuccess){
                ToastUtils.showShort("ui_save_action_success");
                NewActionInfo actionInfo = ((ActionsEditHelper)mHelper).getNewActionInfo();
                Intent intent = new Intent(this, SaveSuccessActivity.class);
                startActivityForResult(intent, 555);
            }

        }else if(requestCode == 555) {
            finish();
        }

    }
}

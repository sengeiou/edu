package com.ubt.alpha1e.action.actioncreate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.custom.ActionGuideView;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.utils.log.UbtLog;

import static com.ubt.alpha1e.base.Constant.SP_GUIDE_STEP;


public class ActionTestActivity extends BaseActivity implements IEditActionUI, BaseActionEditLayout.OnSaveSucessListener, ActionsEditHelper.PlayCompleteListener {

    private static String TAG = "ActionTestActivity";

    ActionEditsStandard mActionEdit;

    private BaseHelper mHelper;

    private boolean isSaveSuccess;

    private ActionGuideView actionGuideView;

    ConfirmDialog confirmDialog;

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

        ((ActionsEditHelper)mHelper).setListener(this);
        mHelper.RegisterHelper();
        mActionEdit = (ActionEditsStandard) findViewById(R.id.action_edit);
        mActionEdit.setUp(mHelper);
        mActionEdit.setOnSaveSucessListener(this);

        confirmDialog = new ConfirmDialog(ActionTestActivity.this).builder()
                .setTitle("提示")
                .setMsg(getStringResources("ui_habits_process_start"))
                .setCancelable(false)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "确定");
                        showDialog = false;
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable("ActionTestActivity");
        super.onResume();
        if(!SPUtils.getInstance().getString(SP_GUIDE_STEP).equals("12")){
            if(actionGuideView == null){
                DisplayMetrics dm = new DisplayMetrics();
                dm = getResources().getDisplayMetrics();
                float density = dm.density;
                actionGuideView = new ActionGuideView(this, null,density);
            }
        }

        UbtLog.d(TAG, "isStartHibitsProcess:"+ mHelper.isStartHibitsProcess());
        if(mHelper.isStartHibitsProcess()){
            mHelper.showStartHibitsProcess(new IDismissCallbackListener() {
                @Override
                public void onDismissCallback(Object obj) {
                    UbtLog.d(TAG,"onDismissCallback = obj == " + obj);
                    if((boolean)obj){
                        //行为习惯流程未结束，退出当前流程
                        finish();
                    }
                }
            });
        }

    }




    boolean showDialog = false;
    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS){
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            UbtLog.d(TAG, "isHibitsProcessStatus:"+ event.isHibitsProcessStatus());
            if(event.isHibitsProcessStatus() && !showDialog){
                showDialog = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  /*       new ConfirmDialog(ActionTestActivity.this).builder()
                                .setTitle("提示")
                                .setMsg(getStringResources("ui_habits_process_start"))
                                .setCancelable(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "确定");
                                        showDialog = false;
                                        finish();
                                    }
                                }).show();*/

                    if(confirmDialog != null && !confirmDialog.isShowing()){
                      confirmDialog.show();
                    }

                    }
                });
            }else{
                if(confirmDialog != null && confirmDialog.isShowing()){
                    confirmDialog.dismiss();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        UbtLog.d(TAG, "onStop");
        super.onStop();
        if(actionGuideView != null){
            actionGuideView.closeAppGuideView();
            actionGuideView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "onDestroy");
        if(mActionEdit != null){
            mActionEdit.doReset();
        }
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
            if(mHelper != null){
                ((ActionsEditHelper) mHelper).doEnterOrExitActionEdit((byte)0x04);
            }

            if(data == null){
                return;
            }
            isSaveSuccess =(Boolean) data.getExtras().get(ActionsEditHelper.SaveActionResult);
            if(isSaveSuccess){
                NewActionInfo actionInfo = ((ActionsEditHelper)mHelper).getNewActionInfo();
                Intent intent = new Intent(this, SaveSuccessActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }

    @Override
    public void playComplete() {

    }

    @Override
    public void onDisconnect() {
        UbtLog.d("ActionTest", "onDisconnect");
//        ToastUtils.showShort("蓝牙断开");
//        finish();
    }

    @Override
    public void tapHead() {

    }
}

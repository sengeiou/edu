package com.ubt.alpha1e_edu.action.actioncreate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.baoyz.pg.PG;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.AppManager;
import com.ubt.alpha1e_edu.base.SPUtils;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.data.model.NewActionInfo;
import com.ubt.alpha1e_edu.event.RobotEvent;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.custom.ActionGuideView;
import com.ubt.alpha1e_edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e_edu.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e_edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e_edu.ui.helper.BaseHelper;
import com.ubt.alpha1e_edu.ui.helper.IEditActionUI;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import static com.ubt.alpha1e_edu.base.Constant.SP_GUIDE_STEP;


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
        UbtLog.d(TAG, "stopEventSound = ");
        byte[] mCmd = {0};
        mCmd[0] = 0;
        mHelper.doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT, mCmd);
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
            if(event.isHibitsProcessStatus()){
                mActionEdit.doStopPlay();
            }
            if(event.isHibitsProcessStatus() && !showDialog){
                showDialog = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    if(confirmDialog != null && !confirmDialog.isShowing()){
                      confirmDialog.show();
                    }

                    }
                });
            }else{
                if(!event.isHibitsProcessStatus() && confirmDialog != null && confirmDialog.isShowing()){
                    UbtLog.d(TAG, "confirmDialog dismiss ");
                    confirmDialog.dismiss();
                }
            }
        }else if(event.getEvent() == RobotEvent.Event.LOW_BATTERY_LESS_FIVE_PERCENT){
            if(BaseHelper.isLowBatteryNotExecuteAction){
                new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                        .setTitle("提示")
                        .setMsg("机器人电量低动作不能执行，请充电！")
                        .setCancelable(true)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //调到主界面
                                UbtLog.d(TAG, "确定 ");
                                mActionEdit.doFinish();
                            }
                        }).show();
                return;
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

            if(data == null){
                return;
            }
            isSaveSuccess =(Boolean) data.getExtras().get(ActionsEditHelper.SaveActionResult);
            if(isSaveSuccess){
                if(mHelper != null){
                    UbtLog.d(TAG, "退出动作编辑模式");
                    ((ActionsEditHelper) mHelper).doEnterOrExitActionEdit((byte)0x04);
                }
                NewActionInfo actionInfo = ((ActionsEditHelper)mHelper).getNewActionInfo();

                Intent intent = new Intent(this, SaveSuccessActivity.class);
                intent.putExtra("NewActionInfo", PG.convertParcelable(actionInfo));
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

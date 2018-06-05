package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.business.FileSendManager;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.event.BlocklyEvent;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class WalkActionHelper extends BaseHelper implements FileSendManager.IFileSendManager{

    public static final String TAG = "WalkActionHelper";
    private ActionPlayer mPlayer;
    private List<String> mActionsNames;

    public WalkActionHelper(BaseActivity baseActivity){
        super(baseActivity);
        if (mActionsNames == null){
            mActionsNames = new ArrayList<String>();
        }
        if (!mBaseActivity.checkCoon()) {
            return;
        }
        if (mPlayer == null){
            mPlayer = ActionPlayer.getInstance(
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getBlueToothManager(),
                    ((AlphaApplication) mBaseActivity.getApplicationContext())
                            .getCurrentBluetooth().getAddress());
        }
    }

    public void doReadAction(){
        mActionsNames.clear();
        try{
//            MyActionsHelper.getDataType = MyActionsHelper.Action_type.MY_WALK;
            doSendComm(ConstValue.DV_GETACTIONFILE, FileTools.actions_walk_robot_path.getBytes("GBK"));
        } catch (Exception e) {

        }
    }

    public void sendFiles(List<String> file_names){
        String names = "";
        for (int i = 0; i < file_names.size(); i++) {
            names += file_names.get(i) + ";";
        }
        UbtLog.d(TAG, "send file");
        //log-------------------------------------------------end
        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).start().doSendWalkFile(file_names);
    }


    @Override
    public void onStartSend() {

    }

    @Override
    public void onSendProgressReport(int value) {

    }

    @Override
    public void onSendFileFinish(boolean isSuccess, FileSendManager.IFileSendManager.SEND_FAIL_TYPE type) {
//        Message msg = new Message();
//        msg.what = MSG_ON_SEND_FILE_FINIAH;
//        msg.obj = isSuccess;
//        mHandler.sendMessage(msg);
//        FileSendManager.getInstance(this, mBaseActivity.getApplicationContext()).release();
    }



    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
    }


    @Override
    public void DistoryHelper() {

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
//        UbtLog.d(TAG, "walk action onReceiveData");
        //逐个动作表文件名
        if ((cmd & 0xff) == (ConstValue.UV_GETACTIONFILE & 0xff)) {
            try {
                String name = new String(param, "GBK");
                mActionsNames.add(name);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        //动作文件读取完毕
        else if ((cmd & 0xff) == (ConstValue.UV_STOPACTIONFILE & 0xff)) {
//            mPlayer.setRobotActions(mActionsNames);
            EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_READ_WALK_FILE_FINISH, mActionsNames));
//            changeActionsLength();
//            Message msg = new Message();
//            msg.what = MSG_DO_READ_ACTIONS;
//            mHandler.sendMessage(msg);

        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {
            //TF卡状态
      /*      if (param[0] == 4) {
                // 拔出
                if (param[1] == 0) {
                    Message msg = new Message();
                    msg.what = MSG_ON_TF_PULLED;
                    mHandler.sendMessage(msg);
                }
                // 插入
                else {

                }
            }*/
        }
    }


    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }
}

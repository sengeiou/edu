package com.ubt.alpha1e.maincourse.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2017/11/20.
 */

public class MainCourseHelper extends BaseHelper {

    private static final String TAG = MainCourseHelper.class.getSimpleName();

    public MainCourseHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

    }

    public void stopOnlineRes(){
        UbtLog.d(TAG, "stopEventSound = ");
        byte[] mCmd = {0};
        mCmd[0] = 0;
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT, mCmd);
    }
    public void stopActionPlayRes(){
        UbtLog.d(TAG, "stopActionPlayRes = ");
        byte[] mCmd = {0};
        mCmd[0] = 0;
        doSendComm(ConstValue.DV_STOPPLAY, mCmd);
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG,"--onDeviceDisConnected--" + mac );
        /*PrincipleEvent event = new PrincipleEvent(PrincipleEvent.Event.DISCONNECTED);
        EventBus.getDefault().post(event);*/
        super.onDeviceDisConnected(mac);
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

    @Override
    public void DistoryHelper() {

    }

}

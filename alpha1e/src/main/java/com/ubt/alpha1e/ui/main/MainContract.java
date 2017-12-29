package com.ubt.alpha1e.ui.main;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.userinfo.model.MyRobotModel;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseView {
        void showCartoonAction(int value );

        void showBluetoothStatus(String status);

        void showCartoonText(String text);

        void onGetRobotInfo(int result, MyRobotModel model);

    }

    interface Presenter extends BasePresenter<View> {
        void requestCartoonAction(String json);

        void requestCartoonText(String text);

        void requestBluetoothStatus(String status);

        void commandRobotAction(byte cmd,byte[]params);

        void dealMessage(String json);

        void checkMyRobotState();
    }
}

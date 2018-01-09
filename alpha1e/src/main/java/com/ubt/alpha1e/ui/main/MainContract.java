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
    public interface View extends BaseView {
        void showCartoonAction(int value);

        void dealMessage(byte cmd);

        void showBatteryCapacity(boolean isCharging, int value);

        void onGetRobotInfo(int result, MyRobotModel model);

        void showGlobalButtonAnmiationEffect(boolean status);


    }

    public interface Presenter extends BasePresenter<View> {
        int[] requestCartoonAction(int value);

        String getBuddleText(int type);

        void commandRobotAction(byte cmd, byte[] params);

        void dealMessage(String json);

        void setRobotStatus(int status);

        int getRobotStatus();

        void checkMyRobotState();

        //收起全局控制按钮
        void exitGlocalControlCenter();
        //全局按钮动画通知, status=true 动作执行，有动画 status=false 动画不执行，没有动画
        void requestGlobalButtonControl(boolean status);
        //test usage
        void setView(MainContract.View view);
    }
}

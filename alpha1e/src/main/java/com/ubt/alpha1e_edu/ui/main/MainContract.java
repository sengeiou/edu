package com.ubt.alpha1e_edu.ui.main;

import android.os.Handler;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;
import com.ubt.alpha1e_edu.userinfo.model.MyRobotModel;


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
        //隐藏汽包文字
        void hiddenBuddleText();

        Handler getHandler();

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
        //循环播放正在播放的情况下，清楚播放状态
        void resetGlobalActionPlayer();
    }
}

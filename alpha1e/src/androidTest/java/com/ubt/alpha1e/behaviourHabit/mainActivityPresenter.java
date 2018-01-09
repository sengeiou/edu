package com.ubt.alpha1e.behaviourHabit;

/**
 * @作者：ubt
 * @日期: 2018/1/8 19:34
 * @描述:
 */

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.ui.main.MainContract;
import com.ubt.alpha1e.ui.main.MainPresenter;
import com.ubt.alpha1e.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class mainActivityPresenter {

    private MainContract.View mTaskView;
    private MainContract.Presenter mPresenter;
    private String TAG="mainActivityPresenter";
    @Before
    private  void  setup() throws Exception{
           mPresenter=new MainPresenter();
           mTaskView=new MainContract.View() {
               @Override
               public void showCartoonAction(int value) {

               }

               @Override
               public void dealMessage(byte cmd) {

               }

               @Override
               public void showBatteryCapacity(boolean isCharging, int value) {
                   UbtLog.d(TAG,"VALUE "+value);
               }

               @Override
               public void onGetRobotInfo(int result, MyRobotModel model) {

               }

               @Override
               public void showGlobalButtonAnmiationEffect(boolean status) {
                   assert(status=true);
               }

               @Override
               public Context getContext() {
                   return null;
               }
           };
    }
    @After
    private void tearDown()throws Exception{

    }

    @Test
    public void requestCartoonAction() throws Exception {

    }

    @Test
    public void getBuddleText() throws Exception {

    }

    @Test
    public void commandRobotAction() throws Exception {

    }

    @Test
    public void dealMessage() throws Exception {

        mTaskView.showBatteryCapacity(true,10);
        mTaskView.showBatteryCapacity(true,0);
        mTaskView.showBatteryCapacity(true,5);
    }

    @Test
    public void setRobotStatus() throws Exception {


    }

    @Test
    public void getRobotStatus() throws Exception {

    }

    @Test
    public void checkMyRobotState() throws Exception {


    }

    @Test
    public void reqeustGlobalButton()throws  Exception{

    }

    @Test
    public void exitGlocalControlCenter() throws Exception {
      mPresenter.requestGlobalButtonControl(true);

    }






}

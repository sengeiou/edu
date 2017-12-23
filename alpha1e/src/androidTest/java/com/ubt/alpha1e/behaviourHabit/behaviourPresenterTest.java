package com.ubt.alpha1e.behaviourHabit;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class behaviourPresenterTest {
    BehaviorHabitsContract.Presenter mPresenter;
   // @Mock
    private BehaviorHabitsContract.View mTaskView;
    @Before
    public void setUp()throws Exception {
       // MockitoAnnotations.initMocks(this);
        mTaskView=new BehaviorHabitsContract.View() {
            @Override
            public void onTest(boolean isSuccess) {

            }

            @Override
            public void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> modelList, String errorMsg) {
                Log.d("TEST","showBehaviourList "+modelList.details.get(0));
            }

            @Override
            public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {
                 Log.d("TEST","showBehaviourEventContent "+content.contentIds.get(2));
            }

            @Override
            public void showBehaviourPlayContent(boolean status, List<PlayContent> playList, String errorMsg) {

            }

            @Override
            public void showNetworkRequestError() {

            }

            @Override
            public Context getContext() {
                return null;
            }
        };
        mPresenter = new BehaviorHabitsPresenter();
        mPresenter.attachView(mTaskView);
    }
    @After
    public void tearDown(){

    }
    @Test
    public void testDoTest() throws Exception {
      // mPresenter.doTest();
    }

    @Test
    public void testDealayAlertTime() throws Exception {
       // mPresenter.dealayAlertTime(5);
    }

    @Test
    public void testGetBehaviourList() throws Exception {
        mPresenter.getBehaviourList("1","1");
    }

    @Test
    public void testGetBehaviourEvent() throws Exception {
        mPresenter.getBehaviourEvent("12");
    }

    @Test
    public void testGetBehaviourPlayContent() throws Exception {
        //mPresenter.getBehaviourPlayContent("1", "1");
    }

    @Test
    public void testSetBehaviourEvent() throws Exception {
        //mPresenter.setBehaviourEvent("34334",1);
    }

    @Test
    public void testSaveBehaviourEvent() throws Exception {
        HabitsEventDetail mHabitsEventDetail=new HabitsEventDetail();
        mHabitsEventDetail.setEventId("12");
        mHabitsEventDetail.setRemindOne("12:11");
        mHabitsEventDetail.setRemindTwo("12:22");
        mHabitsEventDetail.setStatus("1");
        mPresenter.saveBehaviourEvent(mHabitsEventDetail);
    }
}

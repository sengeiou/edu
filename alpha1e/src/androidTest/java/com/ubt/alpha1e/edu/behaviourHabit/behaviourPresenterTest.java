package com.ubt.alpha1e.edu.behaviourHabit;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ubt.alpha1e.edu.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.edu.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.edu.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.edu.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.edu.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.edu.behaviorhabits.model.UserScore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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
            public void showParentBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

            }

            @Override
            public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {
                 Log.d("TEST","showBehaviourEventContent "+content.contents);
            }

            @Override
            public void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg) {
                Log.d("TEST","showBehaviourPlayContent "+playList.get(0));
            }

            @Override
            public void onUserPassword(String password) {
                Log.d("TEST","onUserPassword "+password);
            }



            @Override
            public void onAlertSelectItem(int index, String language, int alertType) {

            }

            @Override
            public void onRequestStatus(int requestType, int errorCode) {
                Log.d("TEST","onRequestStatus   "+ requestType+" errorCode :"+errorCode);
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
        mPresenter.delayBehaviourEventAlert("62","5");
    }

    @Test
    public void testGetBehaviourList() throws Exception {
         mPresenter.getBehaviourList("1","1");
    }
    @Test
    public void testSetBehaviourEvent() throws Exception {
        mPresenter.enableBehaviourEvent("62",0);
    }
    @Test
    public void testGetBehaviourEvent() throws Exception {
       mPresenter.getBehaviourEvent("62");
    }

    @Test
    public void testGetBehaviourPlayContent() throws Exception {
       // mPresenter.getParentBehaviourList("1","1","1");
        mPresenter.getBehaviourPlayContent("62");
    }

    @Test
    public void testSaveBehaviourEvent() throws Exception {
        List<String> contentId=new ArrayList<>();
        contentId.add("123");
        contentId.add("456");
        EventDetail mEventDetail=new EventDetail();
        mEventDetail.remindFirst="5";
        mEventDetail.remindSecond="10";
        mEventDetail.eventId="62";
        mEventDetail.status="1";
        mEventDetail.contentIds=contentId;
        mPresenter.saveBehaviourEvent(mEventDetail,1);
    }
    @Test
    public void testGetUserPassword(){
        mPresenter.getUserPassword();
    }
}

package behaviourHabit;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;


/**
 * @作者：ubt
 * @日期: 2017/12/20 16:57
 * @描述:
 */


public class behaviourPresenterTest {
    static Boolean isUnit=false;
    @Mock
    private BehaviorHabitsContract.View mTasksView;

    BehaviorHabitsContract.Presenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPresenter=new BehaviorHabitsPresenter();
        mPresenter.attachView(mTasksView);
        isUnit=true;
    }

    public void tearDown() throws Exception {
       isUnit=false;
    }
    @Test
    public void testDoTest() throws Exception {
        mPresenter=new BehaviorHabitsPresenter();
        mPresenter.attachView(mTasksView);
    }
    @Test
    public void testDealayAlertTime() throws Exception {
        mPresenter.getBehaviourList("1","1");
    }
    @Test
    public void testGetBehaviourList() throws Exception {
        //mPresenter.getBehaviourList("1","5");
       // assert(mPresenter!=null);
    }
    @Test
    public void testGetBehaviourEvent() throws Exception {

    }
    @Test
    public void testGetBehaviourPlayContent() throws Exception {
        //mPresenter.getBehaviourPlayContent("1","6");
    }
    @Test
    public void testSetBehaviourEvent() throws Exception {
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

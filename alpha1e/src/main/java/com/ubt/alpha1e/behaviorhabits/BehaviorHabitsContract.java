package com.ubt.alpha1e.behaviorhabits;

import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BehaviorHabitsContract {
    public interface View extends BaseView {
        void onTest(boolean isSuccess);

        void showBehaviourList(List<HabitsEvent> modelList);
    }

    public interface  Presenter extends BasePresenter<View> {
        void doTest();

        void getBehaviourList(int sex, int grade);

    }
}

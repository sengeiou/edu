package com.ubt.alpha1e.behaviorhabits;

import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
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
        //显示行为列表以及分数
        void showBehaviourList(List<HabitsEvent> modelList);
        //显示编辑的EventID具体内容
        void showBehaviourEventContent(HabitsEventDetail content);
        //显示行为习惯播放内容
        void showBehaviourPlayContent(List<PlayContent> playList);
    }

    public interface  Presenter extends BasePresenter<View> {
        void doTest();
        //延时提醒时间count=-1,count=0; count=5; count=10; 对应不提醒，准时，延时5分钟，延时10分钟
        void dealayAlertTime(int count);
        //获得行为习惯列表以及分数
        void getBehaviourList(String  sex, String grade);
        //获得行为习惯某个EventId具体详情
        void getBehaviourEvent(String eventId);
        //获得行为习惯的播放内容
        void getBehaviourPlayContent(String sex,String grade);
        //开启/关闭行为习惯具体某个EventId状态，status=0 关闭，status=1 打开
        void setBehaviourEvent(String eventId, int status);
        //保存编辑的行为习惯
        void saveBehaviourEvent(HabitsEventDetail content);
    }
}

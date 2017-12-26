package com.ubt.alpha1e.behaviorhabits;

import android.content.Context;

import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
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
        void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg);
        //显示编辑的EventID具体内容
        void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg);
        //显示行为习惯播放内容
        void showBehaviourPlayContent(boolean status,List<PlayContent> playList,String errorMsg);
        void showNetworkRequestError();
        //提醒对话框点击
        void onAlertSelectItem(int index,String language,int alertType);
    }

    public interface  Presenter extends BasePresenter<View> {
        void doTest();
        //获得行为习惯列表以及分数
        void getBehaviourList(String  sex, String grade);
        //获得行为习惯某个EventId具体详情
        void getBehaviourEvent(String eventId);
        //获得行为习惯的播放内容
        void getBehaviourPlayContent(String sex,String grade);
        //开启/关闭行为习惯具体某个EventId状态，status=0 关闭，status=1 打开
        void setBehaviourEvent(String eventId, int status);
        //保存编辑的行为习惯 workday=1; holiday=2
        void saveBehaviourEvent(HabitsEventDetail content,int dayType);
        //显示提醒时间对话框
        void showAlertDialog(Context context, int currentPosition, List<String> alertList, int alertType);
        //延时提醒  delayTime: -1：关闭；0：准时；5：延迟5分钟；10：延迟10分钟
        void delayBehaviourEventAlert(String eventId, String delayTime);
    }
}

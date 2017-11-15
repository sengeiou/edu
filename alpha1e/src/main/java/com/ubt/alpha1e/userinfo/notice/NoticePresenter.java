package com.ubt.alpha1e.userinfo.notice;

import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.model.NoticeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticePresenter extends BasePresenterImpl<NoticeContract.View> implements NoticeContract.Presenter {


    @Override
    public void getNoticeData(int type) {
        if (type == 0) {
            mView.setNoticeData(null);
        } else {
            List<NoticeModel> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                NoticeModel noticeModel = new NoticeModel();
                noticeModel.setNoticeTitle("系统消息" + i);
                noticeModel.setNoticeContent("测试数据测试数据测试数据测试数据测试数据");
                list.add(noticeModel);
            }
            mView.setNoticeData(list);
        }
    }
}

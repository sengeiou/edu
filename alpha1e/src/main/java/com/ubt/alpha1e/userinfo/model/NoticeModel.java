package com.ubt.alpha1e.userinfo.model;

/**
 * @author：liuhai
 * @date：2017/11/3 14:06
 * @modifier：ubt
 * @modify_date：2017/11/3 14:06
 * [A brief description]
 * 消息实体类
 */

public class NoticeModel {
    private String noticeId;
    private String noticeTime;
    private String noticeContent;
    private String noticeTitle;

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        this.noticeTime = noticeTime;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }
}

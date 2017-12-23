package com.ubt.alpha1e.behaviorhabits.model;

/**
 * @作者：ubt
 * @日期: 2017/12/19 11:21
 * @描述:
 */


public class HabitsEventDetail {
    String eventTime;
    String eventId;
    String[] contentIds;
    String remindOne;
    String remindTwo;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }


    public String[] getContentIds() {
        return contentIds;
    }

    public void setContentIds(String[] contentIds) {
        this.contentIds = contentIds;
    }


    public String getRemindOne() {
        return remindOne;
    }

    public void setRemindOne(String remindOne) {
        this.remindOne = remindOne;
    }


    public String getRemindTwo() {
        return remindTwo;
    }

    public void setRemindTwo(String remindTwo) {
        this.remindTwo = remindTwo;
    }

}

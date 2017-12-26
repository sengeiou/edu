package com.ubt.alpha1e.behaviorhabits.model;

import java.util.List;

/**
 * @作者：ubt
 * @日期: 2017/12/19 11:21
 * @描述:
 */


public class HabitsEventDetail{
    String eventTime;
    String eventId;
    List<String> contentIds;
    String remindOne;
    String remindTwo;
    String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



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


    public List<String> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<String> contentIds) {
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

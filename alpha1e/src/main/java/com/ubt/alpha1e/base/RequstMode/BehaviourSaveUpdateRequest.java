package com.ubt.alpha1e.base.RequstMode;

import java.util.List;

/**
 * @作者：ubt
 * @日期: 2017/12/19 17:12
 * @描述:
 */


public class BehaviourSaveUpdateRequest extends BaseRequest {
    String eventId;
    String eventTime;
    List<String> contentIds;
    String remindOne;
    String remindTwo;
    String status;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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




}

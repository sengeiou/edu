package com.ubt.alpha1e.base.RequstMode;

/**
 * @作者：ubt
 * @日期: 2017/12/19 17:12
 * @描述:
 */


public class BehaviourSaveUpdateRequest extends BaseRequest {
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    String eventId;

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    String eventTime;

    public String[] getContentIds() {
        return contentIds;
    }

    public void setContentIds(String[] contentIds) {
        this.contentIds = contentIds;
    }

    String[] contentIds;

}

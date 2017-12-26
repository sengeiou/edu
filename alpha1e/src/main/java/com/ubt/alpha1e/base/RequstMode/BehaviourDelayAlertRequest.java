package com.ubt.alpha1e.base.RequstMode;

/**
 * @作者：ubt
 * @日期: 2017/12/26 15:59
 * @描述:
 */


public class BehaviourDelayAlertRequest extends  BaseRequest {

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    private String delayTime;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    private String eventId;

}

package com.ubt.alpha1e.base.RequstMode;

/**
 * @作者：ubt
 * @日期: 2017/12/19 17:03
 * @描述:
 */


public class BehaviourControlRequest extends BaseRequest {
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    String eventId;

    public String getType() {
        return type;
    }

    public void setType(String operateType) {
        this.type = operateType;
    }

    String type;

}

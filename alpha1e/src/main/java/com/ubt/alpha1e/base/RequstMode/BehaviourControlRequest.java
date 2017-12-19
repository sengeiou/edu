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

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    String operateType;

}

package com.ubt.alpha1e.base.RequstMode;

import com.sina.weibo.sdk.api.share.Base;

/**
 * @作者：ubt
 * @日期: 2017/12/19 16:50
 * @描述:
 */


public class BehaviourEventRequest extends BaseRequest {
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    String eventId;
}

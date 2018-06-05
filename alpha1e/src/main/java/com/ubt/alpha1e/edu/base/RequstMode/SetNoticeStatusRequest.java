package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author：dicy.cheng
 * @date：2017/12/22 14:03
 * @modifier：ubt
 * @modify_date：2017/12/22 14:03
 * [A brief description]
 * version
 */

public class SetNoticeStatusRequest extends BaseRequest {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SetNoticeStatusRequest{" +
                "status='" + status + '\'' +
                '}';
    }
}

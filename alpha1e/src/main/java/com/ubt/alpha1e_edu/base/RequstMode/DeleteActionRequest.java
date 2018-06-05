package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/12/28 14:24
 * @modifier：ubt
 * @modify_date：2017/12/28 14:24
 * [A brief description]
 * version
 */

public class DeleteActionRequest extends BaseRequest {
    private int actionId;

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    @Override
    public String toString() {
        return "DeleteActionRequest{" +
                "actionId=" + actionId +
                '}';
    }
}

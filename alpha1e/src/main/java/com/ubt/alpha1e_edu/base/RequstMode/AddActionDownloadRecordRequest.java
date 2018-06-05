package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/12/28 14:24
 * @modifier：ubt
 * @modify_date：2017/12/28 14:24
 * [A brief description]
 * version
 */

public class AddActionDownloadRecordRequest extends BaseRequest {
    private int actionId;

    private int postId;

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "BaseActionRequest{" +
                "actionId=" + actionId +
                ", postId=" + postId +
                '}';
    }
}

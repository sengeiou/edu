package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/13 11:04
 * @modifier：ubt
 * @modify_date：2017/11/13 11:04
 * [A brief description]
 * version
 */

public class XGGetAccessIdRequest {
    private String appId;
    private String createTime;
    private String token;
    private String userId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

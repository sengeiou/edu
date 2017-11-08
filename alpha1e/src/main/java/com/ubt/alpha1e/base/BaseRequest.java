package com.ubt.alpha1e.base;

/**
 * @author：liuhai
 * @date：2017/11/8 13:59
 * @modifier：ubt
 * @modify_date：2017/11/8 13:59
 * [A brief description]
 * version
 */

public class BaseRequest {
    private String userId;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

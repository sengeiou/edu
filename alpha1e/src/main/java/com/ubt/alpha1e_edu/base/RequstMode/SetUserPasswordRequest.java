package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class SetUserPasswordRequest extends BaseRequest {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SetUserPasswordRequest{" +
                "password='" + password + '\'' +
                '}';
    }
}

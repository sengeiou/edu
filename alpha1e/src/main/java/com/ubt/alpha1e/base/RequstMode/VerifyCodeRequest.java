package com.ubt.alpha1e.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class VerifyCodeRequest extends BaseRequest {
    private String verifyCode;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public String toString() {
        return "VerifyCodeRequest{" +
                "verifyCode='" + verifyCode + '\'' +
                '}';
    }
}

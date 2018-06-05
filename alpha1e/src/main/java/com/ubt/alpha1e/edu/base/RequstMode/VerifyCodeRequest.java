package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class VerifyCodeRequest extends BaseRequest {

    private String phone;

    private String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "VerifyCodeRequest{" +
                "phone='" + phone + '\'' +
                "code='" + code + '\'' +
                '}';
    }
}

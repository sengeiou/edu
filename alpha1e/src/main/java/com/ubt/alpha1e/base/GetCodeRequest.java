package com.ubt.alpha1e.base;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class GetCodeRequest extends BaseRequest {
    private String Phone;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    @Override
    public String toString() {
        return "GetCodeRequest{" +
                "Phone='" + Phone + '\'' +
                '}';
    }
}

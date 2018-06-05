package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class CheckUpdateRequest extends BaseRequest {

    private String version;
    private String type;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CheckUpdateRequest{" +
                "version='" + version + '\'' +
                "type='" + type + '\'' +
                '}';
    }
}

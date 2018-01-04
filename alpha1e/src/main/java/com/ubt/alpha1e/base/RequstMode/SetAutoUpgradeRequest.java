package com.ubt.alpha1e.base.RequstMode;

/**
 * @author：dicy.cheng
 * @date：2017/12/22 14:03
 * @modifier：ubt
 * @modify_date：2017/12/22 14:03
 * [A brief description]
 * version
 */

public class SetAutoUpgradeRequest extends BaseRequest {
    private String autoUpdate;
    private String systemType;

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(String autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    @Override

    public String toString() {
        return "SetUserPasswordRequest{" +
                "autoUpdate='" + autoUpdate + '\'' +
                "systemType='" + systemType + '\'' +
                '}';
    }
}

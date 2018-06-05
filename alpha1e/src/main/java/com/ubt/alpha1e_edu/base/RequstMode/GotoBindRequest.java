package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：dicy.cheng
 * @date：2017/12/22 14:03
 * @modifier：ubt
 * @modify_date：2017/12/22 14:03
 * [A brief description]
 * version
 */

public class GotoBindRequest extends BaseRequest {
    private String equipmentId;
    private String systemType;

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    @Override
    public String toString() {
        return "SetUserPasswordRequest{" +
                "equipmentId='" + equipmentId + '\'' +
                "systemType='" + systemType + '\'' +
                '}';
    }
}

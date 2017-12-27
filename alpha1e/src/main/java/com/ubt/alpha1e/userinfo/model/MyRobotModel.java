package com.ubt.alpha1e.userinfo.model;

/**
 * 我的机器人
 */
//  "equipmentId": 967402,
//          "equipmentType": 3,
//          "equipmentStatus": null,
//          "activeTime": null,
//          "equipmentXY": null,
//          "serialNumber": "27102100007",
//          "equipmentVersion": null,
//          "autoUpgrade": "1",
//          "serverVersion": "v1.2.5",
//          "systemVersion": "v1.2.5",
//          "breastplateVersion": "v1.2.5",
//          "macAddress": null,
//          "equipmentSeq": "27102100007",
//          "activeArea": null,
//          "token": null,
//          "userId": 0,
//          "equipmentUid": "27102100007"
public class MyRobotModel {
    private String autoUpgrade;
    private String equipmentSeq;
    private String equipmentVersion;
    private String serverVersion;

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(String autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public String getEquipmentSeq() {
        return equipmentSeq;
    }

    public void setEquipmentSeq(String equipmentSeq) {
        this.equipmentSeq = equipmentSeq;
    }

    public String getEquipmentVersion() {
        return equipmentVersion;
    }

    public void setEquipmentVersion(String equipmentVersion) {
        this.equipmentVersion = equipmentVersion;
    }
}

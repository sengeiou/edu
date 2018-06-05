package com.ubt.alpha1e_edu.base.ResponseMode;

/**
 * @author：liuhai
 * @date：2017/11/13 13:52
 * @modifier：ubt
 * @modify_date：2017/11/13 13:52
 * [A brief description]
 * version
 */

public class XGDeviceMode {
    private String id;
    private String appId;
    private String appName;
    private String type;
    private String device;
    private String createTime;
    private String updateTime;
    private String  accessId;
    private String accessKey;
    private String secretKey;
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "XGDeviceMode{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", type='" + type + '\'' +
                ", device='" + device + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", accessId='" + accessId + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

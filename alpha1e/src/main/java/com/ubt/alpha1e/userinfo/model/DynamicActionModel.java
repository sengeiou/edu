package com.ubt.alpha1e.userinfo.model;

/**
 * @author：liuhai
 * @date：2017/11/3 14:45
 * @modifier：ubt
 * @modify_date：2017/11/3 14:45
 * [A brief description]
 * version
 */

public class DynamicActionModel {
    private long ActionId;
    /**
     * 动作名称
     */
    private String ActionName;
    /**
     * 动作时长
     */
    private int ActionTime;
    /**
     * 动作详情
     */
    private String actionDetail;
    /**
     * 动作类型
     */
    private String ActionType;
    private String downloadUrl;//下载地址
    /**
     * 动作创建时间
     */
    private long createTime;
    /**
     * 动作是否下载
     */
    private boolean isDownload;
    /**
     * 下载进度
     */
    private double downloadProgress;
    /**
     * 动作状态
     */
    private int ActionStatu;//状态 播放1 停止0 下载2

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }


    public int getActionTime() {
        return ActionTime;
    }

    public void setActionTime(int actionTime) {
        ActionTime = actionTime;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    public int getActionStatu() {
        return ActionStatu;
    }

    public void setActionStatu(int actionStatu) {
        ActionStatu = actionStatu;
    }

    public long getActionId() {
        return ActionId;
    }

    public void setActionId(long actionId) {
        ActionId = actionId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }


    public double getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(double downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    @Override
    public String toString() {
        return "DynamicActionModel{" +
                "ActionId=" + ActionId +
                ", ActionName='" + ActionName + '\'' +
                ", ActionTime=" + ActionTime +
                ", ActionType='" + ActionType + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", createTime=" + createTime +
                ", isDownload=" + isDownload +
                ", downloadProgress=" + downloadProgress +
                ", ActionStatu=" + ActionStatu +
                '}';
    }
}

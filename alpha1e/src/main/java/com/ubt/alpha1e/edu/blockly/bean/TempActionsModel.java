package com.ubt.alpha1e.edu.blockly.bean;

import java.io.Serializable;

/**
 * @author：liuhai
 * @date：2017/11/3 14:45
 * @modifier：ubt
 * @modify_date：2017/11/3 14:45
 * [A brief description]
 * version
 */

public class TempActionsModel implements Serializable {
    private int actionId;

    /**
     * 动作名称
     */
    private String actionName;
    /**
     * 动作时长
     */
    private int actionTime;
    /**
     * 动作详情
     */
    private String actionDesciber;
    /**
     * 动作类型
     */
    private int actionType;
    /**
     * 动作下载链接
     */
    private String actionUrl;//下载地址
    /**
     * 图片链接
     */
    private String actionHeadUrl;

    /**
     * 动作创建时间
     */
    private String actionDate;
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


    private String actionOriginalId;


    private String actionUserId;


    private String actionStatus;

    private String actionImagePath;

    /**
     * 帖子ID
     */
    private int postId;

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public int getActionTime() {
        return actionTime;
    }

    public void setActionTime(int actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionDesciber() {
        return actionDesciber;
    }

    public void setActionDesciber(String actionDesciber) {
        this.actionDesciber = actionDesciber;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionHeadUrl() {
        return actionHeadUrl;
    }

    public void setActionHeadUrl(String actionHeadUrl) {
        this.actionHeadUrl = actionHeadUrl;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
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

    public int getActionStatu() {
        return ActionStatu;
    }

    public void setActionStatu(int actionStatu) {
        ActionStatu = actionStatu;
    }

    public String getActionOriginalId() {
        return actionOriginalId;
    }

    public void setActionOriginalId(String actionOriginalId) {
        this.actionOriginalId = actionOriginalId;
    }

    public String getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(String actionUserId) {
        this.actionUserId = actionUserId;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    public String getActionImagePath() {
        return actionImagePath;
    }

    public void setActionImagePath(String actionImagePath) {
        this.actionImagePath = actionImagePath;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "DynamicActionModel{" +
                "actionId=" + actionId +
                ", actionName='" + actionName + '\'' +
                ", actionTime=" + actionTime +
                ", actionDesciber='" + actionDesciber + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionUrl='" + actionUrl + '\'' +
                ", actionHeadUrl='" + actionHeadUrl + '\'' +
                ", actionDate='" + actionDate + '\'' +
                ", isDownload=" + isDownload +
                ", downloadProgress=" + downloadProgress +
                ", ActionStatu=" + ActionStatu +
                ", actionOriginalId='" + actionOriginalId + '\'' +
                ", actionUserId=" + actionUserId +
                ", actionStatus='" + actionStatus + '\'' +
                ", actionImagePath='" + actionImagePath + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }
}

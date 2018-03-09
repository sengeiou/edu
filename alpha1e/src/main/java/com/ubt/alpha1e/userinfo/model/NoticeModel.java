package com.ubt.alpha1e.userinfo.model;

/**
 * @author：liuhai
 * @date：2017/11/3 14:06
 * @modifier：ubt
 * @modify_date：2017/11/3 14:06
 * [A brief description]
 * 消息实体类
 */

public class NoticeModel {
    private int id;

    private String userId;

    private String content;

    private String imageUrl;

    private String type;

    private String status;

    private long createTime;

    private long updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "NoticeModel{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}

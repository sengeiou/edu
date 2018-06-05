package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2018/3/20 15:22
 * @modifier：ubt
 * @modify_date：2018/3/20 15:22
 * [A brief description]
 * version
 */

public class SendMessageRequest extends BaseRequest {
    private String type;
    private String content;
    private String linkUrl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}

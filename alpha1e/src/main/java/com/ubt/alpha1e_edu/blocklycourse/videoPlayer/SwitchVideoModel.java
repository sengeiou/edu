package com.ubt.alpha1e_edu.blocklycourse.videoPlayer;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class SwitchVideoModel {

    private String url;
    private String name;

    public SwitchVideoModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SwitchVideoModel{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

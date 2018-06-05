package com.ubt.alpha1e.edu.action.model;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class ActionDataModel {

    private final String TAG = "ActionDataModel";

    private String xmlRunTime;
    private String xmlFrameStatus;
    private String xmlFrameAll;
    private String xmldata;
    private String xmlAllRunTime;
    private String xmlFrameIndex;


    public String getTAG() {
        return TAG;
    }

    public String getXmlRunTime() {
        return xmlRunTime;
    }

    public void setXmlRunTime(String xmlRunTime) {
        this.xmlRunTime = xmlRunTime;
    }

    public String getXmlFrameStatus() {
        return xmlFrameStatus;
    }

    public void setXmlFrameStatus(String xmlFrameStatus) {
        this.xmlFrameStatus = xmlFrameStatus;
    }

    public String getXmlFrameAll() {
        return xmlFrameAll;
    }

    public void setXmlFrameAll(String xmlFrameAll) {
        this.xmlFrameAll = xmlFrameAll;
    }

    public String getXmldata() {
        return xmldata;
    }

    public void setXmldata(String xmldata) {
        this.xmldata = xmldata;
    }

    public String getXmlAllRunTime() {
        return xmlAllRunTime;
    }

    public void setXmlAllRunTime(String xmlAllRunTime) {
        this.xmlAllRunTime = xmlAllRunTime;
    }

    public String getXmlFrameIndex() {
        return xmlFrameIndex;
    }

    public void setXmlFrameIndex(String xmlFrameIndex) {
        this.xmlFrameIndex = xmlFrameIndex;
    }


    @Override
    public String toString() {
        return "ActionDataModel{" +
                "TAG='" + TAG + '\'' +
                ", xmlRunTime='" + xmlRunTime + '\'' +
                ", xmlFrameStatus='" + xmlFrameStatus + '\'' +
                ", xmlFrameAll='" + xmlFrameAll + '\'' +
                ", xmldata='" + xmldata + '\'' +
                ", xmlAllRunTime='" + xmlAllRunTime + '\'' +
                ", xmlFrameIndex='" + xmlFrameIndex + '\'' +
                '}';
    }
}

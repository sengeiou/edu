package com.ubt.alpha1e_edu.behaviorhabits.model;

/**
 * User : Cyan(newbeeeeeeeee@gmail.com)
 * Date : 2017/1/4
 */
public class SampleEntity {

    private boolean dragEnable;
    private boolean dropEnable;
    private PlayContentInfo playContentInfo;

    public boolean isDragEnable() {
        return dragEnable;
    }

    public void setDragEnable(boolean dragEnable) {
        this.dragEnable = dragEnable;
    }

    public boolean isDropEnable() {
        return dropEnable;
    }

    public void setDropEnable(boolean dropEnable) {
        this.dropEnable = dropEnable;
    }

    public PlayContentInfo getPlayContentInfo() {
        return playContentInfo;
    }

    public void setPlayContentInfo(PlayContentInfo playContentInfo) {
        this.playContentInfo = playContentInfo;
    }
}

package com.ubt.alpha1e.blocklycourse.model;

import com.ubt.alpha1e.base.RequstMode.BaseRequest;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class UpdateCourseRequest extends BaseRequest {

    private int currGraphProgramId;

    public int getCurrGraphProgramId() {
        return currGraphProgramId;
    }

    public void setCurrGraphProgramId(int currGraphProgramId) {
        this.currGraphProgramId = currGraphProgramId;
    }

    @Override
    public String toString() {
        return "UpdateCourseRequest{" +
                "currGraphProgramId=" + currGraphProgramId +
                '}';
    }
}

package com.ubt.alpha1e_edu.blockly;

import com.ubt.alpha1e_edu.data.model.BaseResponseModel;

import java.util.List;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyProjectDelRespond extends BaseResponseModel {

    private List<String> programIds;

    public List<String> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(List<String> programIds) {
        this.programIds = programIds;
    }

    @Override
    public String toString() {
        return "BlocklyProjectDelRequest{" +
                "programIds=" + programIds +
                '}';
    }
}

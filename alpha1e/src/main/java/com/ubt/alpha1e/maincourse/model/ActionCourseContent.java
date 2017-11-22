package com.ubt.alpha1e.maincourse.model;

import org.litepal.crud.DataSupport;

/**
 * @author：liuhai
 * @date：2017/11/17 16:22
 * @modifier：ubt
 * @modify_date：2017/11/17 16:22
 * [A brief description]
 * version
 */

public class ActionCourseContent extends DataSupport {
    /**
     * 课时索引
     */
    private int index;
    /**
     * 课时内容
     */
    private String content;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.ubt.alpha1e.maincourse.model;

/**
 * @author：liuhai
 * @date：2017/11/20 11:17
 * @modifier：ubt
 * @modify_date：2017/11/20 11:17
 * [A brief description]
 * 课时具体类
 */

public class CourseOne1Content {

    private int index;
    private String content;

    /**
     * 控件ID
     */
    private int id;

    /**
     * 方向
     */
    private int direction;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "CourseOne1Content{" +
                "index=" + index +
                ", content='" + content + '\'' +
                ", id=" + id +
                ", direction=" + direction +
                '}';
    }
}

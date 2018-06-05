package com.ubt.alpha1e_edu.event;

import com.ubt.alpha1e_edu.data.model.LessonInfo;
import com.ubt.alpha1e_edu.data.model.LessonTaskInfo;

import java.util.List;

/**
 * 类名
 *
 * @author lihai
 * @description 实现的主要功能。
 * @date
 * @update 修改者，修改日期，修改内容。
 */


public class LessonEvent {

    private Event event;
    private List<LessonInfo> lessonInfoList;
    private List<LessonTaskInfo> lessonTaskInfoList;

    public LessonEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        DO_TOKEN_ERROR,
        DO_GET_LESSON,
        DO_GET_LESSON_TASK,
        DO_DOWNLOAD_LESSON_TASK,
        DO_SYNC_TASK_DATA_FAIL,
        DO_SYNC_TASK_DATA_SUCCESS,
        DO_LOAD_SHARE_PIC_START,
        DO_LOAD_SHARE_PIC_FAIL,
        DO_LOAD_SHARE_PIC_SUCCESS,
        DO_GET_COURSE_ACCESS_TOKEN_FAIL,
        DO_GET_COURSE_ACCESS_TOKEN_SUCCESS
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<LessonInfo> getLessonInfoList() {
        return lessonInfoList;
    }

    public void setLessonInfoList(List<LessonInfo> lessonInfoList) {
        this.lessonInfoList = lessonInfoList;
    }

    public List<LessonTaskInfo> getLessonTaskInfoList() {
        return lessonTaskInfoList;
    }

    public void setLessonTaskInfoList(List<LessonTaskInfo> lessonTaskInfoList) {
        this.lessonTaskInfoList = lessonTaskInfoList;
    }
}

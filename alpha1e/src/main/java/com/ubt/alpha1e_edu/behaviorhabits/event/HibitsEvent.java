package com.ubt.alpha1e_edu.behaviorhabits.event;


import com.ubt.alpha1e_edu.behaviorhabits.model.EventPlayStatus;

/**
 * @className PrincipleEvent
 *
 * @author wmma
 * @description 机器课程原理相关事件
 * @date 2017/11/20
 * @update
 */


public class HibitsEvent {

    private Event event;
    private int status;
    private EventPlayStatus eventPlayStatus;

    public HibitsEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        CONTROL_PLAY,
        READ_EVENT_PLAY_STATUS
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public EventPlayStatus getEventPlayStatus() {
        return eventPlayStatus;
    }

    public void setEventPlayStatus(EventPlayStatus eventPlayStatus) {
        this.eventPlayStatus = eventPlayStatus;
    }
}

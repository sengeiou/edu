package com.ubt.alpha1e.course.event;


/**
 * @className PrincipleEvent
 *
 * @author wmma
 * @description 机器课程原理相关事件
 * @date 2017/11/20
 * @update
 */


public class PrincipleEvent {

    private Event event;
    private int status;
    private int infraredDistance;

    public PrincipleEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        PLAY_SOUND,
        PLAY_ACTION_FINISH,
        CALL_GET_INFRARED_DISTANCE,
        CALL_CLICK_HEAD,
        DISCONNECTED,
        VOICE_WAIT,
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

    public int getInfraredDistance() {
        return infraredDistance;
    }

    public void setInfraredDistance(int infraredDistance) {
        this.infraredDistance = infraredDistance;
    }
}

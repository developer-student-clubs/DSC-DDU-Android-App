package com.dscddu.dscddu.Model_Class;

public class EventHistoryModel {
    private String eventName;
    private boolean attended;

    public EventHistoryModel() {

    }

    public EventHistoryModel(String eventName, boolean attended) {
        this.eventName = eventName;
        this.attended = attended;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

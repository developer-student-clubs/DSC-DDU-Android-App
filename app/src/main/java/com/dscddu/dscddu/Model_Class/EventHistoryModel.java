package com.dscddu.dscddu.Model_Class;

public class EventHistoryModel {
    private String eventName;
    private boolean attended;
    private String qrString;

    public String getQrString() {
        return qrString;
    }

    public void setQrString(String qrString) {
        this.qrString = qrString;
    }

    public EventHistoryModel() {

    }

    public EventHistoryModel(String eventName, boolean attended, String qrString) {
        this.eventName = eventName;
        this.attended = attended;
        this.qrString = qrString;
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

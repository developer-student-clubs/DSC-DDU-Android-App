package com.dscddu.dscddu.Model_Class;

public class EventHistoryModel {
    private String eventName;
    private boolean attended;
    private String qrCodeString;

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    public EventHistoryModel() {

    }

    public EventHistoryModel(String eventName, boolean attended, String qrString) {
        this.eventName = eventName;
        this.attended = attended;
        this.qrCodeString = qrString;
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

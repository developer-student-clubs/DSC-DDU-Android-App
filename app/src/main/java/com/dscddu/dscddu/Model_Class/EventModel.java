package com.dscddu.dscddu.Model_Class;

public class EventModel {
    private String imageUrl, eventName;

    public EventModel(){

    }

    public EventModel(String imageUrl, String eventName) {
        this.imageUrl = imageUrl;
        this.eventName = eventName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

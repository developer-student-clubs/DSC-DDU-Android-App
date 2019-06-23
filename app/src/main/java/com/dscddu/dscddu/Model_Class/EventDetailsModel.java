package com.dscddu.dscddu.Model_Class;

public class EventDetailsModel {
    private String branch, date, description, eventName, extraInfo, imageUrl, semester, timings,
            venue, what_to_bring;

    public EventDetailsModel() {

    }

    public EventDetailsModel(String branch, String date, String description, String eventName, String extraInfo, String imageUrl, String semester, String timings, String venue, String what_to_bring) {
        this.branch = branch;
        this.date = date;
        this.description = description;
        this.eventName = eventName;
        this.extraInfo = extraInfo;
        this.imageUrl = imageUrl;
        this.semester = semester;
        this.timings = timings;
        this.venue = venue;
        this.what_to_bring = what_to_bring;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getWhat_to_bring() {
        return what_to_bring;
    }

    public void setWhat_to_bring(String what_to_bring) {
        this.what_to_bring = what_to_bring;
    }
}

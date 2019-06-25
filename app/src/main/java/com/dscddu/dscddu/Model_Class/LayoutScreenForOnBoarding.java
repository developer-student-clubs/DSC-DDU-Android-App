package com.dscddu.dscddu.Model_Class;

public class LayoutScreenForOnBoarding {

    String Title,Description;
    int screenImg;

    public LayoutScreenForOnBoarding(String title, String description, int screenImg) {
        Title = title;
        Description = description;
        this.screenImg = screenImg;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        this.screenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return screenImg;
    }
}

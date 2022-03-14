package com.example.database;

public class Badge {
    private String title="";
    private String description="";
    private String badgeURL="";
    private String titleeng;
    private String descriptioneng;
    private String condition;
    private String category;
    private String badgeID;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeURL() {
        return badgeURL;
    }

    public void setBadgeURL(String badgeURL) {
        this.badgeURL = badgeURL;
    }

    public String getTitleeng() {
        return titleeng;
    }

    public void setTitleeng(String titleeng) {
        this.titleeng = titleeng;
    }

    public String getDescriptioneng() {
        return descriptioneng;
    }

    public void setDescriptioneng(String descriptioneng) {
        this.descriptioneng = descriptioneng;
    }

    public String getBadgeID() {
        return badgeID;
    }

    public void setBadgeID(String badgeID) {
        this.badgeID = badgeID;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Badge(String title, String description, String badgeURL, String titleeng, String descriptioneng, String condition, String category, String badgeID) {
        this.title = title;
        this.description = description;
        this.badgeURL = badgeURL;
        this.titleeng = titleeng;
        this.descriptioneng = descriptioneng;
        this.condition = condition;
        this.category = category;
        this.badgeID = badgeID;
    }
}


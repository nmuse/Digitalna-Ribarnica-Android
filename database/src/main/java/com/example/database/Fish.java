package com.example.database;

public class Fish {
    private String name="";
    private String nameeng="";
    private String url="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNameeng() {
        return nameeng;
    }

    public void setNameeng(String nameeng) {
        this.nameeng = nameeng;
    }

    public Fish(String name, String nameeng, String url) {
        this.name = name;
        this.nameeng = nameeng;
        this.url = url;
    }
}

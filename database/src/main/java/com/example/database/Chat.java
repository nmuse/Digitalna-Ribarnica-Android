package com.example.database;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Chat {

    private String idKorisnika;
    private String name;
    private String lastMessage;
    private String imageurl;
    private com.google.firebase.Timestamp date;

    public Chat(String idKorisnika, String name, String lastMessage, String imageurl, Timestamp date) {
        this.idKorisnika = idKorisnika;
        this.name = name;
        this.lastMessage = lastMessage;
        this.imageurl = imageurl;
        this.date = date;
    }

    public String getIdKorisnika() {
        return idKorisnika;
    }

    public void setIdKorisnika(String idKorisnika) {
        this.idKorisnika = idKorisnika;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

}

package com.example.database;

import com.google.firebase.Timestamp;

public class Contacts {
    private String id;
    private String lastMessage;
    private Timestamp lastMessageDateTime;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getLastMessageDateTime() {
        return lastMessageDateTime;
    }

    public void setLastMessageDateTime(Timestamp lastMessageDateTime) {
        this.lastMessageDateTime = lastMessageDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Contacts(String id) {
        this.id = id;
    }
}

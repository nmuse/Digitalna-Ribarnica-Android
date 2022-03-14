package com.example.database;

import com.google.firebase.Timestamp;

public class Messages {
    private String sender;
    private String message;
    private Timestamp datetimeMessage;

    public Messages(String sender, String message, Timestamp datetimeMessage) {
        this.sender = sender;
        this.message = message;
        this.datetimeMessage = datetimeMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDatetimeMessage() {
        return datetimeMessage;
    }

    public void setDatetimeMessage(Timestamp datetimeMessage) {
        this.datetimeMessage = datetimeMessage;
    }
}

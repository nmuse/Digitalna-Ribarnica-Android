package com.example.repository.Listener;

import com.example.database.Messages;

import java.util.ArrayList;

public interface MessageCallback {
    void onCallback(ArrayList<Messages> messages);
}

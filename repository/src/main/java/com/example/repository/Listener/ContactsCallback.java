package com.example.repository.Listener;

import com.example.database.Contacts;
import com.example.database.User;

import java.util.ArrayList;

public interface ContactsCallback {
    void onCallback(ArrayList<Contacts> contacts);
}

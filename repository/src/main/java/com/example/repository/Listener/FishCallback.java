package com.example.repository.Listener;

import com.example.database.Fish;
import com.example.database.User;

import java.util.ArrayList;

public interface FishCallback {
    void onCallback(ArrayList<Fish> fishes);
}

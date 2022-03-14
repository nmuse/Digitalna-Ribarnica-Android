package com.example.repository.Listener;

import com.example.database.Fish;
import com.example.database.Location;

import java.util.ArrayList;

public interface LocationCallback {
    void onCallback(ArrayList<Location> locations);
}

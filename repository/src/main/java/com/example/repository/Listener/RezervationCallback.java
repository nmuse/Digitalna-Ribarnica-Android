package com.example.repository.Listener;

import com.example.repository.Data.ReservationsData;

import java.util.ArrayList;

public interface RezervationCallback {
    void onCallback(ArrayList<ReservationsData> rezervations);
}

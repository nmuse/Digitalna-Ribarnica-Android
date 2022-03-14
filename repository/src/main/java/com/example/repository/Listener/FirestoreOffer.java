package com.example.repository.Listener;

import com.example.repository.Data.OffersData;

import java.util.ArrayList;

public interface FirestoreOffer {
    void onCallback(ArrayList<OffersData> offersData);
}

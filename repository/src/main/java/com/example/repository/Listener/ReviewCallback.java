package com.example.repository.Listener;

import com.example.database.Review;

import java.util.ArrayList;

public interface ReviewCallback {
    void onCallback(ArrayList<Review> reviews);
}
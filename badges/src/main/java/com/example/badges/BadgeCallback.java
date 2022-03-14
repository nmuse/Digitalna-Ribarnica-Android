package com.example.badges;
import com.example.badges.BadgesData;

import java.util.ArrayList;

public interface BadgeCallback {
    void onCallback(ArrayList<BadgesData> badges);
}

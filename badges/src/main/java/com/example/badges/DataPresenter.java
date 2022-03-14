package com.example.badges;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import com.example.database.User;

public interface DataPresenter {
    void setData(User user, BadgesData badge);

    void setContexPrikazivanja(Context context);

    void izvrsiUpdateKorisnika();

    void prikaziDialogKorisniku();

}

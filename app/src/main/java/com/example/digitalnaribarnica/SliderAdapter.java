package com.example.digitalnaribarnica;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;



import java.util.Locale;


public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;


    public SliderAdapter(Context context){
        this.context=context;
    }
    public int[] slide_images={
            R.drawable.homeikona,
            R.drawable.rezervacijeikona,
            R.drawable.ponudeikona,
            R.drawable.chatikona,
            R.drawable.profilikona,
            R.drawable.badgeikona
    };

/*
    public String[] slide_headings={
            "Početna stranica",
            "Rezervacije",
            "Ponude",
            "Razgovori",
            "Profil"
    };

    public String[] slide_descriptions={
            "Postoje dvije uloge u aplikaciji; Kupac i Ponuditelj.\n"
                    +"Odabirom uloge mijenjaju se i funkcionalnosti koje možete koristiti, odnosno skrivaju nepotrebne.",

            "Kao Kupac, možete vidjeti sve trenutno rezervirane ponude i povijest rezervacija.\n"
                    +
                    "Kao ponuditelj, možete vidjeti sve trenutne zahtjeve za vaše ponude ali i sve rezervacije, odnosno zahtjeve koje ste potvrdili.\n",

            "Ponude je moguće pretražiti, filtrirati i sortirati." +
                    "Odabirom ponute otvaraju se detalji i mogućnost rezervacije." +
                    "Ukoliko ste ulozi ponuditelja, moguće je pregledati vlastite ponude i kreirati nove.",

            "Razgovarajte sa drugim korisnicima i dogovorite prodaju.",

            "Pogledajte detalje o svom profilu, uredite ga i pogledajte svoje ocjene."
    };
*/

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.homeHomeText);
            case 1:
                return context.getString(R.string.homeRezervacijeText);
            case 2:
                return context.getString(R.string.homePonudeText);
            case 3:
                return context.getString(R.string.homeChatText);
            case 4:
                return context.getString(R.string.homeProfilText);
            case 5:
                return context.getString(R.string.homeZnackeText);
        }
        return null;
    }

    public CharSequence getPageDescription(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.homeHomeDescription);
            case 1:
                return context.getString(R.string.homeRezervacijeDescription);
            case 2:
                return context.getString(R.string.homePonudeDescription);
            case 3:
                return context.getString(R.string.homeChatDescription);
            case 4:
                return context.getString(R.string.homeProfilDescription);
            case 5:
                return context.getString(R.string.homeZnackeDescription);
        }
        return null;
    }

    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView =(ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading=(TextView) view.findViewById(R.id.slide_header);
        TextView slideDescription=(TextView) view.findViewById(R.id.slide_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(getPageTitle(position));
        slideDescription.setText(getPageDescription(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}

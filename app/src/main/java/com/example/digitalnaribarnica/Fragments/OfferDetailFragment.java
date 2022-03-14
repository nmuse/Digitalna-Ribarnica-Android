package com.example.digitalnaribarnica.Fragments;

import com.example.badges.BadgeID;
import com.example.badges.BadgesData;
import com.example.database.Fish;
import com.example.database.Review;
import com.example.database.User;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Listener.ReviewCallback;
import com.google.firebase.Timestamp;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.database.FirestoreService;
import com.example.repository.Listener.FirestoreOffer;
import com.example.digitalnaribarnica.R;
import com.example.repository.Repository;
import com.example.digitalnaribarnica.databinding.FragmentOfferDetailBinding;
import com.example.repository.Data.OffersData;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OfferDetailFragment extends Fragment {

    FragmentOfferDetailBinding binding;
    //ArrayList<BadgesData> badgesList;
    private ImageView btnMinusSmall;
    private ImageView btnPlusSmall;
    private ImageView btnMinusMedium;
    private ImageView btnPlusMedium;
    private ImageView btnMinusLarge;
    private ImageView btnPlusLarge;

    private  Button btnReserve;

    private EditText smallQuantity;
    private EditText mediumQuantity;
    private EditText largeQuantity;

    private TextView availableSmall;
    private TextView availableMedium;
    private TextView availableLarge;
    private ImageView userImage;
    private ImageView fishImage;
    private ImageView trophyImage;
    private ImageView chatCloud;
    private TextView userName;
    private TextView date;
    private TextView price;
    private TextView totalPrice;
    private TextView location;
    private TextView fish;
    private TextView contactSeller;
    private ImageView chatIconOfferDetail;
    private TextView noQuantities;
    private TextView fishClassTitle;
    private LinearLayout totalCostLayout;

    private LinearLayout linearSmallFish;
    private LinearLayout linearMediumFish;
    private LinearLayout linearLargeFish;
    private View divider1;
    private View divider2;

    private String priceWithoutKn;
    private String offerID;
    private String userID = "";
    private String sellerID = "";
    private Boolean cameFromMyOffers = false;

    private RatingBar rating;

    private SharedViewModel sharedViewModel;

    public OfferDetailFragment(String offerID, String userId, Boolean myOffers){
        this.offerID = offerID;
        this.userID = userId;
        this.cameFromMyOffers = myOffers;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.offerDetails));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding= FragmentOfferDetailBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        price = binding.cijenaPonude;
        totalPrice = binding.totalPrice;
        location = binding.lokacijaPonude;
        fish = binding.nazivPonude;
        fishImage = binding.slikaRibe;
        userImage = binding.slikaPonuditelja;
        trophyImage = binding.slikaZnacke;
        userName = binding.imePrezimePonuditelja;
        btnMinusSmall = binding.btnMinusSmall;
        btnPlusSmall = binding.btnPlusSmall;
        btnMinusMedium = binding.btnMinusMedium;
        btnPlusMedium = binding.btnPlusMedium;
        btnMinusLarge = binding.btnMinusLarge;
        btnPlusLarge = binding.btnPlusLarge;

        btnReserve = binding.btnRezerviraj;
        date = binding.textDate;
        smallQuantity = binding.smallFishQuantity;
        mediumQuantity = binding.mediumFishQuantity;
        largeQuantity = binding.largeFishQuantity;

        contactSeller = binding.contactSeller;
        chatIconOfferDetail = binding.chat;

        chatCloud = binding.chat;

        smallQuantity.setText("0");
        mediumQuantity.setText("0");
        largeQuantity.setText("0");

        availableSmall = binding.availableSmall;
        availableMedium = binding.availableMedium;
        availableLarge = binding.availableLarge;


        linearSmallFish = binding.linearSmallFish;
        linearMediumFish = binding.linearMediumFish;
        linearLargeFish = binding.linearLargeFish;

        divider1 = binding.divider1;
        divider2 = binding.divider2;

        smallQuantity.setFilters(new InputFilter[] { filterDecimals });
        mediumQuantity.setFilters(new InputFilter[] { filterDecimals });
        largeQuantity.setFilters(new InputFilter[] { filterDecimals });

        noQuantities = binding.noQuantities;
        fishClassTitle = binding.fishClassTitle;
        totalCostLayout = binding.linearTotalPrice;

        rating = binding.ratingBar;

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.DohvatiPonuduPrekoID(offerID);

        sharedViewModel.offerDataArrayList.observe(this, new Observer<ArrayList<OffersData>>() {
            @Override
            public void onChanged(ArrayList<OffersData> offersData) {
                if(!Locale.getDefault().getDisplayLanguage().equals("English")){
                    fish.setText(offersData.get(0).getName());
                }else{
                    sharedViewModel.DohvatiRibuPoImenu(offersData.get(0).getName());
                    sharedViewModel.fishNameEng.observe(getActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            fish.setText(s);
                        }
                    });
                }

                location.setText(offersData.get(0).getLocation());
                String priceText = offersData.get(0).getPrice() + " " + getString(R.string.knperkg);
                price.setText(priceText);
                priceWithoutKn = offersData.get(0).getPrice();
                availableSmall.setText(offersData.get(0).getSmallFish());
                availableMedium.setText(offersData.get(0).getMediumFish());
                availableLarge.setText(offersData.get(0).getLargeFish());

                if (availableSmall.getText().toString().equals("0.0")) {
                    availableSmall.setText("0");
                }
                if (availableMedium.getText().toString().equals("0.0")) {
                    availableMedium.setText("0");
                }
                if (availableLarge.getText().toString().equals("0.0")) {
                    availableLarge.setText("0");
                }


                if (availableSmall.getText().toString().equals("0")) {
                    linearSmallFish.setVisibility(View.GONE);
                }

                if (availableMedium.getText().toString().equals("0")) {
                    linearMediumFish.setVisibility(View.GONE);
                }

                if (availableLarge.getText().toString().equals("0")) {
                    linearLargeFish.setVisibility(View.GONE);
                }

                if(availableMedium.getText().toString().equals("0") && availableSmall.getText().toString().equals("0") && availableLarge.getText().toString().equals("0")){
                    noQuantities.setVisibility(View.VISIBLE);
                    fishClassTitle.setVisibility(View.GONE);
                    totalCostLayout.setVisibility(View.GONE);
                }
                else{
                    noQuantities.setVisibility(View.GONE);
                    fishClassTitle.setVisibility(View.VISIBLE);
                    totalCostLayout.setVisibility(View.VISIBLE);
                }

                if(!availableMedium.getText().toString().equals("0") && !availableSmall.getText().toString().equals("0") && !availableLarge.getText().toString().equals("0")){
                    divider1.setVisibility(View.VISIBLE);
                    divider2.setVisibility(View.VISIBLE);
                }else if(!availableMedium.getText().toString().equals("0") && !availableSmall.getText().toString().equals("0")){
                    divider1.setVisibility(View.VISIBLE);
                }else if((!availableMedium.getText().toString().equals("0") && !availableLarge.getText().toString().equals("0")) || (!availableSmall.getText().toString().equals("0") && !availableLarge.getText().toString().equals("0"))){
                    divider2.setVisibility(View.VISIBLE);
                }
                Calendar calendar = DateParse.dateToCalendar(offersData.get(0).getDate().toDate());
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd.MM.yyyy.");
                date.setText(dateFormat.format(calendar.getTime()));

                String fishPhoto = offersData.get(0).getImageurl();
                if(fishPhoto!=""){
                    Glide.with(getContext())
                            .asBitmap()
                            .load(fishPhoto)
                            .into(fishImage);
                }

                sellerID = offersData.get(0).getIdKorisnika();

                if(!((RegisterActivity) getActivity()).buyer){
                    btnReserve.setVisibility(view.GONE);
                }

                if (userID.equals(sellerID)){
                    btnReserve.setVisibility(view.GONE);
                    chatCloud.setVisibility(View.GONE);
                    contactSeller.setVisibility(View.GONE);
                    contactSeller.setVisibility(TextView.INVISIBLE);
                    chatIconOfferDetail.setVisibility(ImageView.INVISIBLE);
                }

                String userID = offersData.get(0).getIdKorisnika();

                sharedViewModel.DohvatiKorisnikaPoID(userID);
                sharedViewModel.userMutableLiveData.observe(getActivity(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        userName.setText(user.getFullName());
                        String userPhoto = user.getPhoto();
                        if(userPhoto!=""){
                            if(getActivity() == null){
                                return;
                            }
                            Glide.with(getContext())
                                    .asBitmap()
                                    .load(userPhoto)
                                    .into(userImage);
                        }
                    }
                });

                sharedViewModel.VratiSveZnacke();
                sharedViewModel.badgesDataArrayList.observe(getActivity(), new Observer<ArrayList<BadgesData>>() {
                    @Override
                    public void onChanged(ArrayList<BadgesData> badgesList) {
                        try {
                            sharedViewModel.VratiSveIDZnackeKorisnika(userID);
                            sharedViewModel.badgesIDDataArrayList.observe(getActivity(), new Observer<ArrayList<BadgeID>>() {
                                @Override
                                public void onChanged(ArrayList<BadgeID> badgeIDS) {
                                    //if (badgeIDS.size() != 0){
                                    for (int i = 0; i < badgesList.size(); i++) {
                                        for (int j = 0; j < badgeIDS.size(); j++) {
                                            if (badgesList.get(i).getBadgeID().equals(badgeIDS.get(j).getId())) {
                                                if (badgesList.get(i).getCategory().equals("seller")) {
                                                    Glide.with(getActivity())
                                                            .asBitmap()
                                                            .load(badgesList.get(i).getBadgeURL())
                                                            .into(trophyImage);
                                                }
                                            }
                                        }
                                    }
                                    //  }
                                }
                            });
                        }catch (Exception ex){}
                    }
                });
                /*try {
                    sharedViewModel.VratiSveIDZnackeKorisnika(userID);
                    sharedViewModel.badgesIDDataArrayList.observe(getActivity(), new Observer<ArrayList<BadgeID>>() {
                        @Override
                        public void onChanged(ArrayList<BadgeID> badgeIDS) {
                            //if (badgeIDS.size() != 0){
                                for (int i = 0; i < badgesList.size(); i++) {
                                    for (int j = 0; j < badgeIDS.size(); j++) {
                                        if (badgesList.get(i).getBadgeID().equals(badgeIDS.get(j).getId())) {
                                            if (badgesList.get(i).getCategory().equals("seller")) {
                                                Glide.with(getActivity())
                                                        .asBitmap()
                                                        .load(badgesList.get(i).getBadgeURL())
                                                        .into(trophyImage);
                                            }
                                        }
                                    }
                                }
                      //  }
                        }
                    });
                }catch (Exception ex){}*/

                sharedViewModel.DohvatiOcjenePoID(userID);
                sharedViewModel.reviewDataArrayList.observe(getActivity(), new Observer<ArrayList<Review>>() {
                    @Override
                    public void onChanged(ArrayList<Review> reviews) {
                        if(reviews.size()!=0){

                            float sum = 0;
                            for (int i = 0; i < reviews.size(); i++) {
                                sum = sum + Float.parseFloat(reviews.get(i).getRating());
                            }
                            float ratingTotal = sum / reviews.size();
                            rating.setRating(ratingTotal);
                        }
                    }
                });
            }
        });


                btnPlusSmall.setOnClickListener(view1 -> {
                    btnPlusSmall.requestFocus();
                    String currentValue = smallQuantity.getText().toString();
                    if (currentValue.equals("")) {
                        currentValue = "0";
                    }
                    String availableQuantity = availableSmall.getText().toString();
                    if (Double.parseDouble(availableQuantity) > Double.parseDouble(currentValue)) {
                        smallQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) + 0.1) * 100.0) / 100.0));
                    }
                    updateTotal();
                });

        smallQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = smallQuantity.getText().toString();
                String smallAvailable= availableSmall.getText().toString();
                if(currentValue.equals(".")){
                    currentValue = "";
                    smallQuantity.setText("");
                }
                if(!currentValue.isEmpty()){
                  if(Double.parseDouble(smallAvailable) < Double.parseDouble(currentValue)){
                      StyleableToast.makeText(getActivity(), getActivity().getString(R.string.unavailable), 3, R.style.Toast).show();
                      smallQuantity.setText(smallAvailable);
                  }
               }
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                selectedFragment = new ProfileFragment(sellerID, userID, "Details", offerID);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        smallQuantity.setOnFocusChangeListener((view15, fokusiran) -> {
            if (!fokusiran) {
                if (smallQuantity.getText().length() > 1) {
                    if (smallQuantity.getText().toString().charAt(0) == '0' && smallQuantity.getText().toString().charAt(1) != '.') {
                        smallQuantity.setText(smallQuantity.getText().toString().substring(1));
                    }
                    if (smallQuantity.getText().toString().charAt(0) == '.') {
                        smallQuantity.setText(getString(R.string._0) + smallQuantity.getText().toString());
                    }
                    if(smallQuantity.getText().toString().charAt(smallQuantity.getText().toString().length() - 1) == '.'){
                        smallQuantity.setText(smallQuantity.getText().toString() + getString(R.string._0));
                    }
                }
            }
            updateTotal();
        });

        btnMinusSmall.setOnClickListener(view12 -> {
            String currentValue = smallQuantity.getText().toString();
            if(currentValue.equals("")){
                currentValue="0";
            }

            if (Double.parseDouble(currentValue) >= 0.1){
                smallQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)-0.1)*100.0)/100.0));
            } else if(Double.parseDouble(currentValue) < 0.1 && Double.parseDouble(currentValue) > 0){
                smallQuantity.setText("0");
            }
            updateTotal();
        });


        btnPlusMedium.setOnClickListener(view13 -> {
            String currentValue = mediumQuantity.getText().toString();
            if(currentValue.equals("")){
                currentValue="0";
            }
            String availableQuantity = availableMedium.getText().toString();
            if (Double.parseDouble(availableQuantity) > Double.parseDouble(currentValue)){
                mediumQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)+ 0.2)*100.0)/100.0));
            }
            updateTotal();
        });

        mediumQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = mediumQuantity.getText().toString();
                String mediumAvailable= availableMedium.getText().toString();
                if(currentValue.equals(".")){
                    currentValue = "";
                    mediumQuantity.setText("");
                }
                if(!currentValue.isEmpty()){
                    if(Double.parseDouble(mediumAvailable) < Double.parseDouble(currentValue)){
                        StyleableToast.makeText(getActivity(), getActivity().getString(R.string.unavailable), 3, R.style.Toast).show();
                        mediumQuantity.setText(mediumAvailable);
                    }
                }
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mediumQuantity.setOnFocusChangeListener((v, fokusiran) -> {
            if (!fokusiran) {
                if (mediumQuantity.getText().length() > 1) {
                    if (mediumQuantity.getText().toString().charAt(0) == '0' && mediumQuantity.getText().toString().charAt(1) != '.') {
                        mediumQuantity.setText(mediumQuantity.getText().toString().substring(1));
                    }
                    if (mediumQuantity.getText().toString().charAt(0) == '.') {
                        mediumQuantity.setText(getString(R.string._0) + mediumQuantity.getText().toString());
                    }
                    if(mediumQuantity.getText().toString().charAt(mediumQuantity.getText().toString().length() - 1) == '.'){
                        mediumQuantity.setText(mediumQuantity.getText().toString() + getString(R.string._0));
                    }
                }
            }
            updateTotal();
        });

        btnMinusMedium.setOnClickListener(view14 -> {
            String currentValue = mediumQuantity.getText().toString();
            if(currentValue.equals("")){
                currentValue="0";
            }
            if (Double.parseDouble(currentValue) >= 0.2){
                mediumQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)-0.2)*100.0)/100.0));
            }  else if(Double.parseDouble(currentValue) < 0.2 && Double.parseDouble(currentValue) > 0){
                mediumQuantity.setText("0");
            }
            updateTotal();
        });

        btnMinusLarge.setOnClickListener(v -> {
            String currentValue = largeQuantity.getText().toString();
            if(currentValue.equals("")){
                currentValue="0";
            }
            if(Double.parseDouble(currentValue) >= 0.5) {
                largeQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) - 0.5) * 100.0) / 100.0));
            }  else if(Double.parseDouble(currentValue) < 0.5 && Double.parseDouble(currentValue) > 0){
                largeQuantity.setText("0");
            }
            updateTotal();
        });

        btnPlusLarge.setOnClickListener(v -> {
            String currentValue = largeQuantity.getText().toString();
            if(currentValue.equals("")){
                currentValue="0";
            }
            String availableQuantity = availableLarge.getText().toString();
            if (Double.parseDouble(availableQuantity) > Double.parseDouble(currentValue)){
                largeQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)+ 0.5)*100.0)/100.0));
            }
            updateTotal();
        });

        largeQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = largeQuantity.getText().toString();
                String largeAvailable = availableLarge.getText().toString();
                if(currentValue.equals(".")){
                    currentValue = "";
                    largeQuantity.setText("");
                }
                if(!currentValue.isEmpty()){
                    if(Double.parseDouble(largeAvailable) < Double.parseDouble(currentValue)){
                        StyleableToast.makeText(getActivity(), getActivity().getString(R.string.unavailable), 3, R.style.Toast).show();
                        largeQuantity.setText(largeAvailable);
                    }
                }
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        largeQuantity.setOnFocusChangeListener((v, fokusiran) -> {
        if (!fokusiran) {
            if (largeQuantity.getText().length() > 1) {
                if (largeQuantity.getText().toString().charAt(0) == '0' && largeQuantity.getText().toString().charAt(1) != '.') {
                    largeQuantity.setText(largeQuantity.getText().toString().substring(1));
                }
                if (largeQuantity.getText().toString().charAt(0) == '.') {
                    largeQuantity.setText(getString(R.string._0) + largeQuantity.getText().toString());
                }
                if(largeQuantity.getText().toString().charAt(largeQuantity.getText().toString().length() - 1) == '.'){
                    largeQuantity.setText(largeQuantity.getText().toString() + getString(R.string._0));
                }
            }
        }
            updateTotal();
    });


        contactSeller.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                selectedFragment = new ConversationFragment(sellerID, userID, "Details", offerID);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        chatCloud.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View v) {
                selectedFragment = new ConversationFragment(sellerID, userID, "Details", offerID);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        btnReserve.setOnClickListener(v -> {
            smallQuantity.clearFocus();
            mediumQuantity.clearFocus();
            largeQuantity.clearFocus();
            if(smallQuantity.getText().toString().equals("0.0") || smallQuantity.getText().toString().equals("")){
                smallQuantity.setText("0");
            }
            if(mediumQuantity.getText().toString().equals("0.0") || mediumQuantity.getText().toString().equals("")){
                mediumQuantity.setText("0");
            }
            if(largeQuantity.getText().toString().equals("0.0") || largeQuantity.getText().toString().equals("")){
                largeQuantity.setText("0");
            }

            if(smallQuantity.getText().toString().equals("0") && mediumQuantity.getText().toString().equals("0") && largeQuantity.getText().toString().equals("0")){
                StyleableToast.makeText(getActivity(), getActivity().getString(R.string.quantityNotSelected), 3, R.style.Toast).show();
            }
            else {
                sharedViewModel.DodajRezervaciju(offerID, Timestamp.now(), price.getText().toString(), smallQuantity.getText().toString(),
                        mediumQuantity.getText().toString(), largeQuantity.getText().toString(), userID, "Nepotvrđeno");


                sharedViewModel.DohvatiPonuduPrekoID(offerID);
                sharedViewModel.offerDataArrayList.observe(this, new Observer<ArrayList<OffersData>>() {
                    @Override
                    public void onChanged(ArrayList<OffersData> offersData) {
                        String currentSmall = offersData.get(0).getSmallFish();
                        Double updatedSmall = Math.round((Double.parseDouble(currentSmall) - Double.parseDouble(smallQuantity.getText().toString()))*100.0)/100.0;

                        String currentMedium = offersData.get(0).getMediumFish();
                        Double updatedMedium = Math.round((Double.parseDouble(currentMedium) - Double.parseDouble(mediumQuantity.getText().toString()))*100.0)/100.0;

                        String currentLarge = offersData.get(0).getLargeFish();
                        Double updatedLarge = Math.round((Double.parseDouble(currentLarge) - Double.parseDouble(largeQuantity.getText().toString()))*100.0)/100.0;

                        if(updatedSmall < 0 || updatedMedium < 0 || updatedLarge < 0){
                            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.fishQuantityUnavailableAnymore), 3, R.style.Toast).show();

                            availableSmall.setText(offersData.get(0).getSmallFish());
                            availableMedium.setText(offersData.get(0).getMediumFish());
                            availableLarge.setText(offersData.get(0).getLargeFish());

                            smallQuantity.setText("0");
                            mediumQuantity.setText("0");
                            largeQuantity.setText("0");

                        }
                        else {
                            //  firestoreService.updateOfferQuantity(offerID, updatedSmall.toString(), updatedMedium.toString(), updatedLarge.toString(), "Offers");
                            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.reservationSuccessfullyCreated), 3, R.style.ToastGreen).show();
                            Fragment newFragment;
                            ((RegisterActivity) getActivity()).changeOnReservationsNavigationBar();
                            newFragment = new ReservationFragment(userID);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_containter, newFragment).commit();
                        }
                    }
                });
            }
        });

        return view;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        menu.findItem((R.id.action_search)).setVisible(false);
        menu.findItem(((R.id.sort_offers_menu))).setVisible(false);
        menu.findItem((R.id.language)).setVisible(false);
        menu.findItem((R.id.current_language)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(false);
        if (((RegisterActivity) getActivity()).buyer){
            menu.findItem((R.id.my_offers_menu)).setVisible(false);
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                setHasOptionsMenu(false);
                Fragment selectedFragment = null;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                if (!cameFromMyOffers){
                    selectedFragment = new SearchFragment(userID);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }else{
                    selectedFragment = new SearchFragment(userID, true, true);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }

                break;
            case R.id.all_offers_menu:
                setHasOptionsMenu(true);
                Fragment selectedFragment1 = null;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                selectedFragment1 = new SearchFragment(userID);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment1).commit();

                break;
            case R.id.my_offers_menu:
                setHasOptionsMenu(false);
                Fragment selectedFragment2 = null;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                selectedFragment2 = new SearchFragment(userID, true);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment2).commit();
                break;

            case R.id.filter_menu:
                FilterOffersFragment selectedFragment3 = new FilterOffersFragment(userID);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment3).commit();
                break;
        }

        return true;
    }

    InputFilter filterDecimals = (source, start, end, dest, dstart, dend) -> {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString());
        if (!builder.toString().matches(
                "(([0-9])([0-9]{0,"+(3 - 1)+"})?)?(\\.[0-9]{0,"+ 2 +"})?"
        )) {
            if(source.length()==0)
                return dest.subSequence(dstart, dend);
            return "";
        }
        return null;
    };

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void updateTotal(){
        Double smallQuantitySum;
        Double mediumQuantitySum;
        Double largeQuantitySum;

        if(smallQuantity.getText().toString().equals("0.0") || smallQuantity.getText().toString().equals("")){
            smallQuantitySum = 0.0;
        }
        else{
            smallQuantitySum = Double.parseDouble(smallQuantity.getText().toString());
        }
        if(mediumQuantity.getText().toString().equals("0.0") || mediumQuantity.getText().toString().equals("")){
            mediumQuantitySum = 0.0;
        }
        else{
            mediumQuantitySum = Double.parseDouble(mediumQuantity.getText().toString());
        }
        if(largeQuantity.getText().toString().equals("0.0") || largeQuantity.getText().toString().equals("")){
            largeQuantitySum = 0.0;
        }
        else{
            largeQuantitySum = Double.parseDouble(largeQuantity.getText().toString());
        }

        Double ukupnaCijena = (smallQuantitySum + mediumQuantitySum + largeQuantitySum) * Double.parseDouble(priceWithoutKn);
        totalPrice.setText(String.format("%.2f", ukupnaCijena) + " kn");
    }

}

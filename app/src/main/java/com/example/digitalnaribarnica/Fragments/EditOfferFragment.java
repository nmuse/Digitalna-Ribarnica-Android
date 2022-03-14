package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.R;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.digitalnaribarnica.databinding.FragmentEditOfferBinding;
import com.example.repository.Data.OffersData;
import com.google.firebase.Timestamp;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditOfferFragment extends Fragment {

    FragmentEditOfferBinding binding;

    private String priceWithoutKn;
    private String offerID;
    private String userID = "";
    private String sellerID = "";
    private Boolean cameFromMyOffers = false;

    private ImageView btnMinusSmall;
    private ImageView btnPlusSmall;
    private ImageView btnMinusMedium;
    private ImageView btnPlusMedium;
    private ImageView btnMinusLarge;
    private ImageView btnPlusLarge;

    private  Button btnSaveChanges;

    private EditText smallQuantity;
    private EditText mediumQuantity;
    private EditText largeQuantity;

    private TextView availableSmall;
    private TextView availableMedium;
    private TextView availableLarge;
    private ImageView userImage;
    private ImageView fishImage;
    private ImageView trophyImage;
    private TextView userName;
    private TextView date;
    private TextView price;
    private TextView totalPrice;
    private TextView location;
    private TextView fish;

    private RatingBar rating;

    private SharedViewModel sharedViewModel;

    public EditOfferFragment(){}

    public EditOfferFragment(String offerID, String userId, Boolean myOffers){
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.editOffer));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = FragmentEditOfferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        price = binding.cijenaPonude;
        totalPrice = binding.totalPrice;
        location = binding.lokacijaPonude;
        fish = binding.nazivPonude;
        fishImage = binding.slikaRibe;
        btnMinusSmall = binding.btnMinusSmall;
        btnPlusSmall = binding.btnPlusSmall;
        btnMinusMedium = binding.btnMinusMedium;
        btnPlusMedium = binding.btnPlusMedium;
        btnMinusLarge = binding.btnMinusLarge;
        btnPlusLarge = binding.btnPlusLarge;
        btnSaveChanges = binding.btnSpremiPromjene;
        date = binding.textDate;
        smallQuantity = binding.smallFishQuantity;
        mediumQuantity = binding.mediumFishQuantity;
        largeQuantity = binding.largeFishQuantity;


        availableSmall = binding.availableSmall;
        availableMedium = binding.availableMedium;
        availableLarge = binding.availableLarge;

        smallQuantity.setFilters(new InputFilter[] { filterDecimals });
        mediumQuantity.setFilters(new InputFilter[] { filterDecimals });
        largeQuantity.setFilters(new InputFilter[] { filterDecimals });

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.DohvatiPonuduPrekoID(offerID);
        sharedViewModel.offerDataArrayList.observe(this, new Observer<ArrayList<OffersData>>() {
            @Override
            public void onChanged(ArrayList<OffersData> offersData) {
                fish.setText(offersData.get(0).getName());
                location.setText(offersData.get(0).getLocation());
                String priceText = offersData.get(0).getPrice() + " " + getString(R.string.knperkg);
                price.setText(priceText);
                priceWithoutKn = offersData.get(0).getPrice();
                availableSmall.setText(offersData.get(0).getSmallFish());
                availableMedium.setText(offersData.get(0).getMediumFish());
                availableLarge.setText(offersData.get(0).getLargeFish());

                smallQuantity.setText(offersData.get(0).getSmallFish());
                mediumQuantity.setText(offersData.get(0).getMediumFish());
                largeQuantity.setText(offersData.get(0).getLargeFish());

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

            }
        });


        btnPlusSmall.setOnClickListener(view1 -> {
                    btnPlusSmall.requestFocus();
                    String currentValue = smallQuantity.getText().toString();
                    if (currentValue.equals("")) {
                        currentValue = "0";
                    }
            if (Double.parseDouble(currentValue) <999){
                smallQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)+ 0.5)*100.0)/100.0));
            }else {
                smallQuantity.setText("999");
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
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
            if (Double.parseDouble(currentValue) <999){
                mediumQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)+ 0.5)*100.0)/100.0));
            }else {
                mediumQuantity.setText("999");
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
            if (Double.parseDouble(currentValue) <999){
                largeQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue)+ 0.5)*100.0)/100.0));
            }else {
                largeQuantity.setText("999");
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

        btnSaveChanges.setOnClickListener(v -> {
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
                StyleableToast.makeText(getActivity(), getActivity().getString(R.string.enterQuantityFirst), 3, R.style.Toast).show();
            }
            else {
                sharedViewModel.AzurirajDostupneKolicinePonude(offerID, smallQuantity.getText().toString(), mediumQuantity.getText().toString(),
                        largeQuantity.getText().toString(), "Offers");
                sharedViewModel.AzurirajStatusPonude(offerID, "Aktivna");
                StyleableToast.makeText(getActivity(), getActivity().getString(R.string.offerSuccessfullyUpdated), 3, R.style.ToastGreen).show();
                Fragment newFragment;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                newFragment = new SearchFragment(userID, true, true);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter, newFragment).commit();
            }
        });

        return view;
    }


    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        menu.findItem((R.id.action_search)).setVisible(false);
        menu.findItem((R.id.filter_menu)).setVisible(false);
        menu.findItem((R.id.all_offers_menu)).setVisible(false);
        menu.findItem((R.id.my_offers_menu)).setVisible(false);
        menu.findItem((R.id.sort_offers_menu)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(false);
        menu.findItem((R.id.my_offers_menu)).setVisible(false);
        menu.findItem((R.id.language)).setVisible(false);
        menu.findItem((R.id.current_language)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(false);
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
                setHasOptionsMenu(false);
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


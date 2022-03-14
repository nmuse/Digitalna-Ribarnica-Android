package com.example.digitalnaribarnica.Fragments;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.database.Fish;
import com.example.database.Location;
import com.example.digitalnaribarnica.R;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.repository.Repository;
import com.example.digitalnaribarnica.databinding.FragmentAddOfferBinding;
import com.google.firebase.Timestamp;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.Locale;


public class AddOfferFragment extends Fragment {
    FragmentAddOfferBinding binding;

    private Button btnSaveNewOffer;
    private ImageView btnMinusSmall;
    private ImageView btnPlusSmall;
    private ImageView btnMinusMedium;
    private ImageView btnPlusMedium;
    private ImageView btnMinusLarge;
    private ImageView btnPlusLarge;
    private AutoCompleteTextView fishSpecies;
    private AutoCompleteTextView location;
    private EditText price;
    private EditText smallQuantity;
    private EditText mediumQuantity;
    private EditText largeQuantity;
    private CheckBox checkSmall;
    private CheckBox checkMedium;
    private CheckBox checkLarge;
    private String userId = "";
    private SharedViewModel sharedViewModel;
    public AddOfferFragment() {}
    public AddOfferFragment(String userId) {
        this.userId = userId;
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.newOffer));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = FragmentAddOfferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        btnSaveNewOffer = binding.btnAdd;
        price = binding.priceOffer;

        btnMinusSmall = binding.btnMinusSmall;
        btnPlusSmall = binding.btnPlusSmall;
        btnMinusMedium = binding.btnMinusMedium;
        btnPlusMedium = binding.btnPlusMedium;
        btnMinusLarge = binding.btnMinusLarge;
        btnPlusLarge = binding.btnPlusLarge;

        btnMinusSmall.setEnabled(false);
        btnPlusSmall.setEnabled(false);
        btnMinusMedium.setEnabled(false);
        btnPlusMedium.setEnabled(false);
        btnMinusLarge.setEnabled(false);
        btnPlusLarge.setEnabled(false);

        smallQuantity = binding.smallFishQuantity;
        mediumQuantity = binding.mediumFishQuantity;
        largeQuantity = binding.largeFishQuantity;

        smallQuantity.setText("0");
        mediumQuantity.setText("0");
        largeQuantity.setText("0");

        smallQuantity.setEnabled(false);
        mediumQuantity.setEnabled(false);
        largeQuantity.setEnabled(false);

        checkSmall = binding.radioSmall;
        checkMedium = binding.radioMedium;
        checkLarge = binding.radioLarge;

        smallQuantity.setFilters(new InputFilter[]{filterDecimals});
        mediumQuantity.setFilters(new InputFilter[]{filterDecimals});
        largeQuantity.setFilters(new InputFilter[]{filterDecimals});

        price.setFilters(new InputFilter[]{filterDecimals});

        btnPlusSmall.setOnClickListener(view1 -> {
            String currentValue = smallQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            smallQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) + 0.1) * 100.0) / 100.0));
        });

        btnMinusSmall.setOnClickListener(view12 -> {
            String currentValue = smallQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            if (Double.parseDouble(currentValue) >= 0.1) {
                smallQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) - 0.1) * 100.0) / 100.0));
            } else if (Double.parseDouble(currentValue) < 0.1 && Double.parseDouble(currentValue) > 0) {
                smallQuantity.setText("0");
            }
        });

        smallQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = smallQuantity.getText().toString();
                if (currentValue.equals(".")) {
                    currentValue = "";
                    smallQuantity.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        smallQuantity.setOnFocusChangeListener((v, fokusiran) -> {
            if (!fokusiran) {
                if (smallQuantity.getText().length() > 1) {
                    if (smallQuantity.getText().toString().charAt(0) == '0' && smallQuantity.getText().toString().charAt(1) != '.') {
                        smallQuantity.setText(smallQuantity.getText().toString().substring(1));
                    }
                    if (smallQuantity.getText().toString().charAt(0) == '.') {
                        smallQuantity.setText(getString(R.string._0) + smallQuantity.getText().toString());
                    }
                    if (smallQuantity.getText().toString().charAt(smallQuantity.getText().toString().length() - 1) == '.') {
                        smallQuantity.setText(smallQuantity.getText().toString() + getString(R.string._0));
                    }
                }
            }
        });

        btnPlusMedium.setOnClickListener(view13 -> {
            String currentValue = mediumQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            mediumQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) + 0.2) * 100.0) / 100.0));
        });

        btnMinusMedium.setOnClickListener(view14 -> {
            String currentValue = mediumQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            if (Double.parseDouble(currentValue) >= 0.2) {
                mediumQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) - 0.2) * 100.0) / 100.0));
            } else if (Double.parseDouble(currentValue) < 0.2 && Double.parseDouble(currentValue) > 0) {
                mediumQuantity.setText("0");
            }
        });

        mediumQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = mediumQuantity.getText().toString();
                if (currentValue.equals(".")) {
                    currentValue = "";
                    mediumQuantity.setText("");
                }
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
                    if (mediumQuantity.getText().toString().charAt(mediumQuantity.getText().toString().length() - 1) == '.') {
                        mediumQuantity.setText(mediumQuantity.getText().toString() + getString(R.string._0));
                    }
                }
            }
        });

        btnMinusLarge.setOnClickListener(v -> {
            String currentValue = largeQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            if (Double.parseDouble(currentValue) >= 0.5) {
                largeQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) - 0.5) * 100.0) / 100.0));
            } else if (Double.parseDouble(currentValue) < 0.5 && Double.parseDouble(currentValue) > 0) {
                largeQuantity.setText("0");
            }
        });

        btnPlusLarge.setOnClickListener(v -> {
            String currentValue = largeQuantity.getText().toString();
            if (currentValue.equals("")) {
                currentValue = "0";
            }
            largeQuantity.setText(String.valueOf(Math.round((Double.parseDouble(currentValue) + 0.5) * 100.0) / 100.0));
        });

        largeQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentValue = largeQuantity.getText().toString();
                if (currentValue.equals(".")) {
                    currentValue = "";
                    largeQuantity.setText("");
                }
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
                    if (largeQuantity.getText().toString().charAt(largeQuantity.getText().toString().length() - 1) == '.') {
                        largeQuantity.setText(largeQuantity.getText().toString() + getString(R.string._0));
                    }
                }
            }
        });

         checkSmall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             if(!checkSmall.isChecked()){
                 smallQuantity.setText("0");
                 smallQuantity.setEnabled(false);
                 btnMinusSmall.setEnabled(false);
                 btnPlusSmall.setEnabled(false);
                 btnMinusSmall.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
                 btnPlusSmall.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
             }
             else {
                 smallQuantity.setEnabled(true);
                 btnMinusSmall.setEnabled(true);
                 btnPlusSmall.setEnabled(true);
                 btnMinusSmall.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                 btnPlusSmall.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
             }
         }
        });

        checkMedium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!checkMedium.isChecked()){
                    mediumQuantity.setText("0");
                    mediumQuantity.setEnabled(false);
                    btnMinusMedium.setEnabled(false);
                    btnPlusMedium.setEnabled(false);
                    btnMinusMedium.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
                    btnPlusMedium.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
                }
                else {
                    mediumQuantity.setEnabled(true);
                    btnMinusMedium.setEnabled(true);
                    btnPlusMedium.setEnabled(true);
                    btnMinusMedium.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                    btnPlusMedium.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                }
            }
        });

        checkLarge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!checkLarge.isChecked()){
                    largeQuantity.setText("0");
                    largeQuantity.setEnabled(false);
                    btnMinusLarge.setEnabled(false);
                    btnPlusLarge.setEnabled(false);
                    btnMinusLarge.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
                    btnPlusLarge.setColorFilter(getActivity().getResources().getColor(R.color.colorGrayBlue));
                }
                else {
                    largeQuantity.setEnabled(true);
                    btnMinusLarge.setEnabled(true);
                    btnPlusLarge.setEnabled(true);
                    btnMinusLarge.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                    btnPlusLarge.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                }
            }
        });

        fishSpecies = binding.autoFishSpecies;
        location = binding.autoLocation;

        sharedViewModel.DohvatiRibe();
        sharedViewModel.fishDataArrayList.observe(this, new Observer<ArrayList<Fish>>() {
            @Override
            public void onChanged(ArrayList<Fish> fishs) {
                ArrayList<String> fishArrayList = new ArrayList<>();
                for (Fish fish : fishs) {
                    if(!Locale.getDefault().getDisplayLanguage().equals("English")){
                        fishArrayList.add(fish.getName());
                    }else{
                        fishArrayList.add(fish.getNameeng());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_custom, R.id.autocomplete_text, fishArrayList);
                adapter.notifyDataSetChanged();
                fishSpecies.setAdapter(adapter);
            }
        });

        sharedViewModel.DohvatiLokacije();
        sharedViewModel.locationDataArrayList.observe(this, new Observer<ArrayList<Location>>() {
            @Override
            public void onChanged(ArrayList<Location> locations) {
                ArrayList<String> locationArrayList = new ArrayList<>();
                for (Location location : locations) {
                    locationArrayList.add(location.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_custom, R.id.autocomplete_text, locationArrayList);
                adapter.notifyDataSetChanged();
                location.setAdapter(adapter);
            }
        });

        price.setOnFocusChangeListener((v, hasFocus) -> {
            if (price.getText().length() > 1) {
                if (price.getText().toString().charAt(0) == '0' && price.getText().toString().charAt(1) != '.') {
                    price.setText(price.getText().toString().substring(1));
                }
                if (price.getText().toString().charAt(0) == '.') {
                    price.setText(getString(R.string._0) + price.getText().toString());
                }
                if (price.getText().toString().charAt(price.getText().toString().length() - 1) == '.') {
                    price.setText(price.getText().toString() + getString(R.string._0));
                }
            }
            if (price.getText().toString().equals(".")) {
                price.setText("0");
            }
        });

        btnSaveNewOffer.setOnClickListener(v -> {
            smallQuantity.clearFocus();
            mediumQuantity.clearFocus();
            largeQuantity.clearFocus();
            price.clearFocus();

            boolean ispravanUnos = ProvjeraUnosa();
            if (ispravanUnos) {
                sharedViewModel.DohvatiRibe();
                sharedViewModel.fishDataArrayList.observe(this, new Observer<ArrayList<Fish>>() {
                    @Override
                    public void onChanged(ArrayList<Fish> fish) {
                        for (int i = 0; i < fish.size(); i++) {
                            if (fish.get(i).getName().contains(fishSpecies.getText().toString()) || fish.get(i).getNameeng().contains(fishSpecies.getText().toString())) {
                                sharedViewModel.DodajPonuduSAutoID(fish.get(i).getName(), location.getText().toString(), fish.get(i).getUrl(), price.getText().toString(), userId, smallQuantity.getText().toString(),
                                        mediumQuantity.getText().toString(), largeQuantity.getText().toString(), Timestamp.now());
                                Fragment newFragment;
                                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                                newFragment = new SearchFragment(userId);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_containter, newFragment).commit();
                                StyleableToast.makeText(getActivity(), getActivity().getString(R.string.offerSuccessfullyCreated), 3, R.style.ToastGreen).show();
                                return;
                            }
                        }
                        StyleableToast.makeText(getActivity(),  getActivity().getString(R.string.fishDoesntExist), 3, R.style.Toast).show();
                    }
                });
            }
        });

        return view;
    }

    InputFilter filterDecimals = (source, start, end, dest, dstart, dend) -> {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString());
        if (!builder.toString().matches(
                "(([0-9])([0-9]{0," + (3 - 1) + "})?)?(\\.[0-9]{0," + 2 + "})?"
        )) {
            if (source.length() == 0)
                return dest.subSequence(dstart, dend);
            return "";
        }
        return null;
    };

    private boolean ProvjeraUnosa() {
        boolean provjera = true;
        if (fishSpecies.getText().toString().equals("") || location.getText().toString().equals("") || price.getText().toString().equals("")) {
            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.noData), 3, R.style.Toast).show();
            provjera = false;
        } else if (!checkSmall.isChecked() && !checkLarge.isChecked() && !checkMedium.isChecked()) {
            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.fishClassNotSelected), 3, R.style.Toast).show();
            provjera = false;
        } else if (checkSmall.isChecked() && (smallQuantity.getText().toString().equals("0") || smallQuantity.getText().toString().equals("0.0") || smallQuantity.getText().toString().equals(""))) {
            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.quantityNotSelected), 3, R.style.Toast).show();
            provjera = false;
        } else if(checkMedium.isChecked() && (mediumQuantity.getText().toString().equals("0") || mediumQuantity.getText().toString().equals("0.0") || mediumQuantity.getText().toString().equals(""))) {
            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.quantityNotSelected), 3, R.style.Toast).show();
            provjera = false;
        } else if(checkLarge.isChecked() && (largeQuantity.getText().toString().equals("0") || largeQuantity.getText().toString().equals("0.0") || largeQuantity.getText().toString().equals(""))){
            StyleableToast.makeText(getActivity(), getActivity().getString(R.string.quantityNotSelected), 3, R.style.Toast).show();
            provjera = false;
        }
        return provjera;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if(id==android.R.id.home) {
                setHasOptionsMenu(false);
                Fragment selectedFragment = null;
                selectedFragment = new SearchFragment(userId);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter, selectedFragment).commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            return true;
    }
}



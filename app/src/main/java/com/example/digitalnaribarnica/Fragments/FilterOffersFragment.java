package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.database.Fish;
import com.example.database.Location;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Repository;
import  com.example.digitalnaribarnica.databinding.FilterOffersBinding;

import com.example.digitalnaribarnica.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Locale;

public class FilterOffersFragment extends Fragment {

    FilterOffersBinding binding;

    private AutoCompleteTextView editFishSpecies;
    private AutoCompleteTextView editLocations;

    private RangeSeekBar rangePrice;

    private TextView rangeMinimum;
    private TextView rangeMaximum;

    private CheckBox smallRadio;
    private CheckBox mediumRadio;
    private CheckBox largeRadio;

    private EditText lowerPrice;
    private EditText topPrice;

    private Button btnFilter;

    private String userId ="";
    private SharedViewModel sharedViewModel;

    public FilterOffersFragment(){}
    public FilterOffersFragment(String userID){
        this.userId = userID;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.filter));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        binding = FilterOffersBinding.inflate(inflater,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        editFishSpecies = binding.autoCompleteFish;
        editLocations = binding.autoCompleteLocation;
        rangePrice = binding.rangeSeekBar;
        rangeMinimum = binding.rangeMin;
        rangeMaximum = binding.rangeMax;

        smallRadio = binding.radioSmall;
        mediumRadio = binding.radioMedium;
        largeRadio = binding.radioLarge;


        lowerPrice = binding.lowPrice;
        topPrice = binding.topPrice;

        btnFilter = binding.btnFilter;

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        sharedViewModel.DohvatiRibe();
        sharedViewModel.fishDataArrayList.observe(this, new Observer<ArrayList<Fish>>() {
                @Override
                public void onChanged(ArrayList<Fish> fish) {
                    ArrayList<String> fishArrayList=new ArrayList<>();
                    for(Fish riba: fish){
                        if(!Locale.getDefault().getDisplayLanguage().equals("English")){
                            fishArrayList.add(riba.getName());
                        }else{
                            fishArrayList.add(riba.getNameeng());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_custom, R.id.autocomplete_text, fishArrayList);
                    adapter.notifyDataSetChanged();
                    editFishSpecies.setAdapter(adapter);
                }
        });



        sharedViewModel.DohvatiLokacije();
        sharedViewModel.locationDataArrayList.observe(this, new Observer<ArrayList<Location>>() {
            @Override
            public void onChanged(ArrayList<Location> locations) {
                ArrayList<String> locationArrayList=new ArrayList<>();
                for(Location location: locations){
                    locationArrayList.add(location.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_custom, R.id.autocomplete_text, locationArrayList);
                adapter.notifyDataSetChanged();
                editLocations.setAdapter(adapter);
            }
        });

        rangePrice.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                rangeMinimum.setText(String.valueOf(minValue));
                rangeMaximum.setText(String.valueOf(maxValue));

            }
        });

        rangePrice.setNotifyWhileDragging(true);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View v) {
                String min;
                String max;

                if(lowerPrice.getText().toString().equals("")){
                    min = "0";
                    lowerPrice.setText("0");
                    rangePrice.setSelectedMinValue(0);
                }else{
                    min = lowerPrice.getText().toString();
                }

                if(topPrice.getText().toString().equals("")){
                    max="999";
                    topPrice.setText("999");
                    rangePrice.setSelectedMaxValue(999);
                }else{
                    max = topPrice.getText().toString();
                }

                if (Integer.parseInt(min) > Integer.parseInt(max)) {
                    StyleableToast.makeText(getActivity(), getActivity().getString(R.string.higestPriceHigerThanLowest), 3, R.style.Toast).show();
                }

                else {
                    ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                    selectedFragment = new SearchFragment(userId, editFishSpecies.getText().toString(), editLocations.getText().toString(), min, max, smallRadio.isChecked(),
                            mediumRadio.isChecked(), largeRadio.isChecked());
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }
            }
        });

        rangePrice.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                lowerPrice.setText(String.valueOf(minValue));
                topPrice.setText(String.valueOf(maxValue));
            }
        });

        lowerPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    rangePrice.setSelectedMinValue(Integer.parseInt(lowerPrice.getText().toString()));
                    if (Integer.parseInt(lowerPrice.getText().toString()) < 0) {
                        lowerPrice.setText("0");
                        rangePrice.setSelectedMinValue(0);
                    }
                    else {
                        if (Integer.parseInt(lowerPrice.getText().toString()) > Integer.parseInt(topPrice.getText().toString())) {
                            rangePrice.setSelectedMinValue(Integer.parseInt(topPrice.getText().toString()));
                            lowerPrice.setText(topPrice.getText().toString());
                        }
                        else {
                            rangePrice.setSelectedMinValue(Integer.parseInt(lowerPrice.getText().toString()));
                        }
                    }

                } catch (Exception ex){
                    if(lowerPrice.getText().toString().equals("")){
                    rangePrice.setSelectedMinValue(0);
                    }
                    else{
                        rangePrice.setSelectedMinValue(Integer.parseInt(lowerPrice.getText().toString()));
                    }
                }
            }
        });

        topPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    rangePrice.setSelectedMaxValue(Integer.parseInt(topPrice.getText().toString()));
                    if (Integer.parseInt(topPrice.getText().toString()) > 999) {
                        topPrice.setText("999");
                        rangePrice.setSelectedMaxValue(999);
                    }

                    else {
                        if (Integer.parseInt(lowerPrice.getText().toString()) > Integer.parseInt(topPrice.getText().toString())) {
                            rangePrice.setSelectedMaxValue(Integer.parseInt(lowerPrice.getText().toString()));
                        }
                        else {
                            rangePrice.setSelectedMaxValue(Integer.parseInt(topPrice.getText().toString()));
                        }
                    }
                } catch (Exception ex){
                    if(topPrice.getText().toString().equals("")){
                        rangePrice.setSelectedMaxValue(999);
                    }
                    else{
                        rangePrice.setSelectedMaxValue(Integer.parseInt(topPrice.getText().toString()));
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        menu.findItem((R.id.action_search)).setVisible(false);
        menu.findItem((R.id.filter_menu)).setVisible(false);
        menu.findItem(((R.id.sort_offers_menu))).setVisible(false);
        menu.findItem((R.id.language)).setVisible(false);
        menu.findItem((R.id.current_language)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(false);
        if (((RegisterActivity) getActivity()).buyer){
            menu.findItem((R.id.my_offers_menu)).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                setHasOptionsMenu(false);
                Fragment selectedFragment;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                selectedFragment = new SearchFragment(userId);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                selectedFragment).commit();
                break;

            case R.id.all_offers_menu:
                setHasOptionsMenu(false);
                Fragment selectedFragment1;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                selectedFragment1 = new SearchFragment(userId);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment1).commit();

                break;
            case R.id.my_offers_menu:
                setHasOptionsMenu(false);
                Fragment selectedFragment2;
                ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                selectedFragment2 = new SearchFragment(userId, true);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment2).commit();
                break;
        }
        return true;
    }

}

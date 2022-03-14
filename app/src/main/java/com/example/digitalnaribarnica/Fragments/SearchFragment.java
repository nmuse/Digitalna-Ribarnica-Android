package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.Fish;
import com.example.digitalnaribarnica.R;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.recycleviewer.RequestsAdapter;
import com.example.repository.Data.ReservationsData;
import com.example.repository.Listener.FirestoreOffer;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Listener.RezervationCallback;
import com.example.repository.Repository;
import com.example.digitalnaribarnica.databinding.FragmentSearchBinding;
import com.example.repository.Data.OffersData;
import com.example.digitalnaribarnica.recycleviewer.OfferAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SearchFragment extends Fragment {

    Menu actionMenu;

    FragmentSearchBinding binding;
    RecyclerView recyclerView;
    SearchFragment searchFragment;

    private ArrayList<OffersData> offersListGeneral =new ArrayList<>();

    private String fishSpecies;
    private String location;
    private String minPrice;
    private String maxPrice;
    private Boolean smallFish = false;
    private Boolean mediumFish = false;
    private Boolean largeFish = false;

    private FloatingActionButton btnAddOffer;
    private Boolean filtered = false;
    private Boolean myOffers = false;
    private Boolean fromOfferDetails = false;
    private TextView emptyView;

    private String userId = "";

    SearchView searchViewThisSearch;
    MenuItem itemThisSearch;

    public SearchFragment() {}

    public SearchFragment(String userId) {
        this.userId = userId;
        this.filtered = false;
        this.myOffers = false;
    }

    public SearchFragment(String userId, Boolean myOffers) {
        this.userId = userId;
        this.myOffers = myOffers;
        this.filtered = false;
    }

    public SearchFragment(String userId, Boolean myOffers, Boolean fromOfferDetails) {
        this.userId = userId;
        this.myOffers = myOffers;
        this.filtered = false;
        this.fromOfferDetails = fromOfferDetails;
    }

    public SearchFragment(String userID, String fishSpecies, String location, String minPrice, String maxPrice, Boolean smallFish, Boolean mediumFish, Boolean largeFish){
        this.userId = userID;
        this.fishSpecies = fishSpecies;
        this.location = location;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.smallFish = smallFish;
        this.mediumFish = mediumFish;
        this.largeFish = largeFish;
        this.filtered = true;
        this.myOffers = false;
    };

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.offers));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setHasOptionsMenu(true);

        searchFragment = this;
        recyclerView = binding.recycleViewOffer;
        btnAddOffer = binding.floatingbtnAddOffer;
        emptyView = binding.emptyView;

        if (((RegisterActivity) getActivity()).buyer){
            btnAddOffer.setVisibility(View.INVISIBLE);
        }

        ((RegisterActivity) getActivity()).fragmentId = this.getId();

        if(filtered){
                Repository repository = new Repository();
                repository.DohvatiSvePonude(offersData -> {
                    repository.DohvatiRibe(new FishCallback() {
                        @Override
                        public void onCallback(ArrayList<Fish> fishes) {

                            String engFishToCroatian = "";

                            for(int i = 0; i < fishes.size(); i++){
                                if(fishSpecies.equals(fishes.get(i).getNameeng())){
                                    engFishToCroatian = fishes.get(i).getName();
                                    break;
                                }
                            }

                            ArrayList<OffersData> offersList = new ArrayList<>();
                            offersList = offersData;

                            for (int i = 0; i < offersList.size(); i++) {
                                if (!offersList.get(i).getStatus().equals("Aktivna")) {
                                    offersList.remove(offersData.get(i));
                                    i = i - 1;
                                }
                            }

                            for (int i = 0; i < offersList.size(); i++) {
                                if (fishSpecies != null && !fishSpecies.equals("")){
                                    if(!Locale.getDefault().getDisplayLanguage().equals("English")){
                                        if (!(offersList.get(i).getName().contains(fishSpecies))) {
                                            offersList.remove(offersData.get(i));
                                            i = i - 1;
                                            continue;
                                        }
                                    }
                                    else{
                                        if (!(offersList.get(i).getName().contains(engFishToCroatian))) {
                                            offersList.remove(offersData.get(i));
                                            i = i - 1;
                                            continue;
                                        }
                                    }
                                }




                                if (location != null && !location.equals("")) {
                                    if (!offersList.get(i).getLocation().contains(location)){
                                        offersList.remove(offersData.get(i));
                                        i = i - 1;
                                        continue;
                                    }
                                }

                                if (Double.parseDouble(minPrice) > Double.parseDouble(offersList.get(i).getPrice()) || Double.parseDouble(maxPrice) < Double.parseDouble(offersList.get(i).getPrice())) {
                                    offersList.remove(offersData.get(i));
                                    i = i - 1;
                                    continue;
                                }

                                if (smallFish ||  mediumFish || largeFish) {
                                    Boolean smallCheck = false;
                                    Boolean mediumCheck = false;
                                    Boolean largeCheck = false;

                                    if(smallFish && !(offersList.get(i).getSmallFish().equals("0") || offersList.get(i).getSmallFish().equals("0.0")))
                                    {
                                        smallCheck = true;
                                    }
                                    if(mediumFish && !(offersList.get(i).getMediumFish().equals("0") || offersList.get(i).getMediumFish().equals("0.0")))
                                    {
                                        mediumCheck = true;
                                    }
                                    if(largeFish && !(offersList.get(i).getLargeFish().equals("0") || offersList.get(i).getLargeFish().equals("0.0")))
                                    {
                                        largeCheck = true;
                                    }

                                    if (!(smallCheck || mediumCheck || largeCheck)){
                                        offersList.remove(offersData.get(i));
                                        i = i - 1;
                                    }
                                }
                            }

                            offersListGeneral = offersList;
                            OfferAdapter adapter2 = new OfferAdapter(getActivity(), userId, searchFragment);

                            if(offersList.size() == 0){
                                showDialog(getActivity(), getActivity().getString(R.string.filterOffers),  getActivity().getString(R.string.filterOffersOffersNotFound));
                                Fragment newSearchFragment = new SearchFragment(userId);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                                        newSearchFragment).commit();
                            }

                            else {
                                adapter2.setOffers(offersList);
                                adapter2.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter2);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                actionMenu.findItem((R.id.all_offers_menu)).setVisible(true);
                                if (((RegisterActivity) getActivity()).buyer){
                                    actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
                                }
                                else {
                                    actionMenu.findItem((R.id.my_offers_menu)).setVisible(true);
                                }
                            }

                        }
                    });
                });
            }

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment =null;
            @Override
            public void onClick(View view) {
                selectedFragment = new AddOfferFragment(userId);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        return view;
    }

    public void searchOffers(String search) {
        Repository repository = new Repository();
        repository.DohvatiRibe(new FishCallback() {
            @Override
            public void onCallback(ArrayList<Fish> fishes) {
                repository.DohvatiSvePonude(offersData -> {
                    ArrayList<OffersData> offersList = new ArrayList<>();
                    if(myOffers){
                        for (int i = 0; i < offersData.size(); i++) {
                            if(Locale.getDefault().getDisplayLanguage().equals("English")){
                                for (int j = 0; j < fishes.size(); j++) {
                                    if (offersData.get(i).getName().equals(fishes.get(j).getName())) {
                                        if ((fishes.get(j).getNameeng().toLowerCase().contains(search.toLowerCase()) || offersData.get(i).getLocation().toLowerCase().contains(search.toLowerCase())) && offersData.get(i).getIdKorisnika().equals(userId)) {
                                            offersList.add(offersData.get(i));
                                            break;
                                        }
                                    }
                                }
                            }
                            else{
                                if (offersData.get(i).getName().toLowerCase().contains(search.toLowerCase()) || offersData.get(i).getLocation().toLowerCase().contains(search.toLowerCase()) && offersData.get(i).getIdKorisnika().equals(userId)) {
                                    offersList.add(offersData.get(i));
                                }
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < offersData.size(); i++) {
                            if(Locale.getDefault().getDisplayLanguage().equals("English")){
                                for (int j = 0; j < fishes.size(); j++) {
                                    if (offersData.get(i).getName().equals(fishes.get(j).getName())) {
                                        if ((fishes.get(j).getNameeng().toLowerCase().contains(search.toLowerCase()) || offersData.get(i).getLocation().toLowerCase().contains(search.toLowerCase())) && offersData.get(i).getStatus().equals("Aktivna")) {
                                            offersList.add(offersData.get(i));
                                            break;
                                        }
                                    }
                                }

                            }
                            else{
                                if (offersData.get(i).getStatus().equals("Aktivna") && (offersData.get(i).getName().toLowerCase().contains(search.toLowerCase()) || offersData.get(i).getLocation().toLowerCase().contains(search.toLowerCase()))) {
                                    offersList.add(offersData.get(i));
                                }
                            }
                        }
                    }

                    try {
                        if(search.equals("")){
                            if(myOffers) {
                                offersList.clear();
                                for (int i = 0; i < offersData.size(); i++) {
                                    if (offersData.get(i).getIdKorisnika().equals(userId)) {
                                        offersList.add(offersData.get(i));
                                    }
                                }
                            }
                            actionMenu.findItem((R.id.all_offers_menu)).setVisible(false);

                            if (((RegisterActivity) getActivity()).buyer){
                                actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
                            }
                            else {
                                actionMenu.findItem((R.id.my_offers_menu)).setVisible(true);
                            }

                        }

                        else {
                            actionMenu.findItem((R.id.all_offers_menu)).setVisible(true);
                            if (((RegisterActivity) getActivity()).buyer){
                                actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
                            }
                            else {
                                actionMenu.findItem((R.id.my_offers_menu)).setVisible(true);
                            }
                        }
                    }
                    catch (Exception ex){
                    }

                    offersListGeneral = offersList;

                    if(offersListGeneral.size() == 0) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else{
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                    OfferAdapter adapter = new OfferAdapter(getActivity(), userId, searchFragment);

                    if(myOffers){
                        adapter.ShowIcons();
                    }

                    adapter.setOffers(offersList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        actionMenu = menu;
        actionMenu.findItem((R.id.onboardingHelp)).setVisible(false);
        if (((RegisterActivity) getActivity()).buyer){
            actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
        }
        menu.findItem((R.id.all_offers_menu)).setVisible(false);
        menu.findItem((R.id.language)).setVisible(false);
        menu.findItem((R.id.current_language)).setVisible(false);

        MenuItem item = menu.findItem((R.id.action_search));
        SearchView searchView = (SearchView) item.getActionView();

        itemThisSearch = item;
        searchViewThisSearch = searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOffers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!filtered && !fromOfferDetails) {
                    searchOffers(newText);
                }
                filtered = false;
                fromOfferDetails = false;
                return true;
            }
        });

        if(!this.myOffers && !this.filtered) {
            Repository repository = new Repository();
            repository.DohvatiSvePonude(offersData -> {

                ArrayList<OffersData> offersList = new ArrayList<>();
                for (int i = 0; i < offersData.size(); i++) {
                    if (offersData.get(i).getStatus().equals("Aktivna")) {
                        offersList.add(offersData.get(i));
                    }
                }

                offersListGeneral = offersList;

                if(offersListGeneral.size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                OfferAdapter adapter = new OfferAdapter(getActivity(), userId, this);
                adapter.setOffers(offersList);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            });
        }

        if(this.myOffers && !this.filtered){
            getMyOffers();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Repository repository=new Repository();
        OfferAdapter adapter = new OfferAdapter(getActivity(), userId, this);
        switch (id){
            case android.R.id.home:
                myOffers = false;
                repository.DohvatiSvePonude(offersData -> {
                    ArrayList<OffersData> offersList = new ArrayList<>();
                    for (int i = 0; i < offersData.size(); i++) {
                        if (offersData.get(i).getStatus().equals("Aktivna")) {
                            offersList.add(offersData.get(i));
                        }
                    }

                    offersListGeneral = offersList;

                    if(offersListGeneral.size()==0){
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                    adapter.setOffers(offersList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.offers));
                    actionMenu.findItem((R.id.all_offers_menu)).setVisible(false);
                    if (((RegisterActivity) getActivity()).buyer){
                        actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
                    }
                    else {
                        actionMenu.findItem((R.id.my_offers_menu)).setVisible(true);
                    }
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                });
                break;

            case R.id.all_offers_menu:
                myOffers = false;
                repository.DohvatiSvePonude(offersData -> {
                    ArrayList<OffersData> offersList = new ArrayList<>();
                    for (int i = 0; i < offersData.size(); i++) {
                        if (offersData.get(i).getStatus().equals("Aktivna")) {
                            offersList.add(offersData.get(i));
                        }
                    }
                    offersListGeneral = offersList;

                    if(offersListGeneral.size()==0){
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                    adapter.setOffers(offersList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.offers));
                        actionMenu.findItem((R.id.all_offers_menu)).setVisible(false);
                    if (((RegisterActivity) getActivity()).buyer){
                        actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);
                    }
                    else {
                        actionMenu.findItem((R.id.my_offers_menu)).setVisible(true);
                    }
                });
                break;

            case R.id.my_offers_menu:
                getMyOffers();
                break;

            case R.id.filter_menu:
                destroySearch();
                FilterOffersFragment selectedFragment = new FilterOffersFragment(userId);
                getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
                break;
            case R.id.sort_offers_menu:
                String[] items = {getActivity().getString(R.string.most_expensive), getActivity().getString(R.string.least_expensive)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.showFirst));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Collections.sort(offersListGeneral, (exp1, exp2) -> Double.compare(Double.parseDouble(exp2.getPrice()), Double.parseDouble(exp1.getPrice())));

                                OfferAdapter adapter2 = new OfferAdapter(getActivity(), userId, SearchFragment.this);
                                adapter2.setOffersWithoutSortDate(offersListGeneral);
                                adapter2.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter2);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                break;

                            case 1:
                                Collections.sort(offersListGeneral, (exp1, exp2) -> Double.compare(Double.parseDouble(exp1.getPrice()), Double.parseDouble(exp2.getPrice())));

                                OfferAdapter adapter3 = new OfferAdapter(getActivity(), userId, SearchFragment.this);
                                adapter3.setOffersWithoutSortDate(offersListGeneral);
                                adapter3.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter3);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                break;
                        }
                    }
                });
                builder.show();
            }
        return true;
    }

    public void getMyOffers(){
        Repository repository=new Repository();
        OfferAdapter adapter2 = new OfferAdapter(getActivity(), userId, this);
        repository.DohvatiSvePonude(offersData -> {
            ArrayList<OffersData> offersList = offersData;
            for (int i = 0; i < offersList.size(); i++) {

                if (!offersList.get(i).getIdKorisnika().equals(userId)) {
                    offersList.remove(offersData.get(i));
                    i = i - 1;
                }
            }

            myOffers = true;
            offersListGeneral = offersList;
            adapter2.ShowIcons();
            adapter2.setOffers(offersList);
            adapter2.notifyDataSetChanged();
            recyclerView.setAdapter(adapter2);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.my_offers));
            actionMenu.findItem((R.id.all_offers_menu)).setVisible(true);
            actionMenu.findItem((R.id.my_offers_menu)).setVisible(false);


            if(offersList.size() == 0){
                showDialog(getActivity(), getActivity().getString(R.string.my_offers), getActivity().getString(R.string.emptyMyOffers));
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

        });
    }


    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton( getActivity().getString(R.string.okay), null);
        builder.show();
    }

    public boolean getLastVisited(){
        return myOffers;
    }

    public void destroySearch(){
        /*searchViewThisSearch.setQuery("", false);
        searchViewThisSearch.clearFocus();
        searchViewThisSearch.setIconified(true);
        searchViewThisSearch.setIconified(true);
        searchViewThisSearch.onActionViewCollapsed();*/
        MenuItemCompat.collapseActionView(itemThisSearch);
    }
}
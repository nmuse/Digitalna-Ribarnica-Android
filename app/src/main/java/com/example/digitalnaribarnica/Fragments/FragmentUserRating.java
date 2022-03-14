package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.database.FirestoreService;
import com.example.database.Review;
import com.example.database.User;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.repository.Data.OffersData;
import com.example.repository.Listener.FirestoreCallback;
import com.example.digitalnaribarnica.R;
import com.example.repository.Repository;
import com.example.digitalnaribarnica.databinding.FragmentUserRatingBinding;
import com.example.repository.Data.ReservationsData;
import com.google.firebase.Timestamp;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;

public class FragmentUserRating extends Fragment {

    FragmentUserRatingBinding binding;
    private Button rateUser;
    private RatingBar rating;
    private EditText comment;
    private TextView name;
    private SharedViewModel sharedViewModel;
    private ReservationsData reservation;

    String userID;
    String ratedUser;
    String reservationID;
    String ratedby="";

    private Boolean fromRequests = false;
    private Boolean fromConfirmed = false;
    public FragmentUserRating() {}

    public FragmentUserRating( String userId, String ratedUser) {
        this.userID = userId;
        this.ratedUser = ratedUser;
    }

    public FragmentUserRating( String userId, String ratedUser, String reservationID) {
        this.userID = userId;
        this.ratedUser = ratedUser;
        this.reservationID = reservationID;
    }

    public FragmentUserRating( String userId, String ratedUser, String reservationID, String ratedby) {
        this.userID = userId;
        this.ratedUser = ratedUser;
        this.reservationID = reservationID;
        this.ratedby = ratedby;
    }

    public FragmentUserRating( String userId, String ratedUser, String reservationID, Boolean fromRequests, Boolean fromConfirmed) {
        this.userID = userId;
        this.ratedUser = ratedUser;
        this.reservationID = reservationID;
        this.fromRequests = fromRequests;
        this.fromConfirmed =  fromConfirmed;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.rateUser));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        binding = FragmentUserRatingBinding.inflate(inflater,container,false);
        View view =binding.getRoot();
        rateUser = binding.btnOcijeni;
        rating = binding.ratingBar;
        comment = binding.comment;
        name = binding.imePrezime;

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if(reservation == null) {
            sharedViewModel.DohvatiKorisnikaPoID(ratedUser);
            sharedViewModel.userMutableLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    name.setText(user.getFullName());
                }
            });
        }
        else{
            sharedViewModel.DohvatiSvePonude();
            sharedViewModel.offerDataArrayList.observe(this, new Observer<ArrayList<OffersData>>() {
                @Override
                public void onChanged(ArrayList<OffersData> offersData) {
                    for (int i = 0; i < offersData.size(); i++) {
                        if(offersData.get(i).getOfferID().equals(reservation.getOfferID())) {
                            sharedViewModel.DohvatiKorisnikaPoID(offersData.get(i).getIdKorisnika());
                            sharedViewModel.userMutableLiveData.observe(getActivity(), new Observer<User>() {
                                @Override
                                public void onChanged(User user) {
                                    name.setText(user.getFullName());
                                }
                            });
                            break;
                        }
                    }
                }
            });
        }

        rateUser.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment =null;
            @Override
            public void onClick(View v) {

                if(!ratedby.equals("")){
                    Log.d("TagPolje", ratedby);
                    if (ratedby.equals("seller")){
                        sharedViewModel.AzurirajStatusOcijenioProdavatelj(reservationID, true);
                    }else{
                        sharedViewModel.AzurirajStatusOcijenioKupac(reservationID, true);                    }
                }

                sharedViewModel.DodajOcjenu(ratedUser, String.valueOf(rating.getRating()), comment.getText().toString(), userID, Timestamp.now());
                StyleableToast.makeText(getActivity(), getActivity().getString(R.string.userSuccessfullyRated), 3, R.style.ToastGreen).show();
                sharedViewModel.DohvatiOcjenePoID(ratedUser);
                sharedViewModel.reviewDataArrayList.observe(getActivity(), new Observer<ArrayList<Review>>() {
                    @Override
                    public void onChanged(ArrayList<Review> reviews) {
                        if(reviews.size()!=0){
                            float sum = 0;
                            for (int i = 0; i < reviews.size(); i++) {
                                sum = sum + Float.parseFloat(reviews.get(i).getRating());
                            }
                            float ratingTotal = sum / reviews.size();
                            Log.d("TagPolje",  String.valueOf(ratingTotal));
                            sharedViewModel.AzurirajRating(ratedUser, String.valueOf(ratingTotal));
                        }
                    }
                });
                if(fromRequests){
                    selectedFragment = new ReservationFragment(userID, true, true, false);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                } else if(fromConfirmed){
                    sharedViewModel.AzurirajStatusOcijenioProdavatelj(reservationID, true);
                    selectedFragment = new ReservationFragment(userID, true, true, true);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }else{
                    selectedFragment = new ReservationFragment(userID, true);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }
            }
        });
        return  view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment =null;
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(fromConfirmed){
                    selectedFragment = new ReservationFragment(userID, true, true, true);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }else{
                    selectedFragment = new ReservationFragment(userID, true);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }
                break;
        }

        return true;
    }

}

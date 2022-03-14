package com.example.digitalnaribarnica;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.database.User;
import com.example.digitalnaribarnica.Fragments.AddOfferFragment;
import com.example.digitalnaribarnica.Fragments.ChatFragment;
import com.example.digitalnaribarnica.Fragments.HomeFragment;
import com.example.digitalnaribarnica.Fragments.ProfileFragment;
import com.example.digitalnaribarnica.Fragments.ReservationFragment;
import com.example.digitalnaribarnica.Fragments.SearchFragment;
import com.example.digitalnaribarnica.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    GoogleSignInAccount acct;
    FirebaseUser mUser;
    String personName = "";
    String personEmail ="";
    String personId="";
    String personPhoto="";
    String phone="";
    String adress="";

    public int fragmentId = 2131231000;
    public boolean onReservation = false;
    public boolean onSearch = false;

    public boolean buyer = true;
    public int refreshTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding=ActivityRegisterBinding.inflate((getLayoutInflater()));
        View view=binding.getRoot();
        setContentView(view);

        refreshTimes = 0;

        bottomNavigationView=binding.bottomNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            personId = acct.getId();

            if(acct.getPhotoUrl() != null) {
                personPhoto = acct.getPhotoUrl().toString();
            }
        }
        else if(mUser!=null){
            personName=mUser.getDisplayName();
            personEmail=mUser.getEmail();
            personId=mUser.getUid();

            if(mUser.getPhotoUrl() != null) {
                personPhoto=mUser.getPhotoUrl().toString();
            }
        }
        else {
            User user=(User)getIntent().getSerializableExtra("CurrentUser");
            personName=user.getFullName();
            personEmail=user.getEmail();
            personId=user.getUserID();
            personPhoto=user.getPhoto();
            adress=user.getAdress();
            phone=user.getPhone();

            if(mUser.getPhotoUrl() != null) {
                personPhoto=mUser.getPhotoUrl().toString();
            }
        }


        Log.d("TagPolje", "onCreate: ");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                new HomeFragment(personId)).commit();

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment =null;

                    //Ruši kod dodjele značke
                    /*if(onReservation) {
                        try {
                            ReservationFragment fragment = (ReservationFragment) getSupportFragmentManager().findFragmentById(fragmentId);
                            assert fragment != null;
                            fragment.destroySearch();
                        }catch (Exception e){}
                    }
                    if(onSearch){
                        try {
                            SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(fragmentId);
                            assert fragment != null;
                            fragment.destroySearch();
                        }catch (Exception e){}
                    }*/

                    onReservation = false;
                    onSearch = false;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            destroySearch();
                            selectedFragment = new HomeFragment(personId);
                            break;
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment(personId);
                            break;
                        case R.id.nav_person:
                            selectedFragment = new ProfileFragment(personId, mGoogleSignInClient, mUser, mAuth);
                            break;
                        case R.id.nav_ponude:
                            selectedFragment = new ReservationFragment(personId);
                            onReservation = true;
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment(personId);
                            onSearch = true;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                    return true;
                }
            };

    public void destroySearch(){
        if(onReservation) {
            try {
                ReservationFragment fragment = (ReservationFragment) getSupportFragmentManager().findFragmentById(fragmentId);
                assert fragment != null;
                fragment.destroySearch();
            }catch (Exception e){}
        }
        if(onSearch){
            try {
                SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(fragmentId);
                assert fragment != null;
                fragment.destroySearch();
            }catch (Exception e){}
        }
    }

    public void refreshFragmentHome(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                new HomeFragment(personId)).commit();
    }


            private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RegisterActivity.this,getString(R.string.userSignOut),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
    boolean doubleBackToExitPressedOnce = false;

    public void changeOnSearchNavigationBar(){
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
    }

    public void changeOnReservationsNavigationBar(){
        bottomNavigationView.setSelectedItemId(R.id.nav_ponude);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.exitApp));
            alertDialogBuilder
                    .setMessage(getString(R.string.pressYes))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.pressBackforExit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void changeRole(Boolean buyerRole){
        if(buyerRole){
            buyer = true;
        }
        else{
            buyer = false;
        }
    }
}

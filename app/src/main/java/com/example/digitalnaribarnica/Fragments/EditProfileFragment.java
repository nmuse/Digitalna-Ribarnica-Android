package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.database.CallbackUser;
import com.example.database.FirestoreService;
import com.example.database.User;
import com.example.database.Utils.SHA256;
import com.example.repository.Listener.FirestoreCallback;
import com.example.digitalnaribarnica.R;
import com.example.repository.Repository;
import com.example.digitalnaribarnica.databinding.FragmentEditProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.NoSuchAlgorithmException;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {
    FragmentEditProfileBinding binding;
    private Button btnDetails;
    private String photo="";
    private String ime="";
    private String id="";
    private String email="";
    private String adress="";
    private String phone="";
    private String cameFrom = "";
    private String offerID = "";
    private String currentUser = "";
    GoogleSignInAccount acct;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Uri imageUri;
    private static final int PICK_IMAGE = 100;
    public EditProfileFragment() {}

    //Google
    public EditProfileFragment(String userId, String currentUser, GoogleSignInClient mGoogleSignInClient, FirebaseUser mUser, FirebaseAuth mAuth, String cameFrom) {
        this.id = userId;
        this.currentUser = currentUser;
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.mUser = mUser;
        this.mAuth = mAuth;
        this.cameFrom = cameFrom;
    }

    public EditProfileFragment(String userId, String currentUser, GoogleSignInClient mGoogleSignInClient, FirebaseUser mUser, FirebaseAuth mAuth, String cameFrom, String offerID) {
        this.id = userId;
        this.currentUser = currentUser;
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.mUser = mUser;
        this.mAuth = mAuth;
        this.offerID = offerID;
        this.cameFrom = cameFrom;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.editProfile));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Repository repository = new Repository();
        repository.DohvatiKorisnikaPoID(id, new FirestoreCallback() {
            @Override
            public void onCallback(User user) {
                binding.emailEditPe.setText(user.getEmail());
                if(user.getFullName()!=""){
                    binding.imeEditEp.setText(user.getFullName().split(" ")[0]);
                }
                if(user.getFullName()!=""){
                    binding.prezimeEditEp.setText(user.getFullName().split(" ")[1]);
                }
                if(user.getAdress()!=""){
                    binding.adresaEditPe.setText(user.getAdress());
                }
                if(user.getPhone()!=""){
                    binding.brojMobitelaEditPe.setText(user.getPhone());
                }
                if(user.getEmail() != "") {
                    binding.emailEditPe.setText(user.getEmail());
                    email = user.getEmail();
                }
                Glide.with(getActivity())
                        .asBitmap()
                        .load(user.getPhoto())
                        .into(binding.slikaProfila);
            }
        });

        binding.btnUcitajSLiku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        binding.btnSpremiPromjene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirestoreService firestoreService=new FirestoreService();
                Repository repository=new Repository();
                repository.DohvatiKorisnikaPoEmailu(email, new FirestoreCallback() {
                    @Override
                    public void onCallback(User user) {
                        FirestoreService.getProfilePhotoWithID(id, new CallbackUser() {
                            @Override
                            public void onCallback(Uri slika) {
                                if(slika != null) {
                                    photo=slika.toString();
                                }
                                else {
                                    photo = "https://firebasestorage.googleapis.com/v0/b/digitalna-ribarnica-fb.appspot.com/o/default_profilna%2Fuser_image.jpg?alt=media&token=e30a1426-9be2-40d8-8e5a-b5e4c43337e7";
                                }

                                User updateKorisnik;
                                if(binding.lozinkaEditPe.getText().toString() ==" ")
                                    updateKorisnik = new User(id,binding.imeEditEp.getText().toString()+" "+binding.prezimeEditEp.getText().toString(),binding.emailEditPe.getText().toString(),binding.brojMobitelaEditPe.getText().toString(),binding.adresaEditPe.getText().toString(),photo, user.getPassword(),false);
                                else {
                                    try {
                                        updateKorisnik = new User(id,binding.imeEditEp.getText().toString()+" "+binding.prezimeEditEp.getText().toString(),binding.emailEditPe.getText().toString(),binding.brojMobitelaEditPe.getText().toString(),binding.adresaEditPe.getText().toString(),photo, SHA256.toHexString(SHA256.getSHA(binding.lozinkaEditPe.getText().toString())),false);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                        updateKorisnik = new User(id,binding.imeEditEp.getText().toString()+" "+binding.prezimeEditEp.getText().toString(),binding.emailEditPe.getText().toString(),binding.brojMobitelaEditPe.getText().toString(),binding.adresaEditPe.getText().toString(),photo, user.getPassword(),false);

                                    }
                                }

                                firestoreService.updateUser(updateKorisnik,"Users");
                                try {
                                    Log.d("TagPolje", cameFrom);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new ProfileFragment(id, mGoogleSignInClient, mUser, mAuth, cameFrom)).commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }
                });

            }
        });
        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(binding.slikaProfila);
            photo=imageUri.toString();
            FirestoreService.addPhotoWithID(imageUri,id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                setHasOptionsMenu(false);
                Fragment selectedFragment = null;
                // ((RegisterActivity) getActivity()).changeOnSearchNavigationBar();
                //selectedFragment = new PersonFragment(userId);
                if (cameFrom.equals("Person")) {
                    selectedFragment = new ProfileFragment(this.id, mGoogleSignInClient, mUser, mAuth);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                } else if (cameFrom.equals("Details")) {
                    selectedFragment = new ProfileFragment(this.id, currentUser, cameFrom, offerID);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                } else {
                    selectedFragment = new ProfileFragment(this.id, currentUser, cameFrom);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
        }

        return true;
    }
}

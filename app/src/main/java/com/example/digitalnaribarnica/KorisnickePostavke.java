package com.example.digitalnaribarnica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.digitalnaribarnica.databinding.ActivityKorisnickePostavkeBinding;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KorisnickePostavke extends AppCompatActivity {
    private RelativeLayout rlayout;
    private Animation animation;
    ActivityKorisnickePostavkeBinding binding;
    ImageView imageView;
    TextView name,email,id;
    Button signOut;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_korisnicke_postavke);

        //Incijalizacija Firebase
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding=ActivityKorisnickePostavkeBinding.inflate((getLayoutInflater()));
        View view=binding.getRoot();
        setContentView(view);
        imageView = binding.Slika;
        name = binding.Name;
        email = binding.Email;
        id = binding.ID;
        signOut=binding.odjava;
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.odjava:
                        if(mUser!=null){
                            mAuth.signOut();
                            LoginManager.getInstance().logOut();;
                            finish();
                        }
                        signOut();
                        break;
                }
            }
        });
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            email.setText(personEmail);
            id.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);
        }
        else if(mUser!=null){
            String personName=mUser.getDisplayName();
            String personEmail=mUser.getEmail();
            String personId=mUser.getUid();
            String personPhoto=mUser.getPhotoUrl().toString();

            name.setText(personName);
            email.setText(personEmail);
            id.setText(personId);
            Glide.with(this).load(personPhoto).into(imageView);

        }

    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(KorisnickePostavke.this, getString(R.string.userSignOut),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
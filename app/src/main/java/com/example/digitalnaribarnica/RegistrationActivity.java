package com.example.digitalnaribarnica;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.digitalnaribarnica.databinding.ActivityRegistrationBinding;
import com.example.repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class RegistrationActivity extends AppCompatActivity {

    private TextView imeTextView, prijava;

    private EditText ime;
    private EditText prezime;
    private EditText adresa;
    private EditText brojMobitela;
    private EditText email;
    private EditText lozinka;
    private EditText ponovljenaLozinka;

    FirebaseAuth fAuth;
    Repository repository;
    String imePrezime;
    String userID;

    private Button registracija;

    private Boolean boolIme = false;
    private Boolean boolPrezime = false;
    private Boolean boolAdresa = false;
    private Boolean boolMobitel = false;
    private Boolean boolEmail = false;
    private Boolean boolLozinka = false;

    private Boolean vrijemeTece = false;
    private SharedViewModel sharedViewModel;
    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityRegistrationBinding.inflate((getLayoutInflater()));
        View view = binding.getRoot();
        setContentView(view);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        fAuth = FirebaseAuth.getInstance();

        imeTextView = binding.imeTextView;

        ime = binding.imeR;
        prezime = binding.prezimeR;
        adresa = binding.adresaR;
        brojMobitela = binding.brojMobitelaR;
        email = binding.emailR;
        lozinka = binding.lozinkaR;
        ponovljenaLozinka = binding.ponovljenaLozinkaR;

        registracija = binding.btnRegistracija;

        prijava = binding.buttonPrijava;
        prijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int brojSlova = ime.length();
                if (brojSlova > 15) {
                    boolIme = false;
                } else {
                    boolIme = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean fokusiran) {
                if(!fokusiran){
                    if(ime.getText().toString().equals("")){}
                    else if (!boolIme) {
                        showToast(view, getString(R.string.nameTooBig));
                        ime.setTextColor(Color.RED);
                    }
                }
                else{
                    ime.setTextColor(imeTextView.getCurrentTextColor());
                }
            }
        });

        prezime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int brojSlovaPrezimena = prezime.length();
                if (brojSlovaPrezimena > 20) {
                    boolPrezime = false;
                }
                else {
                    boolPrezime = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        prezime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean fokusiran) {
                if(!fokusiran){
                    if(prezime.getText().toString().equals("")){}
                    else if (!boolPrezime) {
                        showToast(view, getString(R.string.surnameTooBig));
                        prezime.setTextColor(Color.RED);
                    }
                }
                else{
                    prezime.setTextColor(imeTextView.getCurrentTextColor());
                }
            }
        });

        adresa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int brojSlovaAdrese = adresa.length();
                if (brojSlovaAdrese > 40) {
                    boolAdresa = false;
                } else {
                    boolAdresa = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        adresa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean fokusiran) {
                if(!fokusiran) {
                    if(adresa.getText().toString().equals("")){}
                    else if (!boolAdresa) {
                        showToast(view, getString(R.string.addressTooBig));
                        adresa.setTextColor(Color.RED);
                    }
                }
                else{
                    adresa.setTextColor(imeTextView.getCurrentTextColor());
                }
            }
        });

        brojMobitela.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (brojMobitela.getText().toString().matches("^([+])([0-9\\s]){2,13}$|(0)([0-9\\s]){2,12}$")) {
                    boolMobitel = true;
                } else {
                    boolMobitel = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        brojMobitela.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean fokusiran) {
                if (!fokusiran) {

                    if(brojMobitela.getText().toString().equals("")){}
                    else if (!boolMobitel) {
                        showToast(view, getString(R.string.phoneNumberNotCorrect));
                        brojMobitela.setTextColor(Color.RED);
                    }
                }
                else{
                    brojMobitela.setTextColor(imeTextView.getCurrentTextColor());
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolEmail = email.getText().toString().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean fokusiran) {
                if (!fokusiran){
                    if(email.getText().toString().equals("")){}
                    else {
                        if (!boolEmail) {
                            showToast(view, getString(R.string.emailNotCorrect));
                            email.setTextColor(Color.RED);
                        }
                    }
                }
                else{
                    email.setTextColor(imeTextView.getCurrentTextColor());
                }
            }
        });

        lozinka.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolLozinka = lozinka.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$.!%#?&]{6,}$");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ime.getText().length() == 0){
                    boolIme = false;
                    showToast(view, getString(R.string.nameInputMissing));
                }
                else if(prezime.getText().length() == 0){
                    boolPrezime = false;
                    showToast(view, getString(R.string.surnameInputMissing));
                }
                else if(adresa.getText().length() == 0){
                    boolAdresa = false;
                    showToast(view, getString(R.string.addressInputMissing));
                }
                else if(brojMobitela.getText().length() == 0){
                    boolMobitel = false;
                    showToast(view, getString(R.string.phoneNumberInputMissing));
                }
                else if(email.getText().length() == 0){
                    boolEmail = false;
                    showToast(view, getString(R.string.emailInputMissing));
                }
                else if(lozinka.getText().length() == 0){
                    boolLozinka = false;
                    showToast(view, getString(R.string.passwordInputMissing));
                }
                else if(ponovljenaLozinka.getText().length() == 0){
                    showToast(view, getString(R.string.repeatPasswordInputMissing));
                }
                else if(!boolLozinka){
                    showToast(view, getString(R.string.passwordCondition));
                    lozinka.setText("");
                    ponovljenaLozinka.setText("");
                }
                else if(!lozinka.getText().toString().equals(ponovljenaLozinka.getText().toString())){
                    showToast(view, getString(R.string.passwordsNotIdentical));
                    lozinka.setText("");
                    ponovljenaLozinka.setText("");
                }
                else if (boolIme == true && boolPrezime == true && boolAdresa == true && boolMobitel == true && boolEmail == true && boolLozinka == true) {
                    kreirajKorisnika(email.getText().toString().trim(), lozinka.getText().toString().trim());
                }
                else{
                    showToast(view, getString(R.string.dataInputNotCorrect));
                }
            }
        });
    }

    private void kreirajKorisnika(String user_email, String user_lozinka) {
        fAuth.createUserWithEmailAndPassword(user_email, user_lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                imePrezime = ime.getText().toString() + " " +  prezime.getText().toString();
                                userID = fAuth.getUid();
                                repository = new Repository();

                                sharedViewModel.DodajKorisnikaUBazuSaID(userID, imePrezime, email.getText().toString(), brojMobitela.getText().toString(),
                                        lozinka.getText().toString(), "https://firebasestorage.googleapis.com/v0/b/digitalna-ribarnica-fb.appspot.com/o/default_profilna%2Fuser_image.jpg?alt=media&token=e30a1426-9be2-40d8-8e5a-b5e4c43337e7", adresa.getText().toString(),true);
                                startActivity(new Intent(RegistrationActivity.this, EmailVerificationActivity.class));
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showToast(View v, String poruka){
        if(!vrijemeTece){
            vrijemeTece = true;
            StyleableToast.makeText(this, poruka, 3, R.style.Toast).show();
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    vrijemeTece = false;
                }
            }.start();
        }
    }

}


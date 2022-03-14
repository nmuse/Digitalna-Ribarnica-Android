package com.example.digitalnaribarnica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalnaribarnica.databinding.ActivityEmailVerificationBinding;

public class EmailVerificationActivity extends AppCompatActivity {

    private Button otvoriMail;
    private TextView povratakNaPrijavu;

    ActivityEmailVerificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        getSupportActionBar().hide();

        binding = ActivityEmailVerificationBinding.inflate((getLayoutInflater()));
        View view = binding.getRoot();
        setContentView(view);

        otvoriMail = (Button) findViewById(R.id.btnOtvoriMailApp);
        otvoriMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(EmailVerificationActivity.this, EmailVerificationActivity.this.getString(R.string.noGmail), Toast.LENGTH_LONG).show();
                }
                }
        });


        povratakNaPrijavu = binding.prijavaConfirmation;
        povratakNaPrijavu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (EmailVerificationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
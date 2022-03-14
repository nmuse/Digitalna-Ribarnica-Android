package com.example.digitalnaribarnica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.digitalnaribarnica.databinding.ActivityLoadingBinding;

public class LoadingActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    ActivityLoadingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding=ActivityLoadingBinding.inflate((getLayoutInflater()));
        View view=binding.getRoot();
        setContentView(view);

        progressBar=binding.progress;

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();
        
        
    }

    private void startApp() {
        Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void doWork() {
        for (int progress=0;progress<100;progress+=20){
            try {
                Thread.sleep(150);
                progressBar.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
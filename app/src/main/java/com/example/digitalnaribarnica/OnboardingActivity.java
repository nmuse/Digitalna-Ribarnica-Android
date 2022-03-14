package com.example.digitalnaribarnica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.digitalnaribarnica.databinding.ActivityOnboardingBinding;


public class OnboardingActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    ActivityOnboardingBinding binding;
    private SliderAdapter sliderAdapter;

    private TextView[] mDots;

    private ImageButton exitOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_onboarding);

        binding = ActivityOnboardingBinding.inflate((getLayoutInflater()));
        View view = binding.getRoot();
        setContentView(view);

        mSlideViewPager=binding.slideViewPager;
        mDotLayout=binding.dotsLayout;

        exitOnboarding=binding.exitOnboardingButton;

        sliderAdapter=new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        exitOnboarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void addDotsIndicator(int position){
        mDots= new TextView[6];
        mDotLayout.removeAllViews();

        for(int i =0;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
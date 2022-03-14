package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.database.User;
import com.example.digitalnaribarnica.MainActivity;
import com.example.digitalnaribarnica.OnboardingActivity;
import com.example.digitalnaribarnica.R;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.digitalnaribarnica.databinding.FragmentHomeBinding;
import com.example.digitalnaribarnica.recycleviewer.OfferAdapter;
import com.example.repository.Data.OffersData;
import com.example.repository.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String userId = "";
    private RelativeLayout roleBuyer;
    private RelativeLayout roleSeller;
    private ImageView statusBuyer;
    private ImageView statusSeller;
    private TextView textBuyer;
    private TextView textSeller;
    private SharedViewModel sharedViewModel;
    private Boolean userFirstLogin;

    public HomeFragment(){}

    public HomeFragment(String userId) {
        this.userId = userId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.home));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);

        roleBuyer = binding.roleBuyer;
        roleSeller = binding.roleSeller;

        statusBuyer = binding.statusBuyer;
        statusSeller = binding.statusSeller;

        textBuyer = binding.textbuyer;
        textSeller = binding.textseller;

        if(textBuyer.getText().equals("Nastavi kao")){
            updateResources(getContext(), "");
        }
        else{
            updateResources(getContext(), "en");
        }


        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.DohvatiKorisnikaPoID(userId);
        sharedViewModel.userMutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                userFirstLogin = user.getUserFirstLogin();
                if (userFirstLogin) {
                    Intent intent = new Intent(getActivity(), OnboardingActivity.class);
                    startActivity(intent);
                    sharedViewModel.AzurirajKorisnikovuPrvuPrijavu(user);
                }
            }
        });


        /*if(korisnikovaPrvaPrijava==true){

        Intent intent = new Intent(getActivity(), OnboardingActivity.class);
                startActivity(intent);

            i sad samo u bazi set   korisnikovaPrvaPrijava=false

         }*/
        //Intent intent = new Intent(getActivity(), OnboardingActivity.class);
        //startActivity(intent);

        if(((RegisterActivity) getActivity()).buyer){
            statusBuyer.setVisibility(view.VISIBLE);
            statusSeller.setVisibility(view.INVISIBLE);
            textBuyer.setVisibility(view.INVISIBLE);
            textSeller.setVisibility(view.VISIBLE);
        }else{
            statusBuyer.setVisibility(view.INVISIBLE);
            statusSeller.setVisibility(view.VISIBLE);
            textBuyer.setVisibility(view.VISIBLE);
            textSeller.setVisibility(view.INVISIBLE);
        }

        roleBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity) getActivity()).changeRole(true);
                statusBuyer.setVisibility(view.VISIBLE);
                statusSeller.setVisibility(view.INVISIBLE);
                textBuyer.setVisibility(view.INVISIBLE);
                textSeller.setVisibility(view.VISIBLE);
            }
        });

        roleSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity) getActivity()).changeRole(false);
                statusBuyer.setVisibility(view.INVISIBLE);
                statusSeller.setVisibility(view.VISIBLE);
                textBuyer.setVisibility(view.VISIBLE);
                textSeller.setVisibility(view.INVISIBLE);
            }
        });



        return view;
    };


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        menu.findItem((R.id.all_offers_menu)).setVisible(false);
        menu.findItem((R.id.my_offers_menu)).setVisible(false);

        menu.findItem((R.id.action_search)).setVisible(false);
        menu.findItem((R.id.action_search)).collapseActionView();

        menu.findItem((R.id.filter_menu)).setVisible(false);
        menu.findItem((R.id.sort_offers_menu)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(true);
        if(textBuyer.getText().equals("Nastavi kao")){
            menu.findItem((R.id.current_language)).setTitle("HR");
        }
        else{
            menu.findItem((R.id.current_language)).setTitle("EN");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language:
                String[] items = {getActivity().getString(R.string.croatian), getActivity().getString(R.string.english)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getActivity().getString(R.string.chooseLanguage));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                updateResources(getContext(), "");
                                Intent intent = ((RegisterActivity) getActivity()).getIntent();
                                ((RegisterActivity) getActivity()).finish();
                                startActivity(intent);
                                break;

                            case 1:
                                updateResources(getContext(), "en");
                                Intent intent1 = ((RegisterActivity) getActivity()).getIntent();
                                ((RegisterActivity) getActivity()).finish();
                                startActivity(intent1);
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            case R.id.onboardingHelp:
                Intent intent = new Intent(getActivity(), OnboardingActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public String getLanguage(){
       if(textBuyer.getText().equals("Nastavi kao")){
           return "HR";
       }else{
           return "EN";
       }
    }

}

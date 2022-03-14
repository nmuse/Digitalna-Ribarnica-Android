package com.example.digitalnaribarnica.Fragments;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.Contacts;
import com.example.database.Messages;
import com.example.database.User;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.R;
import com.example.digitalnaribarnica.RegisterActivity;
import com.example.digitalnaribarnica.databinding.FragmentChatBinding;
import com.example.digitalnaribarnica.databinding.FragmentSearchBinding;
import com.example.digitalnaribarnica.recycleviewer.ChatAdapter;
import com.example.digitalnaribarnica.recycleviewer.OfferAdapter;
import com.example.digitalnaribarnica.recycleviewer.ReservationsAdapter;
import com.example.repository.Data.ChatData;
import com.example.repository.Data.OffersData;
import com.example.repository.Data.ReservationsData;
import com.example.repository.Listener.ContactsCallback;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Listener.FirestoreOffer;
import com.example.repository.Listener.MessageCallback;
import com.example.repository.Listener.RezervationCallback;
import com.example.repository.Repository;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ChatFragment extends Fragment {
    private String userId = "";
    private ArrayList<ChatData> chatMessagesGeneral =new ArrayList<>();
    public String searchText;

    Boolean isSearching = false;
    Boolean startDontSearch = true;
    FragmentChatBinding binding;
    RecyclerView recyclerView;
    SearchView searchViewThisSearch;
    MenuItem itemThisSearch;
    TextView emptyView;

    public ChatFragment(String userId) {
        this.userId = userId;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.chatFragment));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.colorBlue)));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerViewChat;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyView = binding.emptyView;

        Repository repository = new Repository();

        repository.DohvatiImenikPoID(userId, new ContactsCallback() {
            @Override
            public void onCallback(ArrayList<Contacts> contacts) {

                if(contacts.size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                for(Contacts d:contacts){
                    repository.DohvatiKorisnikaPoID(d.getId(), new FirestoreCallback() {
                        @Override
                        public void onCallback(User user) {

                            chatMessagesGeneral.add(new ChatData(user.getUserID(), user.getFullName(), d.getLastMessage(), user.getPhoto(), d.getLastMessageDateTime()));
                            ChatAdapter adapterChat = new ChatAdapter(getContext(), chatMessagesGeneral, userId);
                            adapterChat.setChatMessages(chatMessagesGeneral);
                            adapterChat.notifyDataSetChanged();
                            recyclerView.setAdapter(adapterChat);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            sortMessages(chatMessagesGeneral);
                        }
                    });
                }
            }
        });
        return view;
    }

    public ArrayList<ChatData> sortMessages(ArrayList<ChatData> messages) {
        Collections.sort(messages, new Comparator<ChatData>() {
            @Override
            public int compare(ChatData c1, ChatData c2) {
                return c1.getDate().compareTo(c2.getDate());
            }
        });

        Collections.reverse(messages);

        return messages;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem((R.id.action_search));
        menu.findItem((R.id.filter_menu)).setVisible(false);
        menu.findItem((R.id.all_offers_menu)).setVisible(false);
        menu.findItem((R.id.my_offers_menu)).setVisible(false);
        menu.findItem((R.id.sort_offers_menu)).setVisible(false);
        menu.findItem((R.id.onboardingHelp)).setVisible(false);
        menu.findItem((R.id.language)).setVisible(false);
        menu.findItem((R.id.current_language)).setVisible(false);
        SearchView searchView = (SearchView) item.getActionView();

        itemThisSearch = item;
        searchViewThisSearch = searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchText = query;
                searchChat(query);
                if (query.equals("")) {
                    isSearching = false;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(!startDontSearch) {
                    searchText = newText;
                    searchChat(newText);
                    if (newText.equals("")) {
                        isSearching = false;
                    }
                }
                startDontSearch = false;
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void searchChat(String search) {
        isSearching = true;
        ArrayList<ChatData> novi=new ArrayList<>();
        for(ChatData chat:chatMessagesGeneral){
            if(chat.getName().toLowerCase().contains(search.toLowerCase())){
                novi.add(chat);
            }
        }

        ChatAdapter adapterChat = new ChatAdapter(getActivity(), userId, ChatFragment.this);
        adapterChat.setChatMessages(novi);
        adapterChat.notifyDataSetChanged();

        if(novi.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        
        recyclerView.setAdapter(adapterChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(search.equals("")){
            adapterChat = new ChatAdapter(getActivity(), userId, ChatFragment.this);
            adapterChat.setChatMessages(chatMessagesGeneral);
            adapterChat.notifyDataSetChanged();
            
            if(chatMessagesGeneral.size() == 0){
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            
            recyclerView.setAdapter(adapterChat);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    public void destroySearch() {
        MenuItemCompat.collapseActionView(itemThisSearch);
    }
}

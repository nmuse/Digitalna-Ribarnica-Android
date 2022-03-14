package com.example.digitalnaribarnica.recycleviewer;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.Fragments.ChatFragment;
import com.example.digitalnaribarnica.Fragments.ConversationFragment;
import com.example.digitalnaribarnica.Fragments.OfferDetailFragment;
import com.example.digitalnaribarnica.R;
import com.example.repository.Data.ChatData;
import com.example.repository.Data.OffersData;
import com.example.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private ArrayList<ChatData> chatMessages = new ArrayList<>();
    private Context context;
    private CardView cardView;
    private String userID;
    private ChatFragment chatFragment;

    public ChatAdapter(Context context, String userID, ChatFragment chatFragment) {
        this.context = context;
        this.userID = userID;
        this.chatFragment = chatFragment;
    }

    public ChatAdapter(Context context, ArrayList<ChatData> chatMessages, String userID) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.userID = userID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        ChatAdapter.ViewHolder holder = new ChatAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.contactName.setText(chatMessages.get(position).getName());
        holder.chatZadnjaPoruka.setText(chatMessages.get(position).getLastMessage());

        Calendar calendar = DateParse.dateToCalendar(chatMessages.get(position).getDate().toDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy.");
        String textDate = dateFormat.format(calendar.getTime());
        holder.chatLastMessageDate.setText(textDate);

        Glide.with(context)
                .asBitmap()
                .load(chatMessages.get(position).getImageurl())
                .into(holder.chatImage);


        String idSugovornika = chatMessages.get(position).getIdKorisnika();

        cardView.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View v) {
                selectedFragment = new ConversationFragment(userID, idSugovornika, "Chat");
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setChatMessages(ArrayList<ChatData> chatMessages) {
        Collections.sort(chatMessages, new Comparator<ChatData>() {
            @Override
            public int compare(ChatData chatData1, ChatData chatData2) {
                return chatData1.getDate().compareTo(chatData2.getDate());
            }
        });
        Collections.reverse(chatMessages);
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView contactName;
        private ImageView chatImage;
        private TextView chatZadnjaPoruka;
        private TextView chatLastMessageDate;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            chatImage= itemView.findViewById(R.id.chatImage);
            chatZadnjaPoruka = itemView.findViewById(R.id.chatZadnjaPoruka);
            chatLastMessageDate = itemView.findViewById(R.id.chatLastMessageDate);
            cardView=itemView.findViewById(R.id.chatParent);
        }
    }
}

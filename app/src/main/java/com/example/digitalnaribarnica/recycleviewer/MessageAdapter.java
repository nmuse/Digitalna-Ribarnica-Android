package com.example.digitalnaribarnica.recycleviewer;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.Messages;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<Messages> messagesList;
    private String userId;

    public MessageAdapter(Context context, ArrayList<Messages> messagesList, String userId) {
        this.context = context;
        this.messagesList = messagesList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        holder.show_message.setText(messages.getMessage());

        Calendar calendar = DateParse.dateToCalendar(messagesList.get(position).getDatetimeMessage().toDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy.");
        String textDate = dateFormat.format(calendar.getTime());

        if(messagesList.get(position).getSender().equals(userId)) {
            holder.date_time_message_right.setText(Html.fromHtml(textDate));
        }
        else {
            holder.date_time_message_left.setText(Html.fromHtml(textDate));
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public TextView date_time_message_left;
        public TextView date_time_message_right;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            date_time_message_left = (TextView)itemView.findViewById(R.id.messageDateLeft);
            date_time_message_right = itemView.findViewById(R.id.messageDateRight);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesList.get(position).getSender().equals(userId)) {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}

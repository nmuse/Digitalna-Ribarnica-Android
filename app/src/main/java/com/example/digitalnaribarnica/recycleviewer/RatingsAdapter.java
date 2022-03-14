package com.example.digitalnaribarnica.recycleviewer;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.Review;
import com.example.database.User;
import com.example.database.Utils.DateParse;
import com.example.repository.Listener.FirestoreCallback;
import com.example.digitalnaribarnica.Fragments.RatingsFragment;
import com.example.digitalnaribarnica.R;
import com.example.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder>{

    private ArrayList<Review> ratings=new ArrayList<>();
    private Context context;
    private String userID;
    private RatingsFragment ratingsFragemnt;

    public RatingsAdapter(Context context, String userID, RatingsFragment fragment) {
        this.userID = userID;
        this.context = context;
        this.ratingsFragemnt = fragment;
    }


    @NonNull
    @Override
    public RatingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item, parent, false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsAdapter.ViewHolder holder, int position) {

        holder.ratingBar.setRating(Float.parseFloat(ratings.get(position).getRating()));
        holder.ratingDescription.setText(ratings.get(position).getComment());

        Calendar calendar = DateParse.dateToCalendar(ratings.get(position).getDate().toDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
        String textDate = dateFormat.format(calendar.getTime());
        holder.date.setText(Html.fromHtml(textDate));

        Repository repository = new Repository();
        repository.DohvatiKorisnikaPoID(ratings.get(position).getReviewer(), new FirestoreCallback() {
            @Override
            public void onCallback(User user) {
                String text = "Ocjenjivač: " + user.getFullName();
                holder.reviewer.setText(text);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public void setRatings(ArrayList<Review> allRatings) {
        this.ratings = allRatings;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private RatingBar ratingBar;
        private TextView ratingDescription;
        private TextView reviewer;
        private TextView date;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar= itemView.findViewById(R.id.ratingBar);
            ratingDescription = itemView.findViewById(R.id.comment);
            reviewer = itemView.findViewById(R.id.reviewer);
            date = itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.parent);
        }
    }
}

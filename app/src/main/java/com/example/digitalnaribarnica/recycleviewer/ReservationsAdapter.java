package com.example.digitalnaribarnica.recycleviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.badges.BadgeCallback;
import com.example.badges.BadgeID;
import com.example.badges.BadgeIDCallback;
import com.example.badges.BadgesData;
import com.example.badges.BadgesRepository;
import com.example.database.FirestoreService;
import com.example.database.Fish;
import com.example.database.User;
import com.example.database.Utils.DateParse;
import com.example.digitalnaribarnica.Fragments.ProfileFragment;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Listener.FirestoreOffer;
import com.example.digitalnaribarnica.Fragments.FragmentUserRating;
import com.example.digitalnaribarnica.Fragments.ReservationFragment;
import com.example.digitalnaribarnica.R;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Repository;
import com.example.repository.Data.OffersData;
import com.example.repository.Data.ReservationsData;

import androidx.fragment.app.Fragment;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder>{

    private ReservationFragment  reservationFragment;
    private ArrayList<ReservationsData> reservations=new ArrayList<>();
    private Context context;
    private CardView cardView;
    private ImageView deleteReservation;
    String userID ="";
    String ReservationID ="";
    Boolean history = false;
    String cameFrom;


    public ReservationsAdapter(Context context, ReservationFragment reservationFragment, String userId) {
        this.context = context;
        this.reservationFragment = reservationFragment;
        this.userID = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        ViewHolder holder= new ViewHolder(view);


        holder.deleteReservation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(reservationFragment.getActivity(), reservationFragment.getActivity().getString(R.string.warning), reservationFragment.getActivity().getString(R.string.wantToDeleteReservation));
                ReservationID = reservations.get(holder.getAdapterPosition()).getReservationID();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         if(history){
            holder.rateSeller.setText(R.string.rateBuyer);
            holder.buyer.setText(R.string.buyer);
             if((reservations.get(position).getStatus().equals("Uspješno") || reservations.get(position).getStatus().equals("Neuspješno")) && !reservations.get(position).getRatedBySeller()){
                 holder.rateSeller.setVisibility(View.VISIBLE);
             }

         }else{
             holder.rateSeller.setText(R.string.rateSeller);
             holder.buyer.setText(R.string.seller);
             if((reservations.get(position).getStatus().equals("Uspješno") || reservations.get(position).getStatus().equals("Neuspješno")) && !reservations.get(position).getRatedByBuyer()){
                 holder.rateSeller.setVisibility(View.VISIBLE);
             }
         }
        Repository repository = new Repository();
           repository.DohvatiPonuduPrekoIdPonude(reservations.get(position).getOfferID(), new FirestoreOffer() {
                @Override
                public void onCallback(ArrayList<OffersData> offersData) {

                    if(!Locale.getDefault().getDisplayLanguage().equals("English")){
                        holder.fish.setText(offersData.get(0).getName());
                    }else{
                        repository.DohvatiRibuPoImenu(offersData.get(0).getName(), new FishCallback() {
                            @Override
                            public void onCallback(ArrayList<Fish> fishes) {
                                holder.fish.setText(fishes.get(0).getNameeng());
                            }
                        });
                    }

                    BadgesRepository badgesRepository = new BadgesRepository();
                    if(history){
                        repository.DohvatiKorisnikaPoID(reservations.get(position).getCustomerID(), new FirestoreCallback() {
                            @Override
                            public void onCallback(User user) {
                                holder.textBuyer.setText(user.getFullName());
                                badgesRepository.DohvatiSveZnačke(new BadgeCallback() {
                                    @Override
                                    public void onCallback(ArrayList<BadgesData> badgesList) {
                                        badgesRepository.DohvatiIDZnackiKorisnika(reservations.get(position).getCustomerID(), new BadgeIDCallback() {
                                            @Override
                                            public void onCallback(ArrayList<BadgeID> badgeIDS) {
                                                for (int i = 0; i < badgesList.size(); i++) {
                                                    for (int j = 0; j < badgeIDS.size(); j++) {
                                                        if(badgesList.get(i).getBadgeID().equals(badgeIDS.get(j).getId())){
                                                            if(badgesList.get(i).getCategory().equals("buyer")){
                                                                Glide.with(context)
                                                                        .asBitmap()
                                                                        .load(badgesList.get(i).getBadgeURL())
                                                                        .into(holder.badgeImage);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }else{
                        repository.DohvatiKorisnikaPoID(offersData.get(0).getIdKorisnika(), new FirestoreCallback() {
                            @Override
                            public void onCallback(User user) {
                                holder.textBuyer.setText(user.getFullName());
                                badgesRepository.DohvatiSveZnačke(new BadgeCallback() {
                                    @Override
                                    public void onCallback(ArrayList<BadgesData> badgesList) {
                                        badgesRepository.DohvatiIDZnackiKorisnika(offersData.get(0).getIdKorisnika(), new BadgeIDCallback() {
                                            @Override
                                            public void onCallback(ArrayList<BadgeID> badgeIDS) {
                                                for (int i = 0; i < badgesList.size(); i++) {
                                                    for (int j = 0; j < badgeIDS.size(); j++) {
                                                        if(badgesList.get(i).getBadgeID().equals(badgeIDS.get(j).getId())){
                                                            if(badgesList.get(i).getCategory().equals("seller")){
                                                                Glide.with(context)
                                                                        .asBitmap()
                                                                        .load(badgesList.get(i).getBadgeURL())
                                                                        .into(holder.badgeImage);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    holder.location.setText(offersData.get(0).getLocation());
                    Double quantity = 0.0;
                    String text ="";
                    if (!reservations.get(position).getSmallFish().equals("0")){
                        text = text + "<b>" + context.getString(R.string.smallFish) + "</b>" +  " "  + reservations.get(position).getSmallFish() + " kg" + "<br>";
                        quantity = quantity + Double.parseDouble(reservations.get(position).getSmallFish());
                    }
                    if (!reservations.get(position).getMediumfish().equals("0")){
                        text = text + "<b>" + context.getString(R.string.mediumFish) + "</b>" +  " " + reservations.get(position).getMediumfish() + " kg" + "<br>";
                        quantity = quantity + Double.parseDouble(reservations.get(position).getMediumfish());
                    }
                    if (!reservations.get(position).getLargeFish().equals("0")){
                        text = text +  "<b>" + context.getString(R.string.largeFish) + "</b>" +  " " + reservations.get(position).getLargeFish() + " kg";
                        quantity = quantity + Double.parseDouble(reservations.get(position).getLargeFish());
                    }
                    holder.fishClassText.setText(Html.fromHtml(text));

                    switch(reservations.get(position).getStatus()){
                        case "Nepotvrđeno":
                            holder.status.setTextColor(context.getResources().getColor(R.color.colorGray));
                            break;

                        case "Potvrđeno":
                            holder.status.setTextColor(context.getResources().getColor(R.color.colorBlue));
                            break;

                        case "Neuspješno":
                            holder.status.setTextColor(context.getResources().getColor(R.color.colorRed));
                            break;

                        case "Uspješno":
                            holder.status.setTextColor(context.getResources().getColor(R.color.colorGreen));
                            break;
                    }

                    if(Locale.getDefault().getDisplayLanguage().equals("English")){
                        if(reservations.get(position).getStatus().equals("Nepotvrđeno")){
                             String statusText = "<b>" + context.getString(R.string.status) + ":</b>" +  " Unconfirmed";
                             holder.status.setText(Html.fromHtml(statusText));
                        }else if(reservations.get(position).getStatus().equals("Potvrđeno")){
                            String statusText = "<b>" + context.getString(R.string.status) + ":</b>" +  " Confirmed";
                            holder.status.setText(Html.fromHtml(statusText));
                        }else if(reservations.get(position).getStatus().equals("Uspješno")){
                            String statusText = "<b>" + context.getString(R.string.status) + ":</b>" +  " Successful";
                            holder.status.setText(Html.fromHtml(statusText));
                        }else if(reservations.get(position).getStatus().equals("Neuspješno")){
                            String statusText = "<b>" + context.getString(R.string.status) + ":</b>" +  " Unsuccessful";
                            holder.status.setText(Html.fromHtml(statusText));
                        }
                    }else{
                        String statusText = "<b>" + context.getString(R.string.status) + ":</b>" +  " "  + reservations.get(position).getStatus();
                        holder.status.setText(Html.fromHtml(statusText));
                    }

                    Double priceQuantity = quantity * Double.parseDouble(offersData.get(0).getPrice());
                    @SuppressLint("DefaultLocale") String textPrice = String.format("%.2f", priceQuantity) + " kn";
                    holder.price.setText(textPrice);

                    Glide.with(context)
                            .asBitmap()
                            .load(offersData.get(0).getImageurl())
                            .into(holder.fishImage);


                }
            });

        Calendar calendar = DateParse.dateToCalendar(reservations.get(position).getDate().toDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd.MM.yyyy.");
        String textDate = "<b>" +  context.getString(R.string.dateReservation) + "</b>" + "\n" + dateFormat.format(calendar.getTime());
        holder.date.setText(Html.fromHtml(textDate));

        holder.textBuyer.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                if(history) {
                    selectedFragment = new ProfileFragment(reservations.get(holder.getAdapterPosition()).getCustomerID(), userID, "History");
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                            selectedFragment).commit();
                }else{
                    repository.DohvatiPonuduPrekoIdPonude(reservations.get(holder.getAdapterPosition()).getOfferID(), new FirestoreOffer() {
                                @Override
                                public void onCallback(ArrayList<OffersData> offersData) {
                                    if(cameFrom.equals("ReservationHistory")){
                                        selectedFragment = new ProfileFragment(offersData.get(0).getIdKorisnika(), userID, cameFrom);
                                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                                                selectedFragment).commit();
                                    }else{
                                        selectedFragment = new ProfileFragment(offersData.get(0).getIdKorisnika(), userID, cameFrom);
                                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                                                selectedFragment).commit();
                                    }

                                }
                           });
                }
            }
        });


        holder.rateSeller.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                if(reservations.get(position).getStatus().equals("Neuspješno") || reservations.get(position).getStatus().equals("Uspješno")) {
                    repository.DohvatiPonuduPrekoIdPonude(reservations.get(position).getOfferID(), new FirestoreOffer() {
                                @Override
                                public void onCallback(ArrayList<OffersData> offersData) {
                                    if(history){
                                        selectedFragment = new FragmentUserRating(userID, reservations.get(position).getCustomerID(), reservations.get(position).getReservationID(), "seller");
                                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                                                selectedFragment).commit();
                                    }else{

                                        selectedFragment = new FragmentUserRating(userID, offersData.get(0).getIdKorisnika(), reservations.get(position).getReservationID(), "buyer");
                                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                                                selectedFragment).commit();
                                    }
                                }

                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setReservations(ArrayList<ReservationsData> reservations, String cameFrom) {
        Collections.sort(reservations, new Comparator<ReservationsData>() {
            public int compare(ReservationsData m1, ReservationsData m2) {
                return m1.getDate().compareTo(m2.getDate());
            }
        });
        Collections.reverse(reservations);
        this.reservations = reservations;
        this.cameFrom = cameFrom;
        notifyDataSetChanged();
    }

    public void setReservationsHistory(ArrayList<ReservationsData> reservations) {
        Collections.sort(reservations, new Comparator<ReservationsData>() {
            public int compare(ReservationsData m1, ReservationsData m2) {
                return m1.getDate().compareTo(m2.getDate());
            }
        });
        Collections.reverse(reservations);
        this.reservations = reservations;
        this.history = true;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fish;
        private TextView status;
        private TextView location;
        private TextView price;
        private ImageView fishImage;
        private TextView fishClassText;
        private TextView date;
        private ImageView deleteReservation;
        private ImageView badgeImage;
        private TextView rateSeller;
        private TextView buyer;
        private TextView textBuyer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fish = itemView.findViewById(R.id.reservationTitle);
            location= itemView.findViewById(R.id.textReservationLocation);
            fishImage = itemView.findViewById(R.id.reservationImage);
            price = itemView.findViewById(R.id.textReservationPrice);
            fishClassText = itemView.findViewById(R.id.textReservationGrade);
            date = itemView.findViewById(R.id.textDate);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.parentReservation);
            deleteReservation = itemView.findViewById(R.id.delete_reservation);
            buyer = itemView.findViewById(R.id.Buyer);
            rateSeller = itemView.findViewById(R.id.rate_seller);
            textBuyer = itemView.findViewById(R.id.textBuyer);
            badgeImage = itemView.findViewById(R.id.badgeImage);
        }
    }

    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(reservationFragment.getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirestoreService firestoreService = new FirestoreService();
                if(!ReservationID.equals("")) {
                    if (history) {
                        firestoreService.updateReservationSellerDeleted(ReservationID, true,  "Rezervation");
                        reservationFragment.showHistory();

                    }else{
                        if(reservationFragment.onMyReservations) {
                            firestoreService.deleteReservation(ReservationID, "Rezervation");
                            reservationFragment.refreshReservationList();
                        }
                        else if (reservationFragment.onReservationsHistory){
                            firestoreService.updateReservationBuyerDeleted(ReservationID, true,  "Rezervation");
                            reservationFragment.showReservationHistoryDelete();
                        }
                    }


                }
            }
        });
        builder.setNegativeButton(reservationFragment.getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

}

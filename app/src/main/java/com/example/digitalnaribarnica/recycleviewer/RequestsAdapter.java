package com.example.digitalnaribarnica.recycleviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import com.example.digitalnaribarnica.Fragments.ReservationFragment;
import com.example.mailservise.JavaMailAPI;
import com.example.digitalnaribarnica.R;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Repository;
import com.example.repository.Data.OffersData;
import com.example.repository.Data.ReservationsData;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private ReservationFragment  reservationFragment;
    private ArrayList<ReservationsData> reservations=new ArrayList<>();
    private Context context;
    private CardView cardView;
    String userID ="";
    String OfferID ="";
    String smallQuantity ="";
    String mediumQuantity ="";
    String largeQuantity="";
    String ReservationID ="";
    String reservationDate ="";
    String buyerID = "";
    String cameFrom ="";

    public RequestsAdapter(Context context, ReservationFragment reservationFragment, String userId) {
        this.context = context;
        this.reservationFragment = reservationFragment;
        this.userID = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAccept(reservationFragment.getActivity(), reservationFragment.getActivity().getString(R.string.warning), reservationFragment.getActivity().getString(R.string.wantToConfirmReservation));
                ReservationID = reservations.get(holder.getAdapterPosition()).getReservationID();
                OfferID = reservations.get(holder.getAdapterPosition()).getOfferID();
                smallQuantity = reservations.get(holder.getAdapterPosition()).getSmallFish();
                mediumQuantity = reservations.get(holder.getAdapterPosition()).getMediumfish();
                largeQuantity = reservations.get(holder.getAdapterPosition()).getLargeFish();
                buyerID = reservations.get(holder.getAdapterPosition()).getCustomerID();
                Calendar calendar = DateParse.dateToCalendar(reservations.get(holder.getAdapterPosition()).getDate().toDate());
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd.MM.yyyy.");
                reservationDate = dateFormat.format(calendar.getTime());
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDecline(reservationFragment.getActivity(), reservationFragment.getActivity().getString(R.string.warning), reservationFragment.getActivity().getString(R.string.wantToDeleteReservation));
                ReservationID = reservations.get(holder.getAdapterPosition()).getReservationID();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Repository repository = new Repository();
        FirestoreService firestoreService=new FirestoreService();

        repository.DohvatiPonuduPrekoIdPonude(reservations.get(position).getOfferID(), new FirestoreOffer() {
            @Override
            public void onCallback(ArrayList<OffersData> offersData) {
                holder.location.setText(offersData.get(0).getLocation());

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

        repository.DohvatiKorisnikaPoID(reservations.get(position).getCustomerID(), new FirestoreCallback() {
            @Override
            public void onCallback(User user) {
                holder.buyer.setText(user.getFullName());
            }
        });

        BadgesRepository badgesRepository = new BadgesRepository();
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

        holder.buyer.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                selectedFragment = new ProfileFragment(reservations.get(holder.getAdapterPosition()).getCustomerID(), userID, cameFrom);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setRequests(ArrayList<ReservationsData> reservations, String cameFrom) {
        this.reservations = reservations;
        this.cameFrom = cameFrom;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fish;
        private TextView location;
        private TextView price;
        private ImageView fishImage;
        private ImageView badgeImage;
        private TextView fishClassText;
        private TextView date;
        private TextView buyer;
        private ImageButton accept;
        private ImageButton decline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fish = itemView.findViewById(R.id.requestTitle);
            location= itemView.findViewById(R.id.textReservationLocation);
            fishImage = itemView.findViewById(R.id.requestImage);
            price = itemView.findViewById(R.id.textReservationPrice);
            fishClassText = itemView.findViewById(R.id.textReservationGrade);
            date = itemView.findViewById(R.id.textDate);
            buyer = itemView.findViewById(R.id.textBuyer);
            accept = itemView.findViewById(R.id.request_accept);
            badgeImage = itemView.findViewById(R.id.badgeImage);
            decline = itemView.findViewById(R.id.request_decline);
            cardView = itemView.findViewById(R.id.parentReservation);
        }
    }

    public void showDialogAccept(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(reservationFragment.getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Repository repository = new Repository();
                FirestoreService firestoreService = new FirestoreService();
                if(!ReservationID.equals("")) {
                    firestoreService.updateReservationStatus(ReservationID, "Potvrđeno", "Rezervation");
                    repository.DohvatiPonuduPrekoIdPonude(OfferID, new FirestoreOffer() {
                        @Override
                        public void onCallback(ArrayList<OffersData> offersData) {

                            String currentSmall = offersData.get(0).getSmallFish();
                            Double updatedSmall = Math.round((Double.parseDouble(currentSmall) - Double.parseDouble(smallQuantity))*100.0)/100.0;

                            String currentMedium = offersData.get(0).getMediumFish();
                            Double updatedMedium = Math.round((Double.parseDouble(currentMedium) - Double.parseDouble(mediumQuantity))*100.0)/100.0;

                            String currentLarge = offersData.get(0).getLargeFish();
                            Double updatedLarge = Math.round((Double.parseDouble(currentLarge) - Double.parseDouble(largeQuantity))*100.0)/100.0;

                            if(updatedSmall < 0 || updatedMedium < 0 || updatedLarge < 0){
                                StyleableToast.makeText(reservationFragment.getActivity(), reservationFragment.getActivity().getString(R.string.fishNotAvailable), 3, R.style.Toast).show();
                                firestoreService.deleteReservation(ReservationID, "Rezervation");
                            } else {
                                final String[] message = {""};
                                final String[] messageSeller = {""};
                                final String[] userEmail = {""};
                                final String[] sellerEmail = {""};

                                repository.DohvatiKorisnikaPoID(buyerID, new FirestoreCallback() {
                                    @Override
                                    public void onCallback(User user) {
                                        userEmail[0] = user.getEmail();
                                        String messageBuyer = user.getFullName() + reservationFragment.getActivity().getString(R.string.offerConfirmedBigText) +
                                                reservationFragment.getActivity().getString(R.string.offerConfirmedBigText2);
                                        message[0] = message[0] + messageBuyer;

                                        String messageForSeller =reservationFragment.getActivity().getString(R.string.offerConfirmedBigTextSeller);
                                        messageSeller[0] = messageSeller[0] + messageForSeller;

                                        String messageForSeller2 = reservationFragment.getActivity().getString(R.string.buyerData) + user.getFullName() + "\nE-mail: " + user.getEmail() + reservationFragment.getActivity().getString(R.string.phoneNumber) + user.getPhone()
                                                + reservationFragment.getActivity().getString(R.string.address) + user.getAdress() + "\n" + "\n";
                                        messageSeller[0] = messageSeller[0] + messageForSeller2;

                                        repository.DohvatiKorisnikaPoID(userID, new FirestoreCallback() {
                                            @Override
                                            public void onCallback(User user) {
                                                sellerEmail[0] = user.getEmail();
                                                String messageString = reservationFragment.getActivity().getString(R.string.sellerData) + user.getFullName() + "\nE-mail: " + user.getEmail() + reservationFragment.getActivity().getString(R.string.phoneNumber) + user.getPhone()
                                                        + reservationFragment.getActivity().getString(R.string.address) + user.getAdress() + "\n" + "\n";
                                                message[0] = message[0] + messageString;

                                                repository.DohvatiPonuduPrekoIdPonude(OfferID, new FirestoreOffer() {
                                                    @Override
                                                    public void onCallback(ArrayList<OffersData> offersData) {
                                                        Double quantity= Double.parseDouble(smallQuantity) + Double.parseDouble(mediumQuantity) + Double.parseDouble(largeQuantity);
                                                        Double priceQuantity = quantity * Double.parseDouble(offersData.get(0).getPrice());
                                                        @SuppressLint("DefaultLocale") String textPrice = String.format("%.2f", priceQuantity);

                                                        String messageOffer = reservationFragment.getActivity().getString(R.string.messageOffer1) + offersData.get(0).getName() + reservationFragment.getActivity().getString(R.string.messageOffer2) + smallQuantity + reservationFragment.getActivity().getString(R.string.messageOffer3) + mediumQuantity +
                                                                reservationFragment.getActivity().getString(R.string.messageOffer4) + largeQuantity + reservationFragment.getActivity().getString(R.string.messageOffer5) + offersData.get(0).getPrice() + reservationFragment.getActivity().getString(R.string.messageOffer6) + textPrice
                                                                + reservationFragment.getActivity().getString(R.string.messageOffer7) + reservationDate + reservationFragment.getActivity().getString(R.string.messageOffer8);
                                                        message[0] = message[0] + messageOffer;

                                                        messageSeller[0] = messageSeller[0] + messageOffer;

                                                        //brisanje svih rezervacija koje su povezane s ponudom čija je dostupna količina jednaka nuli
                                                        if(updatedSmall == 0 && updatedMedium == 0 && updatedLarge == 0) {
                                                            for (int i = 0; i < reservations.size(); i++) {
                                                                if (reservations.get(i).getOfferID().equals(OfferID)) {
                                                                    firestoreService.updateOfferQuantity(OfferID, updatedSmall.toString(), updatedMedium.toString(), updatedLarge.toString(), "Offers");
                                                                    firestoreService.deleteReservation(reservations.get(i).getReservationID(), "Rezervation");
                                                                    reservationFragment.refreshRequestsList();
                                                                }
                                                            }

                                                            //Izmjena statusa prilikom smanjenja dostupne količine ponude na nulu
                                                            firestoreService.updateOfferStatus(OfferID, "Neaktivna", "Offers");
                                                            reservationFragment.refreshRequestsList();

                                                        } else {
                                                            firestoreService.updateOfferQuantity(OfferID, updatedSmall.toString(), updatedMedium.toString(), updatedLarge.toString(), "Offers");
                                                            reservationFragment.refreshRequestsList();
                                                        }

                                                        sendMail(userEmail[0], message[0]);
                                                        sendMail(sellerEmail[0], messageSeller[0]);
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                            reservationFragment.refreshRequestsList();
                        }
                    });
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

    private void sendMail(String email, String text) {
        String subject = reservationFragment.getActivity().getString(R.string.mailReservationConfirmed);

        JavaMailAPI javaMailAPI = new JavaMailAPI(reservationFragment.getActivity(), email, subject, text);
        javaMailAPI.execute();
    }


    public void showDialogDecline(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(reservationFragment.getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Repository repository = new Repository();
                FirestoreService firestoreService = new FirestoreService();
                if(!ReservationID.equals("")) {
                    firestoreService.deleteReservation(ReservationID, "Rezervation");
                    reservationFragment.refreshRequestsList();
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
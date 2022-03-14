package com.example.digitalnaribarnica.recycleviewer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.badges.BadgeCallback;
import com.example.badges.BadgeID;
import com.example.badges.BadgeIDCallback;
import com.example.badges.BadgesData;
import com.example.badges.BadgesRepository;
import com.example.badges.CustomDialogBadge;
import com.example.database.FirestoreService;
import com.example.database.Fish;
import com.example.database.User;
import com.example.digitalnaribarnica.Fragments.EditOfferFragment;
import com.example.digitalnaribarnica.Fragments.FragmentUserRating;
import com.example.digitalnaribarnica.Fragments.OfferDetailFragment;
import com.example.digitalnaribarnica.Fragments.ProfileFragment;
import com.example.digitalnaribarnica.Fragments.SearchFragment;
import com.example.digitalnaribarnica.R;
import com.example.repository.Data.ReservationsData;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Listener.RezervationCallback;
import com.example.repository.Repository;
import com.example.repository.Data.OffersData;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class
OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private ArrayList<OffersData> offers=new ArrayList<>();
    private Context context;
    private CardView cardView;
    private String userID;
    private SearchFragment searchFragment;
    private TextView fishClassText;
    private Boolean myOffers = false;
    Repository repository = new Repository();
    String OfferID;


    public OfferAdapter(Context context, String userID, SearchFragment fragment) {
        this.userID = userID;
        this.context = context;
        this.searchFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item, parent, false);
        ViewHolder holder= new ViewHolder(view);
        fishClassText = view.findViewById(R.id.fish_class_text);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(myOffers){
            holder.btnEditOffer.setVisibility(View.VISIBLE);
            holder.btnDeleteOffer.setVisibility(View.VISIBLE);
        }else{
            holder.btnEditOffer.setVisibility(View.INVISIBLE);
            holder.btnDeleteOffer.setVisibility(View.INVISIBLE);
        }

        if(!Locale.getDefault().getDisplayLanguage().equals("English")){
            holder.fish.setText(offers.get(position).getName());
        }else{
            repository.DohvatiRibuPoImenu(offers.get(position).getName(), new FishCallback() {
                @Override
                public void onCallback(ArrayList<Fish> fishes) {
                    holder.fish.setText(fishes.get(0).getNameeng());
                }
            });
        }

        holder.location.setText(offers.get(position).getLocation());
        String priceText = offers.get(position).getPrice() + " " + context.getString(R.string.knperkg);
        holder.price.setText(priceText);
        holder.fishClassText.setText("");

        if(offers.get(position).getSmallFish()!= null && !offers.get(position).getSmallFish().equals("0") && !offers.get(position).getSmallFish().equals("0.0")){
            holder.fishClassText.append(context.getString(R.string.small));
        }

        if(offers.get(position).getMediumFish()!= null && !offers.get(position).getMediumFish().equals("0") && !offers.get(position).getMediumFish().equals("0.0")) {
            if(!holder.fishClassText.getText().toString().equals("")){
                holder.fishClassText.append(", ");
                holder.fishClassText.append(context.getString(R.string.medium));
            } else{
                holder.fishClassText.append(context.getString(R.string.medium));
            }
        }

        if(offers.get(position).getLargeFish()!= null && !offers.get(position).getLargeFish().equals("0") && !offers.get(position).getLargeFish().equals("0.0")) {
            if(!holder.fishClassText.getText().toString().equals("")){
                holder.fishClassText.append(", ");
                holder.fishClassText.append(context.getString(R.string.large));
            } else{
                holder.fishClassText.append(context.getString(R.string.large));
            }
        }

        if(holder.fishClassText.getText().toString().equals("")){
            fishClassText.setText("");
        }

        holder.seller.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                selectedFragment = new ProfileFragment(offers.get(holder.getAdapterPosition()).getIdKorisnika(), userID, "Search");
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        holder.btnEditOffer.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;
            @Override
            public void onClick(View view) {
                selectedFragment = new EditOfferFragment(offers.get(holder.getAdapterPosition()).getOfferID(), userID, searchFragment.getLastVisited());
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        holder.btnDeleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfferID = offers.get(holder.getAdapterPosition()).getOfferID();
                showDialog(searchFragment.getActivity(), searchFragment.getActivity().getString(R.string.warning), searchFragment.getActivity().getString(R.string.wantToDeleteOffer));
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            Fragment selectedFragment = null;

            @Override
            public void onClick(View v) {
                searchFragment.destroySearch();
                selectedFragment = new OfferDetailFragment(offers.get(holder.getAdapterPosition()).getOfferID(), userID, searchFragment.getLastVisited());
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                        selectedFragment).commit();
            }
        });

        Glide.with(context)
                .asBitmap()
                .load(offers.get(position).getImageurl())
                .into(holder.fishImage);

        String userID = offers.get(position).getIdKorisnika();
        Repository repository = new Repository();
        FirestoreService firestoreService=new FirestoreService();

        BadgesRepository badgesRepository = new BadgesRepository();
        badgesRepository.DohvatiSveZnačke(new BadgeCallback() {
            @Override
            public void onCallback(ArrayList<BadgesData> badgesList) {
                badgesRepository.DohvatiIDZnackiKorisnika(userID, new BadgeIDCallback() {
                    @Override
                    public void onCallback(ArrayList<BadgeID> badgeIDS) {
                        for (int i = 0; i < badgesList.size(); i++) {
                            for (int j = 0; j < badgeIDS.size(); j++) {
                                if(badgesList.get(i).getBadgeID().equals(badgeIDS.get(j).getId())){
                                    if(badgesList.get(i).getCategory().equals("seller")){
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(badgesList.get(i).getBadgeURL())
                                                .into(holder.trophyImage);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });

        repository.DohvatiKorisnikaPoID(userID, user -> {
            String textSeller =  user.getFullName();
            holder.seller.setText(Html.fromHtml(textSeller));
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public void setOffers(ArrayList<OffersData> offers) {
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getStatus().equals("Deleted")) {
                offers.remove(offers.get(i));
                i = i - 1;
            }
        }

        Collections.sort(offers, new Comparator<OffersData>() {
            @Override
            public int compare(OffersData offersData, OffersData t1) {
                return offersData.getDate().compareTo(t1.getDate());
            }
        });
        Collections.reverse(offers);
        this.offers = offers;
        notifyDataSetChanged();
    }

    public void setOffersWithoutSortDate(ArrayList<OffersData> offers) {
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getStatus().equals("Deleted")) {
                offers.remove(offers.get(i));
                i = i - 1;
            }
        }

        this.offers = offers;
        notifyDataSetChanged();
    }

    public void ShowIcons(){
        this.myOffers=true;
    }
    

    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            Fragment selectedFragment =null;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Repository repository = new Repository();
                final Boolean[] deletable = {false};
                repository.DohvatiRezervacije1(new RezervationCallback() {
                    @Override
                    public void onCallback(ArrayList<ReservationsData> rezervations) {
                        for (int i = 0; i < rezervations.size(); i++) {
                            if((rezervations.get(i).getStatus().equals("Nepotvrđeno") || rezervations.get(i).getStatus().equals("Potvrđeno"))
                                    && rezervations.get(i).getOfferID().equals(OfferID)){
                                deletable[0] = true;

                            }
                        }
                        if(!deletable[0]){
                            //repository.DeleteOffer(OfferID, "Offers");
                            repository.UpdateOfferStatus(OfferID,"Deleted");
                            StyleableToast.makeText(context, searchFragment.getActivity().getString(R.string.offerSuccessfullyDeleted), 3, R.style.ToastGreen).show();
                            searchFragment.getMyOffers();
                        }else{
                            StyleableToast.makeText(context, searchFragment.getActivity().getString(R.string.reservationsExistsCantDelete), 3, R.style.Toast).show();
                        }
                    }
                });

            }

        });
        builder.setNegativeButton(searchFragment.getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView fish;
        private TextView location;
        private ImageView fishImage;
        private TextView price;
        private TextView fishClassText;
        private ImageView trophyImage;
        private TextView seller;
        private ImageView btnEditOffer;
        private ImageView btnDeleteOffer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fish = itemView.findViewById(R.id.textOfferName);
            location= itemView.findViewById(R.id.textOfferLocation);
            fishImage = itemView.findViewById(R.id.offerImage);
            price = itemView.findViewById(R.id.textOfferPrice);
            fishClassText = itemView.findViewById(R.id.textOfferFishClass);
            trophyImage = itemView.findViewById(R.id.trophyOfferImage);
            seller = itemView.findViewById(R.id.textSeller);
            btnEditOffer = itemView.findViewById(R.id.btnEditOffer);
            btnDeleteOffer = itemView.findViewById(R.id.btnDeleteOffer);
            cardView=itemView.findViewById(R.id.parent);
        }
    }
}
package com.example.digitalnaribarnica.ViewModel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.badges.BadgeID;
import com.example.badges.BadgeIDCallback;
import com.example.badges.BadgesData;
import com.example.badges.BadgesRepository;
import com.example.database.CallbackUser;
import com.example.database.FirestoreService;
import com.example.database.Fish;
import com.example.database.Location;
import com.example.database.Review;
import com.example.database.User;
import com.example.repository.Data.OffersData;
import com.example.repository.Data.ReservationsData;
import com.example.badges.BadgeCallback;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Listener.FirestoreOffer;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Listener.LocationCallback;
import com.example.repository.Listener.ReviewCallback;
import com.example.repository.Listener.RezervationCallback;
import com.example.repository.Repository;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    public MutableLiveData<ArrayList<BadgesData>> badgesDataArrayList= new MutableLiveData<>();
    public MutableLiveData<ArrayList<BadgeID>> badgesIDDataArrayList= new MutableLiveData<>();
    public MutableLiveData<User> userMutableLiveData= new MutableLiveData<>();
    public MutableLiveData<ArrayList<Review>> reviewDataArrayList= new MutableLiveData<>();
    public MutableLiveData<ArrayList<OffersData>> offerDataArrayList= new MutableLiveData<>();
    public MutableLiveData<ArrayList<Fish>> fishDataArrayList= new MutableLiveData<>();
    public MutableLiveData<ArrayList<Location>> locationDataArrayList= new MutableLiveData<>();
    public MutableLiveData<ArrayList<ReservationsData>> reservationDataArrayList= new MutableLiveData<>();
    public MutableLiveData<Uri> photoUrlData= new MutableLiveData<>();
    public MutableLiveData<String> fishNameEng= new MutableLiveData<>();
    private Repository repository=new Repository();
    private BadgesRepository badgesRepository= new BadgesRepository();

    public SharedViewModel() {
    }

    public void VratiSveZnacke(){

        badgesRepository.DohvatiSveZnačke(new BadgeCallback() {
            @Override
            public void onCallback(ArrayList<BadgesData> badges) {
                badgesDataArrayList.setValue(badges);
            }
        });
    }

    public void VratiSveIDZnackeKorisnika(String userID){
        badgesRepository.DohvatiIDZnackiKorisnika(userID, new BadgeIDCallback() {
            @Override
            public void onCallback(ArrayList<BadgeID> badgesID) {
                badgesIDDataArrayList.setValue(badgesID);
            }
        });
    }

    public void DohvatiKorisnikaPoID(String userId){
        repository.DohvatiKorisnikaPoID(userId, new FirestoreCallback() {
            @Override
            public void onCallback(User user) {
                userMutableLiveData.setValue(user);
            }
        });
    }

    public void DohvatiOcjenePoID(String userId){
        repository.DohvatiOcjenePoID(userId, new ReviewCallback() {
            @Override
            public void onCallback(ArrayList<Review> reviews) {
                reviewDataArrayList.setValue(reviews);
            }
        });
    }

    public void DohvatiKorisnikaPoEmailu(String email){
        repository.DohvatiKorisnikaPoEmailu(email, new FirestoreCallback() {
            @Override
            public void onCallback(User user) {
                userMutableLiveData.setValue(user);
            }
        });
    }

    public void AzurirajKorisnika(User user){
        repository.UpdateKorisnika(user);
    }

    public void DodajSlikuKorisnika(Uri image, String userId){
        repository.DodajSlikuKorisnika(image,userId);
    }

    public void DohvatiSlikuKorisnikaPoId(String userID){
        FirestoreService.getProfilePhotoWithID(userID, new CallbackUser() {
            @Override
            public void onCallback(Uri slika) {
                photoUrlData.setValue(slika);
            }
        });
    }

    public void DohvatiPonuduPrekoID(String offerID){
        repository.DohvatiPonuduPrekoIdPonude(offerID, new FirestoreOffer() {
            @Override
            public void onCallback(ArrayList<OffersData> offersData) {
                offerDataArrayList.setValue(offersData);
            }
        });
    }

    public void DohvatiSvePonude(){
        repository.DohvatiSvePonude(new FirestoreOffer() {
            @Override
            public void onCallback(ArrayList<OffersData> offersData) {
                offerDataArrayList.setValue(offersData);
            }
        });
    }

    public void DodajRezervaciju(String offerID, Timestamp date, String price, String small, String medium, String large, String userID, String status){
        repository.DodajRezervacijuAutoID(offerID, date, price, small, medium, large, userID, status);
    }

    public void DodajOcjenu(String ratedUser,String rating, String comment, String userID, Timestamp date){
        repository.DodajOcjenu(ratedUser, rating, comment, userID, date);
    }

    public void DohvatiRibe(){
        repository.DohvatiRibe(new FishCallback() {
            @Override
            public void onCallback(ArrayList<Fish> fishes) {
                fishDataArrayList.setValue(fishes);
            }
        });
    }

    public void DohvatiLokacije(){
        repository.DohvatiLokacije(new LocationCallback() {
            @Override
            public void onCallback(ArrayList<Location> locations) {
                locationDataArrayList.setValue(locations);
            }
        });
    }

    public void DodajPonuduSAutoID(String name, String location, String imageurl, String price, String idKorisnika, String smallFish, String mediumFish, String largeFish, Timestamp date){
        repository.DodajPonuduSAutoID(name,location,imageurl,price,idKorisnika,smallFish,mediumFish,largeFish, date);
    }

    public void DohvatiRezervacije(){
        repository.DohvatiRezervacije1(new RezervationCallback() {
            @Override
            public void onCallback(ArrayList<ReservationsData> rezervations) {
                reservationDataArrayList.setValue(rezervations);
            }
        });
    }

    public void DohvatiPonuduPrekoIdPonude(String ponudaId){
        repository.DohvatiPonuduPrekoIdPonude(ponudaId, new FirestoreOffer() {
            @Override
            public void onCallback(ArrayList<OffersData> offersData) {
                offerDataArrayList.setValue(offersData);
            }
        });
    }

    public void DeleteReservation(String reservationID){
        repository.DeleteReservation(reservationID);
    }


    public Boolean ProvjeriPassword(String password, String SHA256){
        return repository.ProvjeriPassword(password, SHA256);
    }

    public void DodajKorisnikaUBazuBezLozinke(String GoogleUserID, String GoogleUserName, String GoogleUserEmail,  String GoogleUserPhoto){
        repository.DodajKorisnikaUBazuBezLozinke(GoogleUserID, GoogleUserName, GoogleUserEmail, GoogleUserPhoto);
    }

    public void DodajKorisnikaUBazuSaID(String userID, String imePrezime, String email, String brojMobitela, String lozinka, String photo, String adresa, Boolean userFirstLogin){
        repository.DodajKorisnikaUBazuSaID(userID, imePrezime, email, brojMobitela, lozinka, photo, adresa, userFirstLogin);
    }

    public void AzurirajKorisnikovuPrvuPrijavu(User user){
        repository.UpdateKorisnikovuPrvuPrijavu(user);
    }

    public void AzurirajDostupneKolicinePonude(String offerID, String smallFish, String mediumFish, String largeFish, String collection){
        repository.UpdateOfferQuantity(offerID, smallFish, mediumFish, largeFish, collection);
    }

    public void AzurirajStatusPonude(String offerID, String status){
        repository.UpdateOfferStatus(offerID, status);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void DohvatiRibuPoImenu(String name) {
        repository.DohvatiRibuPoImenu(name, new FishCallback() {
            @Override
            public void onCallback(ArrayList<Fish> fishes) {
                fishNameEng.setValue(fishes.get(0).getNameeng());
            }
        });
    }

    public void AzurirajRating(String ratedUser, String ratingTotal) {
        repository.updateRating(ratedUser, ratingTotal);
    }

    public void AzurirajStatusRezervacijeOcjena(String reservationID, String ocijenjeno, String rezervation) {
        repository.updateReservationRatedStatus(reservationID, ocijenjeno, rezervation);

    }

    public void AzurirajStatusOcijenioProdavatelj(String reservationID, boolean b) {
        repository.AzurirajStatusOcijenioProdavatelj(reservationID, true);
    }

    public void AzurirajStatusOcijenioKupac(String reservationID, boolean b) {
        repository.AzurirajStatusOcijenioKupac(reservationID, true);
    }
}
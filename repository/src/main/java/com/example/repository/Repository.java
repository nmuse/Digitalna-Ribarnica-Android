package com.example.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.database.Contacts;
import com.example.database.FirestoreService;
import com.example.database.Fish;
import com.example.database.Location;
import com.example.database.Messages;
import com.example.database.Offer;
import com.example.database.Review;
import com.example.database.Rezervation;
import com.example.database.User;
import com.example.database.Utils.SHA256;
import com.example.repository.Data.OffersData;
import com.example.repository.Data.ReservationsData;

import com.example.repository.Listener.ContactsCallback;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Listener.FirestoreOffer;
import com.example.repository.Listener.FishCallback;
import com.example.repository.Listener.LocationCallback;
import com.example.repository.Listener.MessageCallback;
import com.example.repository.Listener.ReviewCallback;
import com.example.repository.Listener.RezervationCallback;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Repository {
    FirestoreService firestoreService;
    public Repository() {
        firestoreService=new FirestoreService();
    }

    public void DohvatiKorisnikaPoEmailu(String email, FirestoreCallback firestoreCallback){
        firestoreService.getCollectionWithField("Users","email",email).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    User user=new Gson().fromJson(json,User.class);
                    firestoreCallback.onCallback(user);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    Log.d("TEST",user.getFullName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiKorisnikaPoID(String userID, FirestoreCallback firestoreCallback){
        firestoreService.getCollectionWithField("Users","userID",userID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> objekti=queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d: objekti){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json = new Gson().toJson(d.getData());
                    User user= new Gson().fromJson(json,User.class);
                    firestoreCallback.onCallback(user);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DodajImenikKorisnik(String collection,String userId){
        firestoreService.addCollectionToDocument(collection,userId);
    }

    public void DodatiKorisnikaUImenik(String collection,String userCurrentlyID, String newUserID){
        firestoreService.addUserToContacts(collection,userCurrentlyID,newUserID);
    }

    public void DodajPorukuKorisnik(String collection,String userCurrentlyID, String newUserID,String posiljate, String sadrzaj, Timestamp dateTimeMessage){
        firestoreService.addPorukaToUser(collection,userCurrentlyID, newUserID,posiljate,sadrzaj,dateTimeMessage);
    }
    public void DohvatiPorukeKorisnika(String collection, String userCurrentlyID, String newUserID, MessageCallback messageCallback){
        firestoreService.getAllPorukeOfUser("Contacts",userCurrentlyID,newUserID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> objekti=queryDocumentSnapshots.getDocuments();
                ArrayList<Messages> porukeMNOZINA=new ArrayList<>();
                for(DocumentSnapshot d: objekti){
                    d.getData();
                    String json = new Gson().toJson(d.getData());
                    Messages porukaJEDNINA= new Gson().fromJson(json, Messages.class);
                    porukeMNOZINA.add(porukaJEDNINA);
                }
                messageCallback.onCallback(porukeMNOZINA);
            }
        });
    }

    public void DohvatiBrojPorukaKorisnika(String collection, String userCurrentlyID, String newUserID,Long number, MessageCallback messageCallback){
        firestoreService.getLastNumberOfPorukeOfUser("Contacts",userCurrentlyID,newUserID,number).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> objekti=queryDocumentSnapshots.getDocuments();
                ArrayList<Messages> porukeMNOZINA=new ArrayList<>();
                for(DocumentSnapshot d: objekti){
                    d.getData();
                    String json = new Gson().toJson(d.getData());
                    Messages porukaJEDNINA= new Gson().fromJson(json, Messages.class);
                    porukeMNOZINA.add(porukaJEDNINA);
                }
                messageCallback.onCallback(porukeMNOZINA);
            }
        });
    }


    public void DohvatiImenikPoID(String userID, ContactsCallback contactsCallback){
        firestoreService.getCollectionWithCollection("Contacts",userID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> objekti=queryDocumentSnapshots.getDocuments();
                ArrayList<Contacts> contacts=new ArrayList<>();
                for(DocumentSnapshot d: objekti){
                    d.getData();
                    String json = new Gson().toJson(d.getData());
                    Contacts contact= new Gson().fromJson(json, Contacts.class);
                    contacts.add(contact);
                }
                contactsCallback.onCallback(contacts);
            }
        });
    }

    public void DodajKorisnikaUBazu(String name, String email, String phone,String password,String photo,String adress){
        try {
            firestoreService.writeNewUserWithoutID(name,email,phone, SHA256.toHexString(SHA256.getSHA(password)),photo,adress,"Users");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //dodavanje novog korisnika sa ID
    public void DodajKorisnikaUBazuSaID(String id, String name, String email, String phone,String password,String photo,String adress,Boolean userfirstLogin){
        try {
            firestoreService.writeNewUser(id,name,email,phone, SHA256.toHexString(SHA256.getSHA(password)),photo,adress,userfirstLogin,"Users");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void UpdateKorisnikovuPrvuPrijavu(User korisnik){
        firestoreService.updateUserFirstLogin(korisnik,"Users");
    }

    public void DohvatiPonuduPrekoIdPonude(String id, FirestoreOffer firestoreCallback){
        firestoreService.getCollectionWithField("Offers","offerID",id).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<OffersData> offersDataArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    OffersData offersData=new Gson().fromJson(json,OffersData.class);
                    offersDataArrayList.add(offersData);
                }
                firestoreCallback.onCallback(offersDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiSvePonude(FirestoreOffer firestoreCallback){
        firestoreService.getCollection("Offers").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<OffersData> offersDataArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    OffersData offersData=new Gson().fromJson(json,OffersData.class);
                    offersDataArrayList.add(offersData);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST",offersData.getFullName());
                }
                firestoreCallback.onCallback(offersDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiRibe(FishCallback firestoreCallback){
        firestoreService.getCollection("Fish").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<Fish> fishArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    Fish fish=new Gson().fromJson(json,Fish.class);
                    fishArrayList.add(fish);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST",offersData.getFullName());
                }
                firestoreCallback.onCallback(fishArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiRibuPoImenu(String name, FishCallback firestoreCallback){
        firestoreService.getCollectionWithField("Fish","name", name).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<Fish> fishArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    Fish fish=new Gson().fromJson(json,Fish.class);
                    fishArrayList.add(fish);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST",offersData.getFullName());
                }
                firestoreCallback.onCallback(fishArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiLokacije(LocationCallback firestoreCallback){
        firestoreService.getCollection("Location").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<Location> locationArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    Location location=new Gson().fromJson(json,Location.class);
                    locationArrayList.add(location);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST",offersData.getFullName());
                }
                firestoreCallback.onCallback(locationArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiRezervacije1(RezervationCallback firestoreCallback){
        firestoreService.getCollection("Rezervation").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<ReservationsData> rezervationArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    ReservationsData rezervation=new Gson().fromJson(json,ReservationsData.class);
                    rezervationArrayList.add(rezervation);
                }
                firestoreCallback.onCallback(rezervationArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DodajOcjenu(String ratedUser, String rating, String comment, String reviewer, Timestamp date){
        Review review = new Review(ratedUser, rating, comment, reviewer, date);
        firestoreService.writeReview(review,"Review");
    }

    public void DodajRezervacijuAutoID(String offerID, Timestamp date, String price, String smallFish, String mediumFish, String largeFish, String customerID, String status){
        Rezervation reservation = new Rezervation(offerID, date, price, smallFish, mediumFish, largeFish, customerID, status);
        firestoreService.writeReservationWithAutoID(reservation,"Rezervation");
    }


    public void DodajPonuduSAutoID(String name,String location, String imageurl, String price, String idKorisnika, String smallFish, String mediumFish, String largeFish, Timestamp date){
        Offer offer=new Offer(name, location, imageurl, price, idKorisnika, smallFish, mediumFish, largeFish, date, 1);
        firestoreService.writeOfferWithAutoID(offer,"Offers");
    }

    public boolean ProvjeriPassword(String sha256,String unesenaLozinka){
        return sha256.equals(unesenaLozinka);
    }

    public void DohvatiOcjenePoID(String id, ReviewCallback firestoreCallback){
        firestoreService.getCollectionWithField("Review","ratedUser",id).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<Review> reviewList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    //String fullName = d.getString("fullName");
                    //Toast.makeText(MainActivity.this, fullName, Toast.LENGTH_LONG).show();
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    Review offersData=new Gson().fromJson(json,Review.class);
                    reviewList.add(offersData);
                    //Toast.makeText(MainActivity.this, user.getFullName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST",offersData.getFullName());
                }
                firestoreCallback.onCallback(reviewList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public String random(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < length; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public void UpdateKorisnika(User korisnik){
        firestoreService.updateUser(korisnik,"Users");
    }

    public void DodajSlikuKorisnika(Uri image, String userId){
        FirestoreService.addPhotoWithID(image,userId);
    }

    public void DeleteReservation(String reservationID){
        FirestoreService firestoreService = new FirestoreService();
        firestoreService.deleteReservation(reservationID,"Rezervation");
    }

    public void UpdateOfferQuantity(String offerID, String smallFish, String mediumFish, String largeFish, String collection){
        firestoreService.updateOfferQuantity(offerID, smallFish, mediumFish, largeFish, collection);

    }

    public void DeleteOffer(String offerID, String collection){
        firestoreService.deleteOffer(offerID, "Offers");

    }

    //dodavanje novog korisnika bez lozinke (Google i Facebook)
    public void DodajKorisnikaUBazuBezLozinke(String id, String name, String email,String photo){
        try {
            firestoreService.writeNewUserWithoutPassword(id,name,email,photo,"Users");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRating(String ratedUser, String ratingTotal) {
        firestoreService.updateRating(ratedUser, ratingTotal);
    }

    public void UpdateOfferStatus(String offerID, String status) {
        firestoreService.updateOfferStatus(offerID, status, "Offers");
    }

    public void updateReservationRatedStatus(String reservationID, String ocijenjeno, String rezervation) {
        firestoreService.updateReservationRatedStatus(reservationID, ocijenjeno, rezervation);
    }

    public void AzurirajStatusOcijenioKupac(String reservationID, boolean b) {
        firestoreService.updateReservationRatedByBuyer(reservationID, b);
    }

    public void AzurirajStatusOcijenioProdavatelj(String reservationID, boolean b) {
        firestoreService.updateReservationRatedBySeller(reservationID, b);
    }

    public void AzurirajZadnjuPoruku(String contacts, String currentUserId, String otherUserId, String msg, Timestamp dateTimeLastMsg) {
        firestoreService.updateLastMessage(contacts, currentUserId, otherUserId, msg, dateTimeLastMsg);
    }
}

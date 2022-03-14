package com.example.database;

import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FirestoreService {
    private FirestoreService getInstance() {
        if(instance==null)
            instance=new FirestoreService();
        return instance;
    }

    private void setInstance() {
        if(instance==null)
            this.instance = new FirestoreService();
    }

    private FirestoreService instance= null;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private static ArrayList<User> mArrayList = new ArrayList<>();;

    public ArrayList<User> getListItems(String collection){

        mFirebaseFirestore.collection(collection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            //Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<User> types = documentSnapshots.toObjects(User.class);

                            // Add all to your list
                            mArrayList.addAll(types);
                            //Log.d(TAG, "onSuccess: " + mArrayList);
                        }
                    };


                });
        return mArrayList;
    }


    public User getUserByID(String collection, String document){
        User user=new User();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(collection).document(document);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    if(doc.exists()){
                        Log.d("Document",doc.getData().toString());

                    }else
                    {
                        Log.d("Document","No data");
                    }
                }
            }
        });

        return user;
    }


    public Task<QuerySnapshot> getCollectionWithField(String collection, String field, String value){
        //Log.d("Pokusaj",FirebaseFirestore.getInstance().collection(collection).whereEqualTo("Email", "bozo.kvesic1@gmail.com").get().toString());
        return FirebaseFirestore.getInstance().collection(collection).whereEqualTo(field, value).get();
    }

    public Task<QuerySnapshot> getCollectionWithCollection(String collection,String userId){
        //Log.d("Pokusaj",FirebaseFirestore.getInstance().collection(collection).whereEqualTo("Email", "bozo.kvesic1@gmail.com").get().toString());
        return FirebaseFirestore.getInstance().collection(collection).document(userId).collection("kontakti").get();
    }

    public void addPorukaToUser(String collection, String userCurrentlyID, String newUserID, String posiljaljet, String sadrzaj, Timestamp dateTimeMessage){
        String id=FirebaseFirestore.getInstance().collection(collection).document(userCurrentlyID).collection("kontakti").document(newUserID).collection("poruke").document().getId();
        Messages message=new Messages(posiljaljet,sadrzaj,dateTimeMessage);
        FirebaseFirestore.getInstance().collection(collection).document(userCurrentlyID).collection("kontakti").document(newUserID).collection("poruke").document(id).set(message);
    }

    public Task<QuerySnapshot> getAllPorukeOfUser(String collection,String userCurrentlyID, String newUserID){
        return FirebaseFirestore.getInstance().collection(collection).document(userCurrentlyID).collection("kontakti").document(newUserID).collection("poruke").whereIn("sender", Arrays.asList(newUserID, userCurrentlyID)).get();
    }
    public Task<QuerySnapshot> getLastNumberOfPorukeOfUser(String collection,String userCurrentlyID, String newUserID,Long brojPoruka){
        return FirebaseFirestore.getInstance().collection(collection).document(userCurrentlyID).collection("kontakti").document(newUserID).collection("poruke").whereIn("sender", Arrays.asList(newUserID, userCurrentlyID)).limit(brojPoruka).get();
    }

    public void updateLastMessage(String contacts, String currentUserId, String otherUserId, String msg, Timestamp lastMessageDateTime) {
        FirebaseFirestore.getInstance().collection(contacts).document(currentUserId).collection("kontakti").document(otherUserId).update("lastMessage", msg, "lastMessageDateTime", lastMessageDateTime);
        FirebaseFirestore.getInstance().collection(contacts).document(otherUserId).collection("kontakti").document(currentUserId).update("lastMessage", msg, "lastMessageDateTime", lastMessageDateTime);
    }

    public Task<QuerySnapshot> getCollection(String collection){
        //Log.d("Pokusaj",FirebaseFirestore.getInstance().collection(collection).whereEqualTo("Email", "bozo.kvesic1@gmail.com").get().toString());
        return FirebaseFirestore.getInstance().collection(collection).get();
    }

    public void writeNewUser(String userId, String name, String email, String phone,String password,String photo,String adress,Boolean userFirstLogin, String collection) {
        User user = new User(userId,name,email,phone,adress,photo,password,false,userFirstLogin);
        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        FirebaseFirestore.getInstance().collection(collection).document(user.userID).set(user);
    }

    public void updateUserFirstLogin(User trenutniKorisnik,String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(trenutniKorisnik.userID).update(
                "userFirstLogin",false);
    }

    public void writeNewUserWithoutID(String name, String email, String phone,String password,String photo,String adress,String collection) {

        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        String id=FirebaseFirestore.getInstance().collection(collection).document().getId();
        //FirebaseFirestore.getInstance().collection(collection).document(user.userID).set(user);
        User user = new User(id,name,email,phone,adress,photo,password,false);
        FirebaseFirestore.getInstance().collection(collection).document(id).set(user);
    }

    public void addCollectionToDocument(String collection,String userId){
        Contacts contact=new Contacts(userId);
        FirebaseFirestore.getInstance().collection(collection).document(userId).collection("kontakti").document(userId).set(contact);
    }

    public void addUserToContacts(String collection,String userCurrentlyID, String newUserID){
        Contacts contact=new Contacts(newUserID);
        FirebaseFirestore.getInstance().collection(collection).document(userCurrentlyID).collection("kontakti").document(newUserID).set(contact);
    }

    public void writeReview(Review review, String collection) {
        String id=FirebaseFirestore.getInstance().collection(collection).document().getId();
        review.setReviewID(id);
        FirebaseFirestore.getInstance().collection(collection).document(id).set(review);
    }

    public void writeOfferWithAutoID(Offer offer,String collection) {
        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        String id=FirebaseFirestore.getInstance().collection(collection).document().getId();
        offer.setOfferID(id);
        FirebaseFirestore.getInstance().collection(collection).document(id).set(offer);
    }

    public void writeOffer(Offer offer,String collection) {
        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        FirebaseFirestore.getInstance().collection(collection).document().set(offer);
    }

    public void writeReservationWithAutoID(Rezervation rezervation, String collection) {
        String id=FirebaseFirestore.getInstance().collection(collection).document().getId();
        rezervation.setReservationID(id);
        FirebaseFirestore.getInstance().collection(collection).document(id).set(rezervation);
    }

    public void updateUser(User trenutniKorisnik,String collection) {
        //User user = trenutniKorisnik;
        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        //FirebaseFirestore.getInstance().collection(collection).document(user.userID).update("blokiran",true,"fullName",user.fullName);
        FirebaseFirestore.getInstance().collection(collection).document(trenutniKorisnik.userID).update(
                "adress",trenutniKorisnik.adress,
                "blokiran",trenutniKorisnik.blokiran,
                "email",trenutniKorisnik.email,
                "fullName",trenutniKorisnik.fullName,
                "password",trenutniKorisnik.password,
                "phone",trenutniKorisnik.phone,
                "photo",trenutniKorisnik.photo,
                "userID",trenutniKorisnik.userID);
    }

    public void updateOfferQuantity(String offerID, String smallFish, String mediumFish, String largeFish, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(offerID).update(
                "smallFish", smallFish,
                "mediumFish", mediumFish,
                "largeFish", largeFish);
    }


    public void updateReservationStatus(String reservationID, String status, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(reservationID).update(
                "status", status);
    }

    public void updateReservationRatedStatus(String reservationID, String status, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(reservationID).update(
                "ratedStatus", status);
    }

    public void deleteReservation(String reservationID, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(reservationID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TagPolje", "Deleted Reservation");
            }
            });
    }

    public void deleteOffer(String offerID, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(offerID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TagPolje", "Deleted Offer");
                    }
      });
    }

    public void updateOfferStatus(String offerID, String status, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(offerID).update(
                "status", status);
    }

    public static void addPhotoWithID(Uri data,String id){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://digitalna-ribarnica-fb.appspot.com");
        String path="profilne/" + id + ".png";
        StorageReference profilne =storage.getReference(path);
        UploadTask uploadTask=profilne.putFile(data);
    }

    public static void getProfilePhotoWithID(String id, CallbackUser callbackUser){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://digitalna-ribarnica-fb.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("profilne/" + id + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callbackUser.onCallback(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callbackUser.onCallback(null);
            }
        });
    }

    public static void getData(){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://digitalna-ribarnica-fb.appspot.com");
        StorageReference storageRef = storage.getReference();

        storageRef.child("firememes/5d068fd4-1f37-4ce6-89c9-c85bf5fc5c3b.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    //zapiši novog korisnika bez lozinke (Google i Facebook)
    public void writeNewUserWithoutPassword(String userId, String name, String email,String photo,String collection) {
        User user = new User(userId,name,email,photo,false);
        //FirebaseDatabase.getInstance().getReference().child(collection).child(user.userID).setValue(user).isSuccessful();
        //FirebaseFirestore.getInstance().collection(collection).add(user);
        FirebaseFirestore.getInstance().collection(collection).document(user.userID).set(user);
    }

    public void updateNumberOfSales(String userID, String numberSales, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(userID).update(
                "numberOfSales", numberSales);
    }

    public void updateNumberOfPurchases(String userID, String numberPurchases, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(userID).update(
                "numberOfPurchases", numberPurchases);
    }

    public void updateRating(String ratedUser, String ratingTotal) {
        FirebaseFirestore.getInstance().collection("Users").document(ratedUser).update(
                "userRating", ratingTotal);
    }

    public void updateReservationSellerDeleted(String reservationID, boolean b, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(reservationID).update(
                "deletedSeller",  b);
    }
    public void updateReservationBuyerDeleted(String reservationID, boolean b, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(reservationID).update(
                "deletedBuyer",  b);
    }

    public void updateReservationRatedByBuyer(String reservationID, boolean b) {
        FirebaseFirestore.getInstance().collection("Rezervation").document(reservationID).update(
                "ratedByBuyer",  b);
    }

    public void updateReservationRatedBySeller(String reservationID, boolean b) {
        FirebaseFirestore.getInstance().collection("Rezervation").document(reservationID).update(
                "ratedBySeller",  b);
    }


    public class BadgeID {

        private String id;

        public BadgeID(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public void updateBadgeUser(String userID, String badgeID, String collection) {
            BadgeID badge = new BadgeID(badgeID);
            FirebaseFirestore.getInstance().collection(collection).document(userID).collection("badges").document(badgeID).set(badge);
    }

    public Task<QuerySnapshot> DohvatiZnackeKorisnika(String collection, String userId){
        return FirebaseFirestore.getInstance().collection(collection).document(userId).collection("badges").get();
    }
}
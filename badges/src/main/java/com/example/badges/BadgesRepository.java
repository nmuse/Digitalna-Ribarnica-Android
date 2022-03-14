package com.example.badges;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.database.Contacts;
import com.example.database.FirestoreService;
import com.example.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BadgesRepository {
    FirestoreService firestoreService;
    public BadgesRepository() {
        firestoreService=new FirestoreService();
    }

    public void DohvatiSveZnačke(BadgeCallback firestoreCallback){
        firestoreService.getCollection("Badges").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<BadgesData> badgesDataArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    BadgesData badgesData=new Gson().fromJson(json,BadgesData.class);
                    badgesDataArrayList.add(badgesData);
                }
                firestoreCallback.onCallback(badgesDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "Ne valja", Toast.LENGTH_SHORT).show();
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiZnackuPoNazivu(String naziv,BadgeCallback firestoreCallback){
        firestoreService.getCollectionWithField("Badges","title",naziv).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<BadgesData> badgesDataArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    BadgesData badgesData=new Gson().fromJson(json,BadgesData.class);
                    badgesDataArrayList.add(badgesData);
                }
                firestoreCallback.onCallback(badgesDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiZnackuPoID(String badgeID, BadgeCallback firestoreCallback){
        firestoreService.getCollectionWithField("Badges","badgeID", badgeID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<BadgesData> badgesDataArrayList=new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    BadgesData badgesData=new Gson().fromJson(json,BadgesData.class);
                    badgesDataArrayList.add(badgesData);
                }
                firestoreCallback.onCallback(badgesDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestoreCallback.onCallback(null);
            }
        });
    }

    public void DohvatiKorisnikaPoID(String userID, UserCallback firestoreCallback){
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

    public void DodijeliZnackuKorisniku(User user, String badgeID){
        firestoreService.updateBadgeUser(user.getUserID(), badgeID,"Users");
    }

    public void DohvatiPitanjaZaKviz(QuizCallBack quizCallBack){
        firestoreService.getCollection("Quiz").addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> ime=queryDocumentSnapshots.getDocuments();
                ArrayList<QuizData> quizDataArrayList = new ArrayList<>();
                for(DocumentSnapshot d: ime){
                    d.getData();
                    String json= new Gson().toJson(d.getData());
                    QuizData quizData = new Gson().fromJson(json,QuizData.class);
                    quizDataArrayList.add(quizData);
                }
                quizCallBack.onCallback(quizDataArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                quizCallBack.onCallback(null);
            }
        });
    }

    public void DohvatiIDZnackiKorisnika(String userID, BadgeIDCallback badgeCallback){
        firestoreService.DohvatiZnackeKorisnika("Users", userID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> objekti=queryDocumentSnapshots.getDocuments();
                ArrayList<BadgeID> badgesID=new ArrayList<>();
                for(DocumentSnapshot d: objekti){
                    d.getData();
                    String json = new Gson().toJson(d.getData());
                    BadgeID badge= new Gson().fromJson(json, BadgeID.class);
                    badgesID.add(badge);
                }
                badgeCallback.onCallback(badgesID);
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    badgeCallback.onCallback(null);
                }
        });
    }
}

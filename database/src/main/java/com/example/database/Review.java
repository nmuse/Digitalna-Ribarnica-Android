package com.example.database;


import com.google.firebase.Timestamp;

public class Review {
    private String reviewID;
    private String ratedUser;
    private String rating;
    private String comment;
    private String reviewer;
    private Timestamp date;

    public Review(String ratedUser, String rating, String comment, String reviewer, Timestamp date){
        this.ratedUser = ratedUser;
        this.rating = rating;
        this.comment = comment;
        this.reviewer = reviewer;
        this.date = date;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(String ratedUser) {
        this.ratedUser = ratedUser;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}

package Adapter;

import com.google.firebase.Timestamp;

public class Review {
    private String comment, userId, foodId, imageUrl;
    private int rating;
    private Timestamp timestamp;

    public Review() {
    }

    public Review(String comment, String userId, String foodId, int rating, Timestamp timestamp, String imageUrl) {
        this.comment = comment;
        this.userId = userId;
        this.foodId = foodId;
        this.rating = rating;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

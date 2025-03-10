package com.ltmb.ltmobile.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FoodReview {
    private FirebaseFirestore db;

    public FoodReview() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface ReviewCallback {
        void onSuccess(String reviewId);
        void onFailure(Exception e);
    }

    public void submitReview(String restaurantId, String orderId, String userId, String comment, double rating, ReviewCallback callback) {
        if (rating < 0 || rating > 5) {
            callback.onFailure(new IllegalArgumentException("Rating phải nằm trong khoảng 0 - 5"));
            return;
        }

        String reviewId = db.collection("Restaurants").document(restaurantId)
                .collection("FoodReviews").document().getId(); // Tạo ID ngẫu nhiên

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("orderId", orderId);  // Gán ID đơn hàng
        reviewData.put("userId", userId);
        reviewData.put("comment", comment);
        reviewData.put("rating", rating);
        reviewData.put("timestamp", System.currentTimeMillis()); // Thời gian đánh giá

        db.collection("Restaurants").document(restaurantId)
                .collection("FoodReviews").document(reviewId)
                .set(reviewData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(reviewId))
                .addOnFailureListener(callback::onFailure);
    }

    public interface RatingCallback {
        void onSuccess(double averageRating, int totalReviews);
        void onFailure(Exception e);
    }
    public void calculateRestaurantRating(String restaurantId, RatingCallback callback) {
        db.collection("Restaurants").document(restaurantId)
                .collection("FoodReviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot reviews = task.getResult();
                        if (reviews.isEmpty()) {
                            callback.onSuccess(0.0, 0); // Không có đánh giá
                            return;
                        }

                        AtomicReference<Double> totalRating = new AtomicReference<>(0.0);
                        AtomicInteger totalReviews = new AtomicInteger(0);

                        for (QueryDocumentSnapshot document : reviews) {
                            Double rating = document.getDouble("rating");
                            if (rating != null) {
                                totalRating.updateAndGet(v -> v + rating);
                                totalReviews.incrementAndGet();
                            }
                        }

                        double averageRating = totalRating.get() / totalReviews.get();
                        callback.onSuccess(averageRating, totalReviews.get());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    public void getAllReviews(String restaurantId, ReviewsCallback callback) {
        db.collection("Restaurants").document(restaurantId)
                .collection("FoodReviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> reviewList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> reviewData = document.getData();
                            reviewData.put("reviewId", document.getId()); // Thêm ID review vào dữ liệu
                            reviewList.add(reviewData);
                        }
                        callback.onSuccess(reviewList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    public interface ReviewsCallback {
        void onSuccess(List<Map<String, Object>> reviews);
        void onFailure(Exception e);
    }
}

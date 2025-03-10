package com.ltmb.ltmobile.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Tạo đơn hàng mới
    public void createOrder(String userId, String restaurantId, List<com.ltmb.ltmobile.services.OrderItem> items, double totalPrice, OrderCallback callback) {
        String orderId = db.collection("Orders").document().getId(); // Tạo ID ngẫu nhiên

        // Tạo dữ liệu đơn hàng
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("restaurantId", restaurantId);
        orderData.put("totalPrice", totalPrice);
        orderData.put("status", "pending");
        orderData.put("isReviewed", false); // ✅ Đánh dấu chưa đánh giá
        orderData.put("timestamp", System.currentTimeMillis());

        // Batch để thêm nhiều dữ liệu một lần
        WriteBatch batch = db.batch();
        batch.set(db.collection("Orders").document(orderId), orderData);

        // Thêm danh sách món ăn vào subcollection "items"
        for (com.ltmb.ltmobile.services.OrderItem item : items) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("foodId", item.getFoodId());
            itemData.put("quantity", item.getQuantity());
            itemData.put("price", item.getPrice());

            batch.set(db.collection("Orders").document(orderId).collection("items").document(), itemData);
        }

        // Thực hiện batch write
        batch.commit()
                .addOnSuccessListener(aVoid -> callback.onSuccess(orderId))
                .addOnFailureListener(callback::onFailure);
    }

    //Lấy danh sách đơn hàng chưa được đánh giá
    public void getUnreviewedOrders(String userId, OrderListCallback callback) {
        db.collection("Orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isReviewed", false) // ✅ Chỉ lấy đơn chưa đánh giá
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> orderData = new HashMap<>(document.getData());
                            orderData.put("orderId", document.getId()); // ✅ Thêm ID đơn hàng vào dữ liệu trả về
                            orders.add(orderData);
                        }
                        callback.onSuccess(orders);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // ✅ Interface dùng chung để callback khi tạo đơn hàng
    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }

    // ✅ Interface dùng chung để callback khi lấy danh sách đơn hàng
    public interface OrderListCallback {
        void onSuccess(List<Map<String, Object>> orders);
        void onFailure(Exception e);
    }
}

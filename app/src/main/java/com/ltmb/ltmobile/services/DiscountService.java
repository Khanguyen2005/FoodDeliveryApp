package com.ltmb.ltmobile.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Lấy danh sách mã giảm giá của một nhà hàng
     * @param restaurantId ID của nhà hàng cần lấy mã giảm giá
     * @param callback Callback trả về danh sách mã giảm giá hoặc lỗi
     */
    public void getDiscountsByRestaurantId(String restaurantId, DiscountCallback callback) {
        db.collection("Restaurants")
                .document(restaurantId)
                .collection("discount")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> discountList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> discountData = new HashMap<>(document.getData());
                            discountData.put("discountId", document.getId()); // Thêm ID vào dữ liệu
                            discountList.add(discountData);
                        }
                        callback.onSuccess(discountList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Interface callback để xử lý kết quả lấy mã giảm giá
     */
    public interface DiscountCallback {
        void onSuccess(List<Map<String, Object>> discountList);
        void onFailure(Exception e);
    }
}

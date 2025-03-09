package com.ltmb.ltmobile.services;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Food;

public class RestaurantManagement {

    private FirebaseFirestore db;

    public RestaurantManagement() {
        this.db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
    }

    public interface RestaurantCallback {
        void onSuccess(List<Map<String, Object>> restaurantList);
        void onFailure(Exception e);
    }

    public void getRestaurants(final RestaurantCallback callback) {
        CollectionReference restaurantsRef = db.collection("Restaurants");

        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> restaurantList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> restaurantData = new HashMap<>(document.getData());
                        restaurantData.put("id", document.getId()); // Thêm ID vào dữ liệu
                        restaurantList.add(restaurantData);
                    }
                    // Gửi danh sách nhà hàng qua callback
                    callback.onSuccess(restaurantList);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }


    public interface MenuCategoryCallback {
        void onSuccess(List<Map<String, Object>> categoryList);
        void onFailure(Exception e);
    }

    public void getMenuCategories(String restaurantId, final MenuCategoryCallback callback) {
        CollectionReference menuRef = db.collection("Restaurants")
                .document(restaurantId)
                .collection("menu");

        menuRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> categoryList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> categoryData = new HashMap<>(document.getData());
                        categoryData.put("id", document.getId()); // Thêm ID vào danh mục
                        categoryList.add(categoryData);
                    }
                    // Trả kết quả qua callback
                    callback.onSuccess(categoryList);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public interface DishCallback {
        void onSuccess(List<Map<String, Object>> dishList);
        void onFailure(Exception e);
    }

    public void getDishes(String restaurantId, String categoryId, final DishCallback callback) {
        CollectionReference dishRef = db.collection("Restaurants")
                .document(restaurantId)
                .collection("menu")
                .document(categoryId)
                .collection("menu"); // Lấy danh sách món trong loại món ăn

        dishRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> dishList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dishData = new HashMap<>();
                        dishData.put("id", document.getId()); // ID của món ăn
                        dishData.put("name", document.getString("name")); // Tên món ăn
                        dishData.put("description", document.getString("description")); // Mô tả món ăn
                        dishData.put("price", document.getLong("price")); // Giá món ăn
                        dishData.put("image", document.getString("image")); // Ảnh món ăn
                        dishList.add(dishData);
                    }

                    // Trả kết quả qua callback
                    callback.onSuccess(dishList);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }


}

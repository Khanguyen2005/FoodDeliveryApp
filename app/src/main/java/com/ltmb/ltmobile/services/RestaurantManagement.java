package com.ltmb.ltmobile.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        restaurantData.put("id", document.getId()); // Thêm ID nhà hàng vào dữ liệu

                        // Lấy danh sách category
                        db.collection("Restaurants")
                                .document(document.getId())
                                .collection("menu")
                                .get()
                                .addOnCompleteListener(menuTask -> {
                                    if (menuTask.isSuccessful()) {
                                        List<Map<String, String>> categoryList = new ArrayList<>();
                                        for (QueryDocumentSnapshot categoryDoc : menuTask.getResult()) {
                                            Map<String, String> categoryData = new HashMap<>();
                                            categoryData.put("id", categoryDoc.getId()); // ID category
                                            categoryData.put("name", categoryDoc.getString("name")); // Tên category
//                                            categoryData.put("discount_code", categoryDoc.getString("discount_code")); //Ma giam
//                                            categoryData.put("discount_description", categoryDoc.getString("discount_description")); // mo ta code
//                                            categoryData.put("discount_percentage", categoryDoc.getString("discount_percentage")); // % giam gia
                                            categoryList.add(categoryData);
                                        }
                                        // Thêm danh sách category vào dữ liệu của nhà hàng
                                        restaurantData.put("categories", categoryList);
                                    }
                                    // Thêm dữ liệu vào danh sách nhà hàng
                                    restaurantList.add(restaurantData);

                                    // Nếu đã lấy hết tất cả nhà hàng thì gọi callback
                                    if (restaurantList.size() == task.getResult().size()) {
                                        callback.onSuccess(restaurantList);
                                    }
                                });
                    }
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
                        dishData.put("quantitySold", document.getLong("quantitySold")); // Lượt bán
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
    public void checkDiscountCode(String restaurantId, String categoryId, String dishId, String discountCode, final DiscountCallback callback) {
        // Truy vấn danh mục để lấy thông tin giảm giá
        db.collection("Restaurants")
                .document(restaurantId)
                .collection("menu")
                .document(categoryId)
                .collection("menu") // Lấy danh sách món ăn trong category
                .document(dishId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Nếu món ăn tồn tại trong category, tiếp tục kiểm tra discount
                        db.collection("Restaurants")
                                .document(restaurantId)
                                .collection("menu")
                                .document(categoryId)
                                .get()
                                .addOnCompleteListener(categoryTask -> {
                                    if (categoryTask.isSuccessful() && categoryTask.getResult().exists()) {
                                        DocumentSnapshot categoryDoc = categoryTask.getResult();

                                        // Lấy thông tin giảm giá từ danh mục
                                        String storedDiscountCode = categoryDoc.getString("discount_code");
                                        Long discountPercentage = categoryDoc.getLong("discount_percentage");

                                        // Kiểm tra mã giảm giá có hợp lệ không
                                        if (storedDiscountCode != null && storedDiscountCode.equals(discountCode)) {
                                            callback.onResult(true, discountPercentage != null ? discountPercentage : 0);
                                        } else {
                                            callback.onResult(false, 0L);
                                        }
                                    } else {
                                        callback.onResult(false, 0L); // Không tìm thấy danh mục hoặc lỗi Firestore
                                    }
                                });
                    } else {
                        // Món ăn không tồn tại trong danh mục => Không hợp lệ
                        callback.onResult(false, 0L);
                    }
                });
    }

    // Interface callback để trả về kết quả
    public interface DiscountCallback {
        void onResult(boolean isValid, long discountPercentage);
    }
    public interface ToppingCallback {
        void onSuccess(List<Map<String, Object>> toppingList);
        void onFailure(Exception e);
    }

    public void getAllToppings(String restaurantId, String categoryId, final ToppingCallback callback) {
        CollectionReference toppingRef = db.collection("Restaurants")
                .document(restaurantId)
                .collection("menu")
                .document(categoryId)
                .collection("topping");

        toppingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> categoryToppingList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        Map<String, Object> categoryTopping = new HashMap<>();

                        // Lấy thông tin chung của category topping
                        if (data.containsKey("min")) {
                            categoryTopping.put("min", data.get("min"));
                        }
                        if (data.containsKey("max")) {
                            categoryTopping.put("max", data.get("max"));
                        }
                        if (data.containsKey("name")) {
                            categoryTopping.put("name", data.get("name"));
                        }

                        // Lấy danh sách tất cả các items trong category topping
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().startsWith("item") && entry.getValue() instanceof Map) {
                                Map<String, Object> itemData = (Map<String, Object>) entry.getValue();
                                Map<String, Object> item = new HashMap<>();
                                item.put("name", itemData.get("name"));
                                item.put("price", itemData.get("price"));
                                items.add(item);
                            }
                        }

                        if (!items.isEmpty()) {
                            categoryTopping.put("items", items);
                        }
                        categoryToppingList.add(categoryTopping);
                    }

                    callback.onSuccess(categoryToppingList);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

}

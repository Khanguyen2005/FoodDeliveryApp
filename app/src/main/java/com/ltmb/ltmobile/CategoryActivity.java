package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Adapter.Restaurant;
import Adapter.RestaurantAdapter;

public class CategoryActivity extends AppCompatActivity {
    private List<String> categoryKeywords;
    private List<String> searchKeywords;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> listRes;
    private Set<String> restaurantIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        TextView txtCategoryTitle = findViewById(R.id.cateTitle);
        recyclerView = findViewById(R.id.rcvRes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listRes = new ArrayList<>();
        restaurantIds = new HashSet<>();

        adapter = new RestaurantAdapter(this, restaurant -> {
            Intent i = new Intent(this, RestaurantActivity.class);
            i.putExtra("restaurant_id", restaurant.getId());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            categoryKeywords = intent.getStringArrayListExtra("CATEGORY_KEYWORDS");
            searchKeywords = intent.getStringArrayListExtra("SEARCH_KEYWORDS");

            if (categoryKeywords == null) categoryKeywords = new ArrayList<>();
            if (searchKeywords == null) searchKeywords = new ArrayList<>();

            if (!categoryKeywords.isEmpty()) {
                txtCategoryTitle.setText(categoryKeywords.get(0).toUpperCase());
            } else if (!searchKeywords.isEmpty()) {
                txtCategoryTitle.setText("Kết quả tìm kiếm: " + String.join(" ", searchKeywords));
            } else {
                txtCategoryTitle.setText("Danh mục");
            }

            loadRestaurantsByCategoryOrSearch();
        }

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Chọn phương thức tải dữ liệu phù hợp dựa trên categoryKeywords hoặc searchKeywords.
     */
    private void loadRestaurantsByCategoryOrSearch() {
        if (!categoryKeywords.isEmpty()) {
            loadRestaurantsByCategory(); // Load theo danh mục
        } else if (!searchKeywords.isEmpty()) {
            loadRestaurantsBySearch(); // Load theo từ khóa tìm kiếm
        }
    }

    /**
     * Lấy danh sách nhà hàng dựa trên danh mục (CATEGORY_KEYWORDS).
     */
    private void loadRestaurantsByCategory() {
        RestaurantManagement restaurantManagement = new RestaurantManagement();
        restaurantManagement.getRestaurants(new RestaurantManagement.RestaurantCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> restaurantList) {
                listRes.clear();
                restaurantIds.clear();

                for (Map<String, Object> data : restaurantList) {
                    try {
                        List<Map<String, String>> categories = (List<Map<String, String>>) data.get("categories");
                        if (categories != null) {
                            for (Map<String, String> category : categories) {
                                if (category.get("name") != null) {
                                    String categoryNameLower = category.get("name").toLowerCase();
                                    for (String keyword : categoryKeywords) {
                                        if (categoryNameLower.contains(keyword.toLowerCase())) {
                                            String id = String.valueOf(data.get("id"));

                                            // Kiểm tra nếu ID đã tồn tại, bỏ qua
                                            if (restaurantIds.contains(id)) {
                                                break;
                                            }

                                            String name = (String) data.get("name");
                                            Number starRes = data.get("starRes") != null ? Double.parseDouble(data.get("starRes").toString()) : 5.0;
                                            Number evaluate = data.get("evaluate") != null ? Integer.parseInt(data.get("evaluate").toString()) : 0;
                                            String imgUrl = (String) data.get("image");
                                            String backgroundImgUrl = (String) data.get("backgroundImage");

                                            Restaurant res = new Restaurant(id, name, starRes, evaluate, imgUrl, backgroundImgUrl);
                                            listRes.add(res);
                                            restaurantIds.add(id); // Thêm ID vào Set để tránh trùng lặp
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Firestore", "Lỗi chuyển đổi dữ liệu", e);
                    }
                }
                adapter.setData(listRes);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Lỗi tải danh sách nhà hàng", e);
            }
        });
    }

    /**
     * Lấy danh sách nhà hàng dựa trên tìm kiếm (SEARCH_KEYWORDS).
     */
    private void loadRestaurantsBySearch() {
        RestaurantManagement restaurantManagement = new RestaurantManagement();
        restaurantManagement.getRestaurants(new RestaurantManagement.RestaurantCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> restaurantList) {
                listRes.clear();
                restaurantIds.clear();

                for (Map<String, Object> data : restaurantList) {
                    try {
                        String restaurantName = (String) data.get("name");
                        List<Map<String, String>> categories = (List<Map<String, String>>) data.get("categories");

                        boolean matchesSearch = false;
                        Set<String> searchTokens = new HashSet<>();
                        for (String keyword : searchKeywords) {
                            String[] words = keyword.toLowerCase().split("\\s+");
                            searchTokens.addAll(List.of(words));
                        }

                        // Kiểm tra danh mục nhà hàng
                        if (categories != null) {
                            for (Map<String, String> category : categories) {
                                if (category.get("name") != null) {
                                    String categoryNameLower = category.get("name").toLowerCase();
                                    String[] categoryWords = categoryNameLower.split("\\s+");
                                    Set<String> categoryTokens = new HashSet<>(List.of(categoryWords));

                                    // Kiểm tra nếu có từ khóa trùng với danh mục
                                    categoryTokens.retainAll(searchTokens);
                                    if (!categoryTokens.isEmpty()) {
                                        matchesSearch = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (matchesSearch) {
                            String id = String.valueOf(data.get("id"));
                            if (restaurantIds.contains(id)) {
                                continue;
                            }

                            Number starRes = data.get("starRes") != null ? Double.parseDouble(data.get("starRes").toString()) : 5.0;
                            Number evaluate = data.get("evaluate") != null ? Integer.parseInt(data.get("evaluate").toString()) : 0;
                            String imgUrl = (String) data.get("image");
                            String backgroundImgUrl = (String) data.get("backgroundImage");

                            Restaurant res = new Restaurant(id, restaurantName, starRes, evaluate, imgUrl, backgroundImgUrl);
                            listRes.add(res);
                            restaurantIds.add(id);
                        }

                    } catch (Exception e) {
                        Log.e("Firestore", "Lỗi chuyển đổi dữ liệu", e);
                    }
                }
                adapter.setData(listRes);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Lỗi tải danh sách nhà hàng", e);
            }
        });
    }

}

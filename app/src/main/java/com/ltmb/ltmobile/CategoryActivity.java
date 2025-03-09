package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> listRes;
    private Set<String> restaurantIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        if (intent != null) {
            categoryKeywords = intent.getStringArrayListExtra("CATEGORY_KEYWORDS");
            if (categoryKeywords == null) {
                categoryKeywords = new ArrayList<>();
            }
        }

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

        loadRestaurantsByCategory();
    }

    private void loadRestaurantsByCategory() {
        RestaurantManagement restaurantManagement = new RestaurantManagement();
        restaurantManagement.getRestaurants(new RestaurantManagement.RestaurantCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> restaurantList) {
                listRes.clear();
                restaurantIds.clear(); // Xóa dữ liệu cũ để tránh trùng lặp khi reload

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
                                            String starRes = String.valueOf(data.get("starRes"));
                                            String evaluateRes = (String) data.get("evaluateRes");
                                            String imgUrl = (String) data.get("image");
                                            String backgroundImgUrl = (String) data.get("backgroundImage");

                                            Restaurant res = new Restaurant(id, name, starRes, evaluateRes, imgUrl, backgroundImgUrl);
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
}

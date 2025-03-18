package com.ltmb.ltmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import Fragment.ProfileFragment;
import Fragment.HomeFragment;
import Fragment.OrderFragment;

public class MainActivity extends AppCompatActivity {
    private TextView txtEmail, txtUID, txtToken, txtName, txtPhone, txtAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) { // Chỉ mở Fragment nếu Activity vừa tạo
            loadFragment(new HomeFragment()); // Mở HomeFragment mặc định
        }

        // Kiểm tra xem có intent nào yêu cầu mở HomeFragment không
        if (getIntent() != null && "home".equals(getIntent().getStringExtra("openFragment"))) {
            openHomeFragment();
        }

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String uid = intent.getStringExtra("UID");
        String email = intent.getStringExtra("EMAIL");
        String token = intent.getStringExtra("TOKEN");
        String name = intent.getStringExtra("NAME");
        String phone = intent.getStringExtra("PHONE");
        String address = intent.getStringExtra("ADDRESS");

        // Tạo Bundle để truyền dữ liệu cho Fragment
        Bundle bundle = new Bundle();
        bundle.putString("UID", uid);
        bundle.putString("EMAIL", email);
        bundle.putString("TOKEN", token);
        bundle.putString("NAME", name);
        bundle.putString("PHONE", phone);
        bundle.putString("ADDRESS", address);

        if (getIntent().getBooleanExtra("showEditProfile", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileFragment()) // Thay thế bằng ID container phù hợp
                    .addToBackStack(null)
                    .commit();
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UID", uid);
        editor.apply();
        Log.d("UID", "Saved UID to SharedPreferences: " + uid);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);

        Bundle homeBundle = new Bundle();
        homeBundle.putString("ADDRESS", address);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(homeBundle);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null && getIntent().getStringExtra("UID") != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
        }


        // Xử lý sự kiện khi chọn item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId(); // Lấy ID của item được chọn

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            // Thay thế Fragment nếu hợp lệ
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, selectedFragment)
                        .commit();
            }
            return true;
        });

        updateRestaurantReviews();
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
    private void openHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new HomeFragment()) // Thay R.id.fragment_container bằng ID container của bạn
                .commit();
    }
    private void updateRestaurantReviews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Restaurants").get().addOnSuccessListener(restaurants -> {
            for (QueryDocumentSnapshot restaurantDoc : restaurants) {
                String restaurantId = restaurantDoc.getId();
                Log.d("Firestore", "Đang xử lý nhà hàng: " + restaurantId); // Kiểm tra ID nhà hàng

                db.collection("Restaurants")
                        .document(restaurantId)
                        .collection("FoodReviews")
                        .get()
                        .addOnSuccessListener(reviews -> {
                            int reviewCount = reviews.size();
                            Log.d("Firestore", "Nhà hàng " + restaurantId + " có " + reviewCount + " đánh giá"); // Kiểm tra số review

                            db.collection("Restaurants")
                                    .document(restaurantId)
                                    .update("evaluate", reviewCount)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("Firestore", "Cập nhật thành công evaluate cho " + restaurantId)
                                    )
                                    .addOnFailureListener(e ->
                                            Log.e("Firestore", "Lỗi khi cập nhật evaluate: " + e.getMessage(), e)
                                    );
                        })
                        .addOnFailureListener(e ->
                                Log.e("Firestore", "Lỗi khi lấy danh sách review của " + restaurantId + ": " + e.getMessage(), e)
                        );
            }
        }).addOnFailureListener(e ->
                Log.e("Firestore", "Lỗi khi lấy danh sách nhà hàng: " + e.getMessage(), e)
        );
    }


}

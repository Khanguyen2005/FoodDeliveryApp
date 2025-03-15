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

import Fragment.ProfileFragment;
import Fragment.HomeFragment;
import Fragment.OrderFragment;

public class MainActivity extends AppCompatActivity {
    private TextView txtEmail, txtUID, txtToken, txtName, txtPhone, txtAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UID", uid);
        editor.apply();
        Log.d("UID", "Saved UID to SharedPreferences: " + uid);

        // Khởi tạo Fragment và truyền Bundle
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);

        // Truyền dữ liệu cho HomeFragment
        Bundle homeBundle = new Bundle();
        homeBundle.putString("ADDRESS", address); // Chỉ truyền ADDRESS

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(homeBundle);

        // Thêm Fragment vào MainActivity
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, profileFragment) // ID của container trong layout activity_main.xml
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mở HomeFragment khi ứng dụng chạy
        if (savedInstanceState == null) {
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
    }
}

package com.ltmb.ltmobile.services;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ltmb.ltmobile.MainActivity;
import com.ltmb.ltmobile.R;

public class OrderSuccessActivity extends AppCompatActivity {
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Ánh xạ nút từ layout
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Xử lý sự kiện click nút "Quay về Trang Chủ"
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
            intent.putExtra("OPEN_HOME", true); // Gửi thông tin mở HomeFragment
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Tránh tạo nhiều instance của MainActivity
            startActivity(intent);
            finish(); // Đóng OrderSuccessActivity
        });
    }
}

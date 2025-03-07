package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView txtEmail, txtUID, txtToken, txtName, txtPhone, txtAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các TextView
        txtEmail = findViewById(R.id.txtEmail);
        txtUID = findViewById(R.id.txtUID);
        txtToken = findViewById(R.id.txtToken);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("EMAIL");
        String uid = intent.getStringExtra("UID");
        String token = intent.getStringExtra("TOKEN");
        String name = intent.getStringExtra("NAME");
        String phone = intent.getStringExtra("PHONE");
        String address = intent.getStringExtra("ADDRESS");

        // Hiển thị thông tin với kiểm tra null
        txtEmail.setText("Email: " + (email != null ? email : "Không có email"));
        txtUID.setText("UID: " + (uid != null ? uid : "Không có UID"));
        txtToken.setText("Token: " + (token != null ? token : "Không có token"));
        txtName.setText("Tên: " + (name != null ? name : "Không có tên"));
        txtPhone.setText("Số điện thoại: " + (phone != null ? phone : "Không có số điện thoại"));
        txtAddress.setText("Địa chỉ: " + (address != null ? address : "Không có địa chỉ"));
    }
}

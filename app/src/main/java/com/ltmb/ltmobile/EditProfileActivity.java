package com.ltmb.ltmobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtAddress;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // Đặt layout phù hợp

        // Ánh xạ các thành phần UI
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("FirestoreDebug", "UserID: " + userId);

        // Load dữ liệu hiện tại
        loadUserData();

        // Xử lý khi nhấn nút Lưu
        btnSave.setOnClickListener(v -> updateUserData());
    }

    private void loadUserData() {
        if (userId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy user ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.e("FirestoreError", "Lỗi khi tải dữ liệu: " + error.getMessage(), error);
                Toast.makeText(this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                edtName.setText(documentSnapshot.getString("name"));
                edtPhone.setText(documentSnapshot.getString("phoneNumber"));
                edtAddress.setText(documentSnapshot.getString("address"));
                Log.d("FirestoreDebug", "Dữ liệu tải thành công!");
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phoneNumber", phone);
        userData.put("address", address);

        db.collection("Users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("FirestoreDebug", "Cập nhật thành công!");
                    finish(); // Đóng Activity sau khi cập nhật
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Lỗi cập nhật dữ liệu: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^0[0-9]{9}$", phone);
    }
}

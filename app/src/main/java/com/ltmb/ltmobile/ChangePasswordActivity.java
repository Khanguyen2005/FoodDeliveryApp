package com.ltmb.ltmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ltmb.ltmobile.services.AuthService;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private Button btnChangePassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Ánh xạ view
        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật...");

        // Xử lý sự kiện đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = editOldPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Kiểm tra nhập liệu
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu hợp lệ
        if (!isValidPassword(newPassword)) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 8 ký tự, 1 chữ cái in hoa và 1 ký tự đặc biệt.", Toast.LENGTH_LONG).show();
            return;
        }

        // Gọi hàm changePassword()
        progressDialog.show();
        AuthService authService = new AuthService();
        authService.changePassword(oldPassword, newPassword, (success, message) -> {
            progressDialog.dismiss();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    // Hàm kiểm tra mật khẩu hợp lệ
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}

package com.ltmb.ltmobile;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginSignUpActivity extends AppCompatActivity {
    private AuthService authService;
    private EditText editTextEmail, editTextPassword,editTextConfirmPass;
    private Button btnRegister,btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_sign_up);

        // Ánh xạ các view
        TextView backToSignUpButton = findViewById(R.id.backToSignUp);
        TextView backToLoginButton = findViewById(R.id.backToLogin);
        TextView headerTitle = findViewById(R.id.header_title);

        LinearLayout loginForm = findViewById(R.id.loginForm);
        LinearLayout signUpForm = findViewById(R.id.signUpForm);

        authService = new AuthService();
        editTextEmail = findViewById(R.id.editSignUp);
        editTextPassword = findViewById(R.id.passwordSignUp);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        editTextConfirmPass = findViewById(R.id.confirmPasswordSignUp);

        // Thiết lập màu khi chọn
        int buttonSelectedColor = getResources().getColor(R.color.button_selected); // Màu khi chọn
        int buttonDefaultColor = getResources().getColor(R.color.button_default);  // Màu mặc định

        // Xử lý sự kiện cho nút Sign Up
        backToSignUpButton.setOnClickListener(v -> {
            // Hiển thị form Sign Up và ẩn form Login
            loginForm.setVisibility(View.GONE);
            signUpForm.setVisibility(View.VISIBLE);

            headerTitle.setText("Đăng ký");


            // Thêm animation
            signUpForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

        });

        // Xử lý sự kiện cho nút Login
        backToLoginButton.setOnClickListener(v -> {
            // Hiển thị form Login và ẩn form Sign Up
            signUpForm.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);

            headerTitle.setText("Đăng nhập");


            // Thêm animation
            loginForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));

        });

        btnRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPass.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password);
        });
    }
    private void registerUser(String email, String password) {
        authService.registerUser(email, password, new AuthService.AuthCallback() {
            @Override
            public void onComplete(boolean success, String message) {
                Toast.makeText(LoginSignUpActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
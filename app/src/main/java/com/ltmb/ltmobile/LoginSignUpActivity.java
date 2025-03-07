package com.ltmb.ltmobile;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class LoginSignUpActivity extends AppCompatActivity {
    private AuthService authService;
    private EditText editTextEmail, editTextPassword, editTextConfirmPass;
    private Button btnRegister, btnLogin;
    private ProgressDialog progressDialog;

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
        editTextConfirmPass = findViewById(R.id.confirmPasswordSignUp);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        // Xử lý sự kiện chuyển giữa form đăng nhập và đăng ký
        backToSignUpButton.setOnClickListener(v -> switchToSignUp(headerTitle, loginForm, signUpForm));
        backToLoginButton.setOnClickListener(v -> switchToLogin(headerTitle, loginForm, signUpForm));

        // Xử lý đăng ký tài khoản
        btnRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPass.getText().toString().trim();

            if (!validateInput(email, password, confirmPassword)) {
                return;
            }
            registerUser(email, password, null, null, null, null, null);
        });
    }

    // Chuyển sang form đăng ký
    private void switchToSignUp(TextView headerTitle, LinearLayout loginForm, LinearLayout signUpForm) {
        loginForm.setVisibility(View.GONE);
        signUpForm.setVisibility(View.VISIBLE);
        headerTitle.setText("Đăng ký");
        signUpForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    // Chuyển sang form đăng nhập
    private void switchToLogin(TextView headerTitle, LinearLayout loginForm, LinearLayout signUpForm) {
        signUpForm.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
        headerTitle.setText("Đăng nhập");
        loginForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
    }

    // Kiểm tra đầu vào
    private boolean validateInput(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Đăng ký tài khoản
    private void registerUser(String email, String password, String name, String phoneNumber, String address, Integer role, Long idRes) {
        progressDialog.show();
        btnRegister.setEnabled(false); // Vô hiệu hóa nút để tránh spam

        authService.registerUser(email, password, name, phoneNumber, address, role, idRes, new AuthService.AuthCallback() {
            @Override
            public void onComplete(boolean success, String message) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    btnRegister.setEnabled(true);
                    Toast.makeText(LoginSignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}

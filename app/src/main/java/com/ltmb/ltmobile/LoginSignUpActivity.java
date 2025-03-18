
package com.ltmb.ltmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.services.AuthService;

public class LoginSignUpActivity extends AppCompatActivity {
    private AuthService authService;
    private EditText editTextEmail, editTextPassword, editTextConfirmPass, editTextLoginEmail, editTextLoginPassword;
    private Button btnRegister, btnLogin;
    private ProgressDialog progressDialog;
    private LinearLayout loginForm, signUpForm;
    private TextView headerTitle,txtForgotPassword;
    private EditText editTextName, editTextPhone, editTextAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Kiểm tra trạng thái đăng nhập ngay khi mở ứng dụng
        checkLoginStatus();
    }

    // Kiểm tra trạng thái đăng nhập
    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            // Nếu người dùng đã đăng nhập, chuyển ngay đến màn hình chính
            String uid = sharedPreferences.getString("UID", "");
            String email = sharedPreferences.getString("EMAIL", "");
            String token = sharedPreferences.getString("TOKEN", "");

            // Gọi hàm lấy dữ liệu người dùng từ Firestore
            fetchUserData(uid, email, token);
        } else {
            // Nếu chưa đăng nhập, hiển thị trang đăng nhập/đăng ký
            setContentView(R.layout.activity_login_sign_up);

            // Ánh xạ các view
            headerTitle = findViewById(R.id.header_title);
            TextView backToSignUpButton = findViewById(R.id.backToSignUp);
            TextView backToLoginButton = findViewById(R.id.backToLogin);
            txtForgotPassword = findViewById(R.id.txtForgotPass);

            loginForm = findViewById(R.id.loginForm);
            signUpForm = findViewById(R.id.signUpForm);

            authService = new AuthService();

            // Đăng ký
            editTextName = findViewById(R.id.editTextName);
            editTextPhone = findViewById(R.id.editTextPhone);
            editTextAddress = findViewById(R.id.editTextAddress);
            editTextEmail = findViewById(R.id.editSignUp);
            editTextPassword = findViewById(R.id.passwordSignUp);
            editTextConfirmPass = findViewById(R.id.confirmPasswordSignUp);
            btnRegister = findViewById(R.id.btnRegister);

            // Đăng nhập
            editTextLoginEmail = findViewById(R.id.editLoginEmail);
            editTextLoginPassword = findViewById(R.id.editLoginPassword);
            btnLogin = findViewById(R.id.btnLogin);

            // Khởi tạo ProgressDialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Đang xử lý...");
            progressDialog.setCancelable(false);

            // Xử lý sự kiện chuyển giữa form đăng nhập và đăng ký
            backToSignUpButton.setOnClickListener(v -> switchToSignUp());
            backToLoginButton.setOnClickListener(v -> switchToLogin());

            txtForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginSignUpActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });

            // Xử lý đăng ký tài khoản
            btnRegister.setOnClickListener(v -> {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPass.getText().toString().trim();

                if (!validateRegisterInput(email, password, confirmPassword)) {
                    return;
                }
                registerUser(email, password);
            });

            // Xử lý đăng nhập tài khoản
            btnLogin.setOnClickListener(v -> {
                String email = editTextLoginEmail.getText().toString().trim();
                String password = editTextLoginPassword.getText().toString().trim();

                if (!validateLoginInput(email, password)) {
                    return;
                }
                loginUser(email, password);
            });
        }
    }

    // Chuyển sang form đăng ký
    private void switchToSignUp() {
        loginForm.setVisibility(View.GONE);
        signUpForm.setVisibility(View.VISIBLE);
        headerTitle.setText("Đăng ký");
        signUpForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    // Chuyển sang form đăng nhập
    private void switchToLogin() {
        signUpForm.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
        headerTitle.setText("Đăng nhập");
        loginForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
    }

    // Kiểm tra đầu vào đăng ký
    private boolean validateRegisterInput(String email, String password, String confirmPassword) {
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

    // Kiểm tra đầu vào đăng nhập
    private boolean validateLoginInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        btnRegister.setEnabled(false);

        // Lấy thông tin từ EditText
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        // Gọi phương thức trong AuthService
        authService.registerUser(email, password, name, phoneNumber, address, null, null, new AuthService.AuthCallback() {
            @Override
            public void onComplete(boolean success, String message) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    btnRegister.setEnabled(true);
                    Toast.makeText(LoginSignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (success) {
                        switchToLogin(); // Chuyển sang màn hình đăng nhập
                    }
                });
            }
        });
    }

    // Đăng nhập tài khoản
    private void loginUser(String email, String password) {
        progressDialog.show();
        btnLogin.setEnabled(false);

        authService.loginUser(email, password, new AuthService.AuthCallback() {
            @Override
            public void onComplete(boolean success, String message) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginSignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (success) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String uid = user.getUid();
                                    String email = user.getEmail();
                                    String token = task.getResult().getToken();

                                    // Lưu thông tin người dùng vào SharedPreferences
                                    saveUserSession(uid, email, token);

                                    // Gọi hàm lấy dữ liệu người dùng từ Firestore
                                    fetchUserData(uid, email, token);
                                } else {
                                    Toast.makeText(LoginSignUpActivity.this, "Lỗi lấy token", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    // Lưu thông tin người dùng vào SharedPreferences
    private void saveUserSession(String uid, String email, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UID", uid);
        editor.putString("EMAIL", email);
        editor.putString("TOKEN", token);
        editor.putBoolean("IS_LOGGED_IN", true); // Đánh dấu đã đăng nhập
        editor.apply();
    }

    // Thêm hàm fetchUserData() để lấy thông tin người dùng từ Firestore
    private void fetchUserData(String uid, String email, String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String phoneNumber = document.getString("phoneNumber");
                        String address = document.getString("address");

                        // Chuyển sang MainActivity và truyền dữ liệu
                        Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
                        intent.putExtra("UID", uid);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("TOKEN", token);
                        intent.putExtra("NAME", name);
                        intent.putExtra("PHONE", phoneNumber);
                        intent.putExtra("ADDRESS", address);
                        startActivity(intent);
                        finish(); // Đóng màn hình Login
                    } else {
                        Toast.makeText(LoginSignUpActivity.this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginSignUpActivity.this, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Đăng xuất
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xoá tất cả dữ liệu
        editor.apply();

        // Đăng xuất Firebase
        FirebaseAuth.getInstance().signOut();

        // Chuyển sang màn hình đăng nhập
        startActivity(new Intent(LoginSignUpActivity.this, LoginSignUpActivity.class));
        finish();
    }
}

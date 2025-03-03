package com.ltmb.ltmobile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class AuthService {

    private final FirebaseAuth firebaseAuth;

    public AuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean isValidPassword(String password) {
        // Mật khẩu phải có ít nhất 8 kí tự, 1 chữ cái in hoa và 1 kí tự đặc biệt
        String passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }

    public void registerUser(String email, String password, AuthCallback callback) {
        if (!isValidPassword(password)) {
            callback.onComplete(false, "Mật khẩu phải có ít nhất 8 kí tự, bao gồm 1 chữ cái in hoa và 1 kí tự đặc biệt.");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Gửi email xác thực
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            callback.onComplete(true, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực.");
                                        } else {
                                            callback.onComplete(false, "Đăng ký thành công nhưng gửi email xác thực thất bại.");
                                        }
                                    });
                        }
                    } else {
                        callback.onComplete(false, task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        if (!isValidPassword(password)) {
            callback.onComplete(false, "Mật khẩu không hợp lệ.");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            callback.onComplete(true, "Đăng nhập thành công.");
                        } else {
                            firebaseAuth.signOut();
                            callback.onComplete(false, "Email chưa được xác thực. Vui lòng kiểm tra hộp thư đến của bạn.");
                        }
                    } else {
                        callback.onComplete(false, "Đăng nhập thất bại: " + task.getException().getMessage());
                    }
                });
    }

    public void logoutUser() {
        firebaseAuth.signOut();
    }

    public interface AuthCallback {
        void onComplete(boolean success, String message);
    }
}

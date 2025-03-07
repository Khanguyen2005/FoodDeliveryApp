package com.ltmb.ltmobile.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AuthService {

    private final FirebaseAuth firebaseAuth;

    public AuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // Mật khẩu phải có ít nhất 8 kí tự, 1 chữ cái in hoa và 1 kí tự đặc biệt
        String passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }

    public void registerUser(String email, String password, String name, String phoneNumber, String address, Integer role, Long idRes, AuthCallback callback) {
        if (!isValidEmail(email)) {
            callback.onComplete(false, "Email không đúng định dạng.");
            return;
        }
        if (!isValidPassword(password)) {
            callback.onComplete(false, "Mật khẩu phải có ít nhất 8 kí tự, bao gồm 1 chữ cái in hoa và 1 kí tự đặc biệt.");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Log.d("AuthService", "Email xác thực đã được gửi.");
                                        } else {
                                            Log.e("AuthService", "Lỗi khi gửi email xác thực", verifyTask.getException());
                                        }
                                    });

                            saveUserToFirestore(user.getUid(), email, name, phoneNumber, address, role, idRes, callback);
                        }
                    } else {
                        callback.onComplete(false, "Đăng ký thất bại: " + task.getException().getMessage());
                        Log.e("AuthService", "Lỗi đăng ký: ", task.getException());
                    }
                });
    }
    private void saveUserToFirestore(String uid, String email, String name, String phoneNumber, String address, Integer role, Long idRes, AuthCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();

        userData.put("email", email);
        userData.put("createdAt", System.currentTimeMillis());

        if (name != null && !name.isEmpty()) {
            userData.put("name", name);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            userData.put("phoneNumber", phoneNumber);
        }
        if (address != null && !address.isEmpty()) {
            userData.put("address", address);
        }
        if (role != null) {
            userData.put("role", role);
        }
        if (idRes != null) {
            userData.put("idRes", idRes);
        }

        // Ghi dữ liệu vào Firestore
        db.collection("Users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Lưu thông tin user thành công: " + uid);
                    callback.onComplete(true, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lưu thông tin thất bại", e);
                    callback.onComplete(false, "Lưu dữ liệu thất bại: " + e.getMessage());
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
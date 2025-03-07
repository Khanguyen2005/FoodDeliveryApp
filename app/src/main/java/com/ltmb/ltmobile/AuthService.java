package com.ltmb.ltmobile;

import android.widget.Toast;

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
        return email.contains("@") && email.endsWith(".com");
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
                            String uid = user.getUid(); // Lấy UUID của user
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Chỉ lưu những trường có giá trị
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email); // Email luôn có
                            userData.put("createdAt", System.currentTimeMillis()); // Thời gian tạo luôn có

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

                            // Lưu vào Firestore
                            db.collection("Users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        callback.onComplete(true, "Đăng ký thành công! UID: " + uid);
                                    })
                                    .addOnFailureListener(e -> {
                                        callback.onComplete(false, "Đăng ký thành công nhưng lưu dữ liệu thất bại: " + e.getMessage());
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

//    public void registerUser(String email, String password, AuthCallback authCallback) {
//    }

    public interface AuthCallback {
        void onComplete(boolean success, String message);
    }
}
package com.ltmb.ltmobile.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;
    private Map<String, Object> userData = new HashMap<>();
    private ListenerRegistration listener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private UserManager() {
        // Khởi tạo lắng nghe dữ liệu Firestore
        startListening();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private void startListening() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        listener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("UserManager", "Lỗi lắng nghe dữ liệu: " + e.getMessage(), e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                userData = documentSnapshot.getData();
                Log.d("UserManager", "Dữ liệu cập nhật: " + userData);
            }
        });
    }

    public Map<String, Object> getUserData() {
        return userData;
    }

    public void stopListening() {
        if (listener != null) {
            listener.remove();
        }
    }
}

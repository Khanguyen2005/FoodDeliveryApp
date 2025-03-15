package Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditProfileFragment extends Fragment {

    private EditText edtName, edtPhone, edtAddress;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userId;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các EditText và Button từ layout
        edtName = view.findViewById(R.id.edtName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtAddress = view.findViewById(R.id.edtAddress);
        btnSave = view.findViewById(R.id.btnSave);

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
            Toast.makeText(getActivity(), "Lỗi: Không tìm thấy user ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.e("FirestoreError", "Lỗi khi tải dữ liệu: " + error.getMessage(), error);
                Toast.makeText(getActivity(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                edtName.setText(documentSnapshot.getString("name"));
                edtPhone.setText(documentSnapshot.getString("phoneNumber"));
                edtAddress.setText(documentSnapshot.getString("address"));
                Log.d("FirestoreDebug", "Dữ liệu tải thành công!");
            } else {
                Toast.makeText(getActivity(), "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(getActivity(), "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phoneNumber", phone);
        userData.put("address", address);

        db.collection("Users").document(userId)
                .set(userData) // Dùng set() thay vì update() để tạo nếu chưa có
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("FirestoreDebug", "Cập nhật thành công!");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Lỗi cập nhật dữ liệu: " + e.getMessage(), e);
                    Toast.makeText(getActivity(), "Lỗi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^0[0-9]{9}$", phone);
    }
}

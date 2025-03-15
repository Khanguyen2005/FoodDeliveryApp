package Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.services.UserManager;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView txtEmail, txtUID, txtToken, txtName, txtPhone, txtAddress;
    private Button btnEditProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ TextView từ layout
        txtEmail = view.findViewById(R.id.txtEmail);
        txtUID = view.findViewById(R.id.txtUID);
        txtToken = view.findViewById(R.id.txtToken);
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Thiết lập nút chỉnh sửa
        btnEditProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Lắng nghe dữ liệu người dùng từ UserManager
        updateUI(UserManager.getInstance().getUserData());
    }

    private void updateUI(Map<String, Object> userData) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        // Hiển thị thông tin từ FirebaseAuth
        txtEmail.setText("Email: " + auth.getCurrentUser().getEmail());
        txtUID.setText("UID: " + auth.getCurrentUser().getUid());
        txtToken.setText("Token: " + auth.getCurrentUser().getIdToken(false));

        // Hiển thị thông tin từ Firestore
        if (userData != null) {
            txtName.setText("Tên: " + userData.getOrDefault("name", "Không có tên"));
            txtPhone.setText("Số điện thoại: " + userData.getOrDefault("phoneNumber", "Không có số điện thoại"));
            txtAddress.setText("Địa chỉ: " + userData.getOrDefault("address", "Không có địa chỉ"));
        }
    }
}

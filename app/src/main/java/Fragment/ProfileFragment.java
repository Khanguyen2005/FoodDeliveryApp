package Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ltmb.ltmobile.ChangePasswordActivity;
import com.ltmb.ltmobile.CheckoutActivity;
import com.ltmb.ltmobile.EditProfileActivity;
import com.ltmb.ltmobile.LoginSignUpActivity;
import com.ltmb.ltmobile.MainActivity;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.services.UserManager;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView txtEmail, txtName, txtPhone;
    private Button btnEditProfile, btnLogout, btnChangePassword;
    private FirebaseAuth auth;
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
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        auth = FirebaseAuth.getInstance();

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
        // Lắng nghe dữ liệu người dùng từ UserManager
        updateUI(UserManager.getInstance().getUserData());

    }

    private void updateUI(Map<String, Object> userData) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        // Hiển thị thông tin từ FirebaseAuth
        txtEmail.setText(auth.getCurrentUser().getEmail());

        // Hiển thị thông tin từ Firestore
        if (userData != null) {
            txtName.setText(String.valueOf(userData.getOrDefault("name", "Không có tên")));
            txtPhone.setText(String.valueOf(userData.getOrDefault("phoneNumber", "Không có số điện thoại")));
        }
    }
    private void logout() {
        if (auth != null) {
            auth.signOut();
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(requireActivity(), LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        requireActivity().finishAffinity();
    }

}
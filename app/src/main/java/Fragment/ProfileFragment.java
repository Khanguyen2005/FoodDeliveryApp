package Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ltmb.ltmobile.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private TextView txtEmail, txtUID, txtToken, txtName, txtPhone, txtAddress;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@Nonnull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ TextView từ layout fragment_profile.xml
        txtEmail = view.findViewById(R.id.txtEmail);
        txtUID = view.findViewById(R.id.txtUID);
        txtToken = view.findViewById(R.id.txtToken);
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);

        // Nhận dữ liệu từ Intent của Activity chứa Fragment
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            String email = intent.getStringExtra("EMAIL");
            String uid = intent.getStringExtra("UID");
            String token = intent.getStringExtra("TOKEN");
            String name = intent.getStringExtra("NAME");
            String phone = intent.getStringExtra("PHONE");
            String address = intent.getStringExtra("ADDRESS");

            // Hiển thị thông tin với kiểm tra null
            txtEmail.setText("Email: " + (email != null ? email : "Không có email"));
            txtUID.setText("UID: " + (uid != null ? uid : "Không có UID"));
            txtToken.setText("Token: " + (token != null ? token : "Không có token"));
            txtName.setText("Tên: " + (name != null ? name : "Không có tên"));
            txtPhone.setText("Số điện thoại: " + (phone != null ? phone : "Không có số điện thoại"));
            txtAddress.setText("Địa chỉ: " + (address != null ? address : "Không có địa chỉ"));
        }
    }
}
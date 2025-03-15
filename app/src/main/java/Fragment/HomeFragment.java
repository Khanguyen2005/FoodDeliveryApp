package Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.ltmb.ltmobile.CategoryActivity;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.RestaurantActivity;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import Adapter.Restaurant;
import Adapter.RestaurantAdapter;

public class HomeFragment extends Fragment {
    private TextView txtAddress;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> listRes;
    private RestaurantManagement restaurantManagement;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration addressListener;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtAddress = view.findViewById(R.id.address);
        recyclerView = view.findViewById(R.id.rcvRes);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUserAddress();
        setupSearchBar(view);
        setupCategoryButtons(view);
        setupRecyclerView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (addressListener != null) {
            addressListener.remove();  // Hủy lắng nghe Firestore khi Fragment bị hủy
        }
    }

    private void loadUserAddress() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e("FirestoreError", "Người dùng chưa đăng nhập!");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("Users").document(userId);
        addressListener = userRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.e("FirestoreError", "Lỗi khi tải dữ liệu: " + error.getMessage(), error);
                return;
            }

            if (documentSnapshot == null || !documentSnapshot.exists()) {
                Log.e("FirestoreError", "Không tìm thấy dữ liệu người dùng!");
                return;
            }

            String newAddress = documentSnapshot.getString("address");
            if (newAddress == null || newAddress.trim().isEmpty()) {
                Log.e("FirestoreDebug", "Địa chỉ rỗng hoặc null!");
                return;
            }

            requireActivity().runOnUiThread(() -> {
                txtAddress.setText(newAddress);

                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_PREFS", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ADDRESS", newAddress);
                editor.apply();

                Log.d("FirestoreDebug", "Cập nhật địa chỉ trong HomeFragment: " + newAddress);
            });
        });
    }

    private void setupRecyclerView() {
        listRes = new ArrayList<>();
        adapter = new RestaurantAdapter(getContext(), restaurant -> {
            Intent i = new Intent(getContext(), RestaurantActivity.class);
            i.putExtra("restaurant_id", restaurant.getId());
            startActivity(i);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadRestaurants();
    }

    private void loadRestaurants() {
        restaurantManagement = new RestaurantManagement();
        restaurantManagement.getRestaurants(new RestaurantManagement.RestaurantCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> restaurantList) {
                listRes.clear();
                for (Map<String, Object> data : restaurantList) {
                    try {
                        String id = String.valueOf(data.get("id"));
                        String name = (String) data.get("name");
                        Number starRes = data.get("starRes") != null ? Double.parseDouble(data.get("starRes").toString()) : 5.0;
                        Number evaluate = data.get("evaluate") != null ? Integer.parseInt(data.get("evaluate").toString()) : 0;
                        String imgUrl = (String) data.get("image");
                        String backgroundImgUrl = (String) data.get("backgroundImage");

                        Restaurant res = new Restaurant(id, name, starRes, evaluate, imgUrl, backgroundImgUrl);
                        listRes.add(res);
                    } catch (Exception e) {
                        Log.e("Firestore", "Lỗi chuyển đổi dữ liệu", e);
                    }
                }
                adapter.setData(listRes);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Lỗi tải danh sách nhà hàng", e);
            }
        });
    }

    private void setupCategoryButtons(View view) {
        view.findViewById(R.id.layoutRice).setOnClickListener(v -> openCategory(Arrays.asList("cơm", "rice")));
        view.findViewById(R.id.layoutNoodle).setOnClickListener(v -> openCategory(Arrays.asList("bún, mì, phở", "bún", "mì", "phở", "hủ tiếu", "bánh canh")));
        view.findViewById(R.id.layoutFriedChicken).setOnClickListener(v -> openCategory(Arrays.asList("gà rán", "gà giòn", "pizza", "burger", "khoai tây chiên")));
        view.findViewById(R.id.layoutSnack).setOnClickListener(v -> openCategory(Arrays.asList("Ăn vặt", "bánh tráng", "trà sữa", "tea")));
        view.findViewById(R.id.layoutMilktea).setOnClickListener(v -> openCategory(Arrays.asList("trà sữa", "trà", "cà phê", "tea")));
    }

    private void openCategory(List<String> keywords) {
        Intent intent = new Intent(requireContext(), CategoryActivity.class);
        intent.putStringArrayListExtra("CATEGORY_KEYWORDS", new ArrayList<>(keywords));
        startActivity(intent);
    }

    private void setupSearchBar(View view) {
        EditText searchFood = view.findViewById(R.id.searchFood);
        searchFood.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String searchText = searchFood.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    List<String> searchKeywords = Arrays.asList(searchText.split("\\s+"));

                    Intent intent1 = new Intent(getContext(), CategoryActivity.class);
                    intent1.putStringArrayListExtra("SEARCH_KEYWORDS", new ArrayList<>(searchKeywords));
                    intent1.putExtra("EXTRA_SOURCE", "SEARCH");
                    startActivity(intent1);
                }
                return true;
            }
            return false;
        });
    }
}

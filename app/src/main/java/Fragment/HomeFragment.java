package Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltmb.ltmobile.MainActivity;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
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

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtAddress = view.findViewById(R.id.address);
        recyclerView = view.findViewById(R.id.rcvRes);

        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            String address = intent.getStringExtra("ADDRESS");
            txtAddress.setText((address != null ? address : "Chưa có địa chỉ"));
        }

        listRes = new ArrayList<>();
        adapter = new RestaurantAdapter(getContext(), restaurant -> {
            // Xử lý khi click vào nhà hàng
            Intent i = new Intent(getContext(), MainActivity.class);
            i.putExtra("restaurant_id", restaurant.getId());
            startActivity(i);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadRestaurants();

        return view;
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
                        String starRes = String.valueOf(data.get("starRes"));
                        String evaluateRes = (String) data.get("evaluateRes");
                        String imgUrl = (String) data.get("image");

                        Restaurant res = new Restaurant(id, name, starRes, evaluateRes, imgUrl);
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
}

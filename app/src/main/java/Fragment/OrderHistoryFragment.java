package Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ltmb.ltmobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.OrderHistoryAdapter;
import Adapter.OrderItemModel;
import Adapter.OrderModel;

public class OrderHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            fetchOrderHistory();
        } else {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchOrderHistory() {
        firestore.collection("Orders")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String restaurantId = doc.getString("restaurantId");
                        double totalPrice = doc.getDouble("totalPrice"); // Đổi sang getDouble()

                        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) doc.get("items");
                        List<OrderItemModel> orderItems = new ArrayList<>();

                        if (itemsData != null) {
                            for (Map<String, Object> item : itemsData) {
                                String name = (String) item.get("name");
                                String imageUrl = (String) item.get("imageUrl");
                                int quantity = ((Long) item.get("quantity")).intValue();
                                double price = (item.get("price") instanceof Long) ? ((Long) item.get("price")).doubleValue() : (Double) item.get("price");

                                List<Map<String, Object>> toppings = (List<Map<String, Object>>) item.get("toppings");

                                orderItems.add(new OrderItemModel(name, imageUrl, quantity, price, toppings));
                            }
                        }

                        orderList.add(new OrderModel(user.getUid(), restaurantId, totalPrice, orderItems));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show());
    }


}

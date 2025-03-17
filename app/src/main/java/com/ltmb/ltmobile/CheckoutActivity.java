package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.services.CartDatabaseHelper;
import com.ltmb.ltmobile.services.OrderSuccessActivity;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapter.CartItem;
import Adapter.CheckoutAdapter;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCheckout;
    private TextView tvTotalCheckoutPrice;
    private Button btnConfirmPayment;
    private CartDatabaseHelper dbHelper;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> checkoutList;
    private String currentRestaurantId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout);
        tvTotalCheckoutPrice = findViewById(R.id.tvTotalCheckoutPrice);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        dbHelper = new CartDatabaseHelper(this);
        firestore = FirebaseFirestore.getInstance();

        currentRestaurantId = getIntent().getStringExtra("restaurant_id");

        loadCheckoutItems();

        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
    }

    private void loadCheckoutItems() {
        checkoutList = dbHelper.getCartItems(currentRestaurantId);
        checkoutAdapter = new CheckoutAdapter(checkoutList, this);
        recyclerViewCheckout.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCheckout.setAdapter(checkoutAdapter);

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = dbHelper.getTotalPrice(currentRestaurantId);
        String formattedPrice = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalPrice) + " đ";
        tvTotalCheckoutPrice.setText("Tổng tiền: " + formattedPrice);
    }

    private void confirmPayment() {
        if (checkoutList.isEmpty()) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            tvTotalCheckoutPrice.setText("Lỗi: Người dùng chưa đăng nhập.");
            return;
        }

        String userId = user.getUid(); // Lấy UID của người dùng hiện tại

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId); // Gán UID vào đơn hàng
        orderData.put("restaurantId", currentRestaurantId);
        orderData.put("totalPrice", dbHelper.getTotalPrice(currentRestaurantId));
        orderData.put("items", dbHelper.getCartItemsAsMap(currentRestaurantId));

        firestore.collection("Orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    dbHelper.clearCart(currentRestaurantId);
                    startActivity(new Intent(CheckoutActivity.this, OrderSuccessActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> tvTotalCheckoutPrice.setText("Lỗi thanh toán, vui lòng thử lại."));
    }


}

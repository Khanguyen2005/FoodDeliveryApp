package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.services.CartDatabaseHelper;
import com.ltmb.ltmobile.services.OrderSuccessActivity;

import java.util.HashMap;
import java.util.List;
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
        tvTotalCheckoutPrice.setText("Tổng tiền: " + totalPrice + " đ");
    }

    private void confirmPayment() {
        if (checkoutList.isEmpty()) return;

        // Tạo đơn hàng lưu vào Firestore
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("restaurantId", currentRestaurantId);
        orderData.put("totalPrice", dbHelper.getTotalPrice(currentRestaurantId));
        orderData.put("items", checkoutList);

        firestore.collection("Orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    dbHelper.clearCart(currentRestaurantId);  // Xóa giỏ hàng sau khi thanh toán thành công
                    startActivity(new Intent(CheckoutActivity.this, OrderSuccessActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    tvTotalCheckoutPrice.setText("Lỗi thanh toán, vui lòng thử lại.");
                });
    }
}
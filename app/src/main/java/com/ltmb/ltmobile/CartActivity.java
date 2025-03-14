package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Import để debug
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltmb.ltmobile.services.CartDatabaseHelper;

import java.util.List;

import Adapter.CartAdapter;
import Adapter.CartItem;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartDatabaseHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private String currentRestaurantId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        dbHelper = new CartDatabaseHelper(this);



        // Lấy ID nhà hàng từ Intent
        currentRestaurantId = getIntent().getStringExtra("restaurant_id");
        // Kiểm tra nếu `currentRestaurantId` bị nulls
        if (currentRestaurantId == null || currentRestaurantId.isEmpty()) {
            Log.e("CartActivity", "⚠️ restaurant_id bị NULL hoặc trống!");
            showAlert("Lỗi", "Không tìm thấy ID nhà hàng. Vui lòng thử lại.");
            return;
        }

        Log.d("CartActivity", "✅ restaurant_id nhận được: " + currentRestaurantId);

        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            if (!cartItemList.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("restaurant_id", currentRestaurantId);
                startActivity(intent);
            } else {
                showAlert("Giỏ hàng đang trống!", "Vui lòng thêm món vào giỏ hàng trước khi thanh toán.");
            }
        });
    }

    // Load danh sách món trong giỏ hàng
    private void loadCartItems() {
        cartItemList = dbHelper.getCartItems(currentRestaurantId);

        if (cartItemList == null || cartItemList.isEmpty()) {
            Log.e("CartActivity", "⚠️ Giỏ hàng trống hoặc không thể lấy dữ liệu!");
            showAlert("Giỏ hàng trống", "Không có món nào trong giỏ hàng của bạn.");
            return;
        }

        Log.d("CartActivity", "✅ Số món trong giỏ hàng: " + cartItemList.size());

        cartAdapter = new CartAdapter(cartItemList, this, dbHelper, tvTotalPrice);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalPrice();
    }

    // Cập nhật tổng tiền
    public void updateTotalPrice() {
        double totalPrice = dbHelper.getTotalPrice(currentRestaurantId);
        Log.d("CartActivity", "💰 Tổng tiền: " + totalPrice);
        tvTotalPrice.setText("Tổng tiền: " + totalPrice + " đ");
    }

    // Hiển thị thông báo
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

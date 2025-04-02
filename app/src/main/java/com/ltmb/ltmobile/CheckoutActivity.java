package com.ltmb.ltmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.vnpay.authentication.VNP_AuthenticationActivity; // ✅ Thêm VNPAY SDK

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import Adapter.CartItem;
import Adapter.CheckoutAdapter;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCheckout;
    private TextView tvTotalCheckoutPrice, tvUserAddress;
    private Button btnConfirmPayment, btnBack;
    private CartDatabaseHelper dbHelper;
    private CheckoutAdapter checkoutAdapter;
    private LinearLayout layoutAddress;
    private RadioButton rbCard; // ✅ RadioButton thanh toán VNPay
    private List<CartItem> checkoutList;
    private String currentRestaurantId;
    private FirebaseFirestore firestore;

    // ✅ Khai báo cấu hình VNPay
    private static final String VNP_TMNCODE = "F7BK0FNR";
    private static final String VNP_HASH_SECRET = "RHNNXUA463B6O76DE61NOYTPH3ZHFZKY";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURNURL = "digitalhibpayment://returnfromvnpay";
    private static final String SCHEME = "digitalhibpayment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvUserAddress = findViewById(R.id.txtAddress);
        layoutAddress = findViewById(R.id.layoutAddress);
        btnBack = findViewById(R.id.btnBack);
        rbCard = findViewById(R.id.rbCard); // ✅ Gán ID RadioButton

        btnBack.setOnClickListener(v -> finish());
        layoutAddress.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout);
        tvTotalCheckoutPrice = findViewById(R.id.tvTotalCheckoutPrice);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        dbHelper = new CartDatabaseHelper(this);
        firestore = FirebaseFirestore.getInstance();

        currentRestaurantId = getIntent().getStringExtra("restaurant_id");

        loadCheckoutItems();
        listenForAddressUpdates();

        btnConfirmPayment.setOnClickListener(v -> {
            if (rbCard.isChecked()) {
                int amount = (int) dbHelper.getTotalPrice(currentRestaurantId);
                openVNPaySdk(amount); // ✅ Gọi thanh toán VNPay nếu chọn rbCard
            } else {
                confirmPayment(); // ✅ Giữ nguyên logic cũ nếu không chọn VNPay
            }
        });
    }

    // ✅ Gọi Activity thanh toán của VNPay SDK
    private void openVNPaySdk(int amount) {
        String orderId = "DH_" + System.currentTimeMillis();
        String paymentUrl = getPaymentUrl(amount, orderId);

        Intent intent = new Intent(this, VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("tmn_code", VNP_TMNCODE);
        intent.putExtra("scheme", SCHEME);
        intent.putExtra("is_sandbox", true);
        startActivity(intent);
    }

    // ✅ Tạo URL thanh toán với đầy đủ thông số
    private String getPaymentUrl(int amount, String orderId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_CreateDate", sdf.format(new Date()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", getLocalIpAddress());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng " + orderId);
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_ReturnUrl", VNP_RETURNURL);
        vnp_Params.put("vnp_TmnCode", VNP_TMNCODE);
        vnp_Params.put("vnp_TxnRef", orderId);
        vnp_Params.put("vnp_Version", "2.1.0");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", sdf.format(calendar.getTime()));

        List<Map.Entry<String, String>> paramList = new ArrayList<>(vnp_Params.entrySet());
        paramList.sort(Map.Entry.comparingByKey());

        StringBuilder queryBuilder = new StringBuilder();
        for (Map.Entry<String, String> param : paramList) {
            try {
                queryBuilder.append(param.getKey()).append("=")
                        .append(URLEncoder.encode(param.getValue(), "UTF-8"))
                        .append("&");
            } catch (Exception e) {
                Log.e("VNPay", "Encode Error: " + e.getMessage());
            }
        }
        String query = queryBuilder.substring(0, queryBuilder.length() - 1);
        String secureHash = hmacSHA512(VNP_HASH_SECRET, query);
        return VNP_URL + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    // ✅ Lấy IP local của thiết bị
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("VNPay", "Không thể lấy địa chỉ IP", ex);
        }
        return "127.0.0.1";
    }

    // ✅ Tạo chữ ký SHA512 để xác minh URL
    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            Log.e("VNPay", "HMAC SHA512 Error: " + e.getMessage());
            return null;
        }
    }

    // Hàm xử lý DeepLink trả về
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật intent mới

        Uri data = intent.getData();
        if (data != null) {
            String vnp_ResponseCode = data.getQueryParameter("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
            }
        }
    }




    // ⚠️ Không sửa logic confirmPayment cũ
    private void confirmPayment() {
        if (checkoutList.isEmpty()) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            tvTotalCheckoutPrice.setText("Lỗi: Người dùng chưa đăng nhập.");
            return;
        }
        String userId = user.getUid();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("restaurantId", currentRestaurantId);
        orderData.put("totalPrice", dbHelper.getTotalPrice(currentRestaurantId));
        orderData.put("items", dbHelper.getCartItemsAsMap(currentRestaurantId));

        firestore.collection("Orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    dbHelper.clearCart(currentRestaurantId);
                    startActivity(new Intent(this, OrderSuccessActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> tvTotalCheckoutPrice.setText("Lỗi thanh toán, vui lòng thử lại."));
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

    private void listenForAddressUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        firestore.collection("Users").document(user.getUid())
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Lỗi khi tải địa chỉ: " + error.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String address = documentSnapshot.getString("address");
                        tvUserAddress.setText(address != null ? address : "Chưa có địa chỉ");
                    }
                });
    }
}

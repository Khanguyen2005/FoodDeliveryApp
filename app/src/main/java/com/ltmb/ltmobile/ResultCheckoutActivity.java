package com.ltmb.ltmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ltmb.ltmobile.services.OrderSuccessActivity;

public class ResultCheckoutActivity extends AppCompatActivity {

    TextView result_checkout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        result_checkout = findViewById(R.id.result_checkout);

        Uri data = getIntent().getData();
        if (data != null) {
            String vnp_ResponseCode = data.getQueryParameter("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

                // ✅ Đợi 3 giây trước khi chuyển sang OrderSuccessActivity
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(ResultCheckoutActivity.this, OrderSuccessActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }, 3000); // 3 giây (3000ms)

            } else {
                result_checkout.setText("Thanh toán thất bại!");
                Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
                // ✅ Đợi 3 giây rồi quay lại CheckoutActivity
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(ResultCheckoutActivity.this, CheckoutActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }, 3000);
            }
        }
    }
}
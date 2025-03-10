package com.ltmb.ltmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox; // 🔹 Thêm import CheckBox để tạo danh sách toppings
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.List;
import java.util.Map;

public class BottomSheetAddTopping extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetAddTopping";

    private String restaurantId;
    private String categoryId;
    private RestaurantManagement restaurantManagement;
    private LinearLayout toppingContainer; // 🔹 Thêm LinearLayout để chứa danh sách toppings

    // 🔹 Hàm tạo để nhận dữ liệu từ FoodAdapter
    public static BottomSheetAddTopping newInstance(String restaurantId, String categoryId) {
        BottomSheetAddTopping fragment = new BottomSheetAddTopping();
        Bundle args = new Bundle();
        args.putString("restaurantId", restaurantId);
        args.putString("categoryId", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_topping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Lấy dữ liệu từ Bundle để xác định nhà hàng và danh mục món ăn
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
            categoryId = getArguments().getString("categoryId");
        }

        // 🔹 Tìm LinearLayout toppingContainer để thêm toppings vào
        toppingContainer = view.findViewById(R.id.toppingContainer);
        Button btnAddToCart = view.findViewById(R.id.btnAddToCart); // 🔹 Button "Thêm vào giỏ hàng"

        restaurantManagement = new RestaurantManagement();

        // 🔹 Gọi API để lấy danh sách toppings
        fetchToppings();

        // 🔹 Sự kiện click cho "Thêm vào giỏ hàng" (chỉ hiển thị thông báo)
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            view.setLayoutParams(params);

            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog != null) {
                View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    behavior.setDraggable(false);
                }
            }
        }
    }

    /**
     * 🔹 Gọi Firestore để lấy danh sách toppings và hiển thị trên BottomSheet.
     */
    private void fetchToppings() {
        Log.d(TAG, "Fetching toppings for Restaurant ID: " + restaurantId + ", Category ID: " + categoryId);

        restaurantManagement.getToppings(restaurantId, categoryId, new RestaurantManagement.ToppingCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> toppingList) {
                if (toppingList.isEmpty()) {
                    Log.d(TAG, "⚠️ Không tìm thấy dữ liệu toppings!");
                    return;
                }

                Log.d(TAG, "✅ Toppings retrieved successfully! Tổng số toppings: " + toppingList.size());

                // 🔹 Hiển thị toppings trong BottomSheet dưới dạng CheckBox
                for (Map<String, Object> topping : toppingList) {
                    String name = (String) topping.get("name");
                    Long price = (Long) topping.get("price");

                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(name + " - " + price + "đ");
                    checkBox.setTextSize(16);
                    checkBox.setPadding(10, 10, 10, 10);

                    toppingContainer.addView(checkBox); // 🔹 Thêm CheckBox vào danh sách toppings
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "🚨 Error fetching toppings: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi lấy toppings!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

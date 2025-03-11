package com.ltmb.ltmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox; // 🔹 Thêm import CheckBox để tạo danh sách toppings
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BottomSheetAddTopping extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetAddTopping";

    private String restaurantId;
    private String categoryId;
    ImageView imageView;
    private String foodId;
    TextView nameFood,quantitySold,price_food;
    private RestaurantManagement restaurantManagement;
    private LinearLayout toppingContainer; // 🔹 Thêm LinearLayout để chứa danh sách toppings

    // 🔹 Hàm tạo để nhận dữ liệu từ FoodAdapter
    public static BottomSheetAddTopping newInstance(String restaurantId, String categoryId, String foodId) {
        BottomSheetAddTopping fragment = new BottomSheetAddTopping();
        Bundle args = new Bundle();
        args.putString("restaurantId", restaurantId);
        args.putString("categoryId", categoryId);
        args.putString("foodId", foodId);
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
            foodId = getArguments().getString("foodId");
        }
        nameFood = getView().findViewById(R.id.food_name);
        price_food = getView().findViewById(R.id.price_food);
        quantitySold = getView().findViewById(R.id.quantitySold);
        imageView = getView().findViewById(R.id.img_food);


        // 🔹 Tìm LinearLayout toppingContainer để thêm toppings vào
        toppingContainer = view.findViewById(R.id.toppingContainer);
        Button btnAddToCart = view.findViewById(R.id.btnAddToCart); // 🔹 Button "Thêm vào giỏ hàng"

        restaurantManagement = new RestaurantManagement();

        // 🔹 Gọi API để lấy danh sách toppings
//        fetchToppings();
        fetchToppings();
        fetchFoodDetails();
        // 🔹 Sự kiện click cho "Thêm vào giỏ hàng"

        // 🔹 lấy dữ liệu từ nút này truyền qua giỏ hàng nè chó Khoa
        btnAddToCart.setOnClickListener(v -> {
            if (getView() == null) return;
            // 🔹 Lấy thông tin món ăn
            String name = nameFood.getText().toString();
            String price = price_food.getText().toString();
            String quantity = quantitySold.getText().toString();
            String imageUrl = (String) imageView.getTag();
            // 🔹 Lấy danh sách topping đã chọn
            StringBuilder selectedToppings = new StringBuilder();
            for (int i = 0; i < toppingContainer.getChildCount(); i++) {
                View child = toppingContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        selectedToppings.append(checkBox.getText().toString()).append(", ");
                    }
                }
            }
            Log.d("CartItem", "Món ăn: " + name);
            Log.d("CartItem", "Giá: " + price);
            Log.d("CartItem", "Số lượng: " + quantity);
            Log.d("CartItem", "Ảnh: " + imageUrl);
            Log.d("CartItem", "Toppings đã chọn: " + (selectedToppings.length() > 0 ? selectedToppings.toString() : "Không có"));
            Toast.makeText(getContext(), "Đã log thông tin món ăn", Toast.LENGTH_SHORT).show();
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
//    private void fetchToppings() {
//        Log.d(TAG, "Fetching toppings for Restaurant ID: " + restaurantId + ", Category ID: " + categoryId);
//
//        restaurantManagement.getToppings(restaurantId, categoryId, new RestaurantManagement.ToppingCallback() {
//            @Override
//            public void onSuccess(List<Map<String, Object>> toppingList) {
//                if (toppingList.isEmpty()) {
//                    Log.d(TAG, "⚠️ Không tìm thấy dữ liệu toppings!");
//                    return;
//                }
//
//                Log.d(TAG, "✅ Toppings retrieved successfully! Tổng số toppings: " + toppingList.size());
//
//                // 🔹 Hiển thị toppings trong BottomSheet dưới dạng CheckBox
//                for (Map<String, Object> topping : toppingList) {
//                    String name = (String) topping.get("name");
//                    Long price = (Long) topping.get("price");
//
//                    CheckBox checkBox = new CheckBox(getContext());
//                    checkBox.setText(name + " - " + price + "đ");
//                    checkBox.setTextSize(20);
//                    checkBox.setPadding(10, 10, 10, 10);
//
//                    toppingContainer.addView(checkBox); // 🔹 Thêm CheckBox vào danh sách toppings
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.e(TAG, "🚨 Error fetching toppings: " + e.getMessage(), e);
//                Toast.makeText(getContext(), "Lỗi khi lấy toppings!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    /**
     * 🔹 Gọi Firestore để lấy thông tin của món ăn dựa trên foodId
     */
    /**
     * 🔹 Lấy thông tin món ăn trực tiếp từ Firestore mà không cần gọi `getFoodDetails()`
     */
    private void fetchFoodDetails() {
        Log.d(TAG, "Fetching food details for ID: " + foodId);

        restaurantManagement.getDishes(restaurantId, categoryId, new RestaurantManagement.DishCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> dishList) {
                for (Map<String, Object> dish : dishList) {
                    if (dish.get("id").equals(foodId)) {
                        Log.d(TAG, "✅ Tìm thấy món ăn: " + dish.get("name"));

                        if (getView() == null) {
                            Log.e(TAG, "🚨 View chưa được khởi tạo!");
                            return;
                        }


                        if (nameFood == null || price_food == null || imageView == null) {
                            Log.e(TAG, "🚨 Một trong các view chưa được tìm thấy!");
                            return;
                        }

                        getActivity().runOnUiThread(() -> {
                            nameFood.setText((String) dish.get("name"));
                            price_food.setText(dish.get("price") + "đ");
                            Object quantitySoldValue = dish.get("quantitySold");
                            String quantitySoldText = (quantitySoldValue != null) ? quantitySoldValue + " phần đã bán" : "0 phần đã bán";
                            quantitySold.setText(quantitySoldText);
                            Glide.with(requireContext())
                                    .load((String) dish.get("image"))
                                    .error(R.mipmap.ic_launcher)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(imageView);

                            // 🔹 Gán URL ảnh vào Tag của imageView
                            imageView.setTag((String) dish.get("image"));

                        });

                        break;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "🚨 Lỗi khi lấy danh sách món ăn: ", e);
                Toast.makeText(getContext(), "Lỗi khi lấy thông tin món ăn!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void fetchToppings() {
        Log.d(TAG, "Fetching toppings for Restaurant ID: " + restaurantId + ", Category ID: " + categoryId);

        restaurantManagement.getAllToppings(restaurantId, categoryId, new RestaurantManagement.ToppingCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> toppingList) {
                if (getActivity() == null) return; // Tránh crash nếu Activity bị đóng

                getActivity().runOnUiThread(() -> {
                    toppingContainer.removeAllViews();

                    if (toppingList.isEmpty()) {
                        Log.d(TAG, "⚠️ Không có toppings nào!");
                        return;
                    }

                    for (Map<String, Object> toppingData : toppingList) {
                        String toppingName = (String) toppingData.get("name");
                        int min = 0, max = 1;
                        try {
                            if (toppingData.containsKey("min")) {
                                min = Integer.parseInt(String.valueOf(toppingData.get("min")));
                            }
                            if (toppingData.containsKey("max")) {
                                max = Integer.parseInt(String.valueOf(toppingData.get("max")));
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "🚨 Lỗi ép kiểu min/max: " + e.getMessage());
                        }

                        // **Final copy để sử dụng trong lambda**
                        final int finalMax = max;

                        // **Tiêu đề nhóm toppings**
                        TextView sectionTitle = new TextView(getContext());
                        sectionTitle.setText(toppingName + " (Chọn " + min + " - " + finalMax + ")");
                        sectionTitle.setTextSize(16);
                        sectionTitle.setTextColor(getResources().getColor(R.color.black));
                        sectionTitle.setPadding(10, 20, 10, 10);
                        sectionTitle.setBackgroundColor(getResources().getColor(R.color.light_gray)); // Màu nền nhẹ
                        toppingContainer.addView(sectionTitle);

                        // **Danh sách các toppings (luôn dùng CheckBox)**
                        List<CheckBox> checkBoxList = new ArrayList<>();

                        List<Map<String, Object>> items = (List<Map<String, Object>>) toppingData.get("items");
                        if (items != null) {
                            for (Map<String, Object> itemData : items) {
                                String itemName = (String) itemData.get("name");
                                Long itemPrice = (Long) itemData.get("price");

                                CheckBox checkBox = new CheckBox(getContext());
                                checkBox.setText(itemName + (itemPrice != null && itemPrice > 0 ? " - " + itemPrice + "đ" : " - Miễn phí"));
                                checkBox.setTextSize(14);
                                checkBox.setTextColor(getResources().getColor(R.color.black));

                                // Thêm CheckBox vào danh sách
                                checkBoxList.add(checkBox);
                                toppingContainer.addView(checkBox);
                            }
                        }

                        // **Giới hạn chọn toppings theo max**
                        for (CheckBox checkBox : checkBoxList) {
                            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                long selectedCount = checkBoxList.stream().filter(CheckBox::isChecked).count();

                                // Nếu đạt max, vô hiệu hóa các CheckBox chưa chọn
                                boolean disableUnchecked = selectedCount >= finalMax;
                                for (CheckBox cb : checkBoxList) {
                                    if (!cb.isChecked()) {
                                        cb.setEnabled(!disableUnchecked);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "🚨 Lỗi khi lấy danh sách toppings: ", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi khi lấy toppings!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


}

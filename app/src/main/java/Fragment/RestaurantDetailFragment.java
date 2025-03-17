package Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ltmb.ltmobile.BottomSheetAddTopping;
import com.ltmb.ltmobile.CartActivity;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.RestaurantActivity;
import com.ltmb.ltmobile.services.CartDatabaseHelper;
import com.ltmb.ltmobile.services.DiscountService;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.CartAdapter;
import Adapter.CartItem;
import Adapter.Category;
import Adapter.CategoryAdapter;
import Adapter.Discount;
import Adapter.DiscountAdapter;
import Adapter.Food;
import Adapter.FoodAdapter;
import Adapter.Outstanding;
import Adapter.OutstandingAdapter;
import JSON.ConvertData;

public class RestaurantDetailFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView rcvCate;
    private String restaurantId, name, imageUrl, backgroundImage;
    private double star, evaluate;
    private CategoryAdapter categoryAdapter;
    private ScrollView scrollView;
    private LinearLayout headerLayout;
    private RestaurantManagement restaurantManagement;
    private TextView txtName, txtStar, txtEvaluate;
    private ImageView imgRestaurant, imgBackground;
    private List<Category> listCate = new ArrayList<>();
    private List<Discount> discountList = new ArrayList<>();
    private RecyclerView rcvOutstanding,rcvDiscount;
    private OutstandingAdapter outstandingAdapter;
    private DiscountAdapter discountAdapter;
    private List<Outstanding> listOutstanding = new ArrayList<>();

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_STAR = "star";
    private static final String ARG_EVALUATE = "evaluate";
    private static final String ARG_IMAGE_URL = "imageUrl";
    LinearLayout layoutBtnCate ;

    private static final String ARG_BACKGROUND_IMAGE_URL = "backgroundImageUrl";

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailFragment newInstance(String id, String name, double star, double evaluate, String imageUrl, String backgroundImage) {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putDouble(ARG_STAR, star);  // Sử dụng putDouble thay vì putString
        args.putDouble(ARG_EVALUATE, evaluate);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BACKGROUND_IMAGE_URL, backgroundImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getString(ARG_ID);
            name = getArguments().getString(ARG_NAME, "Không có tên");
            star = getArguments().getDouble(ARG_STAR, 0.0);  // Lấy kiểu double
            evaluate = getArguments().getDouble(ARG_EVALUATE, 0.0);
            imageUrl = getArguments().getString(ARG_IMAGE_URL, "");
            backgroundImage = getArguments().getString(ARG_BACKGROUND_IMAGE_URL, "");
        }
        restaurantManagement = new RestaurantManagement();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        txtName = view.findViewById(R.id.name_Res);
        txtStar = view.findViewById(R.id.star_res);
        txtEvaluate = view.findViewById(R.id.evaluate_Res);
        imgRestaurant = view.findViewById(R.id.img_res);
        imgBackground = view.findViewById(R.id.background_image);
        Button btnBack = view.findViewById(R.id.btnBack);
        scrollView = view.findViewById(R.id.scrollview);
        headerLayout = view.findViewById(R.id.layoutBtn);
        rcvCate = view.findViewById(R.id.rcvCate);

        Button btnCart = view.findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            intent.putExtra("restaurant_id", restaurantId); // Truyền ID nhà hàng
            startActivity(intent);
        });

        Log.d("RestaurantDetail", "restaurantId: " + restaurantId);

        txtName.setText(name);
        txtStar.setText(String.format("⭐ %.1f", star)); // Hiển thị 1 số thập phân
        txtEvaluate.setText(String.format("%.0f đánh giá", evaluate)); // Hiển thị số nguyên
        txtEvaluate.setOnClickListener(v -> {
            // Tạo một instance của ReviewFragment
            ReviewFragment reviewFragment = new ReviewFragment();

            // Lấy FragmentManager để bắt đầu giao dịch
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            // Thay thế fragment hiện tại bằng ReviewFragment và thêm vào back stack
            transaction.replace(R.id.frame_layout_res, reviewFragment);
            transaction.addToBackStack(null); // Cho phép quay lại fragment trước đó
            transaction.commit();
        });

        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        loadImage(imageUrl, imgRestaurant);
        loadImage(backgroundImage.isEmpty() ? imageUrl : backgroundImage, imgBackground);

        // Cấu hình RecyclerView danh mục
        categoryAdapter = new CategoryAdapter(getContext(), listCate, restaurantId);
        rcvCate.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rcvCate.setAdapter(categoryAdapter);
        rcvCate.setNestedScrollingEnabled(false);

        rcvOutstanding = view.findViewById(R.id.rcvOutstanding);
        rcvOutstanding.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        outstandingAdapter = new OutstandingAdapter(getContext());
        rcvOutstanding.setAdapter(outstandingAdapter);

        rcvDiscount = view.findViewById(R.id.rcvDis);
        rcvDiscount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        layoutBtnCate = view.findViewById(R.id.layoutBtnCate);
        // Load danh mục món ăn
        loadCategories();
        loadAndDisplayDiscounts(restaurantId, requireContext(), view);

        // Xử lý hiệu ứng Header
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY();
            headerLayout.setBackgroundColor(scrollY > 100 ? ContextCompat.getColor(getContext(), R.color.white) : Color.TRANSPARENT);
        });


        return view;
    }

    private void loadImage(String url, ImageView imageView) {
        if (url == null || url.isEmpty()) {
            Glide.with(this).load(R.mipmap.ic_launcher).into(imageView);
        } else {
            Glide.with(this).load(url).into(imageView);
        }
    }
    private void addCategoryButtons(List<Category> categories) {
        layoutBtnCate.removeAllViews();

        for (Category category : categories) {
            Button btnCategory = new Button(getContext());
            btnCategory.setText(category.getName());
            btnCategory.setPadding(20, 10, 20, 10);
            btnCategory.setBackground(ContextCompat.getDrawable(getContext(), R.color.white));
            btnCategory.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            btnCategory.setOnClickListener(v -> scrollToCategory(category.getId()));

            layoutBtnCate.addView(btnCategory);
        }
    }

    private void scrollToCategory(String categoryId) {
        View targetView = null;

        for (int i = 0; i < rcvCate.getChildCount(); i++) {
            View child = rcvCate.getChildAt(i);
            Category category = listCate.get(i);

            if (category.getId().equals(categoryId)) {
                targetView = child;
                break;
            }
        }

        if (targetView != null) {
            int scrollY = targetView.getTop() + rcvCate.getTop();

            scrollView.post(() -> {
                ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollY", scrollY);
                animator.setDuration(800);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            });
        }
    }

    private void loadCategories() {
        restaurantManagement.getMenuCategories(restaurantId, new RestaurantManagement.MenuCategoryCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> categoryList) {
                List<Category> categories = ConvertData.convertToCategoryList(categoryList);

                if (categories.isEmpty()) {
                    return;
                }

                listCate.clear();
                listCate.addAll(categories);
                categoryAdapter.notifyDataSetChanged();

                addCategoryButtons(categories);

                // Lấy danh sách món ăn nổi bật cho toàn bộ nhà hàng
                getTopDishesForRestaurant(restaurantId, new DishCallback() {
                    @Override
                    public void onSuccess(List<Map<String, Object>> topDishes) {
                        Log.d("TopDishes", "Danh sách món nổi bật cho nhà hàng: " + topDishes.size());

                        List<Outstanding> outstandingList = ConvertData.convertToOutstandingList(topDishes);

                        // Cập nhật Adapter trên UI thread
                        requireActivity().runOnUiThread(() -> {
                            outstandingAdapter.setData(outstandingList);
                            outstandingAdapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Lỗi lấy danh sách món nổi bật của nhà hàng", e);
                    }
                });

                // Lấy danh sách món ăn theo từng danh mục
                for (Category cate : categories) {
                    restaurantManagement.getDishes(restaurantId, cate.getId(), new RestaurantManagement.DishCallback() {
                        @Override
                        public void onSuccess(List<Map<String, Object>> dishList) {
                            List<Food> foods = ConvertData.convertToFoodList(dishList);
                            cate.setListFood(foods);
                            categoryAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }


    public interface DishCallback {
        void onSuccess(List<Map<String, Object>> dishList);
        void onFailure(Exception e);
    }
    public void getTopDishesForRestaurant(String restaurantId, final DishCallback callback) {
        CollectionReference menuRef = db.collection("Restaurants")
                .document(restaurantId)
                .collection("menu");

        menuRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Map<String, Object>> allDishes = new ArrayList<>();
                List<Task<QuerySnapshot>> dishTasks = new ArrayList<>();
                List<String> categoryIds = new ArrayList<>(); // Lưu danh sách categoryId tránh lỗi index

                // Lặp qua tất cả danh mục để lấy tất cả món ăn
                for (QueryDocumentSnapshot categoryDoc : task.getResult()) {
                    String categoryId = categoryDoc.getId();
                    categoryIds.add(categoryId); // Lưu categoryId vào danh sách
                    CollectionReference dishRef = menuRef.document(categoryId).collection("menu");

                    Task<QuerySnapshot> dishTask = dishRef.get();
                    dishTasks.add(dishTask);
                }

                if (dishTasks.isEmpty()) {
                    callback.onSuccess(new ArrayList<>()); // Trả về danh sách rỗng nếu không có món
                    return;
                }

                // Chạy tất cả truy vấn món ăn song song
                Tasks.whenAllSuccess(dishTasks).addOnSuccessListener(results -> {
                    for (int i = 0; i < results.size(); i++) {
                        if (i >= categoryIds.size()) continue; // Tránh lỗi index

                        QuerySnapshot snapshot = (QuerySnapshot) results.get(i);
                        String categoryId = categoryIds.get(i);

                        for (QueryDocumentSnapshot document : snapshot) {
                            Map<String, Object> dishData = new HashMap<>();
                            dishData.put("id", document.getId());
                            dishData.put("name", document.getString("name"));
                            dishData.put("description", document.getString("description"));
                            dishData.put("price", document.getDouble("price"));
                            dishData.put("image", document.getString("image"));

                            // Kiểm tra null tránh lỗi
                            Long quantitySold = document.getLong("quantitySold");
                            dishData.put("quantitySold", (quantitySold != null) ? quantitySold : 0L);

                            dishData.put("restaurantId", restaurantId);
                            dishData.put("categoryId", categoryId);
                            dishData.put("rank", "Top #?"); // Tạm thời, cập nhật sau

                            allDishes.add(dishData);
                        }
                    }

                    if (allDishes.isEmpty()) {
                        callback.onSuccess(new ArrayList<>()); // Nếu không có món, trả về danh sách rỗng
                        return;
                    }

                    // Sắp xếp danh sách theo quantitySold giảm dần, kiểm tra null
                    allDishes.sort((dish1, dish2) -> {
                        Long q1 = (Long) dish1.get("quantitySold");
                        Long q2 = (Long) dish2.get("quantitySold");
                        return Long.compare(q2, q1); // Sắp xếp giảm dần
                    });

                    // Cập nhật rank
                    for (int i = 0; i < allDishes.size(); i++) {
                        allDishes.get(i).put("rank", "Top #" + (i + 1));
                    }

                    // Giới hạn danh sách còn tối đa 5 món
                    List<Map<String, Object>> topDishes = allDishes.subList(0, Math.min(5, allDishes.size()));

                    callback.onSuccess(topDishes);
                }).addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
    public void loadAndDisplayDiscounts(String restaurantId, Context context, View rootView) {
        rcvDiscount = rootView.findViewById(R.id.rcvDis);
        DiscountService discountService = new DiscountService();

        if (restaurantId == null || restaurantId.isEmpty()) {
            Log.e("DiscountService", "restaurantId không hợp lệ.");
            return;
        }

        Log.d("DiscountService", "Bắt đầu lấy mã giảm giá cho nhà hàng: " + restaurantId);

        discountService.getDiscountsByRestaurantId(restaurantId, new DiscountService.DiscountCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> discountDataList) {
                Log.d("DiscountService", "Dữ liệu mã giảm giá nhận được: " + discountDataList);

                if (discountDataList == null || discountDataList.isEmpty()) {
                    Log.d("DiscountService", "Không có mã giảm giá nào.");
                    return;
                }

                List<Discount> discounts = ConvertData.convertToDiscountList(discountDataList);

                if (discounts.isEmpty()) {
                    Log.d("DiscountService", "Dữ liệu mã giảm giá trống sau khi chuyển đổi.");
                    return;
                }

                // Log chi tiết từng mã giảm giá
                for (Discount discount : discounts) {
                    Log.d("DiscountService", "Mã giảm giá: " +
                            "Code=" + discount.getCode() + ", " +
                            "Description=" + discount.getDescription() + ", " +
                            "Value=" + discount.getValue() + ", " +
                            "StartDate=" + discount.getStartDate() + ", " +
                            "EndDate=" + discount.getEndDate());
                }

                // Cập nhật danh sách
                discountList.clear();
                discountList.addAll(discounts);

                // Cập nhật giao diện trên UI thread
                ((Activity) context).runOnUiThread(() -> {
                    if (rcvDiscount == null) {
                        Log.e("DiscountService", "RecyclerView chưa được khởi tạo.");
                        return;
                    }

                    if (discountAdapter == null) {
                        discountAdapter = new DiscountAdapter(context, restaurantId, discountList);
                        rcvDiscount.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                        rcvDiscount.setAdapter(discountAdapter);
                        Log.d("DiscountService", "Đã thiết lập adapter cho RecyclerView.");
                    } else {
                        discountAdapter.notifyDataSetChanged();
                        Log.d("DiscountService", "Đã cập nhật danh sách mã giảm giá.");
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DiscountService", "Lỗi khi lấy mã giảm giá: " + e.getMessage(), e);
            }
        });
    }
}


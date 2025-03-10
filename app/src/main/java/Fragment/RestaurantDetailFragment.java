package Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ltmb.ltmobile.BottomSheetAddTopping;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.Category;
import Adapter.CategoryAdapter;
import Adapter.Food;
import Adapter.FoodAdapter;
import JSON.ConvertData;

public class RestaurantDetailFragment extends Fragment {
    private RecyclerView rcvCate;
    private String restaurantId, name, star, evaluate, imageUrl, backgroundImage;
    private CategoryAdapter categoryAdapter;
    private ScrollView scrollView;
    private LinearLayout headerLayout;
    private RestaurantManagement restaurantManagement;
    private TextView txtName, txtStar, txtEvaluate;
    private ImageView imgRestaurant, imgBackground;
    private List<Category> listCate = new ArrayList<>();

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_STAR = "star";
    private static final String ARG_EVALUATE = "evaluate";
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BACKGROUND_IMAGE_URL = "backgroundImageUrl";

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailFragment newInstance(String id, String name, String star, String evaluate, String imageUrl, String backgroundImage) {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_STAR, star);
        args.putString(ARG_EVALUATE, evaluate);
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
            star = getArguments().getString(ARG_STAR, "0.0");
            evaluate = getArguments().getString(ARG_EVALUATE, "Chưa có đánh giá");
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
        Button btnOpenBottomSheet = view.findViewById(R.id.btnGaRan);
        scrollView = view.findViewById(R.id.scrollview);
        headerLayout = view.findViewById(R.id.layoutBtn);
        rcvCate = view.findViewById(R.id.rcvCate);

        txtName.setText(name);
        txtStar.setText("⭐ " + star);
        txtEvaluate.setText(evaluate);


        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        loadImage(imageUrl, imgRestaurant);
        loadImage(backgroundImage.isEmpty() ? imageUrl : backgroundImage, imgBackground);


        // Xử lý mở BottomSheet
        btnOpenBottomSheet.setOnClickListener(v -> {
            BottomSheetAddTopping bottomSheet = new BottomSheetAddTopping();
            bottomSheet.show(getParentFragmentManager(), "BottomSheetTopping");
        });

        // Cấu hình RecyclerView danh mục
        categoryAdapter = new CategoryAdapter(getContext(), listCate, restaurantId);
        rcvCate.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rcvCate.setAdapter(categoryAdapter);
        rcvCate.setNestedScrollingEnabled(false);

        // Load danh mục món ăn
        loadCategories();

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

}

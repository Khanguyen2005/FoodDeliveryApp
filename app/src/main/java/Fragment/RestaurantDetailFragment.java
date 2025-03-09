package Fragment;


import android.graphics.Color;
import android.os.Bundle;

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

import com.ltmb.ltmobile.R;
import com.bumptech.glide.Glide;
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.Category;
import Adapter.CategoryAdapter;
import Adapter.Food;
import JSON.ConvertData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailFragment extends Fragment {
    private RecyclerView rcvCate, rcvDis, rcvOut;
    private String restaurantId;
    private CategoryAdapter categoryAdapter;
    private ScrollView scrollView;
    private LinearLayout headerLayout;
    private RestaurantManagement restaurantManagement;
    private String name, star, evaluate, imageUrl,backgroundImage;
    private TextView txtName, txtStar, txtEvaluate;
    private ImageView imgRestaurant;
    private ImageView imgBackground;
    private List<Category> listCate;
    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_STAR = "star";
    private static final String ARG_EVALUATE = "evaluate";
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BACKGROUND_IMAGE_URL = "backgroundImageUrl";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RestaurantDetailFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static RestaurantDetailFragment newInstance(String param1, String param2) {
//        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        // Ánh xạ View từ layout
        txtName = view.findViewById(R.id.name_Res);
        txtStar = view.findViewById(R.id.star_res);
        txtEvaluate = view.findViewById(R.id.evaluate_Res);
        imgRestaurant = view.findViewById(R.id.img_res);
        imgBackground = view.findViewById(R.id.background_image);

        txtName.setText(name);
        txtStar.setText("⭐ " + star);
        txtEvaluate.setText(evaluate);

        Glide.with(this).load(imageUrl).into(imgRestaurant);
        if (backgroundImage == null || backgroundImage.isEmpty()) {
            Glide.with(this).load(backgroundImage).into(imgBackground);
            Glide.with(this).load(imageUrl).into(imgBackground);
        } else {
            Glide.with(this).load(backgroundImage).into(imgBackground);
        }




        rcvCate = view.findViewById(R.id.rcvCate);
        rcvCate.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rcvCate.setNestedScrollingEnabled(false);

        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>());
        rcvCate.setAdapter(categoryAdapter);

        loadCategories();

//        categoryAdapter.setData(getListFood());

        // Discount RecyclerView
//        rcvDis = view.findViewById(R.id.rcvDis);
//        disAdapter = new DiscountAdapter(getContext());
//        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
//        rcvDis.setLayoutManager(linearLayoutManager2);
//        rcvDis.setAdapter(disAdapter);
//        rcvDis.setNestedScrollingEnabled(false);
//        disAdapter.setData(getListDiscount());

        // Outstanding RecyclerView
//        rcvOut = view.findViewById(R.id.rcvOut);
//        outAdapter = new OutstandingAdapter(getContext());
//        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
//        rcvOut.setLayoutManager(linearLayoutManager3);
//        rcvOut.setAdapter(outAdapter);
//        rcvOut.setNestedScrollingEnabled(false);
//        outAdapter.setData(getListOutstanding());

        // ScrollView xử lý hiệu ứng header
        scrollView = view.findViewById(R.id.scrollview);
        headerLayout = view.findViewById(R.id.layoutBtn);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if (scrollY > 100) {
                    headerLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                } else {
                    headerLayout.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        });

        return view;
    }

    public static RestaurantDetailFragment newInstance(String id,String name, String star, String evaluate, String imageUrl, String backgroundImage) {
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


    private void loadCategories() {
        restaurantManagement.getMenuCategories(restaurantId, new RestaurantManagement.MenuCategoryCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> categoryList) {
                List<Category> categories = ConvertData.convertToCategoryList(categoryList);

                if (categories.isEmpty()) {
                    return;
                }

                categoryAdapter = new CategoryAdapter(getContext(), categories);
                rcvCate.setAdapter(categoryAdapter);

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
                            // Xử lý lỗi nếu cần
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Toast.makeText(getContext(), "Lỗi khi tải danh mục!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
package Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ltmb.ltmobile.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.Review;
import Adapter.ReviewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantReviewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private FirebaseFirestore db;
    private String restaurantId = "restaurant_id_1"; // ID của nhà hàng

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantReviewsFragment newInstance(String param1, String param2) {
        RestaurantReviewsFragment fragment = new RestaurantReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_reviews, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);

        db = FirebaseFirestore.getInstance();

        if (restaurantId == null || restaurantId.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy ID nhà hàng!", Toast.LENGTH_SHORT).show();
        } else {
            loadReviews();
        }

        return view;
    }
    private void loadReviews() {
        db.collection("Restaurants")
                .document(restaurantId)  // ID nhà hàng
                .collection("FoodReviews") // Collection chứa đánh giá
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert Firestore document thành đối tượng Review
                            Review review = new Review(
                                    document.getString("comment"),
                                    document.getString("userId"),
                                    document.getString("foodId"),
                                    document.getLong("rating").intValue(), // Chuyển rating về int
                                    document.getTimestamp("timestamp") // Lấy Timestamp
                            );
                            reviewList.add(review);
                        }
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải đánh giá!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
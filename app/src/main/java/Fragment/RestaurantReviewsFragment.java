package Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ltmb.ltmobile.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.Review;
import Adapter.ReviewAdapter;

public class RestaurantReviewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private FirebaseFirestore db;
    private String restaurantId; // ID của nhà hàng

    public RestaurantReviewsFragment() {
        // Required empty public constructor
    }

    // Phương thức khởi tạo fragment với restaurantId
    public static RestaurantReviewsFragment newInstance(String restaurantId) {
        RestaurantReviewsFragment fragment = new RestaurantReviewsFragment();
        Bundle args = new Bundle();
        args.putString("restaurantId", restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
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
        Log.d("RestaurantID", "restaurantId: " + restaurantId);

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
                            // Lấy dữ liệu từ Firestore
                            String comment = document.getString("comment");
                            String userId = document.getString("userId");
                            String foodId = document.getString("foodId");
                            int rating = document.getLong("rating").intValue(); // Chuyển rating về int
                            Timestamp timestamp = document.getTimestamp("timestamp"); // Lấy Timestamp
                            String imageUrl = document.getString("imageUrl"); // Lấy link ảnh

                            Review review = new Review(comment, userId, foodId, rating, timestamp, imageUrl);
                            reviewList.add(review);
                        }
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải đánh giá!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

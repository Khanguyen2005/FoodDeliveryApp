package Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.ltmb.ltmobile.R;

public class ReviewFragment extends Fragment {
    private EditText edtComment;
    private RatingBar ratingBar;
    private Button btnSubmitReview;
    private FirebaseFirestore db;
    private String restaurantId;
    private String userId;

    private static final String ARG_RESTAURANT_ID = "restaurantId";

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String restaurantId) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getString(ARG_RESTAURANT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        // Ánh xạ view
        edtComment = view.findViewById(R.id.edtComment);
        ratingBar = view.findViewById(R.id.ratingBar);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
        db = FirebaseFirestore.getInstance();

        // Lấy userId từ FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để đánh giá!", Toast.LENGTH_SHORT).show();
            btnSubmitReview.setEnabled(false);
        }

        // Xử lý khi nhấn nút gửi đánh giá
        btnSubmitReview.setOnClickListener(v -> submitReview());

        return view;
    }

    private void submitReview() {
        String comment = edtComment.getText().toString().trim();
        int rating = (int) ratingBar.getRating();
        Timestamp timestamp = new Timestamp(new Date());

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(getContext(), "Vui lòng nhập đánh giá và chọn số sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng đánh giá
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("comment", comment);
        reviewData.put("rating", rating);
        reviewData.put("timestamp", timestamp);
        reviewData.put("userId", userId);

        db.collection("Restaurants")
                .document(restaurantId)
                .collection("FoodReviews")
                .add(reviewData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Đánh giá thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi gửi đánh giá!", Toast.LENGTH_SHORT).show());
    }
}

package Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.google.firebase.Timestamp;
import java.util.Date;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {
    private EditText edtComment;
    private RatingBar ratingBar;
    private Button btnSubmitReview;
    private FirebaseFirestore db;
    private String restaurantId = "restaurant_id_1"; // ID nhà hàng (thay đổi nếu cần)
    private String userId = "user_1234"; // ID người dùng (lấy từ đăng nhập)

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        // Ánh xạ view
        edtComment = view.findViewById(R.id.edtComment);
        ratingBar = view.findViewById(R.id.ratingBar);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
        db = FirebaseFirestore.getInstance();

        // Xử lý khi nhấn nút gửi đánh giá
        btnSubmitReview.setOnClickListener(v -> submitReview());

        return view;
    }
    private void submitReview() {
        String comment = edtComment.getText().toString().trim();
        int rating = (int) ratingBar.getRating();
        Timestamp timestamp = new Timestamp(new Date()); // Lấy thời gian hiện tại

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
        reviewData.put("foodId", "food_456"); // Thay bằng ID món ăn thực tế

        // Lưu vào Firestore
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
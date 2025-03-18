package Fragment;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ltmb.ltmobile.MainActivity;
import com.ltmb.ltmobile.R;

public class ReviewFragment extends Fragment {
    //code khá thêm
    private static final int REQUEST_GALLERY = 100;
    private static final int REQUEST_CAMERA = 101;
    private EditText edtComment;
    private RatingBar ratingBar;
    private Button btnSubmitReview;
    private FirebaseFirestore db;
    private String restaurantId;
    private String userId;
    private Button btnChooseImage;
    private ImageView imgReview; // để hiển thị ảnh lên
    private Uri imageUri; // Lưu URI ảnh chụp



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
        btnChooseImage = view.findViewById(R.id.btnChooseImage); // Ánh xạ nút chọn ảnh
        imgReview = view.findViewById(R.id.imgReview); // Ánh xạ ImageView để hiển thị ảnh
        db = FirebaseFirestore.getInstance();

        // Lấy userId từ FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để đánh giá!", Toast.LENGTH_SHORT).show();
            btnSubmitReview.setEnabled(false);
        }


        // Xử lý khi nhấn nút chọn ảnh
        btnChooseImage.setOnClickListener(v -> openImagePicker());

        // Xử lý khi nhấn nút gửi đánh giá
        btnSubmitReview.setOnClickListener(v -> submitReview());

        return view;
    }

    // Hàm mở tùy chọn chọn ảnh từ thư viện hoặc chụp ảnh
    private void openImagePicker() {
        // Hộp thoại chọn Camera hoặc Thư viện
        CharSequence[] options = {"Chụp ảnh", "Chọn ảnh từ thư viện", "Hủy"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn phương thức thêm ảnh");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chụp ảnh
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else if (which == 1) {
                // Chọn ảnh từ thư viện
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_GALLERY);
            } else {
                dialog.dismiss();
            }
        });

        builder.show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null && data.getData() != null) {
                // Ảnh từ thư viện
                Uri selectedImageUri = data.getData();
                imgReview.setImageURI(selectedImageUri);
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                // Chụp ảnh từ Camera, lấy ảnh dạng Bitmap
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imgReview.setImageBitmap(photo);
                } else {
                    Toast.makeText(getContext(), "Không lấy được ảnh từ camera!", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

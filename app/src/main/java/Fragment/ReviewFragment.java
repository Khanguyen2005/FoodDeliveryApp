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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.provider.MediaStore;
import android.util.Base64;
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

import org.json.JSONObject;

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
                imageUri = data.getData();
                imgReview.setImageURI(imageUri);
                Log.d("IMGUR_IMAGE_SELECTED", "Image selected from gallery: " + imageUri.toString());
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                // Ảnh từ Camera, lấy ảnh dạng Bitmap
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imgReview.setImageBitmap(photo);

                    // 👉 Tạo URI giả để upload lên Imgur
                    imageUri = getImageUri(photo);

                    if (imageUri != null) {
                        Log.d("IMGUR_IMAGE_CAPTURED", "Image captured: " + imageUri.toString());
                    } else {
                        Log.e("IMGUR_IMAGE_ERROR", "Không thể tạo URI cho ảnh.");
                        Toast.makeText(getContext(), "Không thể tạo URI cho ảnh!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("IMGUR_IMAGE_ERROR", "Không lấy được ảnh từ camera!");
                    Toast.makeText(getContext(), "Không lấy được ảnh từ camera!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                getActivity().getContentResolver(),
                bitmap,
                "IMG_" + System.currentTimeMillis(),
                null
        );
        return Uri.parse(path);
    }




    private void submitReview() {
        String comment = edtComment.getText().toString().trim();
        int rating = (int) ratingBar.getRating();
        Timestamp timestamp = new Timestamp(new Date());

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(getContext(), "Vui lòng nhập đánh giá và chọn số sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // Nếu có ảnh, upload lên Imgur trước
            uploadImageToImgur(imageUri, new ImgurUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    Log.d("IMGUR_UPLOAD_SUCCESS", "Direct Image URL: " + imageUrl);
                    saveReviewToFirestore(comment, rating, timestamp, imageUrl);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("IMGUR_UPLOAD_ERROR", "Upload failed: " + error);
                    Toast.makeText(getContext(), "Lỗi khi tải ảnh lên Imgur: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu không có ảnh, chỉ lưu đánh giá vào Firestore
            Log.d("IMGUR_UPLOAD_NO_IMAGE", "No image selected, saving review only.");
            saveReviewToFirestore(comment, rating, timestamp, null);
        }
    }


    private void saveReviewToFirestore(String comment, int rating, Timestamp timestamp, String imageUrl) {
        Log.d("IMGUR_UPLOAD_CHECK", "Image URL received: " + imageUrl);

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("comment", comment);
        reviewData.put("rating", rating);
        reviewData.put("timestamp", timestamp);
        reviewData.put("userId", userId);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            reviewData.put("imageUrl", imageUrl);
            Log.d("IMGUR_UPLOAD_SUCCESS", "Image URL saved to Firestore: " + imageUrl);
        } else {
            Log.d("IMGUR_UPLOAD_ERROR", "No image URL to save in Firestore");
        }

        db.collection("Restaurants")
                .document(restaurantId)
                .collection("FoodReviews")
                .add(reviewData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Đánh giá thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi gửi đánh giá!", Toast.LENGTH_SHORT).show());
    }

    private void uploadImageToImgur(Uri imageUri, ImgurUploadCallback callback) {
        new Thread(() -> {
            try {
                // Đọc dữ liệu ảnh từ Uri
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                byte[] imageData = getBytes(inputStream);

                // Imgur API endpoint
                String uploadUrl = "https://api.imgur.com/3/image";
                String clientId = "41a425bcd5bf284"; // Thay bằng Client ID của bạn

                // Mở kết nối HTTP
                HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Client-ID " + clientId);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary");

                // Xây dựng Multipart FormData
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes("------WebKitFormBoundary\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"upload.jpg\"\r\n");
                outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                outputStream.write(imageData);
                outputStream.writeBytes("\r\n------WebKitFormBoundary--\r\n");
                outputStream.flush();
                outputStream.close();

                // Kiểm tra phản hồi từ server
                int responseCode = connection.getResponseCode();
                InputStream responseStream = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Xử lý JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getBoolean("success")) {
                    String imageUrl = jsonResponse.getJSONObject("data").getString("link");

                    // Chuyển đổi sang direct link nếu cần
                    if (imageUrl.contains("imgur.com") && !imageUrl.contains("i.imgur.com")) {
                        imageUrl = imageUrl.replace("imgur.com", "i.imgur.com");
                    }

                    String finalImageUrl = imageUrl;
                    getActivity().runOnUiThread(() -> callback.onSuccess(finalImageUrl));
                } else {
                    getActivity().runOnUiThread(() -> callback.onFailure("Imgur upload failed: " + response.toString()));
                }
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> callback.onFailure("Error uploading to Imgur: " + e.getMessage()));
            }
        }).start();
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }




    interface ImgurUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

}
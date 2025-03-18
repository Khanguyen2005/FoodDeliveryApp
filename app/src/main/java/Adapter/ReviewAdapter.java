package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private FirebaseFirestore db;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());

        // Chuyển Timestamp thành Date
        Timestamp timestamp = review.getTimestamp();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            holder.tvTimestamp.setText("Thời gian: " + sdf.format(date));
        } else {
            holder.tvTimestamp.setText("Không có thời gian");
        }

        // Lấy tên người dùng từ Firestore
        String userId = review.getUserId();
        if (userId != null) {
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("name")) {
                    String userName = documentSnapshot.getString("name");
                    holder.tvUserName.setText("Người dùng: " + userName);
                } else {
                    holder.tvUserName.setText("Người dùng: Không xác định");
                }
            }).addOnFailureListener(e -> {
                holder.tvUserName.setText("Người dùng: Lỗi tải tên");
            });
        } else {
            holder.tvUserName.setText("Người dùng: Không xác định");
        }

        // Load ảnh bằng Glide
        String imageUrl = review.getImageUrl(); // Lấy URL từ đối tượng Review
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher) // Ảnh tạm khi đang tải
                    .error(R.mipmap.ic_launcher) // Ảnh hiển thị nếu lỗi
                    .into(holder.imgReview);
        } else {
            holder.imgReview.setImageResource(R.mipmap.ic_launcher); // Ảnh mặc định nếu không có ảnh
        }
    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment, tvUserName, tvTimestamp;
        RatingBar ratingBar;
        ImageView imgReview; // Thêm ImageView

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvUserName = itemView.findViewById(R.id.tvUserId);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgReview = itemView.findViewById(R.id.imgReview); // Ánh xạ ImageView
        }
    }

}

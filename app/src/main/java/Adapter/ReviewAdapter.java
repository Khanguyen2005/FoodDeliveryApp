package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.ltmb.ltmobile.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
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
        holder.tvUserId.setText("Người dùng: " + review.getUserId());
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
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView tvComment, tvUserId, tvTimestamp;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

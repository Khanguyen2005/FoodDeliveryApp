package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.RestaurantActivity;

import java.util.ArrayList;
import java.util.List;

public class    RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private Context context;
    private List<Restaurant> listRes;
    private OnRestaurantClickListener listener;

    public RestaurantAdapter(Context context, OnRestaurantClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.listRes = new ArrayList<>();
    }
    public void setList(List<Restaurant> list) {
        this.listRes.clear();
        this.listRes.addAll(list);
        notifyDataSetChanged();
    }


    public void setData(List<Restaurant> listRes){
        if (listRes == null) {
            this.listRes = new ArrayList<>();
        } else {
            this.listRes = listRes;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant res = listRes.get(position);
        if (res == null) return;

        // Load ảnh nhà hàng
        if (context != null && res.getImageUrl() != null && !res.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(res.getImageUrl())
                    .into(holder.imgRes);
        } else {
            holder.imgRes.setImageResource(R.mipmap.ic_launcher);
        }

        holder.nameRes.setText(res.getName() != null ? res.getName() : "Không có tên");
        holder.starRes.setText(String.valueOf(res.getStarRes() != null ? res.getStarRes() : 5.0));

        // Kiểm tra nếu đã có số lượng đánh giá thì hiển thị ngay, nếu chưa thì lấy từ Firestore
        if (res.getEvaluateRes() != null) {
            holder.evaluateRes.setText(res.getEvaluateRes() + " đánh giá");
        } else {
            holder.evaluateRes.setText("Đang tải...");

            // Truy vấn Firestore để lấy số lượng review
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("reviews")
                    .whereEqualTo("restaurantId", res.getId()) // Lọc theo ID nhà hàng
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int reviewCount = task.getResult().size();
                            res.setEvaluateRes(reviewCount); // Cập nhật số lượng đánh giá vào đối tượng
                            holder.evaluateRes.setText(reviewCount + " đánh giá");
                        } else {
                            holder.evaluateRes.setText("0 đánh giá");
                        }
                    });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra("id", res.getId());
            intent.putExtra("name", res.getName());
            intent.putExtra("star", res.getStarRes());
            intent.putExtra("evaluate", res.getEvaluateRes());
            intent.putExtra("image", res.getImageUrl());
            intent.putExtra("backgroundImage", res.getBackgroundImg());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return listRes == null ? 0 : listRes.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRes;
        private TextView nameRes, evaluateRes, starRes;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRes = itemView.findViewById(R.id.img_res);
            nameRes = itemView.findViewById(R.id.res_name);
            starRes = itemView.findViewById(R.id.star_res);
            evaluateRes = itemView.findViewById(R.id.evaluate_Res);
        }
    }

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }
}

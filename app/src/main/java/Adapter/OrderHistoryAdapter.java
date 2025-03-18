package Adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.ltmb.ltmobile.R;
import java.util.List;

import Fragment.ReviewFragment;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final Context context;
    private final List<OrderModel> orderList;

    public OrderHistoryAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvOrderTotal.setText(String.format("Tổng tiền: %.0f đ", order.getTotalPrice()));
        holder.tvRestaurantName.setText(order.getRestaurantName());

        // Thiết lập RecyclerView con để hiển thị danh sách món ăn
        OrderItemAdapter itemAdapter = new OrderItemAdapter(context, order.getItems());
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rvOrderItems.setAdapter(itemAdapter);

        holder.btnReview.setOnClickListener(v -> {
            Log.d("OrderHistoryAdapter", "Đang mở ReviewFragment với restaurantId: " + order.getRestaurantId());
            openReviewFragment(order.getRestaurantId());
        });

    }

    private void openReviewFragment(String restaurantId) {
        ReviewFragment reviewFragment = ReviewFragment.newInstance(restaurantId);
        FragmentTransaction transaction = ((FragmentActivity) context)
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.frame_layout, reviewFragment);  // Đảm bảo ID này là FrameLayout trong Activity
        transaction.addToBackStack(null);
        transaction.commit();
    }



    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderTotal, tvRestaurantName;
        RecyclerView rvOrderItems;
        Button btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
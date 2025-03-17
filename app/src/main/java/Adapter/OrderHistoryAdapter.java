package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ltmb.ltmobile.R;
import java.util.List;
import Adapter.OrderModel;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private Context context;
    private List<OrderModel> orderList;

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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderTotal,tvRestaurantName;
        RecyclerView rvOrderItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
        }
    }
}

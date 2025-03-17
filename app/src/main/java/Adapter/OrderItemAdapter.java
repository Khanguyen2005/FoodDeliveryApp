package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ltmb.ltmobile.R;

import java.util.List;
import java.util.Map;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private Context context;
    private List<OrderItemModel> orderItems;

    public OrderItemAdapter(Context context, List<OrderItemModel> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItemModel item = orderItems.get(position);

        holder.tvFoodName.setText(item.getName());
        holder.tvFoodQuantity.setText("Số lượng: " + item.getQuantity() + " - Giá: " + item.getPrice() + " đ");

        // Hiển thị ảnh món ăn
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.fastfood)
                .into(holder.ivFoodImage);

        // Xử lý danh sách topping từ List<Map<String, Object>>
        if (item.getToppings() != null && !item.getToppings().isEmpty()) {
            StringBuilder toppingsText = new StringBuilder("Topping: ");
            for (Map<String, Object> topping : item.getToppings()) {
                if (topping.containsKey("name")) {
                    toppingsText.append(topping.get("name").toString()).append(", ");
                }
            }
            // Xóa dấu phẩy cuối cùng nếu có topping
            if (toppingsText.length() > 9) {
                toppingsText.setLength(toppingsText.length() - 2);
            }
            holder.tvFoodToppings.setText(toppingsText.toString());
        } else {
            holder.tvFoodToppings.setText("Topping: Không có");
        }
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodQuantity, tvFoodToppings;
        ImageView ivFoodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodQuantity = itemView.findViewById(R.id.tvFoodQuantity);
            tvFoodToppings = itemView.findViewById(R.id.tvFoodToppings);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
        }
    }
}

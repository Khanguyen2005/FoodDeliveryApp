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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private List<CartItem> checkoutList;
    private Context context;

    public CheckoutAdapter(List<CartItem> checkoutList, Context context) {
        this.checkoutList = checkoutList;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem item = checkoutList.get(position);
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(formatter.format(item.getTotalPrice()) + " đ");
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());

        // Hiển thị topping rõ ràng
        if (item.getToppings().isEmpty()) {
            holder.tvTopping.setText("Topping: Không có");
        } else {
            StringBuilder toppingsText = new StringBuilder("Topping: ");
            for (int i = 0; i < item.getToppings().size(); i++) {
                toppingsText.append(item.getToppings().get(i).get("name"));
                if (i < item.getToppings().size() - 1) {
                    toppingsText.append(", ");
                }
            }
            holder.tvTopping.setText(toppingsText.toString());
        }

        // Load hình ảnh
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.fastfood) // Ảnh mặc định nếu lỗi
                .into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTopping;
        ImageView imgFood;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodNameCheckout);
            tvPrice = itemView.findViewById(R.id.tvFoodPriceCheckout);
            tvQuantity = itemView.findViewById(R.id.tvFoodQuantityCheckout);
            tvTopping = itemView.findViewById(R.id.tvToppingCheckout);
            imgFood = itemView.findViewById(R.id.imgFoodCheckout);
        }
    }
}
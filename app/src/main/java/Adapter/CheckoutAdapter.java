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

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice() + " đ");
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvTopping.setText("Topping: " + (item.getTopping().isEmpty() ? "Không có" : item.getTopping()));

        Glide.with(context).load(item.getImageUrl()).into(holder.imgFood);
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

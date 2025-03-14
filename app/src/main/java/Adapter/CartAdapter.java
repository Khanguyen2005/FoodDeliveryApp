package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ltmb.ltmobile.R;
import com.ltmb.ltmobile.services.CartDatabaseHelper;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartDatabaseHelper dbHelper;
    private TextView tvTotalPrice;

    public CartAdapter(List<CartItem> cartItems, Context context, CartDatabaseHelper dbHelper, TextView tvTotalPrice) {
        this.cartItems = cartItems;
        this.context = context;
        this.dbHelper = dbHelper;
        this.tvTotalPrice = tvTotalPrice;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice() + " đ");
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvTopping.setText("Topping: " + (item.getTopping().isEmpty() ? "Không có" : item.getTopping()));

        Glide.with(context).load(item.getImageUrl()).into(holder.imgFood);

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            dbHelper.updateCartItem(item.getId(), item.getRestaurantId(), item.getQuantity(), item.getTopping());
            notifyDataSetChanged();
            updateTotalPrice();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                dbHelper.updateCartItem(item.getId(), item.getRestaurantId(), item.getQuantity(), item.getTopping());
                notifyDataSetChanged();
                updateTotalPrice();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa món?")
                    .setMessage("Bạn có chắc chắn muốn xóa món này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        dbHelper.removeCartItem(item.getId(), item.getRestaurantId());
                        cartItems.remove(position);
                        notifyDataSetChanged();
                        updateTotalPrice();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateTotalPrice() {
        double totalPrice = dbHelper.getTotalPrice(cartItems.get(0).getRestaurantId());
        tvTotalPrice.setText("Tổng tiền: " + totalPrice + " đ");
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTopping;
        ImageView imgFood,btnRemove;
        Button btnIncrease, btnDecrease ;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodNameCart);
            tvPrice = itemView.findViewById(R.id.tvFoodPriceCart);
            tvQuantity = itemView.findViewById(R.id.tvFoodQuantityCart);
            tvTopping = itemView.findViewById(R.id.tvFoodToppingCart);
            imgFood = itemView.findViewById(R.id.imgFoodCart);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemoveCartItem);
        }
    }
}

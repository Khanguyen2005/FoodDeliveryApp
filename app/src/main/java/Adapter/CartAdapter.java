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
import Adapter.CartItem;
import com.ltmb.ltmobile.services.CartDatabaseHelper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(currencyFormat.format(item.getPrice()));

        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));


        // Hiển thị danh sách topping
        List<Map<String, Object>> toppings = item.getToppings();
        StringBuilder toppingText = new StringBuilder("Topping: ");
        if (toppings.isEmpty()) {
            toppingText.append("Không có");
        } else {
            for (Map<String, Object> topping : toppings) {
                toppingText.append(topping.get("name")).append(", ");
            }
            toppingText.setLength(toppingText.length() - 2); // Xóa dấu phẩy cuối
        }
        holder.tvTopping.setText(toppingText.toString());

        // Load ảnh món ăn, kiểm tra null
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context).load(item.getImageUrl()).into(holder.imgFood);
        } else {
            holder.imgFood.setImageResource(R.drawable.fastfood);
        }

        // Nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            dbHelper.updateCartItem(item);
            notifyItemChanged(position);
            updateTotalPrice();
        });

        // Nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                dbHelper.updateCartItem(item);
                notifyItemChanged(position);
                updateTotalPrice();
            }
        });

        // Nút xóa món ăn
        holder.btnRemove.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa món?")
                    .setMessage("Bạn có chắc chắn muốn xóa món này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        dbHelper.removeCartItem(item);
                        cartItems.remove(position);
                        notifyItemRemoved(position);
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

    // Cập nhật tổng tiền
    private void updateTotalPrice() {
        if (!cartItems.isEmpty()) {
            double totalPrice = dbHelper.getTotalPrice(cartItems.get(0).getRestaurantId());
            tvTotalPrice.setText("Tổng tiền: " + totalPrice + " đ");
        } else {
            tvTotalPrice.setText("Tổng tiền: 0 đ");
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTopping;
        ImageView imgFood, btnRemove;
        Button btnIncrease, btnDecrease;

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

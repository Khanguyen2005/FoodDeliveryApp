package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ltmb.ltmobile.R;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnFoodClickListener foodClickListener;
    private List<Food> listFood;
    private String categoryId;
    public FoodAdapter(Context context, String categoryId) {
        this.context = context;
        this.categoryId = categoryId;
        this.listFood = new ArrayList<>();
    }

    public FoodAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.foodClickListener = listener;
    }

    public void setData(List<Food> listFood) {
        this.listFood = listFood;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = listFood.get(position);
        if (food == null) return;

        if (context != null) {
            Glide.with(context)
                    .load(food.getImgUrl())
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imgFood);
        }

        holder.itemView.setOnClickListener(v -> {
            if (foodClickListener != null) {
                foodClickListener.onFoodClick(food, categoryId);
            }

            String message = (categoryId != null && !categoryId.isEmpty())
                    ? "Category ID: " + categoryId
                    : "Không lấy được Category ID";

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
        holder.bind(food, onItemClickListener);

        holder.name.setText(food.getName());

        if (food.getQuantitySold() instanceof Number) {
            holder.quantitySold.setText(String.valueOf(((Number) food.getQuantitySold()).longValue()));
        } else {
            holder.quantitySold.setText("0 phần đã bán");
        }

        if (food.getPrice() instanceof Number) {
            holder.price.setText(String.valueOf(((Number) food.getPrice()).longValue()));
        } else {
            holder.price.setText("0");
        }

    }

    @Override
    public int getItemCount() {
        return (listFood != null) ? listFood.size() : 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFood;
        private TextView name, price, quantitySold, category;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.img_food);
            name = itemView.findViewById(R.id.food_name);
            quantitySold = itemView.findViewById(R.id.quantitySold);
            price = itemView.findViewById(R.id.price_food);
            category = itemView.findViewById(R.id.food_category);
        }

        public void bind(Food food, OnItemClickListener listener) {
            name.setText(food.getName());
            price.setText(String.format("%.0f", food.getPrice()));


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(food);
                }
            });
        }
    }

    public interface OnFoodClickListener {
        void onFoodClick(Food food, String CategoryID);
    }
}

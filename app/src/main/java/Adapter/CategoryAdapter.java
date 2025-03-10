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
import com.ltmb.ltmobile.services.RestaurantManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> listCate;
    private OnFoodClickListener foodClickListener;
    private String restaurantId;

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.foodClickListener = listener;
    }

    public CategoryAdapter(Context context, List<Category> listCate, String restaurantId){
        this.context = context;
        this.listCate = listCate;
        this.restaurantId = restaurantId;
    }
    public void setData(List<Category> listCate){
        this.listCate = listCate;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCate.get(position);
        if (category == null) return;
        holder.nameCate.setText(category.getName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL,false);
        holder.rcvFood.setLayoutManager(linearLayoutManager);

        List<Food> foodList = category.getListFood();
        if (foodList == null) {
            foodList = new ArrayList<>();
        }
        FoodAdapter foodAdapter = new FoodAdapter(holder.itemView.getContext(), category.getId(), restaurantId);
        foodAdapter.setData(foodList);



        holder.rcvFood.setAdapter(foodAdapter);
    }

    @Override
    public int getItemCount() {
        if(listCate != null) return listCate.size();
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView nameCate;
        private RecyclerView rcvFood;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            nameCate = itemView.findViewById(R.id.txtCate);
            rcvFood = itemView.findViewById(R.id.rcvFood);
        }
    }
    public interface OnFoodClickListener {
        void onFoodClick(Food food, String categoryId);
    }

}

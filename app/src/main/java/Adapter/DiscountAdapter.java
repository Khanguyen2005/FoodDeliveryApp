package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ltmb.ltmobile.R;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {
    private Context context;
    private List<Discount> listDis;
    private String restaurantId;
    public DiscountAdapter(Context context, String restaurantId, List<Discount> listDis){
        this.context = context;
        this.listDis = listDis;
        this.restaurantId = restaurantId;
    }
    public void setData(List<Discount> listDis){
        this.listDis = listDis;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount,parent,false);
        return new DiscountAdapter.DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Discount discount = listDis.get(position);
        if (discount == null) return;
        holder.title.setText(discount.getCode());
    }

    @Override
    public int getItemCount() {
        return listDis != null ? listDis.size() : 0;
    }

    public class DiscountViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_dis);
        }
    }
}

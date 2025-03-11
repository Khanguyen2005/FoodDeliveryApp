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

import java.text.DecimalFormat;
import java.util.List;

public class OutstandingAdapter extends RecyclerView.Adapter<OutstandingAdapter.OutstandingViewHodle> {
    private Context context;
    private List<Outstanding> listOut;
    public OutstandingAdapter(Context context){
        this.context = context;
    }
    public void setData(List<Outstanding> listOut){
        if (listOut == null) return;

        listOut.sort((o1, o2) -> Integer.compare(o2.getQuantitySold(), o1.getQuantitySold()));
        // Chỉ lấy top 5 món ăn
        this.listOut = listOut.subList(0, Math.min(5, listOut.size()));
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public OutstandingViewHodle onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outstanding,parent,false);
        return new OutstandingViewHodle(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutstandingViewHodle holder, int position) {
        Outstanding out = listOut.get(position);
        if(out == null) return;
        Glide.with(holder.itemView.getContext())
                .load(out.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgOut);
        holder.nameFood.setText(out.getNameFood());
        holder.rank.setText(out.getRank());
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.price.setText(formatter.format(out.getPrice()) + " đ");


    }

    @Override
    public int getItemCount() {
        if(listOut != null) return listOut.size();
        return 0;
    }

    public class OutstandingViewHodle extends RecyclerView.ViewHolder{

        private ImageView imgOut;
        private TextView nameFood,price,rank;
        public OutstandingViewHodle(@NonNull View itemView) {
            super(itemView);

            imgOut = itemView.findViewById(R.id.imgOut);
            nameFood = itemView.findViewById(R.id.nameFood);
            price = itemView.findViewById(R.id.price);
            rank = itemView.findViewById(R.id.rank);
        }
    }
}

package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ltmb.ltmobile.R;

import java.util.List;

public class OutstandingAdapter extends RecyclerView.Adapter<OutstandingAdapter.OutstandingViewHodle> {
    private Context context;
    private List<Outstanding> listOut;
    public OutstandingAdapter(Context context){
        this.context = context;
    }
    public void setData(List<Outstanding> listOut){
        this.listOut = listOut;
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
        holder.imgOut.setImageResource(out.getId());
        holder.nameFood.setText(out.getNameFood());
        holder.rank.setText(out.getRank());
        holder.price.setText(out.getPrice());
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

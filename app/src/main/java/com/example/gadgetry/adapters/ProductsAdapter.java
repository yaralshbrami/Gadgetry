package com.example.gadgetry.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gadgetry.R;
import com.example.gadgetry.activities.DetailActivity;
import com.example.gadgetry.models.ProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder>{
    List<ProductData> list;
    private Context context;
    private LayoutInflater inflater;
    private ProgressDialog progressDialog;

    public ProductsAdapter(List<ProductData> list, Context context) {
        this.list = list;
        this.context = context;

        this.inflater = LayoutInflater.from(this.context);
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Deleting... ");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_view_products, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductData productData = list.get(position);

        Picasso.get().load(productData.getImage()).into(holder.image);
        holder.name.setText(productData.getName());
        holder.price.setText("SAR " + productData.getPrice());
        holder.layout.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DetailActivity.class).putExtra("KEY",productData.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price;
        private ImageView image;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ProImage);
            name = itemView.findViewById(R.id.ProName);
            price = itemView.findViewById(R.id.ProPrice);
            layout = itemView.findViewById(R.id.ProLayout);
        }
    }
}

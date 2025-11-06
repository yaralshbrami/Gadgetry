package com.example.gadgetry.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gadgetry.R;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.listener.OnCallBackLoadedRecyclerView;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    List<ProductData> list;
    private Context context;
    private LayoutInflater inflater;
    private ProgressDialog progressDialog;
    private OnCallBackLoadedRecyclerView onCallBack;

    public CartAdapter(List<ProductData> list, Context context, OnCallBackLoadedRecyclerView onCallBack) {
        this.list = list;
        this.context = context;
        this.onCallBack=onCallBack;
        UtilKeys.grandTotalPrice=0;
        this.inflater = LayoutInflater.from(this.context);
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Removing... ");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_view_cart, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductData productData = list.get(position);

        Picasso.get().load(productData.getImage()).into(holder.image);
        holder.name.setText(productData.getName());
        holder.price.setText(productData.getPrice() + " SAR");
        holder.quantity.setText("Quantity: " + productData.getQuantity());
        int totalPrice=(Integer.parseInt(productData.getPrice()))
                *(Integer.parseInt(productData.getQuantity()));
        holder.totalPrice.setText(totalPrice + " SAR");
        //Grand Total
        UtilKeys.grandTotalPrice=UtilKeys.grandTotalPrice+totalPrice;

        holder.cancel.setOnClickListener(v -> {
            deleteOrder(position);
        });
        // Check if this is the last item being bound
        if (position == list.size() - 1) {
            // Notify that data loading is complete
            if (onCallBack != null) {
                onCallBack.callBackOnLoaded();
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, quantity, totalPrice;
        private ImageView image, cancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ItemImage);
            name = itemView.findViewById(R.id.ItemName);
            price = itemView.findViewById(R.id.ItemPrice);
            quantity = itemView.findViewById(R.id.ItemQuantity);
            cancel = itemView.findViewById(R.id.ItemCancel);
            totalPrice = itemView.findViewById(R.id.ItemTotalPrice);
        }
    }

    private void deleteOrder(int index) {
        progressDialog.show();
        ProductData productData = list.get(index);
        DBHelper dbHelper = new DBHelper(UtilKeys.ORDER_PRODUCT_TABLE);

        dbHelper.deleteOrder(productData.getId()).addOnSuccessListener(suc -> {

                    UtilKeys.grandTotalPrice=0;
                    list.remove(index);
                    notifyItemRemoved(index);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    onCallBack.callBackOnLoaded();
        })
                .addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed to delete Product", Toast.LENGTH_SHORT).show();
                });
    }
    // Method to clear the data from the adapter
    public void clearData() {
        // Clear the list
        list.clear();
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

}

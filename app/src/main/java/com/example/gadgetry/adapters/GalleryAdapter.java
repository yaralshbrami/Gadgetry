package com.example.gadgetry.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gadgetry.R;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    List<ProductData> list;
    private Context context;
    private LayoutInflater inflater;
    private ProgressDialog progressDialog;

    public GalleryAdapter(List<ProductData> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Deleting... ");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_view_gallery, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductData productData = list.get(position);

        Picasso.get().load(productData.getImage()).into(holder.image);
        holder.name.setText(productData.getName());
        holder.price.setText("SAR " + productData.getPrice());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                deleteItem(holder.getAdapterPosition(), v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price;
        private ImageView image, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ProductImage);
            name = itemView.findViewById(R.id.ProductName);
            price = itemView.findViewById(R.id.ProductPrice);
            delete = itemView.findViewById(R.id.Delete);
        }
    }

    private void deleteItem(int index, View view) {
        ProductData productData = list.get(index);
        try {
            //-----------Upload New Image in Firebase Storage
            FirebaseStorage fbStorage = FirebaseStorage.getInstance();
            StorageReference filePath = fbStorage.getReference().child(Uri.parse(productData.getImage()).getLastPathSegment());
            filePath.delete().addOnSuccessListener(suc -> {
                deleteProduct(index);
            }).addOnFailureListener(err -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to delete Item", Toast.LENGTH_SHORT).show();
            });
                    /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        //------Store image as string in ProductData Class
                                        productData = new ProductData(
                                                binding.ProductName.getText().toString(),
                                                binding.Price.getText().toString(),
                                                binding.Description.getText().toString(),
                                                productType,
                                                task.getResult().toString()
                                        );
                                        uploadProduct();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddProductActivity.this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });*/

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteProduct(int index) {
        ProductData productData = list.get(index);
        DBHelper dbHelper = new DBHelper(UtilKeys.PRODUCT_TABLE);

        dbHelper.deleteProduct(productData.getId()).addOnSuccessListener(suc -> {
                    list.remove(index);
                    notifyItemRemoved(index);
                    progressDialog.dismiss();
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed to delete Product", Toast.LENGTH_SHORT).show();
                });
    }
}




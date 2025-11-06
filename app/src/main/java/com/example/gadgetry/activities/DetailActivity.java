package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.databinding.ActivityDetailBinding;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
    private int count=0;
    private ProductData productData=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading... ");
        dbHelper = new DBHelper(UtilKeys.PRODUCT_TABLE);

        String key = getIntent().getStringExtra("KEY");
        loadProductData(key);

        binding.Back.setOnClickListener(v -> {
            finish();
        });
         binding.Subtract.setOnClickListener( v -> {
             if(count>0)
             {
                 --count;
                 binding.Count.setText(count+"");
             }
         });
        binding.Addition.setOnClickListener( v -> {
            ++count;
            binding.Count.setText(count+"");
        });
        binding.ShopNow.setOnClickListener(v -> {

            if(count==0)
            {
                Toast.makeText(DetailActivity.this,"0 Quantity not allowed",Toast.LENGTH_LONG).show();
            }
            else {
                if(productData!=null)
                {
                    uploadSelectedProduct();
                }
                else
                {
                    Toast.makeText(DetailActivity.this,"No Product selected",Toast.LENGTH_LONG).show();
                }
            }


        });
    }
    private void loadProductData(String key) {
        progressDialog.show();
        dbHelper.getProductData(key).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                // Data found for the key, process it here
                productData= dataSnapshot.getValue(ProductData.class); // Assuming you're fetching a Product object
                Picasso.get().load(productData.getImage()).into(binding.Image);
                binding.Name.setText(productData.getName());
                binding.Price.setText("SAR "+productData.getPrice());
                binding.Description.setText(productData.getDescription());
                binding.Count.setText(count+"");
            }
            progressDialog.dismiss();

        }).addOnFailureListener(err -> {
            progressDialog.dismiss();
            Toast.makeText(DetailActivity.this,"Failed to product fetch data",Toast.LENGTH_LONG).show();
        });
    }
    private void uploadSelectedProduct() {
        progressDialog.setMessage("Adding to you card... ");
        productData.setQuantity(count+"");

        DBHelper dbHelper = new DBHelper(UtilKeys.ORDER_PRODUCT_TABLE);
        dbHelper.insertOrder(productData)
                .addOnSuccessListener(suc -> {
                    progressDialog.dismiss();
                    Toast.makeText(DetailActivity.this, "Product added in your cart", Toast.LENGTH_SHORT).show();
                    UtilKeys.CART_ACTIVE=true;
                    finish();
                })
                .addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Toast.makeText(DetailActivity.this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
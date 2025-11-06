package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.gadgetry.adapters.GalleryAdapter;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.databinding.ActivityGalleryBinding;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    ActivityGalleryBinding binding;
    private GalleryAdapter galleryAdapter;
    private List<ProductData> list;
    private DBHelper dbHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Loading... ");

        list = new ArrayList<>();
        dbHelper = new DBHelper(UtilKeys.PRODUCT_TABLE);
        loadProductsData();

        binding.AddNewProduct.setOnClickListener(v -> {
            startActivity(new Intent(GalleryActivity.this, AddProductActivity.class));
        });
        binding.SignOut.setOnClickListener(v -> {
            //Store Data in Shared Preferences
            SharedPreferences.Editor editor = UtilKeys.PREFS.edit();
            editor.putString(UtilKeys.TOKEN, "");
            editor.putString(UtilKeys.USER_KEY, "");
            editor.commit();

            startActivity(new Intent(GalleryActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsData();
    }

    private void loadProductsData() {
        progressDialog.show();
        dbHelper.getAllProductsData().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for (DataSnapshot snapshotObj : snapshot.getChildren()) {
                        ProductData productData = snapshotObj.getValue(ProductData.class);
                        productData.setId(snapshotObj.getKey());
                        list.add(productData);
                    }
                    loadRecyclerView();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(GalleryActivity.this, "No product available", Toast.LENGTH_LONG).show();
                }
            }

            /**
             * This method will be triggered in the event that this listener either failed at the server, or
             * is removed as a result of the security and Firebase Database rules. For more information on
             * securing your data, see: <a
             * href="https://firebase.google.com/docs/database/security/quickstart" target="_blank"> Security
             * Quickstart</a>
             *
             * @param error A description of the error that occurred
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(GalleryActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loadRecyclerView() {
        galleryAdapter = new GalleryAdapter(list, this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.RVGallery.setLayoutManager(new LinearLayoutManager(this));

        binding.RVGallery.setLayoutManager(new GridLayoutManager(this, 2));
        binding.RVGallery.setAdapter(galleryAdapter);
        progressDialog.dismiss();
    }
}
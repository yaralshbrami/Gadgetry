package com.example.gadgetry.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gadgetry.activities.MainActivity;
import com.example.gadgetry.adapters.CartAdapter;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.databinding.FragmentCartBinding;
import com.example.gadgetry.listener.OnCallBackLoadedRecyclerView;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements OnCallBackLoadedRecyclerView {

    FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private List<ProductData> list;
    private DBHelper dbHelper;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        this.progressDialog = new ProgressDialog(getContext());
        this.progressDialog.setMessage("Loading... ");

        list = new ArrayList<>();
        dbHelper = new DBHelper(UtilKeys.ORDER_PRODUCT_TABLE);
        loadOrderData();

        binding.Home.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });
        binding.CheckOut.setOnClickListener(v -> {
                    if (UtilKeys.grandTotalPrice > 0) {
                        placeOrder();
                    }}
        );
        return binding.getRoot();
    }

    private void loadOrderData() {
        progressDialog.show();
        dbHelper.getAllOrderData().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {

                    for (DataSnapshot snapshotObj : snapshot.getChildren()) {
                        ProductData productData = snapshotObj.getValue(ProductData.class);
                        productData.setId(snapshotObj.getKey());
                        list.add(productData);
                    }
                    mergeDuplicateProducts();
                    loadRecyclerView();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No product available", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadRecyclerView() {

        cartAdapter = new CartAdapter(list, this.getContext(), this);
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.RecyclerView.setAdapter(cartAdapter);
        progressDialog.dismiss();
    }

    private void placeOrder() {
        this.progressDialog.setMessage("Order... ");
        progressDialog.show();

        dbHelper.deleteOrderAll().addOnSuccessListener(suc -> {

                    UtilKeys.grandTotalPrice = 0;
                    list.clear();
                    cartAdapter.clearData();
                    progressDialog.dismiss();
                    binding.GrandTotalPrice.setText(UtilKeys.grandTotalPrice + " SAR");

                    Toast.makeText(getContext(), "Order placed successfully" , Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void callBackOnLoaded() {
//        Toast.makeText(getContext(), "GrandTotal "+UtilKeys.grandTotalPrice, Toast.LENGTH_SHORT).show();

        binding.GrandTotalPrice.setText(UtilKeys.grandTotalPrice + "  SAR");
    }
    private void mergeDuplicateProducts() {
        Map<String, ProductData> uniqueProducts = new HashMap<>();
        for (ProductData product : list) {
            String productKey = product.getName();
            if (uniqueProducts.containsKey(productKey)) {
                ProductData existingProduct = uniqueProducts.get(productKey);
                int updatedQuantity = Integer.parseInt(existingProduct.getQuantity()) + Integer.parseInt(product.getQuantity());
                existingProduct.setQuantity(String.valueOf(updatedQuantity));
            } else {
                uniqueProducts.put(productKey, product);
            }
        }
        list = new ArrayList<>(uniqueProducts.values());
    }

}
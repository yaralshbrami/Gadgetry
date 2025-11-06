package com.example.gadgetry.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gadgetry.activities.MainActivity;
import com.example.gadgetry.adapters.ProductsAdapter;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.databinding.FragmentProductsBinding;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductsFragment extends Fragment {
    FragmentProductsBinding binding;
    private ProductsAdapter productsAdapter;
    private List<ProductData> list;
    private DBHelper dbHelper;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading... ");

        list = new ArrayList<>();
        dbHelper = new DBHelper(UtilKeys.PRODUCT_TABLE);
        //ChargingCable will Default List type
        loadProductsData(binding.MobilePhone.getText().toString(), 1);

        binding.Home.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });

        binding.MobilePhone.setOnClickListener(
                v -> loadProductsData(binding.MobilePhone.getText().toString(), 1));
        binding.Camera.setOnClickListener(
                v -> loadProductsData(binding.Camera.getText().toString(), 2));
        binding.MobileAccessories.setOnClickListener(
                v -> loadProductsData(binding.MobileAccessories.getText().toString(), 3));
        binding.CameraAccessories.setOnClickListener(
                v -> loadProductsData(binding.CameraAccessories.getText().toString(), 4));
        return binding.getRoot();
    }

    private void loadProductsData(String productsType, int tabNumber) {
        progressDialog.show();
        decorateTextView(tabNumber);
        dbHelper.getAllProductsData().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshotObj : snapshot.getChildren()) {
                        ProductData productData = snapshotObj.getValue(ProductData.class);
                        productData.setId(snapshotObj.getKey());

                        if (productData.getType().equals(productsType)) {
                            list.add(productData);
                        }
                    }
                    loadRecyclerView();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No product available", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.e("ProductsFragment", "Error fetching data: " + error.getMessage());

            }
        });
    }

    private void loadRecyclerView() {
        productsAdapter = new ProductsAdapter(list, getContext());
        binding.RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.RecyclerView.setAdapter(productsAdapter);
        progressDialog.dismiss();
    }

    private void decorateTextView(int textId) {

        makeNormalText();
        switch (textId) {
            case 1:
                binding.MobilePhone.setTypeface(binding.MobilePhone.getTypeface(), Typeface.BOLD);
                binding.MobilePhone.setPaintFlags(binding.MobilePhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                break;
            case 2:
                binding.Camera.setTypeface(binding.Camera.getTypeface(), Typeface.BOLD);
                binding.Camera.setPaintFlags(binding.Camera.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                break;
            case 3:
                binding.MobileAccessories.setTypeface(binding.MobileAccessories.getTypeface(), Typeface.BOLD);
                binding.MobileAccessories.setPaintFlags(binding.MobileAccessories.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                break;
            case 4:
                binding.CameraAccessories.setTypeface(binding.CameraAccessories.getTypeface(), Typeface.BOLD);
                binding.CameraAccessories.setPaintFlags(binding.CameraAccessories.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                break;
        }
    }

    private void makeNormalText() {
        binding.MobilePhone.setTypeface(Typeface.DEFAULT);
        binding.MobilePhone.setPaintFlags(binding.MobilePhone.getPaintFlags()  & (~Paint.UNDERLINE_TEXT_FLAG));
        binding.Camera.setTypeface(Typeface.DEFAULT);
        binding.Camera.setPaintFlags(binding.Camera.getPaintFlags()  & (~Paint.UNDERLINE_TEXT_FLAG));
        binding.MobileAccessories.setTypeface(Typeface.DEFAULT);
        binding.MobileAccessories.setPaintFlags(binding.MobileAccessories.getPaintFlags()  & (~Paint.UNDERLINE_TEXT_FLAG));
        binding.CameraAccessories.setTypeface(Typeface.DEFAULT);
        binding.CameraAccessories.setPaintFlags(binding.CameraAccessories.getPaintFlags()  & (~Paint.UNDERLINE_TEXT_FLAG));
    }

}

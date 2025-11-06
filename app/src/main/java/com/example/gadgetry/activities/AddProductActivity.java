package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gadgetry.databases.DBHelper;
import com.example.gadgetry.databinding.ActivityAddProductBinding;
import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import javax.annotation.Nullable;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;
    private Uri uri = null;
    private Bitmap bitmap = null;
    private ProgressDialog progressDialog;
    private ProductData productData;
    private String productType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving... ");

        binding.ImageLayout.setOnClickListener(v -> chooseImage());
        binding.AddImage.setOnClickListener(v -> chooseImage());
        binding.Image.setOnClickListener(v -> chooseImage());

        binding.rb1.setOnClickListener(v -> radioButtonChecked(1));
        binding.rb2.setOnClickListener(v -> radioButtonChecked(2));
        binding.rb3.setOnClickListener(v -> radioButtonChecked(3));
        binding.rb4.setOnClickListener(v -> radioButtonChecked(4));

        binding.Save.setOnClickListener(v -> saveData());
        binding.Back.setOnClickListener(v -> {
            finish();
        });
    }

    private void chooseImage() {
        Intent gallery_ImgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_ImgIntent.setType("image/*");
        startActivityForResult(gallery_ImgIntent, UtilKeys.IMAGE_REQUEST_CODE_GALLERY);

    }

    private void radioButtonChecked(int rbNumber) {
        binding.rb1.setChecked(false);
        binding.rb2.setChecked(false);
        binding.rb3.setChecked(false);
        binding.rb4.setChecked(false);

        switch (rbNumber) {
            case 1:
                binding.rb1.setChecked(true);
                productType = binding.tv1.getText().toString();
                break;
            case 2:
                binding.rb2.setChecked(true);
                productType = binding.tv2.getText().toString();
                break;
            case 3:
                binding.rb3.setChecked(true);
                productType = binding.tv3.getText().toString();
                break;
            case 4:
                binding.rb4.setChecked(true);
                productType = binding.tv4.getText().toString();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image Selected From Gallery
        if (resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            binding.Image.setImageURI(uri);
            binding.AddImage.setVisibility(View.GONE);
            binding.CardView.setVisibility(View.VISIBLE);
        }
    }

    private void saveData() {
        if (binding.ProductName.getText().toString().isEmpty()) {
            Toast.makeText(AddProductActivity.this, "Enter product Name", Toast.LENGTH_LONG).show();
        } else if (binding.Price.getText().toString().isEmpty()) {
            Toast.makeText(AddProductActivity.this, "Enter product Price", Toast.LENGTH_LONG).show();
        } else if (!binding.rb1.isChecked()
                && !binding.rb2.isChecked()
                && !binding.rb3.isChecked()
                && !binding.rb4.isChecked()) {
            Toast.makeText(AddProductActivity.this, "Please choose product type", Toast.LENGTH_LONG).show();
        } else if (uri == null) {
            Toast.makeText(AddProductActivity.this, "Please select product image", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            dataUploading();
        }
    }

    private void dataUploading() {
        try {
            FirebaseStorage fbStorage = FirebaseStorage.getInstance();
            StorageReference filePath = fbStorage.getReference().child(UtilKeys.IMAGE_TABLE).child(uri.getLastPathSegment());
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(AddProductActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadProduct() {
        DBHelper dbHelper = new DBHelper(UtilKeys.PRODUCT_TABLE);
        dbHelper.insertProduct(productData)
                .addOnSuccessListener(suc -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "Data uploaded successfully ", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
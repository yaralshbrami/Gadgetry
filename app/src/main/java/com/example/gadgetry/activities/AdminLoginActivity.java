package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gadgetry.databinding.ActivityAdminLoginBinding;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {
    ActivityAdminLoginBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login... ");

        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.AdminName.getText().toString().isEmpty()
                        || binding.AdminPassword.getText().toString().isEmpty()) {
                    Toast.makeText(AdminLoginActivity.this, "Please fill fields", Toast.LENGTH_SHORT).show();
                } else if (!binding.AdminName.getText().toString().equals("admin77@gmail.com")) {
                    Toast.makeText(AdminLoginActivity.this, "You are not admin", Toast.LENGTH_SHORT).show();
                }else {
                    String email = binding.AdminName.getText().toString().trim();
                    String pass = binding.AdminPassword.getText().toString();

                    progressDialog.show();
                    authenticationUserForLogin(email, pass);
                }
            }
        });
    }
    private void authenticationUserForLogin(String email, String pass) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String usrName = email.substring(0, email.indexOf('@'));
                            binding.AdminName.setText("");
                            binding.AdminPassword.setText("");

                            //Store Data in Shared Preferences
                            SharedPreferences.Editor editor = UtilKeys.PREFS.edit();
                            editor.putString(UtilKeys.TOKEN, "admin");
                            editor.putString(UtilKeys.USER_KEY, usrName);
                            editor.commit();

                            progressDialog.dismiss();
                            startActivity(new Intent(AdminLoginActivity.this, GalleryActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Email or Password not matched", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
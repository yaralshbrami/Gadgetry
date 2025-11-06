package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gadgetry.databinding.ActivityLoginBinding;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login... ");

        binding.Admin.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
        });
        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.UserEmail.getText().toString().isEmpty()
                        || binding.UserPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill fields", Toast.LENGTH_SHORT).show();
                } else if (binding.UserEmail.getText().toString().equals("support@gmail.com")) {
                    Toast.makeText(LoginActivity.this, "Email or Password not matched", Toast.LENGTH_SHORT).show();
                } else {
                    String email = binding.UserEmail.getText().toString().trim();
                    String pass = binding.UserPassword.getText().toString();

                    progressDialog.show();
                    authenticationUserForLogin(email, pass);
                }
            }
        });
        binding.SingUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
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
                            binding.UserEmail.setText("");
                            binding.UserPassword.setText("");

                            //Store Data in Shared Preferences
                            SharedPreferences.Editor editor = UtilKeys.PREFS.edit();
                            editor.putString(UtilKeys.TOKEN, "user");
                            editor.putString(UtilKeys.USER_KEY, usrName);
                            editor.commit();

                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Email or Password not matched", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
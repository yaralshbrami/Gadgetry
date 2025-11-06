package com.example.gadgetry.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gadgetry.databinding.ActivitySignUpBinding;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sing up... ");
        binding.SingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.UserEmail.getText().toString().isEmpty()
                        || binding.UserPassword.getText().toString().isEmpty()
                        || binding.UserConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill fields", Toast.LENGTH_SHORT).show();
                } else if (!binding.UserPassword.getText().toString().trim()
                        .equals(binding.UserConfirmPassword.getText().toString().trim())) {
                    Toast.makeText(SignUpActivity.this, "Password and confirm password not matched", Toast.LENGTH_SHORT).show();

                }else if (!isValidEmail(binding.UserEmail.getText().toString().trim())) {
                    Toast.makeText(SignUpActivity.this, "please enter valid email", Toast.LENGTH_SHORT).show();
                }  else {
                    String email = binding.UserEmail.getText().toString().trim();
                    String pass = binding.UserPassword.getText().toString();

                    progressDialog.show();
                    authenticationUserForSingUp(email, pass);
                }
            }
        });
        binding.Login.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
    private void authenticationUserForSingUp(String email, String pass) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String usrName = email.substring(0, email.indexOf('@'));
                            progressDialog.dismiss();
                            binding.UserEmail.setText("");
                            binding.UserPassword.setText("");
                            binding.UserConfirmPassword.setText("");

                            //Store Data in Shared Preferences
                            SharedPreferences.Editor editor = UtilKeys.PREFS.edit();
                            editor.putString(UtilKeys.TOKEN, "user");
                            editor.putString(UtilKeys.USER_KEY, usrName);
                            editor.commit();

                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Failed to register user", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    // Method to validate email using regular expression
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        return pattern.matcher(email).matches();
    }
}
package com.example.gadgetry.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gadgetry.R;
import com.example.gadgetry.databinding.ActivityMainBinding;
import com.example.gadgetry.fragments.AboutUsFragment;
import com.example.gadgetry.fragments.CartFragment;
import com.example.gadgetry.fragments.ContactUsFragment;
import com.example.gadgetry.fragments.HomeFragment;
import com.example.gadgetry.fragments.ProductsFragment;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(UtilKeys.CART_ACTIVE)
        {
            binding.bottomNavigationView.setSelectedItemId(R.id.cart);
            UtilKeys.CART_ACTIVE=false;
            Fragment fragment = new CartFragment();
            openFragment(fragment);
        }
        else {
            Fragment fragment = new HomeFragment();
            openFragment(fragment);
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if (item.getItemId() == R.id.home) {
                    fragment = new HomeFragment();
                } else if (item.getItemId() == R.id.products) {
                    fragment = new ProductsFragment();
                } else if (item.getItemId() == R.id.cart) {
                    fragment = new CartFragment();
                } else if (item.getItemId() == R.id.about_us) {
                    fragment = new AboutUsFragment();
                } else if (item.getItemId() == R.id.contact_us) {
                    fragment = new ContactUsFragment();
                }
                openFragment(fragment);
                return true;
            }
        });
    }
    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}
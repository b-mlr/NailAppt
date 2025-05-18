package com.example.nailappt;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav);

        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new BookingFragment());
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
            bottomNavigationView.setSelectedItemId(R.id.booking);
        }
    }

    protected void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.advertise) {
                selectedFragment = new AdvertiseFragment();
            } else if (itemId == R.id.booking) {
                selectedFragment = new BookingFragment();
            } else if (itemId == R.id.profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

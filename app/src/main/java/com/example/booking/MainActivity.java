package com.example.booking;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.booking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBottomNavigation();
    }

    private void initBottomNavigation() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frm, new HomeFragment())
                .commitNowAllowingStateLoss();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.homeFragment) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.mapFragment) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.recordFragment) {
                selectedFragment = new RecordFragment();
            } else if (itemId == R.id.myFragment) {
                selectedFragment = new MyFragment();
            }
            if(selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frm, selectedFragment)
                        .commitNowAllowingStateLoss();
                return true;
            } else {
                return false;
            }
        });
    }
}
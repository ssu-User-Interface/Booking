package com.example.booking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.booking.databinding.ActivityMainBinding;
import com.example.booking.presentation.home.HomeFragment;
import com.example.booking.presentation.map.MapFragment;
import com.example.booking.presentation.mypage.MyFragment;
import com.example.booking.presentation.record.RecordFragment;

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
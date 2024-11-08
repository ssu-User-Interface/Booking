package com.example.booking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

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
        // 네비게이션 호스트 프래그먼트를 찾아 NavController를 가져온다.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_frm);
        NavController navController = navHostFragment.getNavController();

        // 바텀네비게이션 뷰와 NavController를 연결하여 메뉴 항목 선택 시 네비게이션이 동작하도록 설정
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }
}
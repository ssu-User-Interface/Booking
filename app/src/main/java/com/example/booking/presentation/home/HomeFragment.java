package com.example.booking.presentation.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.booking.R;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class HomeFragment extends Fragment {

    private boolean hasReadingBook = true; // 읽고 있는 책 유무를 확인하는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NavController 초기화
        NavController navController = Navigation.findNavController(view);

        // EditText를 클릭 시 검색 Fragment로 이동
        EditText openBookSearchEditText = view.findViewById(R.id.et_home_search);
        openBookSearchEditText.setFocusable(false);
        openBookSearchEditText.setOnClickListener(v -> {
            navController.navigate(R.id.action_homeFragment_to_searchFragment);
        });

        // 위젯 관련 View 초기화
        TextView tvBookTitle = view.findViewById(R.id.tv_home_first_widget_book_title);
        View viewLine = view.findViewById(R.id.view_home_line);
        TextView tvReadingPeriod = view.findViewById(R.id.tv_home_first_widget_reading_period);
        TextView tvReadingPage = view.findViewById(R.id.tv_home_first_widget_reading_page);
        ProgressBar prg = view.findViewById(R.id.prg); // ProgressBar
        View toTimerButton = view.findViewById(R.id.view_home_first_widget_right_to_timer);
        ImageView ivBookImg = view.findViewById(R.id.iv_home_book);

        ImageView ivBookIcon = view.findViewById(R.id.iv_home_book_ic);
        TextView tvNoBook = view.findViewById(R.id.tv_home_first_widget_no_book);
        View addBookButton = view.findViewById(R.id.view_home_first_widget_right_add_book);

        if (hasReadingBook) {
            // 읽고 있는 책이 있을 때 visibility 설정
            tvBookTitle.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
            tvReadingPeriod.setVisibility(View.VISIBLE);
            tvReadingPage.setVisibility(View.VISIBLE);
            prg.setVisibility(View.VISIBLE);
            toTimerButton.setVisibility(View.VISIBLE);
            ivBookImg.setVisibility(View.VISIBLE);

            ivBookIcon.setVisibility(View.GONE);
            tvNoBook.setVisibility(View.GONE);
            addBookButton.setVisibility(View.GONE);

            // 타이머 버튼 클릭 시 fragment_timer로 이동
            toTimerButton.setOnClickListener(v -> {
                navController.navigate(R.id.action_homeFragment_to_timerFragment);
            });

        } else {
            // 읽고 있는 책이 없을 때 visibility 설정
            tvBookTitle.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
            tvReadingPeriod.setVisibility(View.GONE);
            tvReadingPage.setVisibility(View.GONE);
            prg.setVisibility(View.GONE);
            toTimerButton.setVisibility(View.GONE);
            ivBookImg.setVisibility(View.GONE);

            ivBookIcon.setVisibility(View.VISIBLE);
            tvNoBook.setVisibility(View.VISIBLE);
            addBookButton.setVisibility(View.VISIBLE);

            // 책 추가 버튼 클릭 시 fragment_book_search로 이동
            addBookButton.setOnClickListener(v -> {
                navController.navigate(R.id.action_homeFragment_to_searchFragment);
            });
        }
    }
}


package com.example.booking.presentation.search;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.booking.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BookSearchSaveBottomSheetFragment extends BottomSheetDialogFragment {

    private Button btnToRead, btnReading, btnRead;
    private TextView tvStartDay, tvAmount, tvPeriod, tvReview, tvScore;
    private EditText etStartDay, etAmount, etStartPeriod, etEndPeriod, etReview;
    private LinearLayout layoutPeriod, layoutScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_save_bottom_sheet, container, false);

        // 버튼 초기화
        btnToRead = view.findViewById(R.id.bt_book_search_save_record_to_read);
        btnReading = view.findViewById(R.id.bt_book_search_save_record_reading);
        btnRead = view.findViewById(R.id.bt_book_search_save_record_read);

        // 독서 상태에 따라 표시되는 필드 초기화
        tvStartDay = view.findViewById(R.id.tv_book_search_save_start_day);
        etStartDay = view.findViewById(R.id.et_book_search_save_start_day);
        tvAmount = view.findViewById(R.id.tv_book_search_save_amount);
        etAmount = view.findViewById(R.id.et_book_search_save_amount);
        tvPeriod = view.findViewById(R.id.tv_book_search_save_final_record_period);
        etStartPeriod = view.findViewById(R.id.et_book_search_save_final_start_day);
        etEndPeriod = view.findViewById(R.id.et_book_search_save_final_end_day);
        tvReview = view.findViewById(R.id.tv_book_search_save_final_record_review);
        etReview = view.findViewById(R.id.et_book_search_save_final_record_review);
        tvScore = view.findViewById(R.id.tv_book_search_save_final_record_score);
        layoutPeriod = view.findViewById(R.id.l_layout_book_search_save_final_record_period);
        layoutScore = view.findViewById(R.id.l_layout_book_search_save_final_record_score_star);

        // 기본값
        setReadingView();
        updateButtonStyles(btnReading);

        btnToRead.setOnClickListener(v -> {
            setToReadView();
            updateButtonStyles(btnToRead);
        });

        btnReading.setOnClickListener(v -> {
            setReadingView();
            updateButtonStyles(btnReading);
        });

        btnRead.setOnClickListener(v -> {
            setReadView();
            updateButtonStyles(btnRead);
        });

        return view;
    }

    private void setToReadView() {
        tvStartDay.setVisibility(View.GONE);
        etStartDay.setVisibility(View.GONE);
        tvAmount.setVisibility(View.GONE);
        etAmount.setVisibility(View.GONE);
        tvPeriod.setVisibility(View.GONE);
        etStartPeriod.setVisibility(View.GONE);
        etEndPeriod.setVisibility(View.GONE);
        tvReview.setVisibility(View.GONE);
        etReview.setVisibility(View.GONE);
        tvScore.setVisibility(View.GONE);
        layoutPeriod.setVisibility(View.GONE);
        layoutScore.setVisibility(View.GONE);
    }

    // 읽는 책 상태 설정
    private void setReadingView() {
        // 독서 시작일, 독서량만 표시
        tvStartDay.setVisibility(View.VISIBLE);
        etStartDay.setVisibility(View.VISIBLE);
        tvAmount.setVisibility(View.VISIBLE);
        etAmount.setVisibility(View.VISIBLE);
        tvPeriod.setVisibility(View.GONE);
        etStartPeriod.setVisibility(View.GONE);
        etEndPeriod.setVisibility(View.GONE);
        tvReview.setVisibility(View.GONE);
        etReview.setVisibility(View.GONE);
        tvScore.setVisibility(View.GONE);
        layoutPeriod.setVisibility(View.GONE);
        layoutScore.setVisibility(View.GONE);
    }

    // 읽은 책 상태 설정
    private void setReadView() {
        // 독서 기간, 한줄평, 평점 표시
        tvStartDay.setVisibility(View.GONE);
        etStartDay.setVisibility(View.GONE);
        tvAmount.setVisibility(View.GONE);
        etAmount.setVisibility(View.GONE);
        tvPeriod.setVisibility(View.VISIBLE);
        etStartPeriod.setVisibility(View.VISIBLE);
        etEndPeriod.setVisibility(View.VISIBLE);
        tvReview.setVisibility(View.VISIBLE);
        etReview.setVisibility(View.VISIBLE);
        tvScore.setVisibility(View.VISIBLE);
        layoutPeriod.setVisibility(View.VISIBLE);
        layoutScore.setVisibility(View.VISIBLE);
    }

    private void updateButtonStyles(Button selectedButton) {
        btnToRead.setSelected(false);
        btnReading.setSelected(false);
        btnRead.setSelected(false);

        selectedButton.setSelected(true);
    }
}

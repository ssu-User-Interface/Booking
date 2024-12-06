package com.example.booking.presentation.search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.booking.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookSearchSaveBottomSheetFragment extends BottomSheetDialogFragment {

    private Button btnToRead, btnReading, btnRead ;
    private TextView tvStartDay, tvAmount, tvPeriod, tvReview, tvScore;
    private EditText etStartDay, etAmount, etStartPeriod, etEndPeriod, etReview;
    private LinearLayout layoutPeriod, layoutScore;

    private String bookTitle, bookAuthor, bookPublisher, bookImage, bookDescription;
    private Integer bookTotalPage;
    private String userId;

    private int selectedStars = 0; // 선택된 별 개수
    private ImageView[] stars;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private BookSearchDetailViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
        }

        viewModel = new ViewModelProvider(requireActivity()).get(BookSearchDetailViewModel.class);

        BookSearchSaveBottomSheetFragmentArgs args = BookSearchSaveBottomSheetFragmentArgs.fromBundle(getArguments());
        bookTitle = args.getBookTitle();
        bookAuthor = args.getBookAuthor();
        bookPublisher = args.getBookPublisher();
        bookImage = args.getBookImage();
        bookDescription = args.getBookDescription();
        bookTotalPage = args.getPageCount();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_save_bottom_sheet, container, false);

        btnToRead = view.findViewById(R.id.bt_book_search_save_record_to_read);
        btnReading = view.findViewById(R.id.bt_book_search_save_record_reading);
        btnRead = view.findViewById(R.id.bt_book_search_save_record_read);

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


        setReadingView();
        updateButtonStyles(btnReading);

        setupDatePicker(etStartDay);
        setupDatePicker(etStartPeriod);
        setupDatePicker(etEndPeriod);

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

        stars = new ImageView[]{
                view.findViewById(R.id.iv_book_search_save_final_record_star1),
                view.findViewById(R.id.iv_book_search_save_final_record_star2),
                view.findViewById(R.id.iv_book_search_save_final_record_star3),
                view.findViewById(R.id.iv_book_search_save_final_record_star4),
                view.findViewById(R.id.iv_book_search_save_final_record_star5)
        };

        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> setStarRating(starIndex));
        }

        Button saveButton = view.findViewById(R.id.bt_book_search_save_save_record);
        saveButton.setOnClickListener(v -> saveBookData());

        return view;
    }


    private void setupDatePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);

        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editText.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
    }

    private void setStarRating(int starCount) {
        selectedStars = starCount;

        for (int i = 0; i < stars.length; i++) {
            if (i < starCount) {
                stars[i].setImageResource(R.drawable.img_star_filled); // 활성화된 별
            } else {
                stars[i].setImageResource(R.drawable.img_star_unfilled); // 비활성화된 별
            }
        }
    }

    private void saveBookData() {
        String readingStatus = getSelectedReadingStatus();

        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", bookTitle);
        bookData.put("author", bookAuthor);
        bookData.put("publisher", bookPublisher);
        bookData.put("image", bookImage);
        bookData.put("description", bookDescription);
        bookData.put(("totalPage"),bookTotalPage);
        bookData.put("readingStatus", readingStatus);
        bookData.put("createdAt", new Date());

        if ("reading_books".equals(readingStatus)) {
            String startDateString = etStartDay.getText().toString().trim();
            Timestamp startDate = convertToTimestamp(startDateString);
            bookData.put("startDate", startDate);
            bookData.put("readingPage", Integer.parseInt(etAmount.getText().toString()));
        } else if ("read_books".equals(readingStatus)) {
            String startPeriodString = etStartPeriod.getText().toString().trim();
            String endPeriodString = etEndPeriod.getText().toString().trim();
            Timestamp startDate = convertToTimestamp(startPeriodString);
            Timestamp endDate = convertToTimestamp(endPeriodString);

            bookData.put("startDate", startDate);
            bookData.put("endDate", endDate);
            bookData.put("rating", selectedStars);
            bookData.put("review", etReview.getText().toString());
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("books")
                .add(bookData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "책 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(this);
                    BookSearchSaveBottomSheetFragmentDirections.ActionBookSearchSaveBottomSheetFragmentToBookSearchDetailFragment action =
                            BookSearchSaveBottomSheetFragmentDirections
                                    .actionBookSearchSaveBottomSheetFragmentToBookSearchDetailFragment(
                                            bookTitle,
                                            bookAuthor,
                                            bookPublisher,
                                            bookImage,
                                            bookDescription,
                                            bookTotalPage
                                    );
                    navController.navigate(action);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "책 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Timestamp convertToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setLenient(false);
            Date date = dateFormat.parse(dateString);
            return new Timestamp(date);
        } catch (Exception e) {
            Toast.makeText(getContext(), "날짜 형식을 확인해주세요: " + dateString, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String getSelectedReadingStatus() {
        if (btnToRead.isSelected()) {
            return "will_read_books";
        } else if (btnReading.isSelected()) {
            return "reading_books";
        } else if (btnRead.isSelected()) {
            return "read_books";
        }
        return "";
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        NavController navController = NavHostFragment.findNavController(this);

        backButton.setOnClickListener(v -> navController.popBackStack());
    }

}

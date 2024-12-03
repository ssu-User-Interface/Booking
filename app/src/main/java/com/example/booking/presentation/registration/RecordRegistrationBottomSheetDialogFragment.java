package com.example.booking.presentation.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.booking.R;
import com.example.booking.presentation.record.RecordSpecificFragment;
import com.example.booking.presentation.search.BookSearchFragment;
import com.example.booking.presentation.search.BookSearchSaveBottomSheetFragmentDirections;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecordRegistrationBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private int selectedStars = 0; // 선택된 별 개수
    private ImageView[] stars;
    private RecordRegistrationViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        viewModel = new ViewModelProvider(requireActivity()).get(RecordRegistrationViewModel.class);
    }


    // 바텀시트 저장하기 버튼
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_bottom_sheet, container, false);

        Button saveButton = view.findViewById(R.id.btn_save_record);
        EditText etReview = view.findViewById(R.id.et_final_record_review);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("bookId")) {
            Toast.makeText(getContext(), "Book ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
        String bookId = bundle.getString("bookId");

        stars = new ImageView[]{
                view.findViewById(R.id.iv_final_record_star1),
                view.findViewById(R.id.iv_final_record_star2),
                view.findViewById(R.id.iv_final_record_star3),
                view.findViewById(R.id.iv_final_record_star4),
                view.findViewById(R.id.iv_final_record_star5)
        };

        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> setStarRating(starIndex));
        }

        // 최종 저장 버튼
        saveButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              viewModel.setReview(etReview.getText().toString());
              viewModel.setRating(selectedStars);

              saveToFirestore();
              Bundle bundle1 = new Bundle();
              bundle1.putString("bookId",bookId);
              NavController navController = Navigation.findNavController(requireActivity(), R.id.main_frm);
              navController.navigate(R.id.action_recordRegistrationFragment_to_recordSpecificFragment,bundle1);
              dismiss();
          }
      });

        return view;
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

    private void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        // Arguments 체크
        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("bookId")) {
            Toast.makeText(getContext(), "Book ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String bookId = bundle.getString("bookId");

        // 데이터 준비
        Map<String, Object> bookData = new HashMap<>();
        bookData.put("rating", selectedStars);
        bookData.put("review", viewModel.getReview() != null ? viewModel.getReview().getValue() : "");
        bookData.put("readingStatus", "read_books");
        bookData.put("readingPage", viewModel.getReadPages() != null ? viewModel.getReadPages().getValue() : 0);

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("myTitle", viewModel.getRecordTitle() != null ? viewModel.getRecordTitle().getValue() : "");
        recordData.put("readingTime", viewModel.getReadingTime() != null ? viewModel.getReadingTime().getValue() : 0L);
        recordData.put("pagesRead", viewModel.getReadPages() != null ? viewModel.getReadPages().getValue() : 0);
        recordData.put("address", viewModel.getPlace() != null ? viewModel.getPlace().getValue() : "");
        recordData.put("phrase", viewModel.getLikePhrase() != null ? viewModel.getLikePhrase().getValue() : "");
        recordData.put("memo", viewModel.getMemo() != null ? viewModel.getMemo().getValue() : "");
        recordData.put("recordDate", new Date());

        // Firestore 업데이트
        db.collection("users")
                .document(userId)
                .collection("books")
                .document(bookId)
                .update(bookData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "책 데이터가 성공적으로 업데이트되었습니다!"))
                .addOnFailureListener(e -> Log.e("Firestore", "책 데이터 업데이트 실패", e));

        db.collection("users")
                .document(userId)
                .collection("books")
                .document(bookId)
                .collection("records")
                .add(recordData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "기록이 성공적으로 저장되었습니다!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "기록 저장 실패", e);
                });
    }
}

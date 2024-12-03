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
    private FirebaseFirestore db;
    private FirebaseAuth auth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }


    // 바텀시트 저장하기 버튼
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_bottom_sheet, container, false);

        Button saveButton = view.findViewById(R.id.btn_save_record);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("bookId")) {
            Toast.makeText(getContext(), "Book ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
        String bookId = bundle.getString("bookId");

        saveButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

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
}

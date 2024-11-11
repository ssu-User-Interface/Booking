package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.booking.R;

public class BookSearchDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_deatil, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        Button saveButton = view.findViewById(R.id.bt_book_search_detail_save);

        NavController navController = NavHostFragment.findNavController(this);

        backButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_bookSearchDetailFragment_to_bookSearchFragment);
        });

        saveButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_bookSearchDetailFragment_to_bookSearchSaveBottomSheetFragment);
        });

    }
}

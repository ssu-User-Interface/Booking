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

    private TextView tvTitle, tvAuthor, tvPublisher, tvDescription;
    private ImageView ivCover;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_deatil, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        BookSearchDetailFragmentArgs args = BookSearchDetailFragmentArgs.fromBundle(getArguments());
        bindDataToUI(args.getBookTitle(), args.getBookAuthor(), args.getBookPublisher(), args.getBookImage(), args.getBookDescription());

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

    private void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_book_search_detail_name);
        tvAuthor = view.findViewById(R.id.tv_book_search_detail_author);
        tvPublisher = view.findViewById(R.id.tv_book_search_detail_publisher);
        tvDescription = view.findViewById(R.id.tv_book_search_detail_description);
        ivCover = view.findViewById(R.id.iv_book_search_detail_cover);
    }

    private void bindDataToUI(String title, String author, String publisher, String imageUrl, String description) {
        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvPublisher.setText(publisher);
        tvDescription.setText(description);

        Glide.with(this)
                .load(imageUrl)
                .into(ivCover);
    }
}

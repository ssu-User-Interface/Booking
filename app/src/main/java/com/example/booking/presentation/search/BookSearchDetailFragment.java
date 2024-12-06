package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.booking.R;

public class BookSearchDetailFragment extends Fragment {

    private TextView tvTitle, tvAuthor, tvPublisher, tvDescription, tvPageCount;
    private ImageView ivCover;
    private BookSearchDetailViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_deatil, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BookSearchDetailViewModel.class);

        BookSearchDetailFragmentArgs args = BookSearchDetailFragmentArgs.fromBundle(getArguments());

        viewModel.setBookTitle(args.getBookTitle());
        viewModel.setBookAuthor(args.getBookAuthor());
        viewModel.setBookPublisher(args.getBookPublisher());
        viewModel.setBookImage(args.getBookImage());
        viewModel.setBookDescription(args.getBookDescription());
        viewModel.setPageCount(args.getPageCount());

        observeViewModel();

        NavController navController = NavHostFragment.findNavController(this);

        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        Button saveButton = view.findViewById(R.id.bt_book_search_detail_save);

        backButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_bookSearchDetailFragment_to_bookSearchFragment);
        });

        // 저장 버튼
        saveButton.setOnClickListener(v -> {
            BookSearchDetailFragmentDirections.ActionBookSearchDetailFragmentToBookSearchSaveBottomSheetFragment action =
                    BookSearchDetailFragmentDirections.actionBookSearchDetailFragmentToBookSearchSaveBottomSheetFragment(
                            viewModel.getBookTitle().getValue(),
                            viewModel.getBookAuthor().getValue(),
                            viewModel.getBookPublisher().getValue(),
                            viewModel.getBookImage().getValue(),
                            viewModel.getBookDescription().getValue(),
                            viewModel.getPageCount().getValue()
                    );
            navController.navigate(action);
        });
    }

    private void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_book_search_detail_name);
        tvAuthor = view.findViewById(R.id.tv_book_search_detail_author);
        tvPublisher = view.findViewById(R.id.tv_book_search_detail_publisher);
        tvPageCount = view.findViewById(R.id.tv_book_search_detail_page);
        tvDescription = view.findViewById(R.id.tv_book_search_detail_description);
        ivCover = view.findViewById(R.id.iv_book_search_detail_cover);
    }

    private void observeViewModel() {
        viewModel.getBookTitle().observe(getViewLifecycleOwner(), title -> tvTitle.setText(title));
        viewModel.getBookAuthor().observe(getViewLifecycleOwner(), author -> tvAuthor.setText(author));
        viewModel.getBookPublisher().observe(getViewLifecycleOwner(), publisher -> tvPublisher.setText(publisher));
        viewModel.getBookImage().observe(getViewLifecycleOwner(), image -> {
            if (image != null) {
                Glide.with(this).load(image).into(ivCover);
            }
        });
        viewModel.getBookDescription().observe(getViewLifecycleOwner(), description -> tvDescription.setText(description));
        viewModel.getPageCount().observe(getViewLifecycleOwner(), pageCount -> {
            if (pageCount != null && pageCount > 0) {
                tvPageCount.setText(String.valueOf(pageCount));
            } else {
                tvPageCount.setText("365");
            }
        });
    }
}

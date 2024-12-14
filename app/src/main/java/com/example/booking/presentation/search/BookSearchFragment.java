package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.dto.response.BookSearchResponseDto;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookSearchRVA adapter;
    private BookSearchViewModel viewModel;

    private EditText etBookSearch;
    private ImageView ivSearchButton;
    private Button filterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);

        viewModel = new ViewModelProvider(this).get(BookSearchViewModel.class);

        initView(view);
        initObserver();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rv_book_search);
        etBookSearch = view.findViewById(R.id.et_book_search);
        ivSearchButton = view.findViewById(R.id.iv_book_search_icon);
        filterButton = view.findViewById(R.id.bt_book_search_filter_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookSearchRVA(new ArrayList<>(), book -> openBookDetailFragment(requireView(), book));
        recyclerView.setAdapter(adapter);

        ivSearchButton.setOnClickListener(v -> performSearch());
        filterButton.setOnClickListener(v -> showSortMenu(v));
    }

    private void initObserver() {
        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            if (books != null && !books.isEmpty()) {
                adapter.updateBooks(books);
            } else {
                Toast.makeText(getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                adapter.updateBooks(new ArrayList<>());
            }
        });

        viewModel.getCurrentQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                etBookSearch.setText(query);
            }
        });

        viewModel.getCurrentSort().observe(getViewLifecycleOwner(), sort -> {
            if ("sim".equals(sort)) {
                filterButton.setText("정확도순");
            } else if ("date".equals(sort)) {
                filterButton.setText("출간일순");
            }
        });
    }

    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenu().add("정확도순").setOnMenuItemClickListener(item -> {
            filterButton.setText("정확도순");
            performSearch("sim");
            return true;
        });
        popupMenu.getMenu().add("출간일순").setOnMenuItemClickListener(item -> {
            filterButton.setText("출간일순");
            performSearch("date");
            return true;
        });
        popupMenu.show();
    }

    private void performSearch() {
        performSearch("sim");
    }

    private void performSearch(String sort) {
        String query = etBookSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.searchBooks(query, sort);
    }

    private void openBookDetailFragment(View view, BookSearchResponseDto.BookItemDto bookItem) {
        NavController navController = Navigation.findNavController(view);

        BookSearchFragmentDirections.ActionBookSearchFragmentToBookSearchDetailFragment action =
                BookSearchFragmentDirections.actionBookSearchFragmentToBookSearchDetailFragment(
                        bookItem.getTitle(),
                        bookItem.getAuthor(),
                        bookItem.getPublisher(),
                        bookItem.getImage(),
                        bookItem.getDescription(),
                        bookItem.getPageCount()
                );

        navController.navigate(action);
    }
}

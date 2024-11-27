package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookSearchRVA adapter;
    private BookSearchViewModel viewModel;

    private EditText etBookSearch;
    private ImageView ivSearchButton;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookSearchRVA(new ArrayList<>(), book -> openBookDetailFragment(requireView(), book));
        recyclerView.setAdapter(adapter);

        ivSearchButton.setOnClickListener(v -> performSearch());
    }

    private void initObserver() {
        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            if (books != null && !books.isEmpty()) {
                adapter.updateBooks(books);
            } else {
                Toast.makeText(getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        String query = etBookSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.searchBooks(query);
    }

    private void openBookDetailFragment(View view, BookSearchResponseDto.BookItemDto bookItem) {
        NavController navController = Navigation.findNavController(view);

        BookSearchFragmentDirections.ActionBookSearchFragmentToBookSearchDetailFragment action =
                BookSearchFragmentDirections.actionBookSearchFragmentToBookSearchDetailFragment(
                        bookItem.getTitle(),
                        bookItem.getAuthor(),
                        bookItem.getPublisher(),
                        bookItem.getImage(),
                        bookItem.getDescription()
                );

        navController.navigate(action);
    }
}

package com.example.booking.presentation.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.booking.R;
import com.example.booking.presentation.search.BookSearchFragment;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        EditText openBookSearchEditText = view.findViewById(R.id.et_home_search);
        openBookSearchEditText.setFocusable(false);
        openBookSearchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BookSearchFragment로 전환
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frm, new BookSearchFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
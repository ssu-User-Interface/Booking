package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.booking.R;

public class BookSearchDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search_deatil, container, false);

        TextView titleView = view.findViewById(R.id.tv_book_search_detail_name);
        TextView authorView = view.findViewById(R.id.tv_book_search_detail_author);
        TextView publisherView = view.findViewById(R.id.tv_book_search_detail_publisher);
        TextView pageView = view.findViewById(R.id.tv_book_search_detail_page);
        ImageView coverImageView = view.findViewById(R.id.iv_book_search_detail_cover);
        ImageView backButton = view.findViewById(R.id.iv_back_arrow);

        Bundle bundle = getArguments();
        if (bundle != null) {
            titleView.setText(bundle.getString("title"));
            authorView.setText(bundle.getString("author"));
            publisherView.setText(bundle.getString("publisher"));
            //pageView.setText(bundle.getString("year"));
            String imageUrl = bundle.getString("imageUrl");
            Glide.with(this).load(imageUrl).into(coverImageView);
        }

        backButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frm, new BookSearchFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}

package com.example.booking.presentation.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.example.booking.dto.response.BookSearchResponseDto;

import java.util.List;

public class BookSearchRVA extends RecyclerView.Adapter<BookSearchRVA.BookViewHolder> {

    private List<BookSearchResponseDto.BookItemDto> books;
    private final OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(BookSearchResponseDto.BookItemDto book);
    }

    public BookSearchRVA(List<BookSearchResponseDto.BookItemDto> books, OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    public void updateBooks(List<BookSearchResponseDto.BookItemDto> books) {
        this.books = books;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_search, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookSearchResponseDto.BookItemDto book = books.get(position);
        holder.bind(book, listener);
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final TextView publisher;
        private final TextView publishDate;
        private final ImageView thumbnail;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_item_book_search_book_name);
            author = itemView.findViewById(R.id.tv_item_book_search_book_author);
            publisher = itemView.findViewById(R.id.tv_item_book_search_book_publisher);
            publishDate = itemView.findViewById(R.id.tv_item_book_search_book_publication_year);
            thumbnail = itemView.findViewById(R.id.iv_item_book_search_book_cover);
        }

        public void bind(BookSearchResponseDto.BookItemDto book, OnBookClickListener listener) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            String publisherText = book.getPublisher();
            if (publisherText.length() > 15) {
                publisher.setText(publisherText.substring(0, 15) + "...");
            } else {
                publisher.setText(publisherText);
            }
            publishDate.setText(book.getPubdate());
            Glide.with(thumbnail.getContext()).load(book.getImage()).into(thumbnail);

            itemView.setOnClickListener(v -> listener.onBookClick(book));
        }
    }
}
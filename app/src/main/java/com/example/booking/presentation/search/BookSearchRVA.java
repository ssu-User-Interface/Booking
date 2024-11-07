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

import java.util.List;

public class BookSearchRVA extends RecyclerView.Adapter<BookSearchRVA.BookViewHolder> {

    private List<Book> bookList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookSearchRVA(List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_search, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book, listener);
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, bookAuthor, bookPublisher, bookYear;
        ImageView bookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.tv_item_book_search_book_name);
            bookAuthor = itemView.findViewById(R.id.tv_item_book_search_book_author);
            bookPublisher = itemView.findViewById(R.id.tv_item_book_search_book_publisher);
            bookYear = itemView.findViewById(R.id.tv_item_book_search_book_publication_year);
            bookCover = itemView.findViewById(R.id.iv_item_book_search_book_cover);
        }

        public void bind(Book book, OnItemClickListener listener) {
            bookName.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            bookPublisher.setText(book.getPublisher());
            //bookYear.setText(book.getYear());

            Glide.with(bookCover.getContext())
                    .load(book.getCoverImageUrl())
                    .into(bookCover);

            itemView.setOnClickListener(v -> listener.onItemClick(book));
        }
    }
}
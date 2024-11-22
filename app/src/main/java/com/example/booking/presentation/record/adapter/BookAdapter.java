package com.example.booking.presentation.record.adapter;

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

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<BookSearchResponseDto.BookItemDto> bookList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(BookSearchResponseDto.BookItemDto book);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public BookAdapter(List<BookSearchResponseDto.BookItemDto> bookList) {
        this.bookList = bookList;
    }

    public void updateBooks(List<BookSearchResponseDto.BookItemDto> newBooks) {
        bookList = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_record_list_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookSearchResponseDto.BookItemDto book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, authorTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_record_list_item_book);
            titleTextView = itemView.findViewById(R.id.tv_record_list_item_book_title);
            authorTextView = itemView.findViewById(R.id.tv_record_list_item_book_author);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(bookList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(BookSearchResponseDto.BookItemDto book) {
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());
            Glide.with(imageView.getContext()).load(book.getImage()).into(imageView);
        }
    }
}

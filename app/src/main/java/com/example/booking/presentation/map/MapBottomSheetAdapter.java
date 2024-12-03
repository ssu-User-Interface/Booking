package com.example.booking.presentation.map;

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

public class MapBottomSheetAdapter extends RecyclerView.Adapter<MapBottomSheetAdapter.BookViewHolder> {

    private List<BookItem> bookList;
    private OnItemClickListener listener;

    // 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(BookItem bookItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MapBottomSheetAdapter(List<BookItem> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_book_list, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookItem bookItem = bookList.get(position);
        holder.bind(bookItem);

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(bookItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // ViewHolder 클래스
    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle, bookAuthor, recordTitle, likePhrase, recordDate;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.iv_book_image);
            bookTitle = itemView.findViewById(R.id.tv_book_title);
            bookAuthor = itemView.findViewById(R.id.tv_book_author);
            recordTitle = itemView.findViewById(R.id.tv_item_map_book_record_title);
            likePhrase = itemView.findViewById(R.id.tv_like_phrase);
            recordDate = itemView.findViewById(R.id.tv_record_date);
        }

        public void bind(BookItem bookItem) {
            // 데이터 바인딩
            bookTitle.setText(bookItem.getTitle());
            bookAuthor.setText(bookItem.getAuthor());
            recordTitle.setText(bookItem.getRecordTitle());
            likePhrase.setText(bookItem.getLikePhrase());
            recordDate.setText(bookItem.getRecordDate());
            Glide.with(itemView.getContext()).load(bookItem.getImageUrl()).into(bookImage);
        }
    }
}

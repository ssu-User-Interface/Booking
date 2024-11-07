package com.example.booking.presentation.record.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.booking.R;
import com.example.booking.data.model.Record;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> recordList;

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_record_specific_list_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.titleTextView.setText(record.getTitle());
        holder.title2TextView.setText(record.getTitle2());
        holder.reviewTextView.setText(record.getReview());
        holder.dateTextView.setText(record.getDate());
        holder.recordCountTextView.setText("기록 " + record.getRecordCount() + "개");

        // Set stars based on rating
        for (int i = 0; i < holder.starImageViews.length; i++) {
            holder.starImageViews[i].setVisibility(i < record.getRating() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, title2TextView, reviewTextView, dateTextView, recordCountTextView;
        ImageView[] starImageViews;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_record_specific_item_title1);
            title2TextView = itemView.findViewById(R.id.tv_record_specific_item_title2);
            reviewTextView = itemView.findViewById(R.id.tv_record_specific_review);
            dateTextView = itemView.findViewById(R.id.tv_record_specific_date);
            recordCountTextView = itemView.findViewById(R.id.tv_record_specific_count);
            starImageViews = new ImageView[] {
                    itemView.findViewById(R.id.iv_record_star1),
                    itemView.findViewById(R.id.iv_record_star2),
                    itemView.findViewById(R.id.iv_record_star3),
                    itemView.findViewById(R.id.iv_record_star4),
                    itemView.findViewById(R.id.iv_record_star5)
            };
        }
    }
}

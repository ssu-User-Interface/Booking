package com.example.booking.presentation.record.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.titleTextView.setText(record.getTitle()); // String 값을 전달
        holder.myTitleTextView.setText(record.getMyTitle());
        holder.memoTextView.setText(record.getPhrase());
        holder.addressTextView.setText(record.getAddress());
        holder.dateTextView.setText(String.valueOf(record.getDate()));
        holder.TimeTextView.setText(String.valueOf(record.getTime()));

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, myTitleTextView, memoTextView, addressTextView, dateTextView, TimeTextView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_record_specific_item_title1);
            myTitleTextView = itemView.findViewById(R.id.tv_record_specific_item_myTitle);
            memoTextView = itemView.findViewById(R.id.tv_record_specific_item_memo);
            addressTextView = itemView.findViewById(R.id.tv_record_specific_item_address);
            dateTextView = itemView.findViewById(R.id.tv_record_specific_item_date);
            TimeTextView = itemView.findViewById(R.id.tv_record_specific_item_time);
        }
    }
}



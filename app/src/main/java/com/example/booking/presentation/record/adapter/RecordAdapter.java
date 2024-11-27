package com.example.booking.presentation.record.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.data.model.Record;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.myTitleTextView.setText(record.getMyTitle());
        holder.memoTextView.setText(record.getPhrase()); // 초기 내용은 phrase로 설정
        holder.addressTextView.setText(record.getAddress());

        // recordDate 표시
        Date recordDate = record.getRecordDate();
        if (recordDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / ", Locale.getDefault());
            holder.dateTextView.setText(dateFormat.format(recordDate));
        } else {
            holder.dateTextView.setText("날짜 없음");
        }

        // readingTime 표시
        Long readingTime = record.getReadingTime();
        if (readingTime != null) {
            holder.TimeTextView.setText(formatSecondsToTime(Math.toIntExact(readingTime)));
        } else {
            holder.TimeTextView.setText("시간 없음");
        }

        // memo visibility 설정
        if (record.getMemo() != null && !record.getMemo().isEmpty()) {
            holder.MemoIc.setVisibility(View.VISIBLE);
        } else {
            holder.MemoIc.setVisibility(View.GONE);
        }

        // 초기 MemoIc 배경 설정
        holder.MemoIc.setBackgroundResource(R.drawable.ic_record_specific_list_item_memo);

        // memo 클릭 이벤트 (토글 방식)
        holder.MemoIc.setOnClickListener(v -> {
            boolean isShowingMemo = holder.memoTextView.getText().toString().equals(record.getMemo());

            if (isShowingMemo) {
                // phrase로 변경
                holder.memoTextView.setText(record.getPhrase());
                holder.addressTextView.setVisibility(View.VISIBLE);
                holder.TimeTextView.setVisibility(View.VISIBLE);
                holder.dateTextView.setVisibility(View.VISIBLE);

                // MemoIc 배경 초기화
                holder.MemoIc.setBackgroundResource(R.drawable.ic_record_specific_list_item_memo);
                holder.MemoIc.setTextColor(Color.parseColor("#000000"));
            } else {
                // memo로 변경
                holder.memoTextView.setText(record.getMemo());
                holder.addressTextView.setVisibility(View.GONE);
                holder.TimeTextView.setVisibility(View.GONE);
                holder.dateTextView.setVisibility(View.GONE);

                // MemoIc 배경 변경
                holder.MemoIc.setBackgroundResource(R.drawable.ic_record_specific_list_item_memo_clicked);
                holder.MemoIc.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
    }


    // Helper: 초를 HH:mm:ss로 변환
    private String formatSecondsToTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }



    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, myTitleTextView, memoTextView, addressTextView, dateTextView, TimeTextView, MemoIc;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_record_specific_item_title1);
            myTitleTextView = itemView.findViewById(R.id.tv_record_specific_item_myTitle);
            memoTextView = itemView.findViewById(R.id.tv_record_specific_item_memo);
            addressTextView = itemView.findViewById(R.id.tv_record_specific_item_address);
            TimeTextView = itemView.findViewById(R.id.tv_record_specific_item_time);
            dateTextView = itemView.findViewById(R.id.tv_record_specific_item_date);
            MemoIc = itemView.findViewById(R.id.tv_record_specific_list_item_memo);
        }
    }
}

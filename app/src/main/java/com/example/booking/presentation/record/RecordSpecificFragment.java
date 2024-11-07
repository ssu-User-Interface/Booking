package com.example.booking.presentation.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.booking.R;
import com.example.booking.data.model.Record;
import com.example.booking.presentation.record.adapter.RecordAdapter;
import java.util.ArrayList;
import java.util.List;

public class RecordSpecificFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private List<Record> recordList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_specific, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_record_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recordList = new ArrayList<>();
        recordList.add(new Record("책 제목", "저자", "오늘의 기분을 좋게 해 주는 현실적인 책", "2024.10.20 ~ 2024.11.15", 5, 5));
        recordList.add(new Record("또 다른 책", "작가", "감동을 주는 이야기", "2024.09.10 ~ 2024.09.25", 3, 4));

        recordAdapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(recordAdapter);

        return view;
    }
}

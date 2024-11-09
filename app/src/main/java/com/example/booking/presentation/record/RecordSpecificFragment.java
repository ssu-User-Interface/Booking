package com.example.booking.presentation.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_specific, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_record_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //NavCotroller 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 초기화 및 클릭 이벤트 설정
        Button startButton = view.findViewById(R.id.btn_specific_record_start_timer);
        startButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordSpecificFragment_to_timerFragment);
        });

        // 버튼 초기화 및 클릭 이벤트 설정
        ImageView backButton = view.findViewById(R.id.iv_record_specific_back_arrow);
        backButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordSpecificFragment_to_recordFragment);
        });

        Bundle args = getArguments();
        if (args != null) {
            //int bookImageResId = args.getInt("bookImageResId");
            String bookTitle = args.getString("bookTitle");
            String bookAuthor = args.getString("bookAuthor");

            // 데이터로 UI 업데이트
            //ImageView imageView = view.findViewById(R.id.iv_record_specific_book);
            TextView titleTextView = view.findViewById(R.id.tv_record_specific_book_title);
            TextView authorTextView = view.findViewById(R.id.tv_record_main_book_author);

            //imageView.setImageResource(bookImageResId);
            titleTextView.setText(bookTitle);
            authorTextView.setText(bookAuthor);
        }

        recordList = new ArrayList<>();
        recordList.add(new Record("첫 번째 기록", "나의 모순", "인생이란 때때로 우리로 하여금 기꺼이 악을 선택하게 만들고 우리는 어쩔 수 없이 그 모순과 손잡으며 살아가야 한다는 사실을 정말 조금도 눈치채지 못하고 있는 것일까.", "상도동 123-45", 20241024, 30));
        recordList.add(new Record("두 번째 기록", "너의 모순", "감동을 주는 이야기", "금릉동 234-90", 20241102, 4));

        recordAdapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(recordAdapter);

        return view;
    }

}

package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.example.booking.presentation.mypage.MyViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecordRegistrationFragment extends Fragment {
    private RecordRegistrationViewModel viewModel;
    private Long totalPage = 0L; // Firestore에서 가져올 totalPage

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_registration, container, false);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        viewModel = new ViewModelProvider(requireActivity()).get(RecordRegistrationViewModel.class);

        // 전달받은 타이머 값 설정
        EditText etRecordTitle = view.findViewById(R.id.et_record_book_title);
        EditText etReadPages = view.findViewById(R.id.et_record_page);
        EditText etRecordTime = view.findViewById(R.id.et_record_time);
        TextView tvRecordRegistrationPlaceText = view.findViewById(R.id.tv_record_place);
        ImageView backArrow = view.findViewById(R.id.iv_back_arrow);
        EditText etLikePhrase = view.findViewById(R.id.et_record_like_phrase);
        EditText etMemo = view.findViewById(R.id.et_record_memo);
        TextView tvRecordDate = view.findViewById(R.id.tv_record_when);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        tvRecordDate.setText(formattedDate);

        Bundle bundle = getArguments();
        long elapsedTimeInMillis = 0;

        if (bundle != null) {
            Log.d("RecordRegistrationFragment", "Received Bundle Data:");
            String source = bundle.getString("source", ""); // source 확인 (timer/mapsearch)
            Log.d("sourceSource",source);
            if(source.equals("timer")){
                // bookId 처리
                String bookId = bundle.getString("bookId");
                loadBookDetails(bookId);
                // elapsedTime 처리
                elapsedTimeInMillis = bundle.getLong("elapsedTime", 0L);
                long finalElapsedTimeInMillis = elapsedTimeInMillis;

                // 시간을 hh:mm:ss 형식으로 변환하여 EditText에 표시
                int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
                int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
                int seconds = (int) (elapsedTimeInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                etRecordTime.setText(timeFormatted);

                // 장소 검색
                tvRecordRegistrationPlaceText.setOnClickListener(v -> {
                    String recordTitle = etRecordTitle.getText().toString();
                    String readPagesText = etReadPages.getText().toString();
                    int readPages = readPagesText.isEmpty() ? 0 : Integer.parseInt(readPagesText); // 빈값 처리
                    Bundle Bundle_all = new Bundle();
                    Bundle_all.putLong("elapsedTime", finalElapsedTimeInMillis);
                    Bundle_all.putString("bookId", bookId);
                    Bundle_all.putString("recordTitle",recordTitle);
                    Bundle_all.putInt("readPages",readPages);
                    navController.navigate(R.id.action_recordRegistrationFragment_to_recordRegistrationMapSearchFragment,Bundle_all);
                });

                // 뒤로가기 이미지
                backArrow.setOnClickListener(v -> {
                    // 현재 데이터를 Bundle에 저장하여 다시 TimerFragment로 전달
                    Bundle backBundle = new Bundle();
                    backBundle.putLong("elapsedTime", finalElapsedTimeInMillis);
                    backBundle.putString("bookId",bookId);
                    navController.navigate(R.id.action_recordRegistrationFragment_to_timerFragment, backBundle);
                });

            } else if(source.equals("mapsearch")){
                // bookId 처리
                String bookId = bundle.getString("bookId");
                loadBookDetails(bookId);

                // elapsedTime 처리
                elapsedTimeInMillis = bundle.getLong("elapsedTime", 0L);

                // 장소 정보 처리
                String placeName = bundle.getString("placeName", "선택된 장소 없음");
                String placeAddress = bundle.getString("placeAddress","선택된 주소 없음");
                String recordTitle = bundle.getString("recordTitle");
                Integer readPages = bundle.getInt("readPages");

                // 시간을 hh:mm:ss 형식으로 변환하여 EditText에 표시
                int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
                int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
                int seconds = (int) (elapsedTimeInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                Log.d("RecordRegistrationFragment", "Received elapsedTimeInMillis from mapsearch: " + elapsedTimeInMillis);

                etRecordTitle.setText(recordTitle);
                etReadPages.setText(String.valueOf(readPages));
                etRecordTime.setText(timeFormatted);
                tvRecordRegistrationPlaceText.setText(placeName);

                // 번들에 placeAddress 저장
                bundle.putString("placeAddress",placeAddress);
            }
        }
        Button btnCompleteReadingActive = view.findViewById(R.id.btn_complete_reading_activate);
        Button btnCompleteReadingDeactivate = view.findViewById(R.id.btn_complete_reading_deactivate);


        // 기본적으로 활성화 버튼 숨김, 비활성화 버튼 표시
        btnCompleteReadingActive.setVisibility(View.GONE);
        btnCompleteReadingDeactivate.setVisibility(View.VISIBLE);

        // EditText의 값이 변경될 때 리스너 설정
        etReadPages.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String enteredPagesText = etReadPages.getText().toString();
                int enteredPages = enteredPagesText.isEmpty() ? 0 : Integer.parseInt(enteredPagesText);

                // 읽은 페이지 값과 전체 페이지 값 비교
                if (enteredPages == totalPage) {
                    btnCompleteReadingActive.setVisibility(View.VISIBLE); // 활성화 버튼 표시
                    btnCompleteReadingDeactivate.setVisibility(View.GONE); // 비활성화 버튼 숨김
                } else {
                    btnCompleteReadingActive.setVisibility(View.GONE); // 활성화 버튼 숨김
                    btnCompleteReadingDeactivate.setVisibility(View.VISIBLE); // 비활성화 버튼 표시
                }
            }
        });

        elapsedTimeInMillis = bundle.getLong("elapsedTime", 0L);
        long finalElapsedTimeInMillis = elapsedTimeInMillis;

        // 독서 종료 버튼
        btnCompleteReadingActive.setOnClickListener(v -> {

            String elapsedTime = etRecordTime.getText().toString();
            long elapsedTimeInMillisfinal = 0L;
            try {
                String[] timeParts = elapsedTime.split(":"); // hh:mm:ss를 ":" 기준으로 분리
                int hours = Integer.parseInt(timeParts[0]); // 시간 부분
                int minutes = Integer.parseInt(timeParts[1]); // 분 부분
                int seconds = Integer.parseInt(timeParts[2]); // 초 부분

                // 시간, 분, 초를 밀리초로 변환
                elapsedTimeInMillisfinal = (hours * 3600 + minutes * 60 + seconds) * 1000L;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Log.e("ElapsedTimeConversion", "시간 변환 실패: " + e.getMessage());
            }

            viewModel.setRecordTitle(etRecordTitle.getText().toString());
            viewModel.setReadingTime(elapsedTimeInMillisfinal);
            viewModel.setReadPages(Integer.parseInt(etReadPages.getText().toString()));
            viewModel.setPlace(tvRecordRegistrationPlaceText.getText().toString());
            viewModel.setLikePhrase(etLikePhrase.getText().toString());
            viewModel.setMemo(etMemo.getText().toString());


            String bookId = bundle!=null ? bundle.getString("bookId") : null;
            if (bookId == null) {
                Toast.makeText(getContext(), "Book ID를 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            RecordRegistrationBottomSheetDialogFragment bottomSheetDialogFragment = new RecordRegistrationBottomSheetDialogFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString("bookId",bookId);
            bottomSheetDialogFragment.setArguments(bundle2);
            bottomSheetDialogFragment.show(getParentFragmentManager(), "RecordRegistrationBottomSheetDialogFragment");
        });

        // 기록 저장 버튼
        Button saveRecordButton = view.findViewById(R.id.btn_save_record);
        saveRecordButton.setOnClickListener(v -> {
            // Firestore 참조 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser().getUid();
            String bookId = bundle != null ? bundle.getString("bookId") : null;
            if (bookId == null) {
                Toast.makeText(getContext(), "Book ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 사용자 입력값 가져오기
            String recordTitle = etRecordTitle.getText().toString();
            String readPagesText = etReadPages.getText().toString();
            int readPages = readPagesText.isEmpty() ? 0 : Integer.parseInt(readPagesText); // 빈값 처리
            String elapsedTime = etRecordTime.getText().toString();
            long elapsedTimeInMillisfinal = 0L;
            try {
                String[] timeParts = elapsedTime.split(":"); // hh:mm:ss를 ":" 기준으로 분리
                int hours = Integer.parseInt(timeParts[0]); // 시간 부분
                int minutes = Integer.parseInt(timeParts[1]); // 분 부분
                int seconds = Integer.parseInt(timeParts[2]); // 초 부분

                // 시간, 분, 초를 밀리초로 변환
                elapsedTimeInMillisfinal = (hours * 3600 + minutes * 60 + seconds) * 1000L;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Log.e("ElapsedTimeConversion", "시간 변환 실패: " + e.getMessage());
            }
            String placeName = tvRecordRegistrationPlaceText.getText().toString();
            String likePhrase = etLikePhrase.getText().toString();
            String memo = etMemo.getText().toString();
            String placeAddress = bundle != null ? bundle.getString("placeAddress", "선택된 주소 없음") : "선택된 주소 없음";
            Double selectedLatitude = bundle !=null ? bundle.getDouble("latitude") : 0.0 ;
            Double selectedLongitude = bundle !=null ? bundle.getDouble("longitude") : 0.0;

            // 기본 유효성 검사
            if (recordTitle.isEmpty() || readPages <= 0 || elapsedTime.isEmpty() || placeName.equals("선택된 장소 없음")) {
                Toast.makeText(getContext(), "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore에 저장할 데이터 생성
            Map<String, Object> records = new HashMap<>();
            records.put("myTitle", recordTitle);
            records.put("readingTime", elapsedTimeInMillisfinal);
            records.put("address", placeName);
            records.put("phrase", likePhrase);
            records.put("memo", memo);
            records.put("recordDate", new Date()); // 기록 생성 시간 추가
            records.put("placeAddress",placeAddress);

            // 위도 경도 보내기
            records.put("latitude",selectedLatitude);
            records.put("longitude",selectedLongitude);

            // Firestore 경로 설정
            db.collection("users")
                    .document(userId)
                    .collection("books")
                    .document(bookId)
                    .collection("records")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        // 첫 기록인지 확인
                        if (querySnapshot.isEmpty()) {
                            // 첫 기록인 경우 books 문서에 startDate 저장
                            Map<String, Object> bookUpdate = new HashMap<>();
                            bookUpdate.put("startDate", new Date());

                            db.collection("users")
                                    .document(userId)
                                    .collection("books")
                                    .document(bookId)
                                    .update(bookUpdate)
                                    .addOnSuccessListener(aVoid -> Log.d("RecordRegistration", "startDate가 books 문서에 추가되었습니다."))
                                    .addOnFailureListener(e -> Log.e("RecordRegistration", "startDate 추가 실패", e));
                        }

                        // 기록 추가
                        db.collection("users")
                                .document(userId)
                                .collection("books")
                                .document(bookId)
                                .collection("records")
                                .add(records) // 데이터 추가
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                                    // readingPage를 books 컬렉션의 bookId에 업데이트
                                    db.collection("users")
                                            .document(userId)
                                            .collection("books")
                                            .document(bookId)
                                            .update("readingPage", readPages) // books에 readingPage 업데이트
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("RecordRegistration", "readingPage가 books에 성공적으로 저장되었습니다.");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("RecordRegistration", "readingPage 업데이트 실패", e);
                                            });

                                    db.collection("users")
                                            .document(userId)
                                            .collection("books")
                                            .document(bookId)
                                            .get()
                                            .addOnSuccessListener(snapshot -> {
                                                if (snapshot.exists()) {
                                                    String currentStatus = snapshot.getString("readingStatus");
                                                    if ("will_read_books".equals(currentStatus)) {
                                                        // 상태를 reading_books로 업데이트
                                                        db.collection("users")
                                                                .document(userId)
                                                                .collection("books")
                                                                .document(bookId)
                                                                .update("readingStatus", "reading_books")
                                                                .addOnSuccessListener(aVoid -> Log.d("RecordRegistration", "책 상태가 reading_books로 업데이트되었습니다."))
                                                                .addOnFailureListener(e -> Log.e("RecordRegistration", "책 상태 업데이트 실패", e));
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("RecordRegistration", "책 상태 확인 실패", e));

                                    navController.navigate(R.id.action_recordRegistrationFragment_to_recordSpecificFragment, bundle); // 저장 후 다른 화면으로 이동
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("RecordRegistration", "기록 저장 실패", e);
                                    Toast.makeText(getContext(), "기록 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("RecordRegistration", "레코드 확인 실패", e);
                        Toast.makeText(getContext(), "레코드 확인 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    });
        });
        return view;
    }

    private void loadBookDetails(String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference bookRef = db.collection("users").document(userId)
                .collection("books").document(bookId);

        // Fetch book details
        bookRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String title = snapshot.getString("title");
                String image = snapshot.getString("image");
                Long fetchedTotalPage = snapshot.getLong("totalPage");

                totalPage = fetchedTotalPage;

                // Update UI with book details
                updateBookDetailsUI(title, image,totalPage);
            } else {
                Log.e("TimerFragment", "Book not found for bookId: " + bookId);
            }
        }).addOnFailureListener(e -> Log.e("TimerFragment", "Failed to load book details", e));
    }

    private void updateBookDetailsUI(String title, String image, Long totalPage) {
        TextView bookTitleTextView = getView().findViewById(R.id.tv_record_registration_book_title);
        ImageView bookImageView = getView().findViewById(R.id.iv_record_registration_book);
        TextView bookTotalPage = getView().findViewById(R.id.tv_record_registration_total_page);

        bookTitleTextView.setText(title != null ? title : "제목 없음");
        bookTotalPage.setText(totalPage!=null ? String.valueOf(totalPage):"에러");

        if (image != null) {
            Glide.with(this).load(image).into(bookImageView);
        }
    }
}


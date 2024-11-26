package com.example.booking.presentation.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyProfileChangeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView profileIconView;
    private EditText nicknameEditText;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile_change, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 추가된 부분 1: Firebase 초기화
        // 설명: Firebase Auth, Firestore, Storage 인스턴스 초기화 및 사용자 ID 가져오기.
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        NavController navController = NavHostFragment.findNavController(this);

        // View 초기화
        profileImageView = view.findViewById(R.id.iv_my_page_profile_change_profile);
        profileIconView = view.findViewById(R.id.iv_my_page_profile_change_profile_icon);
        nicknameEditText = view.findViewById(R.id.et_my_page_nick_name_change);

        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        ImageView saveButton = view.findViewById(R.id.iv_my_page_nick_name_change_arrow);

        // 뒤로가기 버튼
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_myProfileChangeFragment_to_myFragment));

        // 프로필 이미지 클릭 -> 갤러리 열기
        profileImageView.setOnClickListener(v -> openGallery());

        // 추가된 부분 2: Firestore에서 사용자 정보 로드
        // 설명: Firestore에서 닉네임과 기존 프로필 이미지 로드.
        loadUserProfile();

        // 저장 버튼 클릭 이벤트 설정
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData(); // 선택한 이미지의 URI 저장
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                profileIconView.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserProfile() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 추가된 부분 3: 닉네임 및 프로필 이미지 설정
                        // 설명: Firestore에서 닉네임 및 프로필 이미지를 가져와 UI에 설정.
                        String nickname = documentSnapshot.getString("nickname");
                        String profileImageUrl = documentSnapshot.getString("profileImage");

                        if (nickname != null) {
                            nicknameEditText.setText(nickname);
                        }
                        if (profileImageUrl != null) {
                            Glide.with(this).load(profileImageUrl).into(profileImageView);
                            profileIconView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void saveProfileChanges() {
        String newNickname = nicknameEditText.getText().toString().trim();

        if (imageUri != null) {
            // 추가된 부분 4: 선택한 이미지를 Firebase Storage에 업로드
            // 설명: 선택된 이미지를 Storage에 업로드한 후 다운로드 URL을 Firestore에 저장.
            StorageReference profileImageRef = storage.getReference().child("users/" + userId + "/profileImage.jpg");
            Log.d("StoragePath", "Storage Path: " + profileImageRef.getPath());

            // 이미지 업로드
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 업로드 성공 시 다운로드 URL 가져오기
                        profileImageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    Log.d("FirebaseStorage", "Download URL: " + imageUrl);

                                    // Firestore에 다운로드 URL 저장
                                    saveToFirestore(newNickname, imageUrl);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseStorage", "다운로드 URL 가져오기 실패: " + e.getMessage());
                                    Toast.makeText(getContext(), "프로필 이미지 URL을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "이미지 업로드 실패: " + e.getMessage());
                        Toast.makeText(getContext(), "프로필 이미지를 업로드하지 못했습니다.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // 이미지가 선택되지 않은 경우 닉네임만 저장
            saveToFirestore(newNickname, null);
        }

    }

    private void saveToFirestore(String newNickname, @Nullable String imageUrl) {
        // 추가된 부분 5: Firestore에 닉네임 및 프로필 이미지 저장
        // 설명: 닉네임과 프로필 이미지를 Firestore에 업데이트.
        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", newNickname);
        if (imageUrl != null) {
            updates.put("profileImage", imageUrl);
        }

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "프로필 정보가 Firestore에 성공적으로 저장되었습니다.");
                    Toast.makeText(getContext(), "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Firestore 업데이트 실패: " + e.getMessage());
                    Toast.makeText(getContext(), "프로필 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

}

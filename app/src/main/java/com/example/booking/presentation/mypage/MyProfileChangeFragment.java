package com.example.booking.presentation.mypage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfileChangeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView profileIconView;
    private EditText nicknameEditText;
    private Uri imageUri;

    private MyProfileChangeViewModel profileChangeViewModel;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile_change, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        profileChangeViewModel = new ViewModelProvider(requireActivity()).get(MyProfileChangeViewModel.class);

        profileImageView = view.findViewById(R.id.iv_my_page_profile_change_profile);
        profileIconView = view.findViewById(R.id.iv_my_page_profile_change_profile_icon);
        nicknameEditText = view.findViewById(R.id.et_my_page_nick_name_change);

        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        ImageView saveButton = view.findViewById(R.id.iv_my_page_nick_name_change_arrow);

        backButton.setOnClickListener(v -> {
            passDataToMyFragment();
        });

        profileImageView.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveProfileChanges());

        observeViewModel();

        loadInitialData();
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
            imageUri = data.getData();
            profileChangeViewModel.setProfileImageUri(imageUri);

            Glide.with(this).load(imageUri).into(profileImageView);
            profileIconView.setVisibility(View.GONE);
        }
    }

    private void saveProfileChanges() {
        String newNickname = nicknameEditText.getText().toString().trim();

        if (newNickname.isEmpty()) {
            Toast.makeText(getContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        profileChangeViewModel.saveNicknameToFirestore(userId, newNickname, new MyProfileChangeViewModel.SaveCallback() {
            @Override
            public void onSuccess() {
                profileChangeViewModel.setNickname(newNickname); // 닉네임을 뷰모델에 저장
                passDataToMyFragment();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "프로필 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void passDataToMyFragment() {
        Bundle args = new Bundle();

        args.putString("nickname", profileChangeViewModel.getNickname().getValue());

        if (imageUri != null) {
            args.putString("profileImageUri", imageUri.toString());
        } else if (profileChangeViewModel.getProfileImageUri().getValue() != null) {
            args.putString("profileImageUri", profileChangeViewModel.getProfileImageUri().getValue().toString());
        }

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_myProfileChangeFragment_to_myFragment, args);
    }

    private void observeViewModel() {
        profileChangeViewModel.getProfileImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this).load(uri).into(profileImageView);
                profileIconView.setVisibility(View.GONE);
            } else {
                profileImageView.setImageResource(R.drawable.img_my_default);
            }
        });

        profileChangeViewModel.getNickname().observe(getViewLifecycleOwner(), nickname -> {
            if (nickname != null) {
                nicknameEditText.setText(nickname);
            }
        });
    }

    private void loadInitialData() {
        profileChangeViewModel.loadNicknameFromFirestore(userId, new MyProfileChangeViewModel.DataLoadCallback() {
            @Override
            public void onDataLoaded(String nickname) {
                profileChangeViewModel.setNickname(nickname);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

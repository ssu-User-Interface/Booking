package com.example.booking.presentation.mypage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.example.booking.presentation.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MyFragment extends Fragment {

    private ImageView profileImageView;
    private ImageView profileIconView;
    private TextView nicknameTextView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImageView = view.findViewById(R.id.iv_my_page_profile);
        profileIconView = view.findViewById(R.id.iv_my_page_profile_icon);
        nicknameTextView = view.findViewById(R.id.iv_my_page_name);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        loadUserProfile();

        NavController navController = Navigation.findNavController(view);

        ImageView profileChangeButton = view.findViewById(R.id.iv_my_page_profile_change);
        profileChangeButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myFragment_to_myProfileChangeFragment)
        );

        ImageView personalInfoButton = view.findViewById(R.id.iv_my_page_personal_info);
        personalInfoButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myFragment_to_myPersonalInfoFragment)
        );

        ImageView logOutButton = view.findViewById(R.id.iv_my_page_logout);
        logOutButton.setOnClickListener(v -> {
            // FirebaseAuth를 사용하여 로그아웃 처리
            FirebaseAuth.getInstance().signOut();

            // LoginActivity로 이동
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // 현재 Activity 종료
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
    private void loadUserProfile() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        String profileImageUrl = documentSnapshot.getString("profileImage");

                        if (nickname != null) {
                            nicknameTextView.setText(nickname);
                        }
                        if (profileImageUrl != null) {
                            Glide.with(this).load(profileImageUrl).into(profileImageView);
                            profileIconView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
    }
}

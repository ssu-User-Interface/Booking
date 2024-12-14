package com.example.booking.presentation.mypage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.example.booking.presentation.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyFragment extends Fragment {

    private ImageView profileImageView;
    private ImageView profileIconView;
    private TextView nicknameTextView;

    private String userId;
    private MyViewModel myViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.iv_my_page_profile);
        profileIconView = view.findViewById(R.id.iv_my_page_profile_icon);
        nicknameTextView = view.findViewById(R.id.iv_my_page_name);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            myViewModel.loadUserProfile(userId);
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        observeViewModel();

        updateProfileImageFromArguments();

        NavController navController = Navigation.findNavController(view);

        ImageView profileChangeButton = view.findViewById(R.id.iv_my_page_profile_change);
        profileChangeButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myFragment_to_myProfileChangeFragment)
        );

        ImageView personalInfoButton = view.findViewById(R.id.iv_my_page_personal_info);
        personalInfoButton.setOnClickListener(v->
                navController.navigate(R.id.action_myFragment_to_myPersonalInfoFragment));

        Button deleteAccountButton = view.findViewById(R.id.bt_my_page_account_del);
        deleteAccountButton.setOnClickListener(v -> deleteAccount());

        ImageView logOutButton = view.findViewById(R.id.iv_my_page_logout);
        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void updateProfileImageFromArguments() {
        if (getArguments() != null) {
            String profileImageUri = getArguments().getString("profileImageUri");
            if (profileImageUri != null) {
                myViewModel.setProfileImageUri(Uri.parse(profileImageUri));
            }
        }
    }

    private void deleteAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDoc = db.collection("users").document(uid);

            userDoc.delete()
                    .addOnSuccessListener(aVoid -> {
                        user.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "계정 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Firestore 데이터 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void observeViewModel() {
        myViewModel.getProfileImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this).load(uri).into(profileImageView);
                profileIconView.setVisibility(View.GONE);
            } else {
                profileImageView.setImageResource(R.drawable.img_my_default);
            }
        });

        myViewModel.getNickname().observe(getViewLifecycleOwner(), nickname -> {
            if (nickname != null) {
                nicknameTextView.setText(nickname);
            }
        });
    }
}

package com.example.booking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booking.MainActivity;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignupFragment extends Fragment {

    private FirebaseAuth mAuth; // Firebase 인증 객체
    private static final String TAG = "SignupFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        EditText emailInput = view.findViewById(R.id.et_signup_email);
        EditText passwordInput = view.findViewById(R.id.et_signup_pw);
        TextView signUpBtn = view.findViewById(R.id.tv_signup_signup);

        // 회원가입 버튼 클릭 리스너
        signUpBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase로 새 사용자 생성
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(requireContext(), "Signup successful!", Toast.LENGTH_SHORT).show();

                            // 회원가입 후 다른 화면으로 이동 (예: 로그인 화면 또는 메인 화면)
                            // Navigate to HomeFragment using NavController
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // 회원가입 실패
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Signup failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}

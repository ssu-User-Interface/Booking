package com.example.booking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booking.MainActivity;
import com.example.booking.R;
import com.example.booking.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth; // Firebase 인증 객체
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseAuth 객체 초기화
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id)) // Firebase에 등록된 웹 클라이언트 ID
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Google Sign-In 버튼 클릭 리스너
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        // 이메일 로그인 버튼 클릭 리스너
        binding.tvLoginLogin.setOnClickListener(v -> emailSignIn());

        // 회원가입 버튼 클릭 리스너
        binding.tvLoginSignup.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new SignupFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 이미 로그인된 사용자 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // 사용자가 이미 로그인되어 있으면 MainActivity로 이동
            updateUI(currentUser);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Google Sign-In 성공
            Toast.makeText(LoginActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();

            // Firebase로 인증
            firebaseAuthWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            // Sign in 실패
            int statusCode = e.getStatusCode();
            Log.e(TAG, "Google Sign-In failed. Status Code: " + statusCode);
            Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + statusCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                            updateUI(user);
                        }
                    } else {
                        Log.w(TAG, "Google sign in failed", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void emailSignIn() {
        String email = binding.etLoginEmail.getText().toString().trim();
        String password = binding.etLoginPw.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserToFirestore(user); // Firestore에 사용자 정보 저장
                        updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        String email = user.getEmail();
        String nickname = email != null ? email.split("@")[0] : "Default Nickname";

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("nickname", nickname);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "사용자 정보 저장 성공"))
                .addOnFailureListener(e -> Log.e(TAG, "사용자 정보 저장 실패", e));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // LoginActivity 종료
        } else {
            Log.w(TAG, "No user signed in");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

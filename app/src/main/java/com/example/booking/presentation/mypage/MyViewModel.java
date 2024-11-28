package com.example.booking.presentation.mypage;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyViewModel extends ViewModel {
    private final MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();
    private final MutableLiveData<String> nickname = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<Uri> getProfileImageUri() {
        return profileImageUri;
    }

    public LiveData<String> getNickname() {
        return nickname;
    }

    public void setProfileImageUri(Uri uri) {
        profileImageUri.setValue(uri);
    }

    public void setNickname(String newNickname) {
        nickname.setValue(newNickname);
    }

    public void loadUserProfile(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nicknameValue = documentSnapshot.getString("nickname");
                        if (nicknameValue != null) {
                            nickname.setValue(nicknameValue);
                        }
                    }
                })
                .addOnFailureListener(e -> nickname.setValue("Default Nickname"));
    }
}

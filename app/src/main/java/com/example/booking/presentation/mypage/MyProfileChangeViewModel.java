package com.example.booking.presentation.mypage;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileChangeViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();
    private final MutableLiveData<String> nickname = new MutableLiveData<>();

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

    public void saveNicknameToFirestore(String userId, String newNickname, SaveCallback callback) {
        db.collection("users").document(userId).update("nickname", newNickname)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void loadNicknameFromFirestore(String userId, DataLoadCallback callback) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        if (nickname != null) {
                            callback.onDataLoaded(nickname);
                        } else {
                            callback.onDataLoaded("");
                        }
                    } else {
                        callback.onFailure(new Exception("Document does not exist"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface SaveCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface DataLoadCallback {
        void onDataLoaded(String nickname);

        void onFailure(Exception e);
    }
}

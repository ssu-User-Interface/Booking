package com.example.booking.presentation.registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecordRegistrationViewModel extends ViewModel {
    private final MutableLiveData<String> recordTitle = new MutableLiveData<>();
    private final MutableLiveData<Long> readingTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> readPages = new MutableLiveData<>();
    private final MutableLiveData<String> place = new MutableLiveData<>();
    private final MutableLiveData<String> likePhrase = new MutableLiveData<>();
    private final MutableLiveData<String> memo = new MutableLiveData<>();
    private final MutableLiveData<String> review = new MutableLiveData<>();
    private final MutableLiveData<Integer> rating = new MutableLiveData<>();

    // Getter and Setter methods
    public LiveData<String> getRecordTitle() {
        return recordTitle;
    }

    public void setRecordTitle(String title) {
        recordTitle.setValue(title);
    }

    public LiveData<Long> getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(Long time) {
        readingTime.setValue(time);
    }

    public LiveData<Integer> getReadPages() {
        return readPages;
    }

    public void setReadPages(Integer pages) {
        readPages.setValue(pages);
    }

    public LiveData<String> getPlace() {
        return place;
    }

    public void setPlace(String placeName) {
        place.setValue(placeName);
    }

    public LiveData<String> getLikePhrase() {
        return likePhrase;
    }

    public void setLikePhrase(String phrase) {
        likePhrase.setValue(phrase);
    }

    public LiveData<String> getMemo() {
        return memo;
    }

    public void setMemo(String memoContent) {
        memo.setValue(memoContent);
    }

    public LiveData<String> getReview() {
        return review;
    }

    public void setReview(String userReview) {
        review.setValue(userReview);
    }

    public LiveData<Integer> getRating() {
        return rating;
    }

    public void setRating(Integer userRating) {
        rating.setValue(userRating);
    }

}

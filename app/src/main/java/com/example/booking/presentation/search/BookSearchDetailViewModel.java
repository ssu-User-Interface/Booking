package com.example.booking.presentation.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookSearchDetailViewModel extends ViewModel {
    private final MutableLiveData<String> bookTitle = new MutableLiveData<>("");
    private final MutableLiveData<String> bookAuthor = new MutableLiveData<>("");
    private final MutableLiveData<String> bookPublisher = new MutableLiveData<>("");
    private final MutableLiveData<String> bookImage = new MutableLiveData<>("");
    private final MutableLiveData<String> bookDescription = new MutableLiveData<>("");

    public LiveData<String> getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String title) {
        bookTitle.setValue(title);
    }

    public LiveData<String> getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String author) {
        bookAuthor.setValue(author);
    }

    public LiveData<String> getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String publisher) {
        bookPublisher.setValue(publisher);
    }

    public LiveData<String> getBookImage() {
        return bookImage;
    }

    public void setBookImage(String imageUrl) {
        bookImage.setValue(imageUrl);
    }

    public LiveData<String> getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String description) {
        bookDescription.setValue(description);
    }
}

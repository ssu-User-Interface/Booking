package com.example.booking.presentation.record;

import androidx.lifecycle.ViewModel;

public class RecordViewModel extends ViewModel {
    private String currentCategory = "will_read_books";

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }
}

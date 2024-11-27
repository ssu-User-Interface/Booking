package com.example.booking.presentation.registration;

import androidx.lifecycle.ViewModel;

public class RecordRegistrationViewModel extends ViewModel {

    private long elapsedTimeInMillis = 0;

    public long getElapsedTimeInMillis() {
        return elapsedTimeInMillis;
    }

    public void setElapsedTimeInMillis(long elapsedTimeInMillis) {
        this.elapsedTimeInMillis = elapsedTimeInMillis;
    }
}

package com.example.booking.presentation.registration;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // 필요하지 않으면 비워둡니다.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 필요하지 않으면 비워둡니다.
    }

    @Override
    public abstract void afterTextChanged(Editable s);
}

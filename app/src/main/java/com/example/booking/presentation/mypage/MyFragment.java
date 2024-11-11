package com.example.booking.presentation.mypage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.booking.R;

public class MyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        ImageView profileChangeButton = view.findViewById(R.id.iv_my_page_profile_change);
        profileChangeButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myFragment_to_myProfileChangeFragment)
        );

        ImageView personalInfoButton = view.findViewById(R.id.iv_my_page_personal_info);
        personalInfoButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myFragment_to_myPersonalInfoFragment)
        );
    }
}
package com.example.booking.presentation.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.booking.R;

import java.io.IOException;

public class MyProfileChangeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView profileIconView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile_change, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);

        ImageView backButton = view.findViewById(R.id.iv_back_arrow);
        backButton.setOnClickListener(v ->
                navController.navigate(R.id.action_myProfileChangeFragment_to_myFragment)
        );
        profileImageView = view.findViewById(R.id.iv_my_page_profile_change_profile);
        profileIconView = view.findViewById(R.id.iv_my_page_profile_change_profile_icon);

        profileImageView.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                profileIconView.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

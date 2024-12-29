package com.example.planttest2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingFragment extends Fragment {
    private static final String ARG_IMAGE = "image";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SUBTITLE = "subtitle";

    private int imageResId;
    private String titleText;
    private String subtitleText;

    public static OnboardingFragment newInstance(int imageResId, String title, String subtitle) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE, imageResId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageResId = getArguments().getInt(ARG_IMAGE);
            titleText = getArguments().getString(ARG_TITLE, "");
            subtitleText = getArguments().getString(ARG_SUBTITLE, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleTextView = view.findViewById(R.id.titleText);
        TextView subtitleTextView = view.findViewById(R.id.subtitleText);

        // Set the image
        imageView.setImageResource(imageResId);

        // Apply spannable string to the title
        if (titleText != null && !titleText.isEmpty()) {
            // Create a SpannableString from the title text
            SpannableString spannableTitle = new SpannableString(titleText);

            // Find the index of the last space
            int lastSpaceIndex = titleText.lastIndexOf(" ");

            // Apply the custom color to the last word if space is found
            if (lastSpaceIndex != -1) {
                spannableTitle.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#4CAF50")), // Custom color
                        lastSpaceIndex + 1, // Start after the space
                        titleText.length(), // End at the end of the string
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            // Set the spannable title text
            titleTextView.setText(spannableTitle);
        } else {
            // If title text is empty, set the default text
            titleTextView.setText(titleText);
        }

        // Set the subtitle text
        subtitleTextView.setText(subtitleText);
    }
}

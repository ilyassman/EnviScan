package com.example.planttest2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends BaseActivity {
    private ViewPager2 viewPager;
    private Button skipButton;
    private Button nextButton;
    private Button getStartedButton;
    private TabLayout tabLayout;
    private OnboardingViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeViews();
        setupViewPager();
        setupButtons();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        getStartedButton = findViewById(R.id.getStartedButton);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupViewPager() {
        adapter = new OnboardingViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // Empty implementation - we just want the indicators
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateButtons(position);
            }
        });
    }

    private void setupButtons() {
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void updateButtons(int position) {
        if (position == 2) {
            skipButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            getStartedButton.setVisibility(View.VISIBLE);
        } else {
            skipButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            getStartedButton.setVisibility(View.GONE);
        }
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity2.class));
        finish();
    }
}
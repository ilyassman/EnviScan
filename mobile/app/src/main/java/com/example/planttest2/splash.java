package com.example.planttest2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.animation.Animator;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView lottieAnimation = findViewById(R.id.lottieAnimation);
        TextView splashMessage = findViewById(R.id.splashMessage);

        // Set animation listener
        lottieAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                // Load fade-in animation
                Animation fadeIn = AnimationUtils.loadAnimation(splash.this, R.anim.fade_in);

                // Apply animation to the TextView
                splashMessage.setVisibility(View.VISIBLE);
                splashMessage.startAnimation(fadeIn);
                SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

                // Delay before moving to the main activity
                new Handler().postDelayed(() -> {
                    Intent intent;
                    if (isFirstTime) {
                        // Si c'est la première fois, montrez l'OnboardingActivity
                        intent = new Intent(splash.this, OnboardingActivity.class);
                        // Mettez à jour les SharedPreferences pour indiquer que l'onboarding a été vu
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isFirstTime", false);
                        editor.apply();
                    } else {
                        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                .getString("access_token", null);
                        if(accessToken==null)
                        // Sinon, redirigez vers la page de login
                        intent = new Intent(splash.this, LoginActivity2.class);
                        else
                            intent = new Intent(splash.this, MainNavigation.class);
                    }

                    startActivity(intent);
                    finish();
                }, 2000); // 2 seconds delay
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }
}

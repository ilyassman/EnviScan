package com.example.planttest2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.planttest2.utils.LanguageManager;

public class OnboardingViewPagerAdapter extends FragmentStateAdapter {
    private final FragmentActivity fragmentActivity;
    private final LanguageManager languageManager;

    public OnboardingViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        this.languageManager = LanguageManager.getInstance();
        // Applique la langue actuelle
        languageManager.setLocale(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // On s'assure d'avoir le bon contexte avec la langue mise à jour
        switch (position) {
            case 0:
                return OnboardingFragment.newInstance(
                        R.drawable.plant1,
                        getLocalizedString(R.string.identify_harmful_plants),
                        getLocalizedString(R.string.identify_harmful_plants_desc)
                );
            case 1:
                return OnboardingFragment.newInstance(
                        R.drawable.plant2,
                        getLocalizedString(R.string.manage_plant_threats),
                        getLocalizedString(R.string.manage_plant_threats_desc)
                );
            default:
                return OnboardingFragment.newInstance(
                        R.drawable.plant3,
                        getLocalizedString(R.string.protect_crops),
                        getLocalizedString(R.string.protect_crops_desc)
                );
        }
    }

    // Méthode helper pour obtenir les strings localisées
    private String getLocalizedString(int resourceId) {
        // On utilise le contexte de l'activité après avoir appliqué la langue
        return fragmentActivity.getString(resourceId);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
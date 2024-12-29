package com.example.planttest2.fragement;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.planttest2.utils.LanguageManager;

public class BaseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageManager.getInstance().setLocale(requireContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LanguageManager.getInstance().setLocale(context);
    }
}

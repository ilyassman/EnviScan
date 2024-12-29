package com.example.planttest2.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.example.planttest2.R;
import com.example.planttest2.ChatBotMsgs;

public class ChatBotFragment extends BaseFragment {
    private EditText editTextMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_bot, container, false);

        editTextMessage = view.findViewById(R.id.editTextMessage);
        editTextMessage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatBotMsgs.class);
            startActivity(intent);
        });

        return view;
    }
}
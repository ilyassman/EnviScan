package com.example.planttest2.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import com.example.planttest2.R;
import com.example.planttest2.api.ApiOpenAi;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.ChatBotResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertFragment extends Fragment {
    private LinearLayout messagesContainer;
    private EditText editTextUserInput;
    private ImageButton buttonSend;
    private NestedScrollView messageScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expert, container, false);

        String accessToken = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE)
                .getString("access_token", null);
        ApiOpenAi apiOpenAi = RetrofitClient.getInstance(accessToken).create(ApiOpenAi.class);

        messagesContainer = view.findViewById(R.id.messagesContainer);
        editTextUserInput = view.findViewById(R.id.editTextUserInput);
        buttonSend = view.findViewById(R.id.buttonSend);
        messageScrollView = view.findViewById(R.id.messageScrollView);

        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextUserInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addUserMessage(userMessage);

                apiOpenAi.getResponseFromBoot(userMessage).enqueue(new Callback<ChatBotResponse>() {
                    @Override
                    public void onResponse(Call<ChatBotResponse> call, Response<ChatBotResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            addBotMessage(response.body().getResponse());
                        } else {
                            Toast.makeText(getContext(), "Erreur lors de la réception de la réponse", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatBotResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Échec de la connexion à l'API", Toast.LENGTH_LONG).show();
                    }
                });

                editTextUserInput.setText("");
            }
        });

        return view;
    }

    private void addUserMessage(String message) {
        View userMessageView = getLayoutInflater().inflate(R.layout.user_message_template, messagesContainer, false);
        TextView textViewUserMessage = userMessageView.findViewById(R.id.textViewUserMessage);
        textViewUserMessage.setText(message);
        messagesContainer.addView(userMessageView);
        scrollToEnd();
    }

    private void addBotMessage(String message) {
        View botMessageView = getLayoutInflater().inflate(R.layout.bot_message_template, messagesContainer, false);
        TextView textViewBotMessage = botMessageView.findViewById(R.id.textViewBotanistMessage);
        textViewBotMessage.setText(message);
        messagesContainer.addView(botMessageView);
        scrollToEnd();
    }

    private void scrollToEnd() {
        messageScrollView.post(() -> messageScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
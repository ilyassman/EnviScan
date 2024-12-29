package com.example.planttest2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.example.planttest2.api.ApiOpenAi;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.ChatBotResponse;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.speech.tts.UtteranceProgressListener;

public class ChatBotMsgs extends AppCompatActivity {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;
    private static final int SPEECH_REQUEST_CODE = 100;

    private LinearLayout messagesContainer;
    private EditText editTextUserInput;
    private ImageButton buttonSend;
    private ImageButton buttonVoiceInput;
    private NestedScrollView messageScrollView;
    private TextToSpeech textToSpeech;
    private ApiOpenAi apiOpenAi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_msgs);

        // Initialisation du token et de l'API
        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("access_token", null);
        apiOpenAi = RetrofitClient.getInstance(accessToken).create(ApiOpenAi.class);

        // Initialisation des vues et des fonctionnalités
        initializeViews();
        initializeViews();
        initializeTextToSpeech();
        setupClickListeners();
    }

    private void initializeViews() {
        messagesContainer = findViewById(R.id.messagesContainer);
        editTextUserInput = findViewById(R.id.editTextUserInput);
        buttonSend = findViewById(R.id.buttonSend);
        buttonVoiceInput = findViewById(R.id.buttonVoiceInput);
        messageScrollView = findViewById(R.id.messageScrollView);
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.FRENCH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Langue française non supportée", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Initialisation TTS échouée", Toast.LENGTH_SHORT).show();
            }
        });
        textToSpeech.setSpeechRate(0.9f);
        textToSpeech.setPitch(1.0f);
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(v -> handleSendMessage());
        buttonVoiceInput.setOnClickListener(v -> checkPermissionAndStartRecognition());
    }

    private void handleSendMessage() {
        String userMessage = editTextUserInput.getText().toString().trim();
        if (!userMessage.isEmpty()) {
            // Ajouter et envoyer le message
            addUserMessage(userMessage);
            sendMessageToApi(userMessage);

            // Effacer le champ de saisie
            editTextUserInput.setText("");
        }
    }

    private void sendMessageToApi(String userMessage) {
        apiOpenAi.getResponseFromBoot(userMessage).enqueue(new Callback<ChatBotResponse>() {
            @Override
            public void onResponse(Call<ChatBotResponse> call, Response<ChatBotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botResponse = response.body().getResponse();
                    addBotMessage(botResponse);
                    speakResponse(botResponse);
                } else {
                    runOnUiThread(() -> Toast.makeText(ChatBotMsgs.this,
                            "Erreur lors de la réception de la réponse", Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<ChatBotResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ChatBotMsgs.this,
                        "Échec de la connexion à l'API", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void checkPermissionAndStartRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            startSpeechRecognition();
        }
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Votre appareil ne supporte pas la reconnaissance vocale",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void speakResponse(String text) {
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void addUserMessage(String message) {
        LinearLayout userMessageLayout = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.user_message_template, messagesContainer, false);

        TextView textViewUserMessage = userMessageLayout.findViewById(R.id.textViewUserMessage);
        textViewUserMessage.setText(message);
        messagesContainer.addView(userMessageLayout);
        scrollToEnd();
    }

    private void addBotMessage(String message) {
        // Inflater le layout du message bot
        LinearLayout botMessageLayout = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.bot_message_template, messagesContainer, false);

        // Trouver la TextView pour le message
        TextView textViewBotMessage = botMessageLayout.findViewById(R.id.textViewBotanistMessage);
        textViewBotMessage.setText(message);

        // Trouver le bouton de volume
        ImageButton speakButton = botMessageLayout.findViewById(R.id.buttonSpeak);
        final boolean[] isSpeaking = {false};

        // Configurer le listener pour le bouton de volume
        speakButton.setOnClickListener(v -> {
            if (!isSpeaking[0]) {
                // Commencer à parler
                if (textToSpeech != null) {
                    String utteranceId = "MESSAGE_" + System.currentTimeMillis();
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                    speakButton.setImageResource(R.drawable.ic_volume_off);
                    isSpeaking[0] = true;
                }
            } else {
                // Arrêter de parler
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                speakButton.setImageResource(R.drawable.ic_volume_up);
                isSpeaking[0] = false;
            }
        });

        // Configurer le listener pour détecter la fin de la synthèse vocale
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Non utilisé mais requis par l'interface
            }

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> {
                    speakButton.setImageResource(R.drawable.ic_volume_up);
                    isSpeaking[0] = false;
                });
            }

            @Override
            public void onError(String utteranceId) {
                runOnUiThread(() -> {
                    speakButton.setImageResource(R.drawable.ic_volume_up);
                    isSpeaking[0] = false;
                    Toast.makeText(ChatBotMsgs.this,
                            "Erreur lors de la lecture du message",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Ajouter le message au container et scroller vers le bas
        messagesContainer.addView(botMessageLayout);
        scrollToEnd();
    }

    private void scrollToEnd() {
        messageScrollView.post(() -> messageScrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);
                editTextUserInput.setText(recognizedText);
                handleSendMessage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechRecognition();
            } else {
                Toast.makeText(this,
                        "Permission refusée pour l'enregistrement audio",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
package com.example.planttest2;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planttest2.api.ApiService;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.Utilisateur;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Interpreter tflite;
    //private TextView tvPrediction;
    private List<String> classNames = new ArrayList<>();
    //private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tvPrediction = findViewById(R.id.tvPrediction);
        //imgView = findViewById(R.id.imgView);
//        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
//                .getString("access_token", null);
//        Log.d("Profil", "token : " + accessToken);
//        ApiService apiService= RetrofitClient.getInstance(accessToken).create(ApiService.class);
//        apiService.getProfil().enqueue(new Callback<Utilisateur>() {
//            @Override
//            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
//                if(response.isSuccessful() && response.body()!=null){
//                    Utilisateur utilisateur = response.body();
//                    Log.d("Profil", "Utilisateur : " + utilisateur);
//                }
//                else
//                {
//                    Log.e("Profil", "Erreur dans la réponse"+response);                }
//            }

//            @Override
//            public void onFailure(Call<Utilisateur> call, Throwable t) {
//                Log.e("Profil", "Erreur réseau : " + t.getMessage());
//
//            }
//        });
        // Charger les classes depuis labels.txt
        try {
            loadClassNames();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de chargement des étiquettes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Charger le modèle TensorFlow Lite
        try {
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(loadModelFile(), options);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de chargement du modèle", Toast.LENGTH_SHORT).show();
            return;
        }

        //Button btnUploadImage = findViewById(R.id.btnUploadImage);
        CardView cardGallery = findViewById(R.id.cardGallery);
        cardGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir la galerie pour sélectionner une image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    // Charger les noms des classes depuis labels.txt
    private void loadClassNames() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            classNames.add(line.trim());
        }
        reader.close();
    }

    // Charger le modèle depuis les assets
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("model_unquant.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Charger l'image
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                //imgView.setImageBitmap(bitmap);

                // Prétraiter l'image et effectuer une prédiction
                ByteBuffer imageData = preprocessImage(bitmap);
                float[][] result = new float[1][classNames.size()];
                tflite.run(imageData, result);

                // Obtenir l'index et le score de confiance
                int predictedClass = getMaxIndex(result[0]);
                String className = classNames.get(predictedClass);
                float confidence = result[0][predictedClass];
                String[] parts = className.split(" ");
                String plantName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                Intent intent = new Intent(MainActivity.this, plantsInfos.class);
                intent.putExtra("plantName", plantName);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
                //tvPrediction.setText("Classe prédite : " + className + "\nScore de confiance : " + confidence);
            } catch (Exception e) {
                //tvPrediction.setText("Erreur de traitement de l'image");
                e.printStackTrace();
            }
        }
    }

    // Prétraitement de l'image
    private ByteBuffer preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[224 * 224];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int pixel : intValues) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); // Rouge
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);  // Vert
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);         // Bleu
        }

        return byteBuffer;
    }

    // Obtenir l'index de la valeur maximale (classe prédite)
    private int getMaxIndex(float[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}

package com.example.planttest2;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;

import com.example.planttest2.config.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.tensorflow.lite.Interpreter;

import com.example.planttest2.fragement.HomeFragment;
import com.example.planttest2.fragement.PlantsFragment;
import com.example.planttest2.fragement.ExpertFragment;
import com.example.planttest2.fragement.GardenFragment;
import com.example.planttest2.fragement.ChatBotFragment;


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

import com.example.planttest2.utils.LanguageManager;


public class MainNavigation extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Interpreter tflite;
    private List<String> classNames = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        LanguageManager.getInstance().setLocale(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("access_token", null);
        SessionManager.getInstance().setAccessToken(accessToken);

        super.onCreate(savedInstanceState);
        LanguageManager.getInstance().setLocale(this);
        setContentView(R.layout.activity_main_navigation);

        // Initialiser TensorFlow Lite et charger les classes
        try {
            loadClassNames();
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(loadModelFile(), options);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de chargement du modèle", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabCamera = findViewById(R.id.fab_camera);

        // Charger le fragment par défaut
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_plants) {
                selectedFragment = new PlantsFragment();
            } else if (itemId == R.id.navigation_camera) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                return true;
            } else if (itemId == R.id.navigation_expert) {
                selectedFragment = new ChatBotFragment();
            } else if (itemId == R.id.navigation_garden) {
                selectedFragment = new GardenFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        fabCamera.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void loadClassNames() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            classNames.add(line.trim());
        }
        reader.close();
    }

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
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                ByteBuffer imageData = preprocessImage(bitmap);
                float[][] result = new float[1][classNames.size()];
                tflite.run(imageData, result);

                int predictedClass = getMaxIndex(result[0]);
                String className = classNames.get(predictedClass);
                String[] parts = className.split(" ");
                String plantName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

                Intent intent = new Intent(MainNavigation.this, plantsInfos.class);
                intent.putExtra("plantName", plantName);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du traitement de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ByteBuffer preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[224 * 224];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int pixel : intValues) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);
        }

        return byteBuffer;
    }

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
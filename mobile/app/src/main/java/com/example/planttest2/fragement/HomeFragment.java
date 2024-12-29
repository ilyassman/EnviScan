package com.example.planttest2.fragement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.planttest2.utils.LanguageManager;  // Exemple d'importation

import com.example.planttest2.R;
import com.example.planttest2.api.WeatherApi;
import com.example.planttest2.api.WeatherResponse;
import com.example.planttest2.plantsInfos;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import android.view.ContextThemeWrapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends BaseFragment {

    private static final int LOCATION_PERMISSION_REQUEST = 1000;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 3;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView currentTemp, minTemp, maxTemp, location, humidity;
    private ImageView weatherIcon, languageFlag;
    private TextView languageText;
    private CardView cardChatbot,reminderCard;
    private Interpreter tflite;
    private List<String> classNames = new ArrayList<>();

    private static final String WEATHER_API_KEY = "faedc608d49cd3290c6d7522b3496861";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        initializeWeatherAndLocation();
        initializeMachineLearning();

        setupClickListeners(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupLanguageSelector();
    }

    private void initializeViews(View view) {
        currentTemp = view.findViewById(R.id.currentTemp);
        minTemp = view.findViewById(R.id.minTemp);
        maxTemp = view.findViewById(R.id.maxTemp);
        location = view.findViewById(R.id.location);
        humidity = view.findViewById(R.id.humidity);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        cardChatbot = view.findViewById(R.id.cardChatbot);
        reminderCard = view.findViewById(R.id.reminderCard);
        languageFlag = view.findViewById(R.id.languageFlag);
        languageText = view.findViewById(R.id.languageText);
    }

    private void setupLanguageSelector() {
        String currentLang = LanguageManager.getInstance().getLanguagePreference(requireContext());
        updateLanguageUI(currentLang);

        View languageSelector = getView().findViewById(R.id.languageSelector);
        if (languageSelector != null) {
            languageSelector.setOnClickListener(v -> {
                Context wrapper = new ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.getMenuInflater().inflate(R.menu.language_menu, popup.getMenu());

                // Appliquer un style personnalisé au PopupMenu
                try {
                    Field field = PopupMenu.class.getDeclaredField("mPopup");
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    String langCode;

                    if (id == R.id.menu_french) {
                        Log.d("pppp", "Français sélectionné");
                        langCode = "fr";
                    } else {
                        Log.d("pppp", "anglais sélectionné");
                        langCode = "en";
                    }

                    LanguageManager.getInstance().saveLanguagePreference(requireContext(), langCode);
                    LanguageManager.getInstance().setLocale(requireContext());
                    requireActivity().recreate();

                    return true;
                });

                popup.show();
            });
        }
    }

    private void updateLanguageUI(String languageCode) {
        int flagResource = languageCode.equals("fr") ? R.drawable.ic_flag_fr : R.drawable.ic_flag_en;
        languageFlag.setImageResource(flagResource);
        languageText.setText(languageCode.toUpperCase());
    }

   /* private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        prefs.edit().putString("language", languageCode).apply();
    }*/

    /*private String getLanguagePreference() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return prefs.getString("language", "fr");
    }*/

    /*private void updateLocale(Locale locale) {
        Resources res = requireContext().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        requireContext().createConfigurationContext(config);
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Mettre à jour l'UI
        updateLanguageUI(locale.getLanguage());

        // Recharger l'activité pour appliquer les changements
        requireActivity().recreate();
    }*/

    private void initializeWeatherAndLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermission();
    }

    private void initializeMachineLearning() {
        try {
            loadClassNames();
            tflite = new Interpreter(loadModelFile(), new Interpreter.Options());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.model_load_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners(View view) {
        cardChatbot.setOnClickListener(v -> {
            ChatBotFragment chatBotFragment = new ChatBotFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatBotFragment)
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.cardGallery).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        view.findViewById(R.id.cardCamera).setOnClickListener(v -> {
            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                startCamera();
            }
        });

        // Dans HomeFragment.java, modifiez le click listener de reminderCard
        reminderCard.setOnClickListener(v -> {
            GardenFragment gardenFragment = new GardenFragment();
            Bundle args = new Bundle();
            args.putBoolean("showReminders", true); // Indique qu'il faut afficher les reminders
            gardenFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, gardenFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocation();
        }
    }



    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("Latitude", "Latitude: " + latitude);
                        Log.d("Latitude", "Longitude: " + longitude);
                        fetchWeatherData(latitude, longitude);
                    }
                });
    }
    private void fetchWeatherData(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getWeather(lat, lon, "metric", WEATHER_API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    updateUI(weatherData);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur de récupération des données météo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherResponse weatherData) {
        currentTemp.setText(String.format("%.1f°C", weatherData.main.temp));
        minTemp.setText(String.format("min %.1f°C", weatherData.main.temp_min));
        maxTemp.setText(String.format("max %.1f°C", weatherData.main.temp_max));
        location.setText(String.format("%s, %s", weatherData.name, weatherData.sys.country));
        humidity.setText(String.format(getString(R.string.humidity) + " %d%% ", weatherData.main.humidity));
        // Mise à jour de l'icône météo
        if (weatherData.weather != null && !weatherData.weather.isEmpty()) {
            String iconCode = weatherData.weather.get(0).icon;
            updateWeatherIcon(iconCode);
        }
    }

    private void updateWeatherIcon(String iconCode) {
        // Mappez les codes d'icônes aux ressources de votre application
        int resourceId;
        switch (iconCode) {
            case "01d":
                resourceId = R.drawable.ic_sun;
                break;
            case "01n":
                resourceId = R.drawable.ic_moon;
                break;
            // Ajoutez d'autres cas selon vos icônes
            default:
                resourceId = R.drawable.ic_sun;
        }
        weatherIcon.setImageResource(resourceId);
    }


    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
        } else {
            Toast.makeText(getContext(), "Aucune application appareil photo trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Permission de la caméra nécessaire", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getContext(), "Permission de localisation nécessaire", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri saveImageToTemp(Bitmap bitmap) {
        try {
            File outputDir = requireActivity().getCacheDir();
            File outputFile = File.createTempFile("captured_image", ".jpg", outputDir);

            FileOutputStream out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadClassNames() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(requireActivity().getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            classNames.add(line.trim());
        }
        reader.close();
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = requireActivity().getAssets().openFd("model_unquant.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == requireActivity().RESULT_OK && data != null) {
            try {
                Bitmap bitmap = null;
                Uri imageUri = null;

                if (requestCode == PICK_IMAGE_REQUEST) {
                    imageUri = data.getData();
                    bitmap = BitmapFactory.decodeStream(requireActivity().getContentResolver().openInputStream(imageUri));
                } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    imageUri = saveImageToTemp(bitmap);
                }

                if (bitmap != null) {
                    ByteBuffer imageData = preprocessImage(bitmap);
                    float[][] result = new float[1][classNames.size()];
                    tflite.run(imageData, result);

                    int predictedClass = getMaxIndex(result[0]);
                    String className = classNames.get(predictedClass);
                    String[] parts = className.split(" ");
                    String plantName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

                    Intent intent = new Intent(getActivity(), plantsInfos.class);
                    intent.putExtra("plantName", plantName);
                    intent.putExtra("imageUri", imageUri.toString());
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Erreur de traitement de l'image", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}
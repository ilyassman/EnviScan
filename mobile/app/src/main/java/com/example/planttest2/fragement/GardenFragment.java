package com.example.planttest2.fragement;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;

import com.example.planttest2.LoginActivity2;
import com.example.planttest2.R;
import com.example.planttest2.adapter.PlantAdapter;
import com.example.planttest2.api.ApiService;
import com.example.planttest2.beans.Plant;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.config.SessionManager;
import com.example.planttest2.plantsInfos;
import com.example.planttest2.splash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.tensorflow.lite.Interpreter;

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
import androidx.appcompat.app.AlertDialog;
import android.widget.PopupWindow;
import com.example.planttest2.profile;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GardenFragment extends BaseFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 3;

    // Variables membres
    private RecyclerView recyclerView;
    private PlantAdapter adapter;
    private MaterialButton addPlantBtn;
    private FrameLayout loadingOverlay;
    private View gardenContent;
    private MaterialButton btnGarden, btnReminders;
    private ImageButton btnReminderClock, btnSettings;
    private boolean isRemindersShowing = false;
    private Handler handler = new Handler();
    private boolean isTransactionPending = false;
    private Interpreter tflite;
    private List<String> classNames = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garden, container, false);

        if (savedInstanceState != null) {
            isRemindersShowing = savedInstanceState.getBoolean("isRemindersShowing", false);
        }
        else {
            // Vérifiez les arguments pour voir si on doit afficher les reminders
            Bundle args = getArguments();
            if (args != null && args.getBoolean("showReminders", false)) {
                isRemindersShowing = true;
            }
        }


        initViews(view);
        setupRecyclerView();
        setupNavigation();
        setupTopButtons();
        initializeMachineLearning();


        if (isRemindersShowing) {
            switchToReminders();
        } else {
            loadPlants();
        }


        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        addPlantBtn = view.findViewById(R.id.btnAddPlant);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        gardenContent = view.findViewById(R.id.gardenContent);
        btnGarden = view.findViewById(R.id.btnGarden);
        btnReminders = view.findViewById(R.id.btnReminders);
        //btnReminderClock = view.findViewById(R.id.btnReminderClock);
        btnSettings = view.findViewById(R.id.btnSettings);

        addPlantBtn.setOnClickListener(v -> {
            if (!isTransactionPending) {
                showImageSourceDialog();
            }
        });
    }

    private void showImageSourceDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_source, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.add_plant))
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        // Appliquer un style arrondi à la boîte de dialogue
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        // Configuration de l'option Galerie
        dialogView.findViewById(R.id.galleryOption).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            dialog.dismiss();
        });

        // Configuration de l'option Appareil Photo
        dialogView.findViewById(R.id.cameraOption).setOnClickListener(v -> {
            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            } else {
                startCamera();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
        } else {
            Toast.makeText(getContext(), R.string.no_camera_app, Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(getContext(), R.string.image_processing_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri saveImageToTemp(Bitmap bitmap) throws IOException {
        File outputDir = requireActivity().getCacheDir();
        File outputFile = File.createTempFile("captured_image", ".jpg", outputDir);

        FileOutputStream out = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();

        return Uri.fromFile(outputFile);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SlideInUpAnimator animator = new SlideInUpAnimator();
        recyclerView.setItemAnimator(animator);
        adapter = new PlantAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        btnGarden.setOnClickListener(v -> {
            if (isRemindersShowing && !isTransactionPending) {
                switchToGarden();
            }
        });

        btnReminders.setOnClickListener(v -> {
            if (!isRemindersShowing && !isTransactionPending) {
                switchToReminders();
            }
        });

        updateNavigationUI();
    }

    private void setupTopButtons() {
        /*btnReminderClock.setOnClickListener(v -> {
            if (!isRemindersShowing && !isTransactionPending) {
                switchToReminders();
            }
        });*/

        btnSettings.setOnClickListener(v -> {
            View menuView = getLayoutInflater().inflate(R.layout.menu_settings, null);
            PopupWindow popupWindow = new PopupWindow(
                    menuView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Add elevation for shadow
            popupWindow.setElevation(24);

            // Setup click listeners
            menuView.findViewById(R.id.profileOption).setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), profile.class);
                startActivity(intent);
                popupWindow.dismiss();
            });

            menuView.findViewById(R.id.logoutOption).setOnClickListener(view -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.logout_confirmation)
                        .setMessage(R.string.logout_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            SharedPreferences preferences = getContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("access_token");
                            editor.apply();
                           Intent intent = new Intent(getContext(), LoginActivity2.class);
                           Toast.makeText(getContext(),"deconnexion",Toast.LENGTH_LONG).show();
                            startActivity(intent);
                            getActivity().finish();

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                popupWindow.dismiss();
            });

            // Show popup below settings button
            popupWindow.showAsDropDown(btnSettings, -popupWindow.getWidth() + btnSettings.getWidth(), 0);


        });
    }

    private void switchToGarden() {
        isTransactionPending = true;
        isRemindersShowing = false;

        gardenContent.setVisibility(View.VISIBLE);
        updateNavigationUI();

        Fragment reminderFragment = getChildFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (reminderFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(reminderFragment)
                    .commitAllowingStateLoss();

            getChildFragmentManager().executePendingTransactions();
        }

        loadPlants();

        handler.postDelayed(() -> isTransactionPending = false, 300);
    }

    private void switchToReminders() {
        isTransactionPending = true;
        isRemindersShowing = true;

        gardenContent.setVisibility(View.GONE);
        updateNavigationUI();

        RemindersFragment remindersFragment = new RemindersFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, remindersFragment)
                .commitAllowingStateLoss();

        getChildFragmentManager().executePendingTransactions();

        handler.postDelayed(() -> isTransactionPending = false, 300);
    }

    private void updateNavigationUI() {
        if (isRemindersShowing) {
            btnReminders.setTextColor(requireContext().getColor(R.color.green_primary));
            btnGarden.setTextColor(requireContext().getColor(R.color.gray));
        } else {
            btnGarden.setTextColor(requireContext().getColor(R.color.green_primary));
            btnReminders.setTextColor(requireContext().getColor(R.color.gray));
        }
    }

    private void loadPlants() {
        if (!isAdded()) return;

        loadingOverlay.setVisibility(View.VISIBLE);
        List<Plant> plants = new ArrayList<>();

        ApiService apiService = RetrofitClient.getInstance(SessionManager.getInstance().getAccessToken())
                .create(ApiService.class);

        apiService.getPlantsByUser().enqueue(new Callback<List<Plant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plant>> call, @NonNull Response<List<Plant>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    plants.addAll(response.body());
                    adapter.setPlants(plants);
                }

                handler.postDelayed(() -> {
                    if (isAdded()) {
                        loadingOverlay.setVisibility(View.GONE);
                    }
                }, 1000);
            }

            @Override
            public void onFailure(@NonNull Call<List<Plant>> call, @NonNull Throwable t) {
                if (!isAdded()) return;

                Log.e("GardenFragment", "Erreur de chargement des plantes", t);

                handler.postDelayed(() -> {
                    if (isAdded()) {
                        loadingOverlay.setVisibility(View.GONE);
                        // TODO: Afficher un message d'erreur
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRemindersShowing", isRemindersShowing);
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
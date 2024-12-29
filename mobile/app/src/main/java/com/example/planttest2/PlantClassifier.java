package com.example.planttest2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.List;

public class PlantClassifier {
    private static final int IMAGE_SIZE = 224; // Taille d'image utilisée par le modèle Keras
    private static final int PIXEL_SIZE = 3;
    private static final int MODEL_INPUT_SIZE = IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE;

    private Interpreter interpreter;
    private ByteBuffer inputBuffer;
    private float[][] outputBuffer;
    private List<String> classNames;

    public PlantClassifier(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        interpreter = new Interpreter(loadModelFile(context), options);

        inputBuffer = ByteBuffer.allocateDirect(4 * MODEL_INPUT_SIZE);
        inputBuffer.order(ByteOrder.nativeOrder());

        outputBuffer = new float[1][getClassCount(context)]; // Adapter selon le nombre de classes
        classNames = loadClassNames(context);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        String modelPath = "model_unquant.tflite"; // Nom du fichier modèle
        try (FileInputStream fileInputStream = context.getAssets().openFd(modelPath).createInputStream()) {
            FileChannel fileChannel = fileInputStream.getChannel();
            long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
            long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private List<String> loadClassNames(Context context) throws IOException {
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("labels.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line.trim());
            }
        }
        return names;
    }

    private int getClassCount(Context context) throws IOException {
        return loadClassNames(context).size();
    }

    public String classifyPlant(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true); // Redimensionner à 224x224
        convertBitmapToByteBuffer(resizedBitmap);
        interpreter.run(inputBuffer, outputBuffer);
        return getPlantName(outputBuffer[0]);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        inputBuffer.rewind();
        int[] intValues = new int[IMAGE_SIZE * IMAGE_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixelValue : intValues) {
            float r = (((pixelValue >> 16) & 0xFF) / 127.5f) - 1; // Normalisation
            float g = (((pixelValue >> 8) & 0xFF) / 127.5f) - 1;
            float b = ((pixelValue & 0xFF) / 127.5f) - 1;
            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }
    }

    private String getPlantName(float[] probabilities) {
        int maxIndex = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }

        // Retourner le nom de la plante basé sur l'index de la probabilité maximale
        return classNames.get(maxIndex) + " (Confidence: " + probabilities[maxIndex] + ")";
    }
}

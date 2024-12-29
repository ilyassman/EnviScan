package com.example.planttest2;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planttest2.api.ApiOpenAi;
import com.example.planttest2.api.ApiService;
import com.example.planttest2.config.PlantSingleton;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.DetailPlant;
import com.example.planttest2.model.request.JardinRequest;
import com.example.planttest2.model.request.PlantRequest;
import com.google.android.material.button.MaterialButton;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Imports pour OSMDroid
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

public class plantsInfos extends AppCompatActivity {
    private ImageView imageplant;
    private TextView plantnameview,categplant,temperature,solei,eau,rompotage,fertilisation,
            fait1,fait2,description,toxicite,mauvaiseherbe,envahissant,dureevie,
            periodeplantation,hauteur_plante,diametre_couronne,periode_floraison,diametre_fleur;
            ;
    private String nomscientifique;
    private LinearLayout isfacilelayout,isFleurissantlayout,ismedicallayout,isplanificateurlayout,iscourtlayout;
    private MaterialButton buttonadd;
    private View loadinglayout,mainlayout;
    private MapView mapView; // Ajout de la variable pour la carte
    private MaterialButton btnView3D;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("access_token", null);
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plants_infos);

////////////////////////////////////////////////////////////////////////////////////////////
// Initialisation de la carte
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);





// Centrer la carte sur le Maroc

// Zone indigène (vert)
//        Polygon indigeneZone = new Polygon();
//        indigeneZone.setFillColor(0x4D4CAF50); // Vert semi-transparent
//        indigeneZone.setStrokeColor(0xFF4CAF50); // Vert
//        indigeneZone.setStrokeWidth(2);

// Points pour la zone indigène (sud du Maroc)
//        List<GeoPoint> pointsIndigene = new ArrayList<>();
//        pointsIndigene.add(new GeoPoint(30.4283, -9.5981)); // Agadir
//        pointsIndigene.add(new GeoPoint(28.4433, -11.1602)); // Sud-Ouest
//        pointsIndigene.add(new GeoPoint(28.4433, -8.1322)); // Sud
//        pointsIndigene.add(new GeoPoint(30.4283, -7.9833)); // Sud-Est
//        indigeneZone.setPoints(pointsIndigene);

// Zone introduite (orange)
//        Polygon introduiteZone = new Polygon();
//        introduiteZone.setFillColor(0x4DFFA000); // Orange semi-transparent
//        introduiteZone.setStrokeColor(0xFFFFA000); // Orange
//        introduiteZone.setStrokeWidth(2);

// Points pour la zone introduite (nord du Maroc)
//        List<GeoPoint> pointsIntroduite = new ArrayList<>();
//        pointsIntroduite.add(new GeoPoint(34.0133, -6.8326)); // Rabat
//        pointsIntroduite.add(new GeoPoint(33.5731, -7.5898)); // Casablanca
//        pointsIntroduite.add(new GeoPoint(34.0531, -5.0033)); // Fès
//        pointsIntroduite.add(new GeoPoint(35.7595, -5.8340)); // Tanger
//        introduiteZone.setPoints(pointsIntroduite);

// Zone naturalisée (bleu)
        Polygon naturaliseeZone = new Polygon();
        naturaliseeZone.setFillColor(0x4D1E88E5); // Bleu semi-transparent
        naturaliseeZone.setStrokeColor(0xFF1E88E5); // Bleu
        naturaliseeZone.setStrokeWidth(2);


// Ajouter les zones à la carte
//        mapView.getOverlays().add(indigeneZone);
//        mapView.getOverlays().add(introduiteZone);

////////////////////////////////////////////////////////////////////////////////////////////
        imageplant=findViewById(R.id.imageplant);
        buttonadd=findViewById(R.id.btnaddplant);
        String plantName = getIntent().getStringExtra("plantName");
        btnView3D = findViewById(R.id.btnView3D);
// Logique de visibilité pour le bouton btnView3D
        if ("Aloe Vera".equals(plantName)) {
            btnView3D.setVisibility(View.VISIBLE);
        } else {
            btnView3D.setVisibility(View.GONE);
        }
        if (getIntent().getStringExtra("imageUri")!=null){
            String imageUriString = getIntent().getStringExtra("imageUri");
            Uri imageUri = Uri.parse(imageUriString);
            imageplant.setImageURI(imageUri);
            ApiService apiService=RetrofitClient.getInstance(accessToken).create(ApiService.class);
            buttonadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] imageBytes = getImageBytes(imageUri);
                    PlantRequest plantRequest = new PlantRequest(
                            null,
                            plantName,
                            nomscientifique,
                            categplant.getText().toString(),
                            imageBytes
                    );
                    apiService.createPlant(plantRequest).enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(Call<Long> call, Response<Long> response) {
                            if(response.isSuccessful()) {
                                JardinRequest.PlantRequest plant = new JardinRequest.PlantRequest(response.body());
                                JardinRequest jardinRequest = new JardinRequest(plant);
                                apiService.addToJardin(jardinRequest).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful())
                                            Toast.makeText(plantsInfos.this, "Plant ajouter", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });



                            }
                        }


                        @Override
                        public void onFailure(Call<Long> call, Throwable t) {
                            Toast.makeText(plantsInfos.this,t.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

                }
            });

        }
       else {
           buttonadd.setVisibility(View.GONE);
            String imageUriString2 = PlantSingleton.getInstance().getPlantImage();

            if (imageUriString2 != null && !imageUriString2.startsWith("data:image")) {
                // Ajouter un préfixe "data:image/png;base64,"
                imageUriString2 = "data:image/png;base64," + imageUriString2;
            }

            // Vérifier si l'image est une chaîne Base64 ou une URL
            if (imageUriString2 != null && imageUriString2.startsWith("data:image")) {
                // Si c'est en base64, décoder l'image
                byte[] decodedString = Base64.decode(imageUriString2.substring(imageUriString2.indexOf(",") + 1), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageplant.setImageBitmap(decodedByte);
            }
        }
        plantnameview=findViewById(R.id.plantname);
        categplant=findViewById(R.id.categplant);
        isfacilelayout=findViewById(R.id.isfacile);
        isFleurissantlayout=findViewById(R.id.isFleurissant);
        solei=findViewById(R.id.solei);
        loadinglayout=findViewById(R.id.loading_layout);

        eau=findViewById(R.id.eau);
        rompotage=findViewById(R.id.rompotage);
        mainlayout=findViewById(R.id.mainlayout);
        mainlayout.setVisibility(View.GONE);
        diametre_fleur=findViewById(R.id.diametre_fleur);
        fertilisation=findViewById(R.id.fertilisation);
        ismedicallayout=findViewById(R.id.ismedicinal);
        toxicite=findViewById(R.id.toxicite);
        mauvaiseherbe=findViewById(R.id.mauvaiseherbe);
        envahissant=findViewById(R.id.envahissant);
        periodeplantation=findViewById(R.id.periodeplantation);
        dureevie=findViewById(R.id.dureevie);
        isplanificateurlayout=findViewById(R.id.ispurficateur);
        iscourtlayout=findViewById(R.id.iscourt);
        description=findViewById(R.id.description);
        hauteur_plante=findViewById(R.id.hauteur_plante);
        diametre_couronne=findViewById(R.id.diametre_couronne);
        periode_floraison=findViewById(R.id.periode_floraison);
        diametre_couronne=findViewById(R.id.diametre_couronne);
        fait1=findViewById(R.id.fait1);
        fait2=findViewById(R.id.fait2);

        temperature=findViewById(R.id.temperature);
        plantnameview.setText(plantName);



        ApiOpenAi apiOpenAi = RetrofitClient.getInstance(accessToken).create(ApiOpenAi.class);
        apiOpenAi.getDetailPlant(plantName).enqueue(new Callback<DetailPlant>() {
            @Override
            public void onResponse(Call<DetailPlant> call, Response<DetailPlant> response) {
                if(response.isSuccessful()){
                    Log.d("detailplant",response.body().toString());
                    categplant.setText(response.body().getCategorie());
                    isfacilelayout.setVisibility(response.body().isFacile() ? View.VISIBLE : View.GONE);
                    iscourtlayout.setVisibility(response.body().isCourt() ? View.VISIBLE : View.GONE);
                    ismedicallayout.setVisibility(response.body().isMedicinal() ? View.VISIBLE : View.GONE);
                    isFleurissantlayout.setVisibility(response.body().isFleurissant() ? View.VISIBLE : View.GONE);
                    isplanificateurlayout.setVisibility(response.body().isPurificateur() ? View.VISIBLE : View.GONE);
                    temperature.setText(response.body().getTemperature());
                    nomscientifique =response.body().getScientificName();
                    description.setText(response.body().getDescriptionfr());
                    solei.setText(response.body().getLumiere());
                    eau.setText(response.body().getfrequencearrosage());
                    rompotage.setText(response.body().getRempotage());
                    fertilisation.setText(response.body().getFertilisation());
                    fait1.setText(response.body().getFaitamusant1());
                    fait2.setText(response.body().getFaitamusant2());
                    toxicite.setText(response.body().getToxicity());
                    String mauvaisherbec="",envahisantc="";
                    if(!response.body().isIsmauvaiseHerbe())
                        mauvaisherbec+="Non ";
                    if(!response.body().isIsenvahissant())
                        envahisantc+="Non ";
                    mauvaisherbec+="signalé comme mauvaise herbe";
                    envahisantc+="signalée comme envahissante";
                    mauvaiseherbe.setText(mauvaisherbec);

                    GeoPoint startPoint = new GeoPoint(response.body().getNaturaliseeZone().get(0).getLatitude(), response.body().getNaturaliseeZone().get(0).getLongitude()); // Centre du Maroc
                    mapView.getController().setZoom(6.0); // Zoom adapté au Maroc
                    mapView.getController().setCenter(startPoint);

// Points pour la zone naturalisée (centre du Maroc)
                    List<GeoPoint> pointsNaturalisee = new ArrayList<>();
                    for(com.example.planttest2.model.GeoPoint point:response.body().getNaturaliseeZone()){
                        pointsNaturalisee.add(new GeoPoint(point.getLatitude(), point.getLongitude()));
                    }
                    naturaliseeZone.setPoints(pointsNaturalisee);
                    mapView.getOverlays().add(naturaliseeZone);
                    envahissant.setText(envahisantc);
                    hauteur_plante.setText(response.body().getHauteurdeplante());
                    diametre_couronne.setText(response.body().getDiametredecourounne());
                    diametre_fleur.setText(response.body().getDiametredefleur());
                    periode_floraison.setText(response.body().getPeriodefloraison());
                    dureevie.setText(response.body().getDureedeVie());
                    periodeplantation.setText(response.body().getPeriodedeplantation());
                    loadinglayout.setVisibility(View.GONE);
                    mainlayout.setVisibility(View.VISIBLE);





                }
            }

            @Override
            public void onFailure(Call<DetailPlant> call, Throwable t) {
                Log.d("detailplant",t.getMessage());

            }
        });

    }

    // Ajout des méthodes du cycle de vie pour la carte
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    private byte[] getImageBytes(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
package com.example.planttest2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.planttest2.api.ApiService;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.Utilisateur;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profile extends AppCompatActivity {

    private MaterialButton editProfileButton;
    private TextView username,name,email,email2;
    private String emailvalue,usernamevalue;
    private ApiService apiService;
    private Long iduser;
    private ShapeableImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("access_token", null);
        apiService= RetrofitClient.getInstance(accessToken).create(ApiService.class);
        username=findViewById(R.id.profile_username);
        name=findViewById(R.id.profile_name);
        email=findViewById(R.id.profile_email);
        email2=findViewById(R.id.profile_email_value);
        image=findViewById(R.id.profile_image);
        editProfileButton = findViewById(R.id.edit_profile_button);

        apiService.getProfil().enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                if(response.isSuccessful()){
                    emailvalue=response.body().getEmail();
                    usernamevalue=response.body().getUsername();
                    username.setText(usernamevalue);
                    String avatarUrl = "https://ui-avatars.com/api/?name=" + usernamevalue + "&background=random";
                    Glide.with(profile.this)
                            .load(avatarUrl)
                            .into(image);

                    name.setText(usernamevalue);
                    email2.setText(emailvalue);
                    email.setText(emailvalue);
                    if(response.body().isGoogleAuth())
                        editProfileButton.setVisibility(View.GONE);
                    else {
                        editProfileButton.setVisibility(View.VISIBLE);
                        iduser=response.body().getId();
                    }
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {

            }
        });


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void showEditProfileDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        bottomSheetDialog.setContentView(dialogView);

        TextInputEditText editUsername = dialogView.findViewById(R.id.edit_username);
        TextInputEditText editEmail = dialogView.findViewById(R.id.edit_email);
        TextInputEditText editPassword = dialogView.findViewById(R.id.edit_password);
        editUsername.setText(usernamevalue);
        editEmail.setText(emailvalue);
        MaterialButton saveButton = dialogView.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String newemail = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if(password.isEmpty())
                    password=null;

                apiService.updateUser(iduser,new Utilisateur(username,newemail,password)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            email2.setText(newemail);
                            email.setText(newemail);
                            emailvalue=newemail;
                            Toast.makeText(profile.this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });



            }
        });

        bottomSheetDialog.show();
    }
}

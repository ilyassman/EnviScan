package com.example.planttest2.model.request;

public class PlantRequest {
    private Long idPlante;
    private String nomPlante;
    private String nomScientifique;
    private String categorie;
    private byte[] image;

    public PlantRequest(Long idPlante, String nomPlante, String nomScientifique, String categorie, byte[] image) {
        this.idPlante = idPlante;
        this.nomPlante = nomPlante;
        this.nomScientifique = nomScientifique;
        this.categorie = categorie;
        this.image = image;
    }

    public void setIdPlante(Long idPlante) {
        this.idPlante = idPlante;
    }

    public void setNomPlante(String nomPlante) {
        this.nomPlante = nomPlante;
    }

    public void setNomScientifique(String nomScientifique) {
        this.nomScientifique = nomScientifique;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Long getIdPlante() {
        return idPlante;
    }

    public String getNomPlante() {
        return nomPlante;
    }

    public String getNomScientifique() {
        return nomScientifique;
    }

    public String getCategorie() {
        return categorie;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
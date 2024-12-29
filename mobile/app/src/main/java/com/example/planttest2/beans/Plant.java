package com.example.planttest2.beans;

public class Plant {
    private Long idPlante;
    private String nomPlante;
    private String categorie;
    private String image;
    private String nomScientifique;

    public Plant(String nomPlante, String categorie, String image, String nomScientifique) {
        this.nomPlante = nomPlante;
        this.categorie = categorie;
        this.image = image;
        this.nomScientifique = nomScientifique;
    }

    public void setName(String nomPlante) {
        this.nomPlante = nomPlante;
    }

    public void setType(String categorie) {
        this.categorie = categorie;
    }

    public void setImageUrl(String image) {
        this.image = image;
    }

    // Getters
    public String getName() { return nomPlante; }
    public String getType() { return categorie; }
    public String getImageUrl() { return image; }

    public String getScientifiquename() {
        return nomScientifique;
    }

    public void setScientifiquename(String nomScientifique) {
        this.nomScientifique = nomScientifique;
    }

    public Long getIdPlante() {
        return idPlante;
    }

    public void setIdPlante(Long idPlante) {
        this.idPlante = idPlante;
    }
}

package com.example.plantBackend.ai.model;
public class PlantSearch {
    private String nomPlante;
    private String categorie;
    private String nomScientifique;

    public PlantSearch(String nomPlante, String categorie, String nomScientifique) {
        this.nomPlante = nomPlante;
        this.categorie = categorie;
        this.nomScientifique = nomScientifique;
    }


    public String getNomPlante() {
        return nomPlante;
    }

    public void setNomPlante(String nomPlante) {
        this.nomPlante = nomPlante;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }


    public String getNomScientifique() {
        return nomScientifique;
    }

    public void setNomScientifique(String nomScientifique) {
        this.nomScientifique = nomScientifique;
    }
}

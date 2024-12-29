package com.example.plantBackend.ai.model;

import java.util.List;

class GeoPoint {
    private double latitude;
    private double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
public class Plant {

    private boolean isPlant;
    private boolean nuisible;
    private List<GeoPoint> naturaliseeZone;// Zone de distribution naturelle
    private String categorie;
    private String toxicity;
    private boolean ismauvaiseHerbe;
    private boolean isenvahissant;
    private String scientificName;
    private String dureedeVie;
    private String periodedeplantation;
    private String descriptionfr;
    private String hauteurdeplante;
    private String diametredecourounne;
    private String periodefloraison;
    private String diametredefleur;
    private String temperature;  // Température (simple)
    private String lumiere;      // Type de lumière
    private String frequencearrosage;     // Fréquence d'arrosage
    private String rempotage;    // Fréquence de rempotage
    private String fertilisation;// Fréquence de fertilisation
    private boolean facile;
    private boolean court;
    private boolean fleurissant;
    private boolean medicinal;
    private boolean purificateur;
    private  String faitamusant1;
    private  String faitamusant2;

    // Constructeur
    public Plant(boolean isPlant, boolean nuisible, List<GeoPoint> naturaliseeZone, String categorie, String toxicity, boolean ismauvaiseHerbe, boolean isenvahissant, String scientificName, String dureedeVie, String periodedeplantation, String descriptionfr,
                 String temperature, String lumiere, String frequencearrosage, String rempotage, String fertilisation, boolean facile, boolean court, boolean fleurissant, boolean medicinal, boolean purificateur, String faitamusant1, String faitamusant2) {
        this.isPlant = isPlant;
        this.nuisible = nuisible;
        this.naturaliseeZone = naturaliseeZone;
        this.categorie = categorie;
        this.toxicity = toxicity;
        this.ismauvaiseHerbe = ismauvaiseHerbe;
        this.isenvahissant = isenvahissant;
        this.scientificName = scientificName;
        this.dureedeVie = dureedeVie;
        this.periodedeplantation = periodedeplantation;
        this.descriptionfr = descriptionfr;
        this.temperature = temperature;
        this.lumiere = lumiere;
        this.frequencearrosage = frequencearrosage;
        this.rempotage = rempotage;
        this.fertilisation = fertilisation;
        this.facile = facile;
        this.court = court;
        this.fleurissant = fleurissant;
        this.medicinal = medicinal;
        this.purificateur = purificateur;
        this.faitamusant1 = faitamusant1;
        this.faitamusant2 = faitamusant2;
    }

    // Getters et Setters
    public boolean isPlant() {
        return isPlant;
    }

    public void setPlant(boolean isPlant) {
        this.isPlant = isPlant;
    }

    public boolean isNuisible() {
        return nuisible;
    }

    public void setNuisible(boolean nuisible) {
        this.nuisible = nuisible;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescriptionfr() {
        return descriptionfr;
    }

    public void setDescriptionfr(String descriptionfr) {
        this.descriptionfr = descriptionfr;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getLumiere() {
        return lumiere;
    }

    public void setLumiere(String lumiere) {
        this.lumiere = lumiere;
    }

    public String getfrequencearrosage() {
        return frequencearrosage;
    }

    public void setfrequencearrosage(String frequencearrosage) {
        this.frequencearrosage = frequencearrosage;
    }

    public String getRempotage() {
        return rempotage;
    }

    public void setRempotage(String rempotage) {
        this.rempotage = rempotage;
    }

    public String getFertilisation() {
        return fertilisation;
    }

    public void setFertilisation(String fertilisation) {
        this.fertilisation = fertilisation;
    }

    // ToString pour afficher les informations
    @Override
    public String toString() {
        return "Plant{" +
                "isPlant=" + isPlant +
                ", nuisible=" + nuisible +
                ", categorie='" + categorie + '\'' +
                ", descriptionfr='" + descriptionfr + '\'' +
                ", temperature='" + temperature + '\'' +
                ", lumiere='" + lumiere + '\'' +
                ", arrosage='" + frequencearrosage + '\'' +
                ", rempotage='" + rempotage + '\'' +
                ", fertilisation='" + fertilisation + '\'' +
                '}';
    }

    public boolean isFacile() {
        return facile;
    }

    public void setFacile(boolean facile) {
        this.facile = facile;
    }

    public boolean isCourt() {
        return court;
    }

    public void setCourt(boolean court) {
        this.court = court;
    }

    public boolean isFleurissant() {
        return fleurissant;
    }

    public void setFleurissant(boolean fleurissant) {
        this.fleurissant = fleurissant;
    }

    public boolean isMedicinal() {
        return medicinal;
    }

    public void setMedicinal(boolean medicinal) {
        this.medicinal = medicinal;
    }

    public boolean isPurificateur() {
        return purificateur;
    }

    public void setPurificateur(boolean purificateur) {
        this.purificateur = purificateur;
    }

    public String getFaitamusant1() {
        return faitamusant1;
    }

    public void setFaitamusant1(String faitamusant1) {
        this.faitamusant1 = faitamusant1;
    }

    public String getFaitamusant2() {
        return faitamusant2;
    }

    public void setFaitamusant2(String faitamusant2) {
        this.faitamusant2 = faitamusant2;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getToxicity() {
        return toxicity;
    }

    public void setToxicity(String toxicity) {
        this.toxicity = toxicity;
    }

    public boolean isIsmauvaiseHerbe() {
        return ismauvaiseHerbe;
    }

    public void setIsmauvaiseHerbe(boolean ismauvaiseHerbe) {
        this.ismauvaiseHerbe = ismauvaiseHerbe;
    }

    public boolean isIsenvahissant() {
        return isenvahissant;
    }

    public void setIsenvahissant(boolean isenvahissant) {
        this.isenvahissant = isenvahissant;
    }

    public String getDureedeVie() {
        return dureedeVie;
    }

    public void setDureedeVie(String dureedeVie) {
        this.dureedeVie = dureedeVie;
    }

    public String getPeriodedeplantation() {
        return periodedeplantation;
    }

    public void setPeriodedeplantation(String periodedeplantation) {
        this.periodedeplantation = periodedeplantation;
    }

    public String getHauteurdeplante() {
        return hauteurdeplante;
    }

    public void setHauteurdeplante(String hauteurdeplante) {
        this.hauteurdeplante = hauteurdeplante;
    }

    public String getDiametredecourounne() {
        return diametredecourounne;
    }

    public void setDiametredecourounne(String diametredecourounne) {
        this.diametredecourounne = diametredecourounne;
    }

    public String getPeriodefloraison() {
        return periodefloraison;
    }

    public void setPeriodefloraison(String periodefloraison) {
        this.periodefloraison = periodefloraison;
    }

    public String getDiametredefleur() {
        return diametredefleur;
    }

    public void setDiametredefleur(String diametredefleur) {
        this.diametredefleur = diametredefleur;
    }

    public List<GeoPoint> getNaturaliseeZone() {
        return naturaliseeZone;
    }

    public void setNaturaliseeZone(List<GeoPoint> naturaliseeZone) {
        this.naturaliseeZone = naturaliseeZone;
    }
}

package com.example.planttest2.model.request;
public class JardinRequest {
    @Override
    public String toString() {
        return "JardinRequest{" +
                "plant=" + plant +
                '}';
    }

    private PlantRequest plant;

    // Constructeurs
    public JardinRequest() {
    }

    public JardinRequest(PlantRequest plant) {
        this.plant = plant;
    }

    // Getters et Setters
    public PlantRequest getPlant() {
        return plant;
    }

    public void setPlant(PlantRequest plant) {
        this.plant = plant;
    }

    // Classe interne pour l'objet `plant`
    public static class PlantRequest {
        @Override
        public String toString() {
            return "PlantRequest{" +
                    "idPlante=" + idPlante +
                    '}';
        }

        private Long idPlante;

        public PlantRequest() {
        }

        public PlantRequest(Long idPlante) {
            this.idPlante = idPlante;
        }

        public Long getIdPlante() {
            return idPlante;
        }

        public void setIdPlante(Long idPlante) {
            this.idPlante = idPlante;
        }
    }
}

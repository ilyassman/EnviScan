import MDTypography from "components/MDTypography";
import MDBox from "components/MDBox";
import MDAvatar from "components/MDAvatar";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useEffect, useState } from "react";
import { getAllPlant,DeletePlant } from "services/plantService";

// Fonction pour s'assurer que l'image est dans le bon format
const formatImageSource = (image) => {
  if (image && !image.startsWith("data:image")) {
    return `data:image/png;base64,${image}`;
  }
  return image;
};

export default function PlantsTableData({ handleEditOpen }) {
  const [plants, setPlants] = useState([]);
  const fetchPlants = async () => {
    try {
      const plantData = await getAllPlant();
      console.log(plantData);

      // Formater les images
      const plantsWithFormattedImages = plantData.map((plant) => {
        const imageDataURL = formatImageSource(plant.image); // Formater l'image
        return { ...plant, imageDataURL }; // Ajouter l'URL de l'image aux données de la plante
      });

      setPlants(plantsWithFormattedImages); // Mettre à jour l'état avec les données des plantes
    } catch (error) {
      console.error("Erreur lors de la récupération des plants :", error);
    }
  };

  useEffect(() => {
    
    fetchPlants();
  }, []);

  const rows = plants.map((plant) => ({
    nom_plante: (
      <MDTypography variant="button" fontWeight="medium">
        {plant.nomPlante}
      </MDTypography>
    ),
    nom_scientifique: <MDTypography variant="caption">{plant.nomScientifique}</MDTypography>,
    categorie: <MDTypography variant="caption">{plant.categorie}</MDTypography>,
    image: (
      <MDBox display="flex" justifyContent="center">
        <MDAvatar src={plant.imageDataURL} alt={plant.nomPlante} size="lg" />{" "}
        {/* Utiliser l'URL de l'image */}
      </MDBox>
    ),
    actions: (
      <MDBox display="flex" alignItems="center">
        <MDTypography
          component="a"
          href="#"
          variant="caption"
          color="text"
          fontWeight="medium"
          onClick={(e) => {
            e.preventDefault();
            handleEditOpen(plant);
          }}
        >
          <EditIcon style={{ marginRight: 15, fontSize: "1.5rem" }} />
        </MDTypography>
        <MDTypography
          component="a"
          href="#"
          variant="caption"
          color="error"
          onClick={async (e) => {
            e.preventDefault();
            console.log(plant.idPlante)
            await DeletePlant(plant.idPlante)
            await fetchPlants()
          
          }}
          fontWeight="medium"
          style={{ marginLeft: 10 }}
        >
          <DeleteIcon style={{ marginRight: 10, fontSize: "1.5rem" }} />
        </MDTypography>
      </MDBox>
    ),
  }));

  return {
    columns: [
      { Header: "Nom de la Plante", accessor: "nom_plante", align: "left" },
      { Header: "Nom Scientifique", accessor: "nom_scientifique", align: "left" },
      { Header: "Catégorie", accessor: "categorie", align: "center" },
      { Header: "Image", accessor: "image", align: "center" },
      { Header: "Actions", accessor: "actions", align: "center" },
    ],
    rows,
    refetch: fetchPlants,
  };
}

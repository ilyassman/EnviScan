// @mui material components
import Grid from "@mui/material/Grid";
import Card from "@mui/material/Card";
import { Icon } from "@mui/material";
import Modal from "@mui/material/Modal";
import { useState } from "react";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import MDAvatar from "components/MDAvatar";

// Material Dashboard 2 React example components
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import DataTable from "examples/Tables/DataTable";
import { AddPlant,UpdatePlant } from "services/plantService";
// Data
import plantsTableData from "layouts/tables/data/plantsTableData";
import { Await } from "react-router-dom";

function Tables() {
  const [open, setOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selectedPlant, setSelectedPlant] = useState(null);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleEditOpen = (plant) => {
    setSelectedPlant(plant);
    setEditOpen(true);
  };

  const handleEditClose = () => {
    setSelectedPlant(null);
    setEditOpen(false);
  };

  const { columns, rows,refetch } = plantsTableData({ handleEditOpen });

  return (
    <DashboardLayout>
      <DashboardNavbar />
      <MDBox pt={6} pb={3}>
        <Grid container spacing={6}>
          <Grid item xs={12}>
            <Card>
              <MDBox
                mx={2}
                mt={-3}
                py={3}
                px={2}
                variant="gradient"
                bgColor="info"
                borderRadius="lg"
                coloredShadow="info"
                display="flex"
                justifyContent="space-between"
                alignItems="center"
              >
                <MDTypography variant="h6" color="white">
                  Liste des Plantes
                </MDTypography>
                <MDButton
                  variant="gradient"
                  color="white"
                  startIcon={<Icon>add</Icon>}
                  onClick={handleOpen}
                >
                  Ajouter une plante
                </MDButton>
              </MDBox>
              <MDBox pt={3}>
                <DataTable
                  table={{ columns, rows }}
                  isSorted={false}
                  entriesPerPage={false}
                  showTotalEntries={false}
                  noEndBorder
                />
              </MDBox>
            </Card>
          </Grid>
        </Grid>
      </MDBox>

      {/* Modal pour le formulaire d'ajout */}
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-add-plant"
        aria-describedby="modal-add-plant-form"
      >
        <MDBox
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 600,
            bgcolor: "background.paper",
            borderRadius: "10px",
            boxShadow: 24,
            p: 4,
          }}
        >
          <MDBox mb={3} display="flex" justifyContent="space-between" alignItems="center">
            <MDTypography variant="h6">Ajouter une nouvelle plante</MDTypography>
            <Icon sx={{ cursor: "pointer" }} onClick={handleClose}>
              close
            </Icon>
          </MDBox>

          <form
  onSubmit={async (e) => {
    e.preventDefault();
    const nomPlante = e.target.elements.nomPlante.value;
    const nomScientifique = e.target.elements.nomScientifique.value;
    const categorie = e.target.elements.categorie.value;
    const image = e.target.elements.image.files[0]; // Pour le fichier image
    const convertImageToBase64 = (file) => {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result.split(',')[1]); // Récupère uniquement la partie Base64
        reader.onerror = (error) => reject(error);
        reader.readAsDataURL(file);
      });
    };
    const imageBase64 = await convertImageToBase64(image);

    // Création de l'objet plante
    const newPlant = {
      nomPlante,
      nomScientifique,
      categorie,
      image:imageBase64
    };
    await AddPlant(newPlant)
    await refetch()
    handleClose();
  }}
>
  <Grid container spacing={2}>
    <Grid item xs={12} md={6}>
      <MDBox mb={2}>
        <MDTypography variant="caption" fontWeight="bold">
          Nom de la plante
        </MDTypography>
        <MDBox mt={1}>
          <input
            name="nomPlante"
            type="text"
            style={{
              width: "100%",
              padding: "8px",
              borderRadius: "8px",
              border: "1px solid #ccc",
            }}
            required
          />
        </MDBox>
      </MDBox>
    </Grid>
    <Grid item xs={12} md={6}>
      <MDBox mb={2}>
        <MDTypography variant="caption" fontWeight="bold">
          Nom scientifique
        </MDTypography>
        <MDBox mt={1}>
          <input
            name="nomScientifique"
            type="text"
            style={{
              width: "100%",
              padding: "8px",
              borderRadius: "8px",
              border: "1px solid #ccc",
            }}
            required
          />
        </MDBox>
      </MDBox>
    </Grid>
    <Grid item xs={12}>
      <MDBox mb={2}>
        <MDTypography variant="caption" fontWeight="bold">
          Catégorie
        </MDTypography>
        <MDBox mt={1}>
          <select
            name="categorie"
            style={{
              width: "100%",
              padding: "8px",
              borderRadius: "8px",
              border: "1px solid #ccc",
            }}
            required
          >
            <option value="">Sélectionnez une catégorie</option>
            <option value="Fleur">Fleur</option>
            <option value="Arbre">Arbre</option>
            <option value="Arbuste">Arbuste</option>
          </select>
        </MDBox>
      </MDBox>
    </Grid>
    <Grid item xs={12}>
      <MDBox mb={2}>
        <MDTypography variant="caption" fontWeight="bold">
          Image
        </MDTypography>
        <MDBox mt={1} p={3} bgcolor="grey.100" borderRadius="lg" textAlign="center">
          <input
            name="image"
            type="file"
            accept="image/*"
            style={{ width: "100%" }}
            required
          />
        </MDBox>
      </MDBox>
    </Grid>
  </Grid>

  <MDBox mt={4} display="flex" justifyContent="flex-end">
    <MDButton variant="outlined" color="info" onClick={handleClose} sx={{ mr: 2 }}>
      Annuler
    </MDButton>
    <MDButton variant="gradient" color="info" type="submit">
      Ajouter
    </MDButton>
  </MDBox>
</form>

        </MDBox>
      </Modal>

      {/* Modal pour modifier une plante */}
      <Modal
        open={editOpen}
        onClose={handleEditClose}
        aria-labelledby="modal-edit-plant"
        aria-describedby="modal-edit-plant-form"
      >
        <MDBox
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 600,
            bgcolor: "background.paper",
            borderRadius: "10px",
            boxShadow: 24,
            p: 4,
          }}
        >
          <MDBox mb={3} display="flex" justifyContent="space-between" alignItems="center">
            <MDTypography variant="h6">Modifier la plante</MDTypography>
            <Icon sx={{ cursor: "pointer" }} onClick={handleEditClose}>
              close
            </Icon>
          </MDBox>

          {selectedPlant && (
            <form
              onSubmit={async (e) => {
                e.preventDefault();
                const nomPlante = e.target.elements.nomPlante.value;
                const nomScientifique = e.target.elements.nomScientifique.value;
                const categorie = e.target.elements.categorie.value;
                var image;
                if( e.target.elements.image.files[0]!=null){
                var image = e.target.elements.image.files[0];
                const convertImageToBase64 = (file) => {
                  return new Promise((resolve, reject) => {
                    const reader = new FileReader();
                    reader.onload = () => resolve(reader.result.split(',')[1]); // Récupère uniquement la partie Base64
                    reader.onerror = (error) => reject(error);
                    reader.readAsDataURL(file);
                  });
                };
                const imageBase64 = await convertImageToBase64(image);
                var newPlant = {
                  nomPlante,
                  nomScientifique,
                  categorie,
                  image:imageBase64
                };
                }
            else{
              var newPlant = {
                nomPlante,
                nomScientifique,
                categorie,
              };
            }
                
                console.log(selectedPlant.idPlante)
                await UpdatePlant(selectedPlant.idPlante,newPlant)
                refetch()
                handleEditClose();
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <MDBox mb={2}>
                    <MDTypography variant="caption" fontWeight="bold">
                      Nom de la plante
                    </MDTypography>
                    <MDBox mt={1}>
                      <input
                        type="text"
                         name="nomPlante"
                        defaultValue={selectedPlant.nomPlante}
                        style={{
                          width: "100%",
                          padding: "8px",
                          borderRadius: "8px",
                          border: "1px solid #ccc",
                        }}
                        required
                      />
                    </MDBox>
                  </MDBox>
                </Grid>
                <Grid item xs={12} md={6}>
                  <MDBox mb={2}>
                    <MDTypography variant="caption" fontWeight="bold">
                      Nom scientifique
                    </MDTypography>
                    <MDBox mt={1}>
                      <input
                        type="text"
                        name="nomScientifique"
                        defaultValue={selectedPlant.nomScientifique}
                        style={{
                          width: "100%",
                          padding: "8px",
                          borderRadius: "8px",
                          border: "1px solid #ccc",
                        }}
                        required
                      />
                    </MDBox>
                  </MDBox>
                </Grid>
                <Grid item xs={12}>
                  <MDBox mb={2}>
                    <MDTypography variant="caption" fontWeight="bold">
                      Catégorie
                    </MDTypography>
                    <MDBox mt={1}>
                      <select
                        name="categorie"
                        defaultValue={selectedPlant.categorie}
                        style={{
                          width: "100%",
                          padding: "8px",
                          borderRadius: "8px",
                          border: "1px solid #ccc",
                        }}
                        required
                      >
                        <option value="">Sélectionnez une catégorie</option>
                        <option value="Fleur">Fleur</option>
                        <option value="Arbre">Arbre</option>
                        <option value="Arbuste">Arbuste</option>
                      </select>
                    </MDBox>
                  </MDBox>
                </Grid>
                <Grid item xs={12}>
                  <MDBox mb={2}>
                    <MDTypography variant="caption" fontWeight="bold">
                      Image actuelle
                    </MDTypography>
                    <MDBox mt={1} display="flex" justifyContent="center">
                      <MDAvatar
                        src={selectedPlant.imageDataURL}
                        alt={selectedPlant.nomPlante}
                        size="xxl"
                      />
                    </MDBox>
                  </MDBox>
                  <MDBox mb={2}>
                    <MDTypography variant="caption" fontWeight="bold">
                      Nouvelle image (optionnel)
                    </MDTypography>
                    <MDBox mt={1} p={3} bgcolor="grey.100" borderRadius="lg" textAlign="center">
                      <input 
                      name="image"
                      type="file" accept="image/*" style={{ width: "100%" }} />
                    </MDBox>
                  </MDBox>
                </Grid>
              </Grid>

              <MDBox mt={4} display="flex" justifyContent="flex-end">
                <MDButton variant="outlined" color="info" onClick={handleEditClose} sx={{ mr: 2 }}>
                  Annuler
                </MDButton>
                <MDButton variant="gradient" color="info" type="submit">
                  Modifier
                </MDButton>
              </MDBox>
            </form>
          )}
        </MDBox>
      </Modal>
    </DashboardLayout>
  );
}

export default Tables;

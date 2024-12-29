// @mui material components
import Grid from "@mui/material/Grid";
import Card from "@mui/material/Card";
import Icon from "@mui/material/Icon";
import Modal from "@mui/material/Modal";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import MDInput from "components/MDInput";

// Material Dashboard 2 React example components
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import { getPorfil,UpdateUser } from "../../services/userService";

import { useState,useEffect } from "react";

function Profile() {
  const [openEditModal, setOpenEditModal] = useState(false);
  const [userData, setUserData] = useState({
    id:"0",
    username: "",
    email: "",
    password: "••••••••",
  });
   const fetchProfil = async () => {
      try {
        const userData = await getPorfil();
       
        setUserData((prevData) => ({
          ...prevData, // Conserver les valeurs non mises à jour (comme `password`)
          username: userData.username || prevData.username,
          email: userData.email || prevData.email,
          id:userData.id||prevData.id
        }));
      } catch (error) {
        console.error("Erreur lors de la récupération de profil :", error);
      }
    };
    useEffect(() => {
      fetchProfil();
      }, []);

  const handleEditClick = () => setOpenEditModal(true);
  const handleCloseModal = () => setOpenEditModal(false);

  return (
    <DashboardLayout>
      <DashboardNavbar />
      <MDBox mt={5} mb={3}>
        <Grid container spacing={3} justifyContent="center">
          <Grid item xs={12} md={8}>
            <Card>
              <MDBox
                variant="gradient"
                bgColor="info"
                borderRadius="lg"
                coloredShadow="info"
                mx={2}
                mt={-3}
                p={3}
                mb={1}
                textAlign="center"
              >
                <MDTypography variant="h4" fontWeight="medium" color="white" mt={1}>
                  Profil Utilisateur
                </MDTypography>
              </MDBox>
              <MDBox pt={4} pb={3} px={3}>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <MDBox
                      display="flex"
                      justifyContent="space-between"
                      alignItems="center"
                      p={3}
                      bg="grey.100"
                      borderRadius="lg"
                    >
                      <MDBox>
                        <MDTypography variant="h6" fontWeight="medium">
                          Nom d utilisateur
                        </MDTypography>
                        <MDTypography variant="button" color="text">
                          {userData.username}
                        </MDTypography>
                      </MDBox>
                      <MDBox>
                        <Icon color="info" fontSize="medium" sx={{ cursor: "pointer" }}>
                          person
                        </Icon>
                      </MDBox>
                    </MDBox>
                  </Grid>
                  <Grid item xs={12}>
                    <MDBox
                      display="flex"
                      justifyContent="space-between"
                      alignItems="center"
                      p={3}
                      bg="grey.100"
                      borderRadius="lg"
                    >
                      <MDBox>
                        <MDTypography variant="h6" fontWeight="medium">
                          Email
                        </MDTypography>
                        <MDTypography variant="button" color="text">
                          {userData.email}
                        </MDTypography>
                      </MDBox>
                      <MDBox>
                        <Icon color="info" fontSize="medium" sx={{ cursor: "pointer" }}>
                          email
                        </Icon>
                      </MDBox>
                    </MDBox>
                  </Grid>
                  <Grid item xs={12}>
                    <MDBox
                      display="flex"
                      justifyContent="space-between"
                      alignItems="center"
                      p={3}
                      bg="grey.100"
                      borderRadius="lg"
                    >
                      <MDBox>
                        <MDTypography variant="h6" fontWeight="medium">
                          Mot de passe
                        </MDTypography>
                        <MDTypography variant="button" color="text">
                          {userData.password}
                        </MDTypography>
                      </MDBox>
                      <MDBox>
                        <Icon color="info" fontSize="medium" sx={{ cursor: "pointer" }}>
                          lock
                        </Icon>
                      </MDBox>
                    </MDBox>
                  </Grid>
                </Grid>
                <MDBox mt={4} mb={1} textAlign="center">
                  <MDButton variant="gradient" color="info" onClick={handleEditClick}>
                    Modifier le profil
                  </MDButton>
                </MDBox>
              </MDBox>
            </Card>
          </Grid>
        </Grid>
      </MDBox>

      {/* Modal de modification */}
      <Modal open={openEditModal} onClose={handleCloseModal} aria-labelledby="edit-profile-modal">
        <MDBox
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 400,
            bgcolor: "background.paper",
            borderRadius: "10px",
            boxShadow: 24,
            p: 4,
          }}
        >
          <MDTypography variant="h6" mb={3}>
            Modifier le profil
          </MDTypography>
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              const username = e.target.elements.username.value;
              const email=e.target.elements.email.value;
              const password=e.target.elements.password.value;
              const newPorfil={
                username,
                email,
                password,
              }
              await UpdateUser(userData.id,newPorfil);
              await fetchProfil()
              handleCloseModal();
            }}
          >
            <MDBox mb={2}>
              <MDInput
                type="text"
                name="username"
                label="Nom d'utilisateur"
                fullWidth
                defaultValue={userData.username}
              />
            </MDBox>
            <MDBox mb={2}>
              <MDInput 
              name="email"
              type="email" label="Email" fullWidth defaultValue={userData.email} />
            </MDBox>
            <MDBox mb={2}>
              <MDInput
                name="password"
                type="password"
                label="Nouveau mot de passe"
                fullWidth
                placeholder="Laissez vide pour ne pas modifier"
              />
            </MDBox>
            <MDBox mt={4} mb={1} display="flex" justifyContent="space-between">
              <MDButton variant="outlined" color="info" onClick={handleCloseModal}>
                Annuler
              </MDButton>
              <MDButton variant="gradient" color="info" type="submit">
                Sauvegarder
              </MDButton>
            </MDBox>
          </form>
        </MDBox>
      </Modal>
    </DashboardLayout>
  );
}

export default Profile;

import Grid from "@mui/material/Grid";
import Card from "@mui/material/Card";
import { Icon } from "@mui/material";
import Modal from "@mui/material/Modal";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import { useState } from "react";
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import DataTable from "examples/Tables/DataTable";
import usersTableData from "layouts/UsersTable/data/usersTableData";
import { AddUser, UpdateUser } from "../../services/userService";

function UsersTable() {
  const [open, setOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [client, setClient] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [selectedUser, setSelectedUser] = useState(null);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleEditOpen = (user) => {
    setSelectedUser(user);
    setClient({
      username: user.username,
      email: user.email,
      password: "",
      confirmPassword: "",
    });
    setEditOpen(true);
  };

  const handleEditClose = () => {
    setSelectedUser(null);
    setEditOpen(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setClient((prevClient) => ({
      ...prevClient,
      [name]: value,
    }));
  };

  const handleAddUser = async (e) => {
    e.preventDefault();
    if (client.password !== client.confirmPassword) {
      alert("Les mots de passe ne correspondent pas.");
      return;
    }
    const newClient = {
      username: client.username,
      email: client.email,
      password: client.password,
    };
    console.log(newClient);
    await AddUser(newClient);
    await refetch();
    handleClose();
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    if (client.password !== client.confirmPassword) {
      alert("Les mots de passe ne correspondent pas.");
      return;
    }
    const updatedClient = {
      username: client.username,
      email: client.email,
      password: client.password,
    };
    
    await UpdateUser(selectedUser.id, updatedClient);
    await refetch();
    handleEditClose();
  };

  const { columns, rows, refetch } = usersTableData({ handleEditOpen });

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
                  Liste des Utilisateurs
                </MDTypography>
                <MDButton
                  variant="gradient"
                  color="white"
                  startIcon={<Icon>person_add</Icon>}
                  onClick={handleOpen}
                >
                  Ajouter un utilisateur
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

      {/* Modal pour ajouter un utilisateur */}
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-add-user"
        aria-describedby="modal-add-user-form"
      >
        <MDBox
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 500,
            bgcolor: "background.paper",
            borderRadius: "10px",
            boxShadow: 24,
            p: 4,
          }}
        >
          <MDBox mb={3} display="flex" justifyContent="space-between" alignItems="center">
            <MDTypography variant="h6">Ajouter un nouvel utilisateur</MDTypography>
            <Icon sx={{ cursor: "pointer" }} onClick={handleClose}>
              close
            </Icon>
          </MDBox>

          <form onSubmit={handleAddUser}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <MDBox mb={2}>
                  <MDTypography variant="caption" fontWeight="bold">
                    Nom d utilisateur
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="text"
                      name="username"
                      placeholder="Entrez le nom d'utilisateur"
                      value={client.username}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
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
                    Email
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="email"
                      name="email"
                      placeholder="exemple@email.com"
                      value={client.email}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
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
                    Mot de passe
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="password"
                      name="password"
                      placeholder="Entrez le mot de passe"
                      value={client.password}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
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
                    Confirmer le mot de passe
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="password"
                      name="confirmPassword"
                      placeholder="Confirmez le mot de passe"
                      value={client.confirmPassword}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                      }}
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

      {/* Modal pour modifier un utilisateur */}
      <Modal
        open={editOpen}
        onClose={handleEditClose}
        aria-labelledby="modal-edit-user"
        aria-describedby="modal-edit-user-form"
      >
        <MDBox
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 500,
            bgcolor: "background.paper",
            borderRadius: "10px",
            boxShadow: 24,
            p: 4,
          }}
        >
          <MDBox mb={3} display="flex" justifyContent="space-between" alignItems="center">
            <MDTypography variant="h6">Modifier l utilisateur</MDTypography>
            <Icon sx={{ cursor: "pointer" }} onClick={handleEditClose}>
              close
            </Icon>
          </MDBox>

          <form onSubmit={handleUpdateUser}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <MDBox mb={2}>
                  <MDTypography variant="caption" fontWeight="bold">
                    Nom d utilisateur
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="text"
                      name="username"
                      placeholder="Entrez le nom d'utilisateur"
                      value={client.username}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
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
                    Email
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="email"
                      name="email"
                      placeholder="exemple@email.com"
                      value={client.email}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
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
                    Mot de passe
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="password"
                      name="password"
                      placeholder="Entrez le mot de passe"
                      value={client.password}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                      }}
                  
                    />
                  </MDBox>
                </MDBox>
              </Grid>

              <Grid item xs={12}>
                <MDBox mb={2}>
                  <MDTypography variant="caption" fontWeight="bold">
                    Confirmer le mot de passe
                  </MDTypography>
                  <MDBox mt={1}>
                    <input
                      type="password"
                      name="confirmPassword"
                      placeholder="Confirmez le mot de passe"
                      value={client.confirmPassword}
                      onChange={handleChange}
                      style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                      }}
                      
                    />
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
        </MDBox>
      </Modal>
    </DashboardLayout>
  );
}

export default UsersTable;

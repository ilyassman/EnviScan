import { useEffect, useState } from "react";
import MDTypography from "components/MDTypography";
import MDBox from "components/MDBox";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { getAllUser, deleteUser } from "../../../services/userService";

export default function UsersTableData({ handleEditOpen }) {
  const [users, setUsers] = useState([]);

  const fetchUsers = async () => {
    try {
      const userData = await getAllUser();
      console.log(userData);
      setUsers(userData);
    } catch (error) {
      console.error("Erreur lors de la récupération des utilisateurs :", error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleDelete = async (userId) => {
  
   await deleteUser(userId);
   await fetchUsers();
   console.log("ID utilisateur à supprimer:", userId);
  };

  const rows = users.map((user) => ({
    username: (
      <MDTypography variant="button" fontWeight="medium">
        {user.username}
      </MDTypography>
    ),
    email: (
      <MDTypography variant="caption" color="text">
        {user.email}
      </MDTypography>
    ),
    password: (
      <MDTypography variant="caption" color="text">
        {"••••••••"}
      </MDTypography>
    ),
    actions: (
      <MDBox display="flex" alignItems="center" justifyContent="center">
        <MDTypography
          component="a"
          href="#"
          variant="caption"
          color="text"
          fontWeight="medium"
          onClick={(e) => {
            e.preventDefault();
            handleEditOpen(user);
          }}
        >
          <EditIcon style={{ marginRight: 15, fontSize: "1.5rem" }} />
        </MDTypography>
        <MDTypography
          component="a"
          href="#"
          variant="caption"
          color="error"
          fontWeight="medium"
          onClick={(e) => {
            e.preventDefault();
            handleDelete(user.id);
          }}
        >
          <DeleteIcon style={{ marginRight: 10, fontSize: "1.5rem" }} />
        </MDTypography>
      </MDBox>
    ),
  }));

  return {
    columns: [
      { Header: "Nom d'utilisateur", accessor: "username", align: "left" },
      { Header: "Email", accessor: "email", align: "left" },
      { Header: "Mot de passe", accessor: "password", align: "center" },
      { Header: "Actions", accessor: "actions", align: "center" },
    ],
    rows,
    refetch: fetchUsers,
  };
}

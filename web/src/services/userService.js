import axiosInstance from "../configAxios/axiosInstance";

export const getAllUser = async () => {
  try {
    const response = await axiosInstance.get("/users");
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la recupuration des users:", error);
    throw error;
  }
};
export const AddUser = async (user) => {
  try {
    const response = await axiosInstance.post("/user",user);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l ajout des users:", error);
    throw error;
  }
};

export const UpdateUser = async (userId, user) => {
  console.log(userId)
  try {
    const response = await axiosInstance.put(`/userupdate/${userId}`, user);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la modification de l'utilisateur :", error);
    throw error;
  }
};

export const deleteUser = async (id) => {
  try {
    const response = await axiosInstance.delete(`user/${id}`);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l ajout des users:", error);
    throw error;
  }
};
export const getPorfil = async () => {
  try {
    const response = await axiosInstance.get("/profil");
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la recupuration des users:", error);
    throw error;
  }
};
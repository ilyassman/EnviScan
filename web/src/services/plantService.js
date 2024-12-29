import axiosInstance from "../configAxios/axiosInstance";

export const getAllPlant = async () => {
  try {
    const response = await axiosInstance.get("/plants");
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la recupuration des plants:", error);
    throw error;
  }
};
export const AddPlant = async (plant) => {
  try {
    const response = await axiosInstance.post("/plants",plant);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l ajout d une plants:", error);
    throw error;
  }
};
export const DeletePlant = async (id) => {
  try {
    const response = await axiosInstance.delete(`plants/${id}`);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de delete plant : ", error);
    throw error;
  }
};
export const UpdatePlant = async (id,plant) => {
  try {
    const response = await axiosInstance.put(`plants/${id}`,plant);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de update plant : ", error);
    throw error;
  }
};



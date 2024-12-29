import axiosInstance from "../configAxios/axiosInstance";

export const getAllStatics = async () => {
  try {
    const response = await axiosInstance.get("/statistics");
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la recupuration des statistics:", error);
    throw error;
  }
};
export const getUserByMonth = async () => {
  try {
    const response = await axiosInstance.get("/usersPerMonth");
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la recupuration des usersPerMonth:", error);
    throw error;
  }
};
import axiosInstance from "../configAxios/axiosInstance";

export const login = async (username, password) => {
  try {
    const response = await axiosInstance.post("/login", {
      username,
      password,
    });
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la connexion:", error);
    throw error;
  }
};
export const sendResetCode = async (to, subject, text, token) => {
  try {
    const response = await axiosInstance.post("/email", {
      to,
      subject,
      text,
    });
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l'envoi du code de réinitialisation:", error);
    throw error;
  }
};

export const updatePassword = async (email, password) => {
  try {
    const response = await axiosInstance.put("/userupdate", {
      email,
      password,
    });
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la connexion:", error);
    throw error;
  }
};

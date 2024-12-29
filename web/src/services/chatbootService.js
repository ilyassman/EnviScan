import axiosInstance from "../configAxios/axiosInstance";
export const getMessageFromChat= async (message) => {
    try {
      const response = await axiosInstance.get(`/adminchatboot?message=${message}`);
      return response.data;
    } catch (error) {
      console.error(`Error response from chatboot`, error);
      throw error;
    }
  };
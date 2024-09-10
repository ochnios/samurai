import axios from "axios";
import { Login } from "../api/Login.ts";
import { User } from "../api/User.ts";

const authUrl = "/auth";

export const loginCall = async (login: Login): Promise<User | void> => {
  return await axios
    .post(`${authUrl}/login`, login)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const logoutCall = async (): Promise<void> => {
  return await axios
    .get(`${authUrl}/logout`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

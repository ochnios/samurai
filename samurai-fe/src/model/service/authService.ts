import axios from "axios";
import { Login } from "../api/auth/Login.ts";
import { User } from "../api/user/User.ts";
import { Register } from "../api/auth/Register.ts";

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

export const registerCall = async (
  register: Register,
): Promise<User | void> => {
  return await axios
    .post(`${authUrl}/register`, register)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const validateUsername = (value: string | undefined): string | null => {
  if (!value || value.trim() === "") {
    return "Username must not be blank";
  }
  if (!/^[a-zA-Z0-9._-]{3,30}$/.test(value)) {
    return "Username must be between 3 and 30 characters and can only contain letters, numbers, dots, underscores, and hyphens";
  }
  return null;
};

export const validateName = (
  value: string | undefined,
  fieldName: string,
): string | null => {
  if (!value || value.trim() === "") {
    return `${fieldName} must not be blank`;
  }
  if (value.length < 3) {
    return `${fieldName} must have at least 3 characters`;
  }
  if (value.length > 50) {
    return `${fieldName} must have at most 50 characters`;
  }
  return null;
};

export const validateEmail = (value: string | undefined): string | null => {
  if (!value || value.trim() === "") {
    return "Email must not be blank";
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(value)) {
    return "Must be a valid email address";
  }
  return null;
};

export const validatePassword = (value: string | undefined): string | null => {
  if (!value || value.trim() === "") {
    return "Password must not be blank";
  }
  if (
    !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()_])[A-Za-z\d@$!%*?&^#()_]{8,30}$/.test(
      value,
    )
  ) {
    return "Password must be 8-30 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character";
  }
  return null;
};

export const validateConfirmPassword = (
  password: string | undefined,
  confirmPassword: string | undefined,
): string | null => {
  if (!confirmPassword || confirmPassword.trim() === "") {
    return "Confirm password must not be blank";
  }
  if (password !== confirmPassword) {
    return "Passwords do not match";
  }
  return null;
};

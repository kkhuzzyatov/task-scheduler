export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const API_ENDPOINTS = {
  auth: {
    login: "/api/auth/login",
  },
  user: {
    register: "/api/user",
    current: "/api/user",
  },
  task: {
    list: "/api/task",
  },
};
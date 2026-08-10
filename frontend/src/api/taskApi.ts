import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";


export interface Task {
  id: string;
  title: string;
  description: string;
  completedAt: string | null;
}


export interface CreateTaskRequest {
  title: string;
  description?: string;
}


export interface UpdateTaskRequest {
  title: string;
  description?: string;
  completed: boolean;
}


function getAuthHeaders() {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error("User is not authenticated");
  }

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}


async function handleResponse<T>(
  response: Response
): Promise<T> {
  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({
        message: "Request failed",
      }));

    throw new Error(
      error.message || "Request failed"
    );
  }

  const contentType =
    response.headers.get("content-type");

  if (
    !contentType ||
    !contentType.includes("application/json")
  ) {
    return undefined as T;
  }

  return response.json();
}


export async function getTasks(): Promise<Task[]> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.list}`,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<Task[]>(response);
}


export async function createTask(
  data: CreateTaskRequest
): Promise<Task> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.list}`,
    {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    }
  );

  return handleResponse<Task>(response);
}


export async function updateTask(
  id: string,
  data: UpdateTaskRequest
): Promise<Task> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.byId(id)}`,
    {
      method: "PATCH",
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    }
  );

  return handleResponse<Task>(response);
}


export async function deleteTask(
  id: string
): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.byId(id)}`,
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<void>(response);
}
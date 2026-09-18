export type AttendanceStatus = "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";

export type Attendance = {
  id: number;
  name: string;
  cpf: string;
  status: AttendanceStatus;
  protocol: string | null;
  createdAt: string;
  updatedAt: string;
};

export type NewAttendance = {
  name: string;
  cpf: string;
};

export const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;
  fieldErrors: Record<string, string>;

  constructor(message: string, status: number, fieldErrors: Record<string, string> = {}) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, init);
  } catch {
    throw new ApiError("Não foi possível conectar ao servidor", 0);
  }

  const body = await response.json().catch(() => null);
  if (!response.ok) {
    throw new ApiError(body?.detail ?? "Erro inesperado no servidor", response.status, body?.errors);
  }
  return body as T;
}

export function openAttendance(data: NewAttendance) {
  return request<Attendance>("/api/attendances", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function getAttendance(id: number) {
  return request<Attendance>(`/api/attendances/${id}`);
}

export function listAttendances() {
  return request<Attendance[]>("/api/attendances");
}

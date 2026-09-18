"use client";

import { onlyDigits } from "@/lib/cpf";

export type StatusFilter = "ALL" | "WAITING" | "COMPLETED" | "FAILED";

const OPTIONS: { value: StatusFilter; label: string }[] = [
  { value: "ALL", label: "Todos" },
  { value: "WAITING", label: "Aguardando" },
  { value: "COMPLETED", label: "Concluído" },
  { value: "FAILED", label: "Falha" },
];

type Props = {
  search: string;
  onSearchChange: (value: string) => void;
  status: StatusFilter;
  onStatusChange: (value: StatusFilter) => void;
};

export function AttendanceFilters({ search, onSearchChange, status, onStatusChange }: Props) {
  return (
    <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
      <input
        value={search}
        onChange={(event) => onSearchChange(event.target.value)}
        placeholder="Buscar por nome ou CPF"
        className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-1.5 text-sm outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-600/20 sm:max-w-56"
      />
      <div className="flex flex-wrap gap-1">
        {OPTIONS.map((option) => (
          <button
            key={option.value}
            type="button"
            onClick={() => onStatusChange(option.value)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition ${
              status === option.value
                ? "bg-teal-700 text-white"
                : "bg-zinc-100 text-zinc-600 hover:bg-zinc-200"
            }`}
          >
            {option.label}
          </button>
        ))}
      </div>
    </div>
  );
}

export function matchesFilters(
  attendance: { name: string; cpf: string; status: string },
  search: string,
  status: StatusFilter,
) {
  if (status === "WAITING" && attendance.status !== "PENDING" && attendance.status !== "PROCESSING") {
    return false;
  }
  if (status === "COMPLETED" && attendance.status !== "COMPLETED") {
    return false;
  }
  if (status === "FAILED" && attendance.status !== "FAILED") {
    return false;
  }

  const term = search.trim().toLowerCase();
  if (!term) return true;

  const cpfDigits = onlyDigits(search);
  return (
    attendance.name.toLowerCase().includes(term) ||
    (cpfDigits.length > 0 && attendance.cpf.includes(cpfDigits))
  );
}

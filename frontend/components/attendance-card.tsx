"use client";

import { useAttendance } from "@/hooks/use-attendance";
import type { Attendance, AttendanceStatus } from "@/lib/api";
import { formatCpf } from "@/lib/cpf";

const statusBadge: Record<AttendanceStatus, { label: string; className: string }> = {
  PENDING: { label: "Aguardando processamento", className: "bg-amber-100 text-amber-800" },
  PROCESSING: { label: "Aguardando processamento", className: "bg-amber-100 text-amber-800" },
  COMPLETED: { label: "Protocolo disponível", className: "bg-emerald-100 text-emerald-800" },
  FAILED: { label: "Falha no processamento", className: "bg-red-100 text-red-800" },
};

type Props = {
  initial: Attendance;
};

export function AttendanceCard({ initial }: Props) {
  const { attendance, error } = useAttendance(initial);
  const badge = statusBadge[attendance.status];
  const waiting = attendance.status === "PENDING" || attendance.status === "PROCESSING";

  return (
    <article className="rounded-xl border border-zinc-200 bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="font-medium">{attendance.name}</p>
          <p className="text-sm text-zinc-500">CPF {formatCpf(attendance.cpf)}</p>
        </div>
        <span
          aria-live="polite"
          className={`inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-medium ${badge.className}`}
        >
          {waiting && (
            <span className="size-3 animate-spin rounded-full border-2 border-current border-t-transparent" />
          )}
          {badge.label}
        </span>
      </div>

      {attendance.status === "COMPLETED" && (
        <div className="mt-4 rounded-lg bg-emerald-50 px-3 py-2">
          <p className="text-xs font-medium uppercase tracking-wide text-emerald-700">Protocolo</p>
          <p className="break-all font-mono text-sm text-emerald-950">{attendance.protocol}</p>
        </div>
      )}

      {attendance.status === "FAILED" && (
        <p className="mt-4 text-sm text-red-600">
          Não foi possível obter o protocolo junto ao serviço externo.
        </p>
      )}

      {error && waiting && (
        <p className="mt-4 text-sm text-amber-700">{error}. Tentando novamente...</p>
      )}
    </article>
  );
}

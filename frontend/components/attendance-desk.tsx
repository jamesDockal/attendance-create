"use client";

import { useState } from "react";
import { AttendanceForm } from "@/components/attendance-form";
import type { Attendance } from "@/lib/api";

export function AttendanceDesk() {
  const [attendance, setAttendance] = useState<Attendance | null>(null);

  return (
    <main className="mx-auto w-full max-w-xl px-4 py-12">
      <header className="mb-8">
        <h1 className="text-2xl font-semibold">Abertura de atendimento</h1>
        <p className="mt-1 text-sm text-zinc-500">
          Informe os dados do paciente para registrar o atendimento.
        </p>
      </header>

      <div className="rounded-xl border border-zinc-200 bg-white p-6 shadow-sm">
        <AttendanceForm onCreated={setAttendance} />
      </div>

      {attendance && (
        <p className="mt-6 text-sm text-zinc-600">
          Atendimento #{attendance.id} registrado. Aguardando processamento.
        </p>
      )}
    </main>
  );
}

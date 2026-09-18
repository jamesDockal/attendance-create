"use client";

import { useEffect, useState } from "react";
import { AttendanceCard } from "@/components/attendance-card";
import { AttendanceForm } from "@/components/attendance-form";
import { listAttendances, type Attendance } from "@/lib/api";

export function AttendanceDesk() {
  const [attendances, setAttendances] = useState<Attendance[]>([]);

  useEffect(() => {
    listAttendances()
      .then(setAttendances)
      .catch(() => {});
  }, []);

  function add(attendance: Attendance) {
    setAttendances((current) => [attendance, ...current]);
  }

  function replace(previousId: number, attendance: Attendance) {
    setAttendances((current) =>
      current.map((item) => (item.id === previousId ? attendance : item)),
    );
  }

  return (
    <main className="mx-auto w-full max-w-xl px-4 py-12">
      <header className="mb-8">
        <h1 className="text-2xl font-semibold">Abertura de atendimento</h1>
        <p className="mt-1 text-sm text-zinc-500">
          Informe os dados do paciente para registrar o atendimento.
        </p>
      </header>

      <div className="rounded-xl border border-zinc-200 bg-white p-6 shadow-sm">
        <AttendanceForm onCreated={add} />
      </div>

      {attendances.length > 0 && (
        <section className="mt-8 space-y-3">
          <h2 className="text-sm font-medium text-zinc-500">Atendimentos</h2>
          {attendances.map((attendance) => (
            <AttendanceCard
              key={attendance.id}
              initial={attendance}
              onRenewed={(next) => replace(attendance.id, next)}
            />
          ))}
        </section>
      )}
    </main>
  );
}

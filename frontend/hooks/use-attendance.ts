import { useEffect, useState } from "react";
import { getAttendance, type Attendance } from "@/lib/api";

const POLL_INTERVAL = 2000;

export function isFinished(attendance: Attendance) {
  return attendance.status === "COMPLETED" || attendance.status === "FAILED";
}

export function useAttendance(initial: Attendance) {
  const [attendance, setAttendance] = useState(initial);
  const [error, setError] = useState<string | null>(null);
  const finished = isFinished(attendance);

  useEffect(() => {
    if (finished) {
      return;
    }

    let cancelled = false;
    let timer: ReturnType<typeof setTimeout>;

    async function poll() {
      try {
        const current = await getAttendance(initial.id);
        if (cancelled) return;
        setAttendance(current);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : "Erro ao consultar atendimento");
      }
      timer = setTimeout(poll, POLL_INTERVAL);
    }

    timer = setTimeout(poll, POLL_INTERVAL);

    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [initial.id, finished]);

  return { attendance, error };
}

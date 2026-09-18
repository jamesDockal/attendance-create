"use client";

import { createContext, useContext, useEffect, useRef } from "react";
import { API_URL, type Attendance } from "@/lib/api";
import { toWebSocketUrl } from "@/lib/websocket-url";

type Listener = (attendance: Attendance) => void;

type ContextValue = {
  enabled: boolean;
  subscribe: (id: number, listener: Listener) => () => void;
};

const AttendanceSocketContext = createContext<ContextValue>({
  enabled: false,
  subscribe: () => () => {},
});

export function AttendanceSocketProvider({
  enabled,
  children,
}: {
  enabled: boolean;
  children: React.ReactNode;
}) {
  const listenersRef = useRef(new Map<number, Set<Listener>>());

  useEffect(() => {
    if (!enabled) return;

    const socket = new WebSocket(toWebSocketUrl(API_URL, "/ws/attendances"));

    socket.onmessage = (event) => {
      let attendance: Attendance;
      try {
        attendance = JSON.parse(event.data);
      } catch {
        return;
      }
      listenersRef.current.get(attendance.id)?.forEach((listener) => listener(attendance));
    };

    return () => socket.close();
  }, [enabled]);

  function subscribe(id: number, listener: Listener) {
    const listeners = listenersRef.current;
    if (!listeners.has(id)) {
      listeners.set(id, new Set());
    }
    listeners.get(id)!.add(listener);
    return () => {
      listeners.get(id)?.delete(listener);
    };
  }

  return (
    <AttendanceSocketContext.Provider value={{ enabled, subscribe }}>
      {children}
    </AttendanceSocketContext.Provider>
  );
}

export function useAttendanceSocket() {
  return useContext(AttendanceSocketContext);
}

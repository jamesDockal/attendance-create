"use client";

type Props = {
  enabled: boolean;
  onChange: (enabled: boolean) => void;
};

export function RealtimeToggle({ enabled, onChange }: Props) {
  return (
    <div className="flex items-center gap-2 text-sm">
      <span className={enabled ? "text-zinc-400" : "font-medium text-zinc-700"}>Polling</span>
      <button
        type="button"
        role="switch"
        aria-checked={enabled}
        onClick={() => onChange(!enabled)}
        className={`relative h-6 w-11 shrink-0 rounded-full transition ${
          enabled ? "bg-teal-700" : "bg-zinc-300"
        }`}
      >
        <span
          className={`absolute top-0.5 left-0.5 size-5 rounded-full bg-white transition-transform ${
            enabled ? "translate-x-5" : ""
          }`}
        />
      </button>
      <span className={enabled ? "font-medium text-zinc-700" : "text-zinc-400"}>WebSocket</span>
    </div>
  );
}

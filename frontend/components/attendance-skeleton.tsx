export function AttendanceSkeleton() {
  return (
    <div className="animate-pulse rounded-xl border border-zinc-200 bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div className="space-y-2">
          <div className="h-4 w-32 rounded bg-zinc-200" />
          <div className="h-3 w-40 rounded bg-zinc-200" />
        </div>
        <div className="h-6 w-28 rounded-full bg-zinc-200" />
      </div>
    </div>
  );
}

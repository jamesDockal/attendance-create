"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ApiError, openAttendance, type Attendance } from "@/lib/api";
import { formatCpf, isValidCpf, onlyDigits } from "@/lib/cpf";

const schema = z.object({
  name: z
    .string()
    .trim()
    .min(3, "Informe o nome completo")
    .max(150, "O nome deve ter no máximo 150 caracteres"),
  cpf: z.string().min(1, "Informe o CPF").refine(isValidCpf, "CPF inválido"),
});

type FormValues = z.infer<typeof schema>;

type Props = {
  onCreated: (attendance: Attendance) => void;
};

const inputClass =
  "w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 outline-none transition focus:border-teal-600 focus:ring-2 focus:ring-teal-600/20 aria-invalid:border-red-500";

export function AttendanceForm({ onCreated }: Props) {
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: "", cpf: "" },
  });

  const cpfField = register("cpf");

  async function onSubmit(values: FormValues) {
    try {
      const attendance = await openAttendance({ name: values.name, cpf: onlyDigits(values.cpf) });
      reset();
      onCreated(attendance);
    } catch (error) {
      if (error instanceof ApiError) {
        for (const [field, message] of Object.entries(error.fieldErrors)) {
          if (field === "name" || field === "cpf") {
            setError(field, { message });
          }
        }
      }
      setError("root", {
        message: error instanceof Error ? error.message : "Erro inesperado",
      });
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
      <div className="space-y-1">
        <label htmlFor="name" className="text-sm font-medium">
          Nome
        </label>
        <input
          id="name"
          autoComplete="name"
          aria-invalid={!!errors.name}
          className={inputClass}
          {...register("name")}
        />
        {errors.name && <p className="text-sm text-red-600">{errors.name.message}</p>}
      </div>

      <div className="space-y-1">
        <label htmlFor="cpf" className="text-sm font-medium">
          CPF
        </label>
        <input
          id="cpf"
          inputMode="numeric"
          placeholder="000.000.000-00"
          aria-invalid={!!errors.cpf}
          className={inputClass}
          {...cpfField}
          onChange={(event) => {
            event.target.value = formatCpf(event.target.value);
            cpfField.onChange(event);
          }}
        />
        {errors.cpf && <p className="text-sm text-red-600">{errors.cpf.message}</p>}
      </div>

      {errors.root && (
        <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
          {errors.root.message}
        </p>
      )}

      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full rounded-lg bg-teal-700 px-4 py-2.5 font-medium text-white transition hover:bg-teal-800 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isSubmitting ? "Enviando..." : "Abrir atendimento"}
      </button>
    </form>
  );
}

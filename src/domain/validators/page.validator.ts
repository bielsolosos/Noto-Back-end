import { z } from "zod";

export const PageCreateSchema = z.object({
  title: z.string().min(1, "O título é obrigatório."),
  content: z.string().optional(),
  archived: z.boolean().optional(),
});

export const PageUpdateSchema = z.object({
  tittle: z.string().min(1).optional(),
  content: z.any().optional(),
  archived: z.boolean().optional(),
});

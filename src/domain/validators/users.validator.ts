import { z } from "zod";

export const UserCreateSchema = z.object({
  email: z.string().min(1, "Email é Obrigatório").email(),
  username: z.string().min(1, "Usuário é obrigatório"),
});

export const ChangePasswordSchema = z.object({
  oldPassword: z.string().min(1, "Senha anterior é Obrigatório"),
  newPassword: z.string().min(1, "Senha nova é Obrigatório"),
});

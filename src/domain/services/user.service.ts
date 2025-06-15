import { comparePasswords, hashPassword } from "../../core/bcrypt";
import {
  ConflictError,
  NotFoundError,
  UnauthorizedError,
} from "../../core/messageValidationUtils";
import {
  changePasswordDto,
  CreateUserDto,
  UserDto,
  UserSummaryDto,
} from "../models/user.model";
import * as repository from "../repositories/user.repository";

const defaultPassword = process.env.DEFAULT_PASSWORD || "SenhaPadrão123";
const API_KEY = process.env.API_KEY || "batatinha123";

export async function createUser(user: CreateUserDto): Promise<UserDto> {
  if (user.apiKey !== API_KEY) {
    throw new UnauthorizedError(
      "Não tenta hackear minha API não mano. PFV. Caso ache algum bug por favor entre em contato com bielrochasantoscoutinho@gmail.com"
    );
  }

  const emailExists = await repository.findByEmail(user.email);
  if (emailExists) {
    throw new ConflictError("Email já está em uso.");
  }

  const usernameExists = await repository.findByUsername(user.username);
  if (usernameExists) {
    throw new ConflictError("Nome de usuário já está em uso.");
  }

  const userToCreate = {
    ...user,
    password: await hashPassword(defaultPassword),
  };

  const userToSend = await repository.createUser(userToCreate);

  return {
    id: userToSend.id,
    email: userToSend.email,
    username: userToSend.username,
  };
}
export async function getAllUsers(): Promise<UserSummaryDto[]> {
  const users = await repository.getUsersSummary();
  return users.map((user) => ({
    id: user.id,
    email: user.email,
    username: user.username,
  }));
}
export async function changePassword(body: changePasswordDto, id: string) {
  // Boleano comparando as senhas.
  const user = await repository.findById(id);

  if (!user) {
    throw new NotFoundError("Usuário não encontrado.");
  }

  const isOldPasswordVerified = await comparePasswords(
    body.oldPassword,
    user?.password
  );

  if (!isOldPasswordVerified) {
    throw new ConflictError("Senha anterior está incorreta.");
  }

  user.password = await hashPassword(body.newPassword);
  return await repository.updateUser(id, user);
}

export async function deleteUser(id: string): Promise<UserDto> {
  const user = await repository.findById(id);

  if (!user) {
    throw new NotFoundError("Usuário não encontrado.");
  }

  return repository.deleteUser(id);
}

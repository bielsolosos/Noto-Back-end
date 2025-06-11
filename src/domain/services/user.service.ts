import { comparePasswords, hashPassword } from "../../core/bcrypt";
import {
  ConflictError,
  NotFoundError,
} from "../../core/messageValidationUtils";
import {
  changePasswordDto,
  CreateUserDto,
  UserDto,
  UserSummaryDto,
} from "../models/user.model";
import * as repository from "../repositories/user.repository";

const defaultPassword = process.env.DEFAULT_PASSWORD || "SenhaPadrão123";

export async function createUser(user: CreateUserDto): Promise<UserDto> {
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

  console.log(isOldPasswordVerified);
  if (!isOldPasswordVerified) {
    throw new ConflictError("Senha anterior está incorreta.");
  }

  user.password = await hashPassword(body.newPassword);
  return await repository.updateUser(id, user);
}

import { hashPassword } from "../../core/bcrypt";
import { ConflictError } from "../../core/messageValidationUtils";
import { CreateUserDto, UserDto, UserSummaryDto } from "../models/user.model";
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

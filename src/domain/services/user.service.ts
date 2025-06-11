import { hashPassword } from "../../core/bcrypt";
import { CreateUserDto, UserDto } from "../models/user.model";
import * as repository from "../repositories/user.repository";

const defaultPassword = process.env.DEFAULT_PASSWORD || "SenhaPadrão123";

export async function createUser(user: CreateUserDto): Promise<UserDto> {
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

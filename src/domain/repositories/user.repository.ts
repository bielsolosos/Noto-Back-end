import { User } from "@prisma/client";
import prisma from "../../core/prisma";
import { CreateUserDto } from "../models/user.model";

export async function createUser(data: CreateUserDto): Promise<User> {
  return prisma.user.create({
    data: {
      email: data.email,
      username: data.username,
      password: data.password as string,
      role_admin: data.role_admin || false,
    },
  });
}

export async function getUsersSummary(): Promise<User[]> {
  return prisma.user.findMany();
}

export async function findById(id: string): Promise<User | null> {
  return prisma.user.findUnique({
    where: {
      id: id,
    },
  });
}

export async function findByEmail(email: string): Promise<User | null> {
  return prisma.user.findUnique({ where: { email } });
}

export async function findByUsername(username: string): Promise<User | null> {
  return prisma.user.findUnique({ where: { username } });
}

export async function deleteUser(id: string): Promise<User> {
  return prisma.user.delete({
    where: {
      id: id,
    },
  });
}

export async function updateUser(id: string, user: User): Promise<User> {
  return prisma.user.update({
    where: { id },
    data: user,
  });
}

// Atualizar senha do usuário
export async function updateUserPassword(
  id: string,
  password: string
): Promise<User> {
  return prisma.user.update({
    where: { id },
    data: { password },
  });
}

// Atualizar role de admin
export async function updateUserRole(
  id: string,
  role_admin: boolean
): Promise<User> {
  return prisma.user.update({
    where: { id },
    data: { role_admin },
  });
}

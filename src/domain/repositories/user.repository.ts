import { User } from "@prisma/client";
import prisma from "../../core/prisma";
import { CreateUserDto } from "../models/user.model";

export async function createUser(data: CreateUserDto): Promise<User> {
  return prisma.user.create({
    data: {
      email: data.email,
      username: data.username,
      password: data.password as string,
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

export async function updatePage(id: string, user: User): Promise<User> {
  return prisma.user.update({
    where: { id },
    data: user,
  });
}

import prisma from "../../core/prisma";
import { User } from "../models/user.model";

export async function createUser(data: User): Promise<User> {
  return prisma.user.create({
    data: data,
  });
}

export async function getUsersSummary(): Promise<User[]> {
  return prisma.user.findMany();
}

export async function getUser(id: string): Promise<User | null> {
  return prisma.user.findUnique({
    where: {
      id: id,
    },
  });
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

import { Page } from "@prisma/client";
import prisma from "../../core/prisma";
import { PageCreateDto, PageUpdateDTO } from "../models/page.model";

export async function createPage(data: PageCreateDto): Promise<Page> {
  return prisma.page.create({
    data: {
      title: data.title,
      content: data.content ? data.content : "",
      userId: data.userId,
    },
  });
}

export async function getPageById(id: string): Promise<Page | null> {
  return prisma.page.findUnique({ where: { id } });
}

export async function getAllPages() {
  return prisma.page.findMany();
}

export async function deletePage(idToDelete: string): Promise<Page> {
  return prisma.page.delete({
    where: {
      id: idToDelete,
    },
  });
}

export async function updatePage(
  id: string,
  data: PageUpdateDTO
): Promise<Page> {
  return prisma.page.update({
    where: { id },
    data: {
      title: data.title,
      content: data.content,
    },
  });
}

import { getUserIdFromToken } from "../../core/jwtUilts";
import {
  PageCreateDto,
  PageDto,
  PageSummaryDto,
  PageUpdateDTO,
} from "../models/page.model";
import * as repository from "../repositories/page.repository";

export async function create(
  data: PageCreateDto,
  token: string | null
): Promise<PageDto> {
  if (!data.content) {
    data.content = "# Bem vindo a sua nova página";
  }

  if (token != null) {
    data.userId = getUserIdFromToken(token);
  }

  const pageToSend = await repository.createPage(data);

  return {
    id: pageToSend.id,
    title: pageToSend.title,
    content: pageToSend.content,
    createdAt: pageToSend.createdAt,
    updatedAt: pageToSend.updatedAt,
    archived: pageToSend.archived,
  };
}

export async function getById(id: string): Promise<PageDto | null> {
  const pageToSend = await repository.getPageById(id);

  if (pageToSend != null) {
    return {
      id: pageToSend.id,
      title: pageToSend.title,
      content: pageToSend.content,
      createdAt: pageToSend.createdAt,
      updatedAt: pageToSend.updatedAt,
      archived: pageToSend.archived,
    };
  }

  return null;
}

export async function update(
  id: string,
  data: PageUpdateDTO
): Promise<PageDto> {
  const pageToSend = await repository.updatePage(id, data);

  return {
    id: pageToSend.id,
    title: pageToSend.title,
    content: pageToSend.content,
    createdAt: pageToSend.createdAt,
    updatedAt: pageToSend.updatedAt,
    archived: pageToSend.archived,
  };
}

export async function list(token: string | null): Promise<PageSummaryDto[]> {
  const userId = getUserIdFromToken(token!);
  const pages = await repository.getAllPagesByUserId(userId);

  const pagesForSummary: PageSummaryDto[] = pages.map((page) => ({
    id: page.id,
    title: page.title,
    updatedAt: page.updatedAt,
  }));

  return pagesForSummary;
}

export async function listFull(token: string | null): Promise<PageDto[]> {
  const userId = getUserIdFromToken(token!);
  return repository.getAllPagesByUserId(userId);
}

export async function deleteById(id: string): Promise<PageDto> {
  return repository.deletePage(id);
}

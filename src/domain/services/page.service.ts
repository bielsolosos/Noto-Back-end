import {
  PageCreateDto,
  PageDto,
  PageSummaryDto,
  PageUpdateDTO,
} from "../models/page.model";
import * as repository from "../repositories/page.repository";

export async function create(data: PageCreateDto): Promise<PageDto> {
  if (!data.content) {
    data.content = "# Bem vindo a sua nova página";
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

export async function list(): Promise<PageSummaryDto[]> {
  const pages = await repository.getAllPages();

  const pagesForSummary: PageSummaryDto[] = pages.map((page) => ({
    id: page.id,
    title: page.title,
    updatedAt: page.updatedAt,
  }));

  return pagesForSummary;
}

export async function listFull(): Promise<PageDto[]> {
  return repository.getAllPages();
}

export async function deleteById(id: string): Promise<PageDto> {
  return repository.deletePage(id);
}

import {
  Page,
  PageCreateDto,
  PageSummaryDto,
  PageUpdateDTO,
} from "../models/page.model";
import * as repository from "../repositories/page.repository";

export async function create(data: PageCreateDto): Promise<Page> {
  if (!data.content) {
    data.content = "# Bem vindo a sua nova página";
  }
  return repository.createPage(data);
}

export async function getById(id: string): Promise<Page | null> {
  return repository.getPageById(id);
}

export async function update(id: string, data: PageUpdateDTO): Promise<Page> {
  return repository.updatePage(id, data);
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

export async function listFull(): Promise<Page[]> {
  return repository.getAllPages();
}

export async function deleteById(id: string): Promise<Page> {
  return repository.deletePage(id);
}

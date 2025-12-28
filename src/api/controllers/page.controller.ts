import { Request, Response } from "express";
import { getTokenFromRequest } from "../../core/jwt-utils";
import { NotFoundError } from "../../core/message-validation-utils";
import { PageCreateDto, PageUpdateDTO } from "../../domain/models/page.model";
import * as service from "../../domain/services/page.service";

export async function createPage(req: Request<{}, {}, PageCreateDto>, res: Response) {
  const token = getTokenFromRequest(req);
  const page = await service.create(req.body, token);
  res.status(201).json(page);
}

export async function getPageById(req: Request, res: Response) {
  const page = await service.getById(req.params.id);
  if (!page) {
    throw new NotFoundError("Page not found");
  }
  res.json(page);
}

export async function updatePage(req: Request<{ id: string }, {}, PageUpdateDTO>, res: Response) {
  const page = await service.update(req.params.id, req.body);
  res.json(page);
}

export async function deletePage(req: Request, res: Response) {
  await service.deleteById(req.params.id);
  res.status(204).send();
}

export async function listPages(req: Request, res: Response) {
  const token = getTokenFromRequest(req);
  const pages = await service.list(token);
  res.json(pages);
}

export async function listPagesFull(req: Request, res: Response) {
  const token = getTokenFromRequest(req);
  const pages = await service.listFull(token);
  res.json(pages);
}

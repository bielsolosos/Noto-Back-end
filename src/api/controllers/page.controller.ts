import { Request, Response } from "express";
import { internalServerError } from "../../core/messageValidationUtils";
import { PageCreateDto, PageUpdateDTO } from "../../domain/models/page.model";
import * as service from "../../domain/services/page.service";

export async function createPage(
  req: Request<{}, {}, PageCreateDto>,
  res: Response
) {
  try {
    const page = await service.create(req.body);
    res.status(201).json(page);
  } catch (error) {
    internalServerError(res);
  }
}

export async function getPageById(req: Request, res: Response) {
  try {
    const page = await service.getById(req.params.id);
    if (!page) {
      res.status(404).json({ error: "Page not found" });
      return;
    }
    res.json(page);
  } catch (error) {
    internalServerError(res);
  }
}

export async function updatePage(
  req: Request<{ id: string }, {}, PageUpdateDTO>,
  res: Response
) {
  try {
    const page = await service.update(req.params.id, req.body);
    res.json(page);
  } catch (error) {
    internalServerError(res);
  }
}

export async function deletePage(req: Request, res: Response) {
  try {
    const page = await service.deleteById(req.params.id);
    res.status(204).send();
  } catch (error) {
    internalServerError(res);
  }
}

export async function listPages(req: Request, res: Response) {
  try {
    const pages = await service.list();
    res.json(pages);
  } catch (error) {
    internalServerError(res);
  }
}

export async function listPagesFull(req: Request, res: Response) {
  try {
    const pages = await service.listFull();
    res.json(pages);
  } catch (error) {
    internalServerError(res);
  }
}

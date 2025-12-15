import { Request, Response } from "express";
import { getTokenFromRequest } from "../../core/jwt-utils";
import { internalServerError } from "../../core/message-validation-utils";
import { PageCreateDto, PageUpdateDTO } from "../../domain/models/page.model";
import * as service from "../../domain/services/page.service";

export async function createPage(req: Request<{}, {}, PageCreateDto>, res: Response) {
  try {
    const token = getTokenFromRequest(req);
    const page = await service.create(req.body, token);
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

export async function updatePage(req: Request<{ id: string }, {}, PageUpdateDTO>, res: Response) {
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
    const token = getTokenFromRequest(req);
    const pages = await service.list(token);
    res.json(pages);
  } catch (error) {
    internalServerError(res);
  }
}

export async function listPagesFull(req: Request, res: Response) {
  try {
    const token = getTokenFromRequest(req);
    const pages = await service.listFull(token);
    res.json(pages);
  } catch (error) {
    internalServerError(res);
  }
}

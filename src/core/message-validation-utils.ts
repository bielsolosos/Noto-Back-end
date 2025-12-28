import { Response } from "express";

export function internalServerError(res: Response) {
  return res.status(500).json({ error: "Internal Server Error" });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 * @deprecated Use o middleware global de erros ao invés desta função
 */
export function conflictErrorMessage(res: Response, error: ConflictError): Response {
  return res.status(409).json({ message: error.message });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 * @deprecated Use o middleware global de erros ao invés desta função
 */
export function notFoundErrorMessage(res: Response, error: NotFoundError): Response {
  return res.status(404).json({ message: error.message });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 * @deprecated Use o middleware global de erros ao invés desta função
 */
export function unauthorizedErrorMessage(res: Response, error: NotFoundError): Response {
  return res.status(503).json({ message: error.message });
}

/**
 * Classe base para erros HTTP customizados
 */
export abstract class HttpError extends Error {
  constructor(public statusCode: number, message: string) {
    super(message);
    this.name = this.constructor.name;
    Error.captureStackTrace(this, this.constructor);
  }
}

/**
 * Erro de Bad Request (400) - Requisição inválida
 */
export class BadRequestError extends HttpError {
  constructor(message: string = "Requisição inválida") {
    super(400, message);
  }
}

/**
 * Erro de Unauthorized (401) - Não autenticado
 */
export class UnauthorizedError extends HttpError {
  constructor(message: string = "Não autorizado") {
    super(401, message);
  }
}

/**
 * Erro de Forbidden (403) - Sem permissão
 */
export class ForbiddenError extends HttpError {
  constructor(message: string = "Acesso negado") {
    super(403, message);
  }
}

/**
 * Erro de Not Found (404) - Recurso não encontrado
 */
export class NotFoundError extends HttpError {
  constructor(message: string = "Recurso não encontrado") {
    super(404, message);
  }
}

/**
 * Erro de Conflict (409) - Conflito de dados
 */
export class ConflictError extends HttpError {
  constructor(message: string = "Conflito de dados") {
    super(409, message);
  }
}

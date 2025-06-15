import { Response } from "express";

export function internalServerError(res: Response) {
  return res.status(500).json({ error: "Internal Server Error" });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 */
export function conflictErrorMessage(
  res: Response,
  error: ConflictError
): Response {
  return res.status(409).json({ message: error.message });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 */
export function notFoundErrorMessage(
  res: Response,
  error: NotFoundError
): Response {
  return res.status(404).json({ message: error.message });
}

/**
 * Função que retorna a formatação Rest para qualquer mensagem de erro usado em conflitos
 * @param res
 * @param error
 * @returns
 */
export function unauthorizedErrorMessage(
  res: Response,
  error: NotFoundError
): Response {
  return res.status(503).json({ message: error.message });
}

/**
 * Classe de validação para erros de conflito (Repositories.)
 */
export class ConflictError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "ConflictError";
  }
}

export class NotFoundError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "NotFoundError";
  }
}

export class UnauthorizedError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "NotFoundError";
  }
}

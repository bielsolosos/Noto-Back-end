import { Prisma } from "@prisma/client";
import { NextFunction, Request, Response } from "express";
import { HttpError } from "../../core/message-validation-utils";

/**
 * Middleware global de tratamento de erros
 * Deve ser registrado após todas as rotas no server.ts
 */
export function errorHandler(error: Error, req: Request, res: Response, next: NextFunction) {
  // Erros HTTP customizados (UnauthorizedError, NotFoundError, ConflictError, etc.)
  if (error instanceof HttpError) {
    return res.status(error.statusCode).json({
      error: error.message,
    });
  }

  // Erros de validação do Prisma
  if (error instanceof Prisma.PrismaClientValidationError) {
    return res.status(400).json({
      error: "Dados inválidos fornecidos",
    });
  }

  // Erros conhecidos do Prisma
  if (error instanceof Prisma.PrismaClientKnownRequestError) {
    // Violação de constraint única (ex: email duplicado)
    if (error.code === "P2002") {
      const target = (error.meta?.target as string[]) || [];
      return res.status(409).json({
        error: `Conflito: ${target.join(", ")} já existe`,
      });
    }

    // Registro não encontrado
    if (error.code === "P2025") {
      return res.status(404).json({
        error: "Recurso não encontrado",
      });
    }

    // Foreign key constraint failed
    if (error.code === "P2003") {
      return res.status(400).json({
        error: "Referência inválida",
      });
    }
  }

  // Erro de conexão com o banco
  if (error instanceof Prisma.PrismaClientInitializationError || error instanceof Prisma.PrismaClientRustPanicError) {
    return res.status(503).json({
      error: "Erro ao conectar com o banco de dados",
    });
  }

  // Erro de validação do Zod (caso não seja tratado pelo middleware de validação)
  if (error.name === "ZodError") {
    return res.status(400).json({
      error: "Erro de validação",
      details: (error as any).errors,
    });
  }

  // Erro genérico (não tratado especificamente)
  return res.status(500).json({
    error: "Erro interno do servidor",
    ...(process.env.NODE_ENV === "development" && {
      details: error.message,
      stack: error.stack,
    }),
  });
}

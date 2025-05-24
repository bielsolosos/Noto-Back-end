import { NextFunction, Request, RequestHandler, Response } from "express";
import { ZodSchema } from "zod";

export function validateBody<T>(schema: ZodSchema<T>): RequestHandler {
  return (req: Request, res: Response, next: NextFunction): void => {
    const result = schema.safeParse(req.body);
    if (!result.success) {
      // Aqui o 'return' é só pra sair da função, não para retornar para o Express
      res.status(400).json({
        error: "Validation Error",
        details: result.error.flatten(),
      });
      return;
    }
    req.body = result.data;
    next();
  };
}

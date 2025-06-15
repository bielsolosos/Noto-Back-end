import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { JwtPayload } from "./jwtUilts";

const JWT_SECRET = process.env.JWT_SECRET || "segredo-jwt";

export interface AuthenticatedRequest extends Request {
  user?: {
    sub: string;
    username: string;
  };
}

export function authenticateToken(
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
): void {
  // <- note o void aqui, sem retorno de Response
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];

  if (!token) {
    res.status(401).json({ message: "Token não fornecido" });
    return; // aqui só retorna para parar a execução do middleware
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET) as JwtPayload;
    req.user = decoded;
    next(); // importante chamar o next para continuar o fluxo
  } catch (err) {
    res.status(403).json({ message: "Token inválido" });
  }
}

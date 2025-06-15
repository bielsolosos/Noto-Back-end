import { Request, Response } from "express";
import {
  internalServerError,
  UnauthorizedError,
  unauthorizedErrorMessage,
} from "../../core/messageValidationUtils";
import { LoginDto } from "../../domain/models/auth.model";
import * as authService from "../../domain/services/auth.service";

export async function login(req: Request<{}, {}, LoginDto>, res: Response) {
  try {
    const { email, password } = req.body;

    const result = await authService.login(email, password);

    res.json(result);
  } catch (error) {
    if (error instanceof UnauthorizedError) {
      unauthorizedErrorMessage(res, error);
    }

    internalServerError(res);
  }
}

export async function refreshToken(req: Request, res: Response) {
  const { refreshToken } = req.body;

  const newToken = await authService.refreshToken(refreshToken);

  if (!newToken) {
    res.status(401).json({ message: "Refresh token inválido ou expirado" });
  } else {
    res.json({ accessToken: newToken });
  }
}

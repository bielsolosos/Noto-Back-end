import { Request, Response } from "express";
import { UnauthorizedError } from "../../core/message-validation-utils";
import { LoginDto } from "../../domain/models/auth.model";
import * as authService from "../../domain/services/auth.service";

export async function login(req: Request<{}, {}, LoginDto>, res: Response) {
  const { email, password, apiKey } = req.body;
  const result = await authService.login(email, password, apiKey);
  res.json(result);
}

export async function refreshToken(req: Request, res: Response) {
  const { refreshToken } = req.body;
  const newToken = await authService.refreshToken(refreshToken);

  if (!newToken) {
    throw new UnauthorizedError("Refresh token inválido ou expirado");
  }

  res.json({ accessToken: newToken });
}

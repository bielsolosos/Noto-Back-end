import { Request, Response } from "express";
import { authLogger, errorLogger } from "../../core/logger";
import {
  internalServerError,
  UnauthorizedError,
  unauthorizedErrorMessage,
} from "../../core/messageValidationUtils";
import { LoginDto } from "../../domain/models/auth.model";
import * as authService from "../../domain/services/auth.service";

export async function login(req: Request<{}, {}, LoginDto>, res: Response) {
  try {
    const { email, password, apiKey } = req.body;

    authLogger.loginAttempt(email, false, {
      action: "login_start",
      ipAddress: req.ip,
    });

    const result = await authService.login(email, password, apiKey);

    authLogger.loginAttempt(email, true, {
      action: "login_success",
      ipAddress: req.ip,
    });

    res.json(result);
  } catch (err: unknown) {
    const error = err instanceof Error ? err : new Error("Unknown error");

    authLogger.loginAttempt(req.body.email, false, {
      action: "login_failed",
      error: error.message,
      ipAddress: req.ip,
    });

    if (error instanceof UnauthorizedError) {
      unauthorizedErrorMessage(res, error);
    } else {
      errorLogger.error(error, {
        action: "login",
        email: req.body.email,
        ipAddress: req.ip,
      });
      internalServerError(res);
    }
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

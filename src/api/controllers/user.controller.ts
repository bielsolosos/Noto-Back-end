import { Request, Response } from "express";
import { getTokenFromRequest } from "../../core/jwt-utils";
import {
  ConflictError,
  conflictErrorMessage,
  internalServerError,
  NotFoundError,
  notFoundErrorMessage,
  UnauthorizedError,
} from "../../core/message-validation-utils";
import { changePasswordDto, CreateUserDto } from "../../domain/models/user.model";
import * as service from "../../domain/services/user.service";

export async function getMe(req: Request<{}, {}>, res: Response) {
  const user = await service.getMe(getTokenFromRequest(req));
  res.status(200).json(user);
}

export async function createUser(req: Request<{}, {}, CreateUserDto>, res: Response) {
  try {
    const user = await service.createUser(req.body);
    res.status(201).json(user);
    return;
  } catch (error) {
    if (error instanceof ConflictError) {
      conflictErrorMessage(res, error as ConflictError);
    }
    if (error instanceof UnauthorizedError) {
      conflictErrorMessage(res, error as UnauthorizedError);
    }
    internalServerError(res);
  }
}

export async function getAllUsers(req: Request<{}, {}>, res: Response) {
  try {
    const users = await service.getAllUsers();
    res.json(users);
  } catch (error) {
    internalServerError(res);
  }
}

export async function changePassword(req: Request<{ id: string }, {}, changePasswordDto>, res: Response) {
  try {
    const user = await service.changePassword(req.body, req.params.id);

    res.json(user);
  } catch (error) {
    if (error instanceof NotFoundError) {
      notFoundErrorMessage(res, error);
    }

    if (error instanceof ConflictError) {
      conflictErrorMessage(res, error);
    }

    internalServerError(res);
  }
}

export async function deleteUser(req: Request<{ id: string }, {}, {}>, res: Response) {
  try {
    const user = await service.deleteUser(req.params.id);

    res.status(204).send();
  } catch (error) {
    if (error instanceof NotFoundError) {
      notFoundErrorMessage(res, error);
    }

    internalServerError(res);
  }
}

export async function updateUserRole(req: Request<{ id: string }, {}, { role_admin: boolean }>, res: Response) {
  try {
    const { id } = req.params;
    const { role_admin } = req.body;

    const user = await service.updateUserRole(id, role_admin);
    res.json(user);
  } catch (error) {
    if (error instanceof NotFoundError) {
      notFoundErrorMessage(res, error);
    }
    internalServerError(res);
  }
}

import { Request, Response } from "express";
import {
  ConflictError,
  conflictErrorMessage,
  internalServerError,
} from "../../core/messageValidationUtils";
import { CreateUserDto } from "../../domain/models/user.model";
import * as service from "../../domain/services/user.service";

export async function createUser(
  req: Request<{}, {}, CreateUserDto>,
  res: Response
) {
  try {
    const user = await service.createUser(req.body);
    res.status(201).json(user);
    return;
  } catch (error) {
    if (error instanceof ConflictError) {
      conflictErrorMessage(res, error as ConflictError);
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

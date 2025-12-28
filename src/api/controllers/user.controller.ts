import { Request, Response } from "express";
import { getTokenFromRequest } from "../../core/jwt-utils";
import { ForbiddenError } from "../../core/message-validation-utils";
import { changePasswordDto, CreateUserDto } from "../../domain/models/user.model";
import * as service from "../../domain/services/user.service";

export async function getMe(req: Request<{}, {}>, res: Response) {
  const user = await service.getMe(getTokenFromRequest(req));
  res.status(200).json(user);
}

export async function createUser(req: Request<{}, {}, CreateUserDto>, res: Response) {
  const user = await service.createUser(req.body);
  res.status(201).json(user);
}

export async function getAllUsers(req: Request<{}, {}>, res: Response) {
  const users = await service.getAllUsers();
  res.json(users);
}

export async function changePassword(req: Request<{ id: string }, {}, changePasswordDto>, res: Response) {
  if (getTokenFromRequest(req) == req.params.id) {
    throw new ForbiddenError("Operação não permitida");
  }

  const user = await service.changePassword(req.body, req.params.id);
  res.json(user);
}

export async function deleteUser(req: Request<{ id: string }, {}, {}>, res: Response) {
  await service.deleteUser(req.params.id);
  res.status(204).send();
}

export async function updateUserRole(req: Request<{ id: string }, {}, { role_admin: boolean }>, res: Response) {
  const { id } = req.params;
  const { role_admin } = req.body;

  const user = await service.updateUserRole(id, role_admin);
  res.json(user);
}

import { Request, Response } from "express";
import { internalServerError } from "../../core/internalServerErrorUtils";
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
    internalServerError(res);
  }
}

//TODO terminar esses cabras aqui

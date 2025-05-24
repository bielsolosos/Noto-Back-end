import { Request, Response } from "express";
import { Home } from "../../domain/models/home.model";

export function testHome(req: Request, res: Response<Home>) {
  res.status(200).send({
    status: "API FUNCIONANDO!",
  });
}

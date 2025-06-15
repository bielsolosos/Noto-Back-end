import { Router } from "express";
import { validateBody } from "../../core/validateBody";
import { LoginSchema } from "../../domain/validators/auth.validator";
import { login, refreshToken } from "../controllers/auth.controller";

const router = Router();

router.post("", validateBody(LoginSchema), login);
router.post("/refresh", refreshToken);

export default router;

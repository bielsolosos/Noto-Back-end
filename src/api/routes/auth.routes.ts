import { Router } from "express";
import { validateBody } from "../../core/validateBody";
import { login, refreshToken } from "../controllers/auth.controller";
import { LoginSchema } from "../validators/auth.validator";

const router = Router();

router.post("", validateBody(LoginSchema), login);
router.post("/refresh", refreshToken);

export default router;

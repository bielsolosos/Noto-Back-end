import { Router } from "express";
import { validateBody } from "../../core/validateBody";
import { UserCreateSchema } from "../../domain/validators/users.validator";
import { createUser, getAllUsers } from "../controllers/user.controller";

const router = Router();

router.get("", getAllUsers);
router.post("", validateBody(UserCreateSchema), createUser);

export default router;

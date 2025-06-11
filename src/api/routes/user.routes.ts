import { Router } from "express";
import { validateBody } from "../../core/validateBody";
import {
  ChangePasswordSchema,
  UserCreateSchema,
} from "../../domain/validators/users.validator";
import {
  changePassword,
  createUser,
  getAllUsers,
} from "../controllers/user.controller";

const router = Router();

router.get("", getAllUsers);
router.post("", validateBody(UserCreateSchema), createUser);
router.post(
  "/change-password/:id",
  validateBody(ChangePasswordSchema),
  changePassword
);

export default router;

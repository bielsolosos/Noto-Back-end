/**
 * @swagger
 * tags:
 *   name: Users
 *   description: Endpoints relacionados a usuários
 */

/**
 * @swagger
 * /users:
 *   get:
 *     tags:
 *       - Users
 *     summary: Lista todos os usuários
 *     operationId: getAllUsers
 *     responses:
 *       200:
 *         description: Lista de usuários
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/UserSummary'
 */

/**
 * @swagger
 * /users:
 *   post:
 *     tags:
 *       - Users
 *     summary: Cria um novo usuário
 *     operationId: createUser
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateUser'
 *     responses:
 *       201:
 *         description: Usuário criado com sucesso
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/User'
 *       409:
 *         description: Conflito - email ou username já está em uso
 */

/**
 * @swagger
 * /users/change-password/{id}:
 *   post:
 *     tags:
 *       - Users
 *     summary: Altera a senha de um usuário
 *     operationId: changePassword
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID do usuário
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/ChangePassword'
 *     responses:
 *       200:
 *         description: Senha alterada com sucesso
 *       404:
 *         description: Usuário não encontrado
 *       409:
 *         description: Senha antiga incorreta
 */

/**
 * @swagger
 * /users/{id}:
 *   delete:
 *     tags:
 *       - Users
 *     summary: Remove um usuário pelo ID
 *     operationId: deleteUser
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID do usuário
 *         schema:
 *           type: string
 *     responses:
 *       204:
 *         description: Usuário removido com sucesso
 *       404:
 *         description: Usuário não encontrado
 */

/**
 * @swagger
 * components:
 *   schemas:
 *     User:
 *       type: object
 *       properties:
 *         id:
 *           type: string
 *           example: "1234"
 *         email:
 *           type: string
 *           example: "usuario@email.com"
 *         username:
 *           type: string
 *           example: "usuario123"
 *
 *     UserSummary:
 *       type: object
 *       properties:
 *         id:
 *           type: string
 *         email:
 *           type: string
 *         username:
 *           type: string
 *
 *     CreateUser:
 *       type: object
 *       required:
 *         - email
 *         - username
 *       properties:
 *         email:
 *           type: string
 *           example: "usuario@email.com"
 *         username:
 *           type: string
 *           example: "usuario123"
 *         password:
 *           type: string
 *           example: "SenhaSegura123"
 *
 *     ChangePassword:
 *       type: object
 *       required:
 *         - oldPassword
 *         - newPassword
 *       properties:
 *         oldPassword:
 *           type: string
 *           example: "SenhaAntiga123"
 *         newPassword:
 *           type: string
 *           example: "SenhaNova123"
 */
import { Router } from "express";
import { authenticateToken } from "../../core/jwtRequestMiddleware";
import { validateBody } from "../../core/validateBody";
import {
  ChangePasswordSchema,
  UserCreateSchema,
} from "../../domain/validators/users.validator";
import {
  changePassword,
  createUser,
  deleteUser,
  getAllUsers,
} from "../controllers/user.controller";

const router = Router();

router.use(authenticateToken);

router.get("", getAllUsers);
router.post("", validateBody(UserCreateSchema), createUser);
router.post(
  "/change-password/:id",
  validateBody(ChangePasswordSchema),
  changePassword
);
router.delete("/:id", deleteUser);

export default router;
